package com.beachape.metascraper.scalatra

import org.scalatra.test.scalatest._
import org.scalatest.FunSuite
import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit, ImplicitSender}
import com.beachape.metascraper.scalatra.models.MetadataScraper

class MetascraperScalatraServletSpec extends TestKit(ActorSystem("testSystem")) with ScalatraSuite with FunSuite {

  // Get a handle to an ActorSystem
  implicit val testActorSystem = system
  implicit val ec = system.dispatcher

  val scraperSuccessful = new TestScraper(false)
  val scraperUnsuccessful = new TestScraper(true)
  // Mount at two different paths for testing
  addServlet(new MetascraperScalatraServlet(scraperSuccessful), "/success/*")
  addServlet(new MetascraperScalatraServlet(scraperUnsuccessful), "/fail/*")

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

  test("POSTing to /scrape with JSON with a URL parameter when the Scraper is unsuccessful") {
    new UnSuccessful {
      post(pathTo("/scrape"), """{"url":  "http://lol.com"}""", Map("Content-Type" -> "application/json")) {
        status should equal (422)
      }
    }
  }

}
