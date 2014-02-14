import sbt._
import Keys._
import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import com.mojolly.scalate.ScalatePlugin._
import ScalateKeys._

object MetascraperScalatraBuild extends Build {
  val Organization = "com.beachape"
  val Name = "Metascraper Scalatra"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.10.3"
  val ScalatraVersion = "2.2.2"

  lazy val project = Project (
    "metascraper-scalatra",
    file("."),
    settings = Defaults.defaultSettings ++ ScalatraPlugin.scalatraWithJRebel ++ scalateSettings ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      resolvers ++= Seq(
      // where Shade lives
        "BionicSpirit Releases" at "http://maven.bionicspirit.com/releases/",
        "BionicSpirit Snapshots" at "http://maven.bionicspirit.com/snapshots/",
        // just in case you don't have it already
        "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
        // for SpyMemcached
        "Spy" at "http://files.couchbase.com/maven2/"
      ),
      libraryDependencies ++= Seq(
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
        "ch.qos.logback" % "logback-classic" % "1.0.6" % "runtime",
        "org.eclipse.jetty" % "jetty-webapp" % "8.1.8.v20121106" % "container",
        "com.beachape.metascraper" %% "metascraper" % "0.2.5",
        "org.scalatra" %% "scalatra-json" % "2.2.2",
        "org.json4s"   %% "json4s-jackson" % "3.2.4", // Going higher than this version breaks Swagger integration
        "com.typesafe.akka" %% "akka-testkit" % "2.2.3" % "test",
        "com.typesafe.akka" %% "akka-actor" % "2.2.3",
        "com.typesafe" % "config" % "1.2.0",
        "com.bionicspirit" %% "shade" % "1.5.0",
        "ch.qos.logback" % "logback-classic" % "1.0.13" % "runtime",
        "org.scalatra" %% "scalatra-swagger"  % "2.2.2" exclude("org.slf4j", "log4j12"),
        "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container;provided;test" artifacts (Artifact("javax.servlet", "jar", "jar"))
      )
    )
  )
}
