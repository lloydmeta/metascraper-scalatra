import org.eclipse.jetty.server.Server
import org.eclipse.jetty.util.thread.QueuedThreadPool
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

object JettyLauncher {

  def main(args: Array[String]) {
    val port = if(System.getenv("PORT") != null) System.getenv("PORT").toInt else 8080

    val server = new Server(port)
    val context = new WebAppContext()

    context.setContextPath("/")
    context.setResourceBase("src/main/webapp")

    context setEventListeners Array(new ScalatraListener)

    server.setHandler(context)

    /* Configure Jetty server */
    val threadPool = new QueuedThreadPool()
    threadPool.setMinThreads(5)
    threadPool.setMaxThreads(30)
    server.setThreadPool(threadPool)

    server.setSendServerVersion(false)
    /* Configure Jetty server */

    server.start
    server.join
  }
}