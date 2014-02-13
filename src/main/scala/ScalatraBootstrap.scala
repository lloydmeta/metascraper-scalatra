import _root_.akka.actor.ActorSystem
import com.beachape.metascraper.scalatra._
import com.beachape.metascraper.scalatra.models.MetadataScraper
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {

  // Get a handle to an ActorSystem
  implicit val system = ActorSystem("main")
  implicit val ec = system.dispatcher
  val scraper = MetadataScraper(10)

  override def init(context: ServletContext) {
    context.mount(new MetascraperScalatraServlet(scraper), "/*")
  }

  // Make sure we shut down the Actor System
  override def destroy(context:ServletContext) {
    system.shutdown()
  }
}
