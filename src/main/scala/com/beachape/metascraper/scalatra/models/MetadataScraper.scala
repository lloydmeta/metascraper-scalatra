package com.beachape.metascraper.scalatra.models
import akka.routing.SmallestMailboxRouter
import akka.pattern.ask

import com.beachape.metascraper.Messages._
import com.beachape.metascraper.ScraperActor
import scala.concurrent.Future
import akka.util.Timeout
import scala.concurrent.duration._
import akka.actor.ActorSystem

/**
 * Companion object to instantiate MetadataScraper instances
 */
object MetadataScraper {

  type Url = String
  implicit val timeout = Timeout(30 seconds) // Urls to scrape must return before this timeout

  def apply(concurrency: Int = 5)(implicit system: ActorSystem): MetadataScraper = {
    new MetadataScraper(concurrency)
  }
}

/**
 * Object for scraping data from a Url that was passed in during construction
 *
 * Most of the heavy lifting is done by the ScraperActors in the companion object,
 * which in turn depends on the Metascraper library
 *
 * Should be constructed/instantiated by using the companion's apply method
 */
class MetadataScraper(val concurrency: Int = 5)(implicit system: ActorSystem) {

  import MetadataScraper._

  lazy val metaScraperActorsRoundRobin = system.actorOf(
    ScraperActor().withRouter(SmallestMailboxRouter(concurrency)), "router")

  /**
   * Returns either a failure to scrape or ScrapedData
   * @param url Url string
   * @return Future[Either[Throwable, ScrapedData]
   */
  def scrape(url: Url): Future[Either[Throwable, ScrapedData]] = {
    ask(metaScraperActorsRoundRobin, ScrapeUrl(url, userAgent = "Metacraper")).
      mapTo[Either[Throwable, ScrapedData]]
  }

}