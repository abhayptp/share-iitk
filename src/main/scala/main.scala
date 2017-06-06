import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.stream.scaladsl.Flow
import akka.http.scaladsl.model._
import akka.stream.scaladsl._
import HttpMethods._

import scala.concurrent.ExecutionContext
import scala.concurrent.{Await, Future}




object Main extends App {
	private implicit val system = ActorSystem()
	protected implicit val executor: ExecutionContext = system.dispatcher
	//protected val log: LoggingAdapter = Logging(system, getClass)
	protected implicit val materializer: ActorMaterializer = ActorMaterializer()


	val server_binding = Http().bind(interface = "localhost", port = 8000)


	val asyncHandler: HttpRequest => HttpResponse = {
		case HttpRequest(GET, Uri.Path("/search"), _ , _, _ ) => {
			print("hell0\n")

			//Return whole db in json
        	HttpResponse(status = 201)
        	
		}

		case HttpRequest(POST, Uri.Path("/upload"), _ , _, _ ) => {
			//Insert a row in resource table
		
        	HttpResponse(status = 201)
        	
		}

        case HttpRequest(GET, Uri.Path("/"), _ , _, _ ) => {
        	//Return the file assosciated with md5 parameter and also increase the score of respective file by 1
        	HttpResponse(status = 201)
        }

        case HttpRequest( _, _ , _ , _, _ ) => {
        	HttpResponse(status = StatusCodes.NotFound)
        }
	}


	val bindingFuture: Future[Http.ServerBinding] = 
		server_binding.to(Sink.foreach {
		connection => connection handleWithSyncHandler asyncHandler
	}).run()

	
}

