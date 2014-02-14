package com.beachape.metascraper.scalatra

import org.scalatra.test.scalatest._
import org.scalatest.FunSuite
import akka.actor.ActorSystem
import akka.testkit.TestKit

class ScraperServletSpec extends TestKit(ActorSystem("testSystem")) with ScalatraSuite with FunSuite {

  // Get a handle to an ActorSystem
  implicit val testActorSystem = system
  implicit val ec = system.dispatcher

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

  test("POSTing to /scrape with JSON with a URL parameter when the Scraper is successful") {
    new Successful {
      post(pathTo("/scrape"), """{"url":  "http://lol.com"}""", Map("Content-Type" -> "application/json")) {
        status should equal (200)
      }
    }
  }

  test("POSTing to /scrape with x-www-form-urlencoded with a URL parameter when the Scraper is successful") {
    new Successful {
      post(pathTo("/scrape"), "url" -> "http://lol.com") {
        status should equal (200)
      }
    }
  }

  test("POSTing to /scrape with JSON with a URL parameter when the Scraper is unsuccessful") {
    new UnSuccessful {
      post(pathTo("/scrape"), """{"url":  "http://lol.com"}""", Map("Content-Type" -> "application/json")) {
        status should equal (422)
      }
    }
  }

  test("POSTing to /scrape with x-www-form-urlencoded with a URL parameter when the Scraper is unsuccessful") {
    new UnSuccessful {
      post(pathTo("/scrape"), "url" -> "http://lol.com") {
        status should equal (422)
      }
    }
  }

}
