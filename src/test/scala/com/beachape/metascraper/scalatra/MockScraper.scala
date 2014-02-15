package com.beachape.metascraper.scalatra

import com.beachape.metascraper.scalatra.models.Scraper
import scala.concurrent.Future
import com.beachape.metascraper.Messages
import com.beachape.metascraper.Messages.ScrapedData

/**
 * A mock scraper
 */
class MockScraper(val willFail: Boolean) extends Scraper {

  def scrape(url: Url): Future[scala.Either[scala.Throwable, Messages.ScrapedData]] = {
    if (willFail) {
      Future.successful(Left(new IllegalArgumentException))
    } else {
      Future.successful(Right(ScrapedData(url, url, url, url, Seq(url))))
    }
  }

}
