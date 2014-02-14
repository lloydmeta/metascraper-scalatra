import _root_.akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import com.beachape.metascraper.scalatra._
import com.beachape.metascraper.scalatra.models.MetadataScraper
import org.scalatra._
import javax.servlet.ServletContext
import shade.memcached.{Memcached, AuthConfiguration, Configuration}

class ScalatraBootstrap extends LifeCycle {

  // Get a handle to an ActorSystem
  implicit val system = ActorSystem("main")
  implicit val ec = system.dispatcher
  val scraper = MetadataScraper(10)

  val env = sys.env.getOrElse("ENV", "development")
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

  implicit val swagger = new ScrapperSwagger

  override def init(context: ServletContext) {
    context.initParameters("org.scalatra.environment") = env
    context.mount(new ScraperServlet(scraper, memcached), "/*")
    context mount (new ResourcesApp(), "/api-docs/*")
  }

  override def destroy(context:ServletContext) {
    memcached.close()
    system.shutdown()
  }
}
