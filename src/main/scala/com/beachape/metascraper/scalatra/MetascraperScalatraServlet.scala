package com.beachape.metascraper.scalatra

import org.scalatra._
import scalate.ScalateSupport

class MetascraperScalatraServlet extends MetascraperScalatraStack {

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }
  
}
