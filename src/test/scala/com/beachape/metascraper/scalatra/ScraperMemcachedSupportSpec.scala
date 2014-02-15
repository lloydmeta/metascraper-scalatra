package com.beachape.metascraper.scalatra

import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, FunSpec}
import org.scalatest.matchers.ShouldMatchers
import akka.actor.ActorSystem
import shade.memcached.{Memcached, Configuration}
import scala.concurrent.duration._
import com.beachape.metascraper.scalatra.models.ScraperMemcachedSupport
import scala.concurrent.ExecutionContext
import com.beachape.metascraper.Messages.ScrapedData

class ScraperMemcachedSupportSpec extends TestKit(ActorSystem("testSystem"))
  with FunSpec
  with ShouldMatchers
  with BeforeAndAfterAll {
  val ec = system.dispatcher

  val scheduler = system.scheduler

  val memcachedConnection = Memcached(Configuration("127.0.0.1:11211", keysPrefix = Some("test")), scheduler, ec)
  val randomScrapedData = ScrapedData("hello", "hello", "hello", "hello", Seq("hello"))

  override def afterAll {
    memcachedConnection.delete("something")
    memcachedConnection.delete("something_else")
    memcachedConnection.close()
  }

  trait fakeScraperMemcachedImp extends ScraperMemcachedSupport {
    val memcached = memcachedConnection
    val cacheDataTTL = 10 seconds
    override implicit def executor: ExecutionContext = ec
  }

  describe("#cacheScrapedData") {
    it ("should put a retrievable ScrapedData into Memcached") {
      new fakeScraperMemcachedImp {
        cacheScrapedData("something", randomScrapedData)
        fetchCachedScrapedData("something").foreach(_ should be(Some(randomScrapedData)))
      }
    }
  }

  describe("#fetchCachedScrapedData") {
    it ("should return nothing if handed a non-existent key") {
      new fakeScraperMemcachedImp {
        fetchCachedScrapedData("non-existent").foreach(_ should be(None))
      }
    }
    it ("should get back a retrievable ScrapedData by key") {
      new fakeScraperMemcachedImp {
        cacheScrapedData("something_else", randomScrapedData)
        fetchCachedScrapedData("something_else").foreach(_ should be(Some(randomScrapedData)))
      }
    }
  }

}
