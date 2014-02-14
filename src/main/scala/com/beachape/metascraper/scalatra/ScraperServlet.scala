package com.beachape.metascraper.scalatra

import org.scalatra._
import com.beachape.metascraper.scalatra.models.{ScraperMemcachedSupport, Scraper}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import scala.concurrent.{Future, ExecutionContext}
import javax.servlet.http.HttpServletRequest
import shade.memcached._
import scala.concurrent.duration._
import org.slf4j.LoggerFactory
import org.scalatra.swagger._
import com.beachape.metascraper.Messages.ScrapedData

/**
 * Case class for extracting URL from JSON params
 * @param url String
 */
case class ScrapeRequest(url: String)

class ScraperServlet(val scraper: Scraper, val memcached: Memcached)(implicit val executor: ExecutionContext, val swagger: Swagger)
  extends ScalatraServlet
  with JacksonJsonSupport
  with FutureSupport
  with ScraperMemcachedSupport
  with SwaggerSupport {

  override protected val applicationName = Some("Metascraper-Scalatra")
  protected val applicationDescription = "The Metascraper-Scalatra API."

  protected implicit val jsonFormats: Formats = DefaultFormats.withBigDecimal
  val cacheDataTTL = 10 minutes

  val logger = LoggerFactory.getLogger(getClass)

  get("/") {
    <html>
      <body>
        <h1>You are lost!</h1>
      </body>
    </html>
  }

  val scrape =
    (apiOperation[List[ScrapedData]]("scrape")
      summary "Scrape the metadata from a URL"
      notes "Scrape the metadata from a URL. Gives images, descriptions, titles, URL."
      parameter bodyParam[String]("url").description("URL to scrape"))

  post("/scrape",  operation(scrape)) {
    val url = urlPostParam(request)
    new AsyncResult {
      contentType = formats("json")
      val is = fetchCachedScrapedData(url) flatMap {
        /*
          This is wrapped inside a future because further down, we are inside another future
          and we need to accomodate by using a flatMap
        */
        case Some(data) => Future.successful(data)
        case None => scraper.scrape(url) map {
          case Right(data) => {
            cacheScrapedData(url, data)
            data
          }
          case Left(err) => {
            status = 422
            Map("error" -> err.getMessage)
          }
        }
      }
    }
  }

  /**
   * Given the current request, extracts out the "url" field
   * regardless of if the request was a JSON or x-www-form-urlencoded
   * body
   *
   * @param req HttpServletRequest
   * @return String
   */
  private def urlPostParam(req: HttpServletRequest, notPovidedUrl: String = "notProvided"): String = req.contentType match {
    case Some(cType) if cType == formats("json") => {
      try { parsedBody(req).extract[ScrapeRequest].url }
      catch { case _: Throwable => notPovidedUrl }
    }
    case _ => params(req).getOrElse("url", notPovidedUrl)
  }

}
