package com.beachape.metascraper.scalatra

import org.scalatra.test.scalatest._
import org.scalatest.FunSuite
import akka.actor.ActorSystem
import akka.testkit.TestKit
import java.net.URLEncoder
import com.beachape.metascraper.scalatra.controllers.{ScrapperSwagger, ScraperServlet}

class ScraperServletSpec extends TestKit(ActorSystem("testSystem")) with ScalatraSuite with FunSuite {

  // Get a handle to an ActorSystem
  implicit val testActorSystem = system
  implicit val ec = system.dispatcher
  implicit val swagger = new ScrapperSwagger

  val scraperSuccessful = new MockScraper(false)
  val scraperUnsuccessful = new MockScraper(true)
  // Mount at two different paths for testing
  addServlet(new ScraperServlet(scraperSuccessful, new MockMemcached), "/success/*")
  addServlet(new ScraperServlet(scraperUnsuccessful, new MockMemcached), "/fail/*")

  trait Successful {
    def pathTo(rootPath: String) = s"/success${rootPath}"
  }

  trait UnSuccessful {
    def pathTo(rootPath: String) = s"/fail${rootPath}"
  }

  test("getting root") {
    new Successful {
      get(pathTo("/")) {
        status should equal (200)
      }
    }
    new UnSuccessful {
      get(pathTo("/")) {
        status should equal (200)
      }
    }
  }

  test("GETing to /scrape with JSON with a URL parameter when the Scraper is successful") {
    new Successful {
      get(pathTo(String.format("/scrape/%s", URLEncoder.encode("http://lol.com", "UTF-8")))) {
        status should equal (200)
      }
    }
  }

  test("GETing to /scrape with x-www-form-urlencoded with a URL parameter when the Scraper is successful") {
    new Successful {
      get(pathTo(String.format("/scrape/%s", URLEncoder.encode("http://lol.com", "UTF-8")))) {
        status should equal (200)
      }
    }
  }

  test("GETing to /scrape with JSON with a URL parameter when the Scraper is unsuccessful") {
    new UnSuccessful {
      get(pathTo(String.format("/scrape/%s", URLEncoder.encode("http://lol.com", "UTF-8")))) {
        status should equal (422)
      }
    }
  }

  test("GETing to /scrape with x-www-form-urlencoded with a URL parameter when the Scraper is unsuccessful") {
    new UnSuccessful {
      get(pathTo(String.format("/scrape/%s", URLEncoder.encode("http://lol.com", "UTF-8")))) {
        status should equal (422)
      }
    }
  }

}
