package com.beachape.metascraper.scalatra.controllers

import org.scalatra.swagger.{JacksonSwaggerBase, Swagger}

import org.scalatra.ScalatraServlet

import org.json4s.{DefaultFormats, Formats}


class ResourcesApp(implicit val swagger: Swagger) extends ScalatraServlet with JacksonSwaggerBase {
  implicit override val jsonFormats: Formats = DefaultFormats
}

class ScrapperSwagger extends Swagger("1.0", "1")