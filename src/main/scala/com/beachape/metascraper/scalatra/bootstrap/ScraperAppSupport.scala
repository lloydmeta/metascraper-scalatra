package com.beachape.metascraper.scalatra.bootstrap

import akka.actor.ActorSystem
import com.beachape.metascraper.scalatra.models.MetadataScraper
import com.typesafe.config.ConfigFactory
import shade.memcached.{Memcached, AuthConfiguration, Configuration}
import com.beachape.metascraper.scalatra.controllers.ScraperSwagger

/**
 * Trait that helps initiate the WebApp in order to
 * keep initialisation DRY between Jetty and ScalatraBootstrap
 */
trait ScraperAppSupport {

  // Set the default environment
  val defaultEnvironment: String = "development"

  // Get a handle to an ActorSystem
  implicit val system = ActorSystem("main")
  implicit val ec = system.dispatcher
  val scraper = MetadataScraper(10)

  val env = sys.env.getOrElse("ENV", defaultEnvironment)
  val configNamespace = "com.beachape.metascraper.scalatra"
  val envConf = ConfigFactory.load().getConfig(configNamespace).getConfig(env)

  // Configure memcached connection
  val memConfig = envConf.getConfig("memcached")
  val memConfiguration = env match {
    case "production" => Configuration(
      memConfig.getString("host"),
      Some(AuthConfiguration(memConfig.getString("user"), memConfig.getString("password"))))
    case _ => Configuration(memConfig.getString("host"))
  }
  val memcached = Memcached(memConfiguration, system.scheduler, ec)

  implicit val swagger = new ScraperSwagger

}
