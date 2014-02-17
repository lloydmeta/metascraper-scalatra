package com.beachape.metascraper.scalatra.models

import shade.memcached.{MemcachedCodecs, Memcached}
import com.beachape.metascraper.Messages.{ScrapedData, Url}
import scala.concurrent.{ExecutionContext, Future}
import org.json4s._
import org.json4s.jackson.Serialization.{read, write}
import scala.concurrent.duration.Duration
import java.security.MessageDigest


trait ScraperMemcachedSupport extends MemcachedCodecs {

  def memcached: Memcached
  def cacheDataTTL: Duration
  implicit def executor: ExecutionContext

  implicit val serializationFormats = DefaultFormats.withBigDecimal
  /**
   * Caches a ScrapedData instance into a specific key
   *
   * An expiry will be set equal to the cacheDataTTL implemented
   *
   * @param url String that acts as a key
   * @param data ScrapeData
   * @return Future[Unit]
   */

  def cacheScrapedData(url: Url, data: ScrapedData): Future[Unit] = memcached.set[String](md5Hash(url), write(data), cacheDataTTL)

  /**
   * Returns a Future[Option[ScrapedData]]
   *
   * @param url String of the URL that acts as a key
   * @return Future[Option[ScrapedData]]
   */
  def fetchCachedScrapedData(url: Url): Future[Option[ScrapedData]] = for {
    maybeString <- memcached.get[String](md5Hash(url))
  } yield maybeString match {
      case Some(jsonString) => Some(read[ScrapedData](jsonString))
      case _ => None
  }

  /**
   * Compute the MD5 hash for the URL string to avoid keys being too long
   * @param s String
   * @return String
   */
  private def md5Hash(s: String) : String =
    MessageDigest.getInstance("MD5").digest(s.getBytes()).map(0xFF & _).map("%02X".format(_)).mkString
}
