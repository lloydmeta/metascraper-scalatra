package com.beachape.metascraper.scalatra.models

import scala.concurrent.Future
import com.beachape.metascraper.Messages

/**
 * Abstract Scraper
 */
trait Scraper {
  type Url = String
  def scrape(url: String): Future[scala.Either[scala.Throwable, Messages.ScrapedData]]
}
