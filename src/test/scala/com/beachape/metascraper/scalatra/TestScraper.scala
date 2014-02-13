package com.beachape.metascraper.scalatra

import com.beachape.metascraper.scalatra.models.Scraper
import scala.concurrent.{ExecutionContext, Future}
import com.beachape.metascraper.Messages
import com.beachape.metascraper.Messages.ScrapedData

/**
 * A mock scraper
 */
class TestScraper(val willFail: Boolean) extends Scraper {

  import ExecutionContext.Implicits.global

  def scrape(url: Url): Future[scala.Either[scala.Throwable, Messages.ScrapedData]] = {
    if (willFail) {
      Future { Left(new IllegalArgumentException) }
    } else {
      Future { Right(ScrapedData(url, url, url, url, Seq(url))) }
    }
  }

}
