import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

def asyncHandler(request: HttpRequest): Future[HttpResponse] = {
	request match {
		case HttpRequest(GET, Uri.path("/search"), _ , _) => {
			//Return whole db in json
		}

		case HttpRequest(POST, Uri.path("/upload"), _ , _ ) => {
			//Insert a row in resource table
		}

        case HttpRequest(GET, Uri.path("/"), _ , _ ) => {
        	//Return the file assosciated with md5 parameter and also increase the score of respective file by 1
        }

        case HttpRequest( _, _ , _ , _ ) => {
        	Future[HttpResponse] {
        		HttpResponse(status = StatusCodes.NotFound)
        	}
        }
}


object Main {
  private implicit val system = ActorSystem()
  protected implicit val executor: ExecutionContext = system.dispatcher
  protected val log: LoggingAdapter = Logging(system, getClass)
  protected implicit val materializer: ActorMaterializer = ActorMaterializer()


  val server_binding = Http().bind(interface = "localhost", port = "8000")

  server_binding.connections.foreach {
  	connection => connection.handleWith(Flow[HttpRequest].mapAsync(asyncHandler))
  }
}

