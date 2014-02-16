import com.beachape.metascraper.scalatra.bootstrap.ScraperAppSupport
import com.beachape.metascraper.scalatra.controllers.{ResourcesApp, ScraperServlet}
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle with  ScraperAppSupport {

  override def init(context: ServletContext) {
    context.initParameters("org.scalatra.environment") = env
    context.mount(new ScraperServlet(scraper, memcached), "/*")
    context.mount (new ResourcesApp(), "/api-docs/*")
  }

  override def destroy(context:ServletContext) {
    memcached.close()
    system.shutdown()
  }
}
