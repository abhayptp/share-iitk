import models._
import DAO.Base._
import utils.MigrationConfig

import java.io.File
import java.io.FileOutputStream
import java.util.UUID

import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.util.ByteString
import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import SprayJsonSupport._


import akka.actor.ActorSystem
//import akka.http.scaladsl.Http._
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl._
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.Source
import akka.http.scaladsl.model._
import akka.stream.scaladsl._
import HttpMethods._

import scala.concurrent.ExecutionContext
import scala.concurrent.{Await, Future}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
	implicit val resourceFormat = jsonFormat8(Resource)
}


object Main extends App with JsonSupport with MigrationConfig {
	private implicit val system = ActorSystem()
	protected implicit val executor: ExecutionContext = system.dispatcher
	//protected val log: LoggingAdapter = Logging(system, getClass)
	protected implicit val materializer: ActorMaterializer = ActorMaterializer()


	def processFile(filePath: String, fileData: Multipart.FormData) = {
    	val fileOutput = new FileOutputStream(filePath)
    	fileData.parts.mapAsync(1) { bodyPart ⇒
      		def writeFileOnLocal(array: Array[Byte], byteString: ByteString): Array[Byte] = {
        		val byteArray: Array[Byte] = byteString.toArray
        		fileOutput.write(byteArray)
        		array ++ byteArray
      		}
      	bodyPart.entity.dataBytes.runFold(Array[Byte]())(writeFileOnLocal)
    	}.runFold(0)(_ + _.length)
  	}

	val route = 
		/* File should be first uploaded to a temporary folder and its md5 should be checked. If it exists, then file 
		should be discarded. Otherwise it should be uploaded to main folder with name as md5. And response should be sent 
		to client giving the file name and file path. And then another POST should be handled(giving the meta data). It has 
		to be implemented. 
		*/

		pathPrefix("resources") {
			
			path("upload") {
				(post & entity(as[Multipart.FormData])) { formData => 
					//val actualFileName = formData.filename
					complete {
						val fileName = UUID.randomUUID().toString
	              		val temp = System.getProperty("java.io.tmpdir")
	               		val filePath = temp + "/" + fileName
	              		processFile(filePath,formData).map { fileSize =>
	              		HttpResponse(StatusCodes.OK, entity = s"File successfully uploaded. Fil size is $fileSize. Path is $filePath")
	              		}.recover {
	               		case ex: Exception => HttpResponse(StatusCodes.InternalServerError, entity = "Error in file uploading")
	               		}
					}
				}
			} ~ 
			path("upload") {
				(post & entity(as[Resource]) ) { resource =>
					complete {
						create(resource)
						HttpResponse(StatusCodes.OK)
					}
				}
			} ~
			path("search") {
				(get)  {
					print("Request received")
					complete {
						
						returnWhole.map(_.toJson)
					}
				}
			} ~
			path("download") {
				(get & entity(as[Resource]) ) { resource =>
					complete {
						HttpResponse(StatusCodes.OK)
					}
				}
			}
		}
	migrate()
	Http().bindAndHandle(route, interface = "localhost", port = 8080)
}