package com.beachape.metascraper.scalatra

import org.scalatra._
import scalate.ScalateSupport
import com.beachape.metascraper.scalatra.models.MetadataScraper
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import com.beachape.metascraper.Messages.ScrapedData
import scala.concurrent.ExecutionContext
import javax.servlet.http.HttpServletRequest

/**
 * Case class for extracting URL from JSON params
 * @param url String
 */
case class ScrapeRequest(url: String)

class MetascraperScalatraServlet(val scraper: MetadataScraper)(implicit val executor: ExecutionContext)
  extends MetascraperScalatraStack
  with JacksonJsonSupport
  with FutureSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats.withBigDecimal


  get("/") {
    <html>
      <body>
        <h1>You are lost!</h1>
      </body>
    </html>
  }

  post("/scrape") {
    val url = urlPostParam(request)
    new AsyncResult {
      contentType = formats("json")
      val is = scraper.scrape(url) map {
        case Right(data) => data
        case Left(err) => Map("error" -> err.getMessage)
      }
    }
  }

  def urlPostParam (req: HttpServletRequest): String = {
    val notPovidedUrl = "notProvided"
    req.contentType match {
      case Some(cType) if cType == formats("json") => {
        try { parsedBody.extract[ScrapeRequest].url }
        catch { case _ => notPovidedUrl }
      }
      case _ => params.getOrElse("url", notPovidedUrl)
    }
  }

}
