package com.beachape.metascraper.scalatra

import org.scalatra.test.scalatest._
import org.scalatest.FunSuite
import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit, ImplicitSender}
import com.beachape.metascraper.scalatra.models.MetadataScraper

class MetascraperScalatraServletSpec extends TestKit(ActorSystem("testSystem")) with ScalatraSuite with FunSuite  {

  // Get a handle to an ActorSystem
  implicit val testActorSystem = system
  implicit val ec = system.dispatcher
  val scraper = MetadataScraper(10)

  addServlet(new MetascraperScalatraServlet(scraper), "/*")

  test("getting root") {
    get("/") {
      status should equal (200)
    }
  }
}
