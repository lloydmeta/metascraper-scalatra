package com.beachape.metascraper.scalatra

import org.scalatra._
import scalate.ScalateSupport
import com.beachape.metascraper.scalatra.models.MetadataScraper
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import com.beachape.metascraper.Messages.ScrapedData
import scala.concurrent.ExecutionContext

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
    new AsyncResult {
      contentType = formats("json")
      val is = scraper.scrape(params.getOrElse("url", "notProvided")) map {
        case Right(data) => data
        case Left(err) => Map("error" -> err.getMessage)
      }
    }
  }

  post("/scrape", request.contentType == Some(formats("json"))) {
    new AsyncResult {
      contentType = formats("json")
      val is = scraper.scrape(parsedBody.extract[ScrapeRequest].url) map {
        case Right(data) => data
        case Left(err) => Map("error" -> err.getMessage)
      }
    }
  }

}
