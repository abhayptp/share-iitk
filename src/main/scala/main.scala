import models._
import DAO.Base._
import utils.{MigrationConfig, CorsSupport}

import scala.util.{Success, Failure}
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import org.apache.commons.io._
import java.util.UUID
import java.security.{MessageDigest, DigestInputStream}
import java.io.{File, FileInputStream, FileOutputStream}
import java.nio.file.{Files, Path, Paths, StandardCopyOption}

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
import scala.concurrent.duration.Duration

import scala.concurrent.ExecutionContext
import scala.concurrent.{Await, Future}


trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val resourceFormat = jsonFormat8(Resource)
  implicit val coursesFormat = jsonFormat2(Courses)
  implicit val UploadResponseFormat = jsonFormat2(UploadResponse)
}


object Main extends App with JsonSupport with MigrationConfig  {
  private implicit val system = ActorSystem()
  protected implicit val executor: ExecutionContext = system.dispatcher
  //protected val log: LoggingAdapter = Logging(system, getClass)
  protected implicit val materializer: ActorMaterializer = ActorMaterializer()

  var original = "";

  def processFile(fileOutput: FileOutputStream, fileData: Multipart.FormData) = {
    fileData.parts.mapAsync(1) { bodyPart ⇒
      original = bodyPart.filename.map(_.toString).getOrElse("")
      def writeFileOnLocal(array: Array[Byte], byteString: ByteString): Array[Byte] = {
        val byteArray: Array[Byte] = byteString.toArray
        fileOutput.write(byteArray)
        array ++ byteArray
      }
      bodyPart.entity.dataBytes.runFold(Array[Byte]())(writeFileOnLocal)
    }.runFold(0)(_ + _.length) 

  }

  def computeHash(path: String): String = {
    val buffer = new Array[Byte](8192)
    val md5 = MessageDigest.getInstance("MD5")

    val dis = new DigestInputStream(new FileInputStream(new File(path)), md5)
    try { while (dis.read(buffer) != -1) { } } finally { dis.close() }
        
    md5.digest.map("%02x".format(_)).mkString
  }
  
  def deleteFile(path: String) = {
    val fileTemp = new File(path)
    if (fileTemp.exists) {
      fileTemp.delete()
    }
  }
  
  def returnCourses(): Seq[Courses] = {
    val buffer = io.Source.fromFile("./src/main/resources/courses.csv")
    var cols: Seq[String] = Seq.empty[String]
    var courses: Seq[Courses] = Seq.empty[Courses]
    for (line <- buffer.getLines) {
        cols = line.split(",").map(_.trim)
        courses = courses :+ Courses(cols(0), cols(1))
    }
    courses
  }

  val route = cors() { 
		/* File should be first uploaded to a temporary folder and its md5 should be checked. If it exists, then file 
		should be discarded. Otherwise it should be uploaded to main folder with name as md5. And response should be sent 
		to client giving the file name and file path. And then another POST should be handled(giving the meta data). It has 
		to be implemented. 
		*/

	  pathPrefix("resources") {
        path("courses")  {
          complete {
            returnCourses().map(_.toJson)
          }
        } ~
        path("upload") {
          (post & entity(as[Multipart.FormData]))  { formData => 
              var newPath = ""
              var md5_hash =""
              var k = 0
              val fileName = UUID.randomUUID().toString
              val temp = System.getProperty("java.io.tmpdir")
              val fileDir =  "./uploadedFiles/"
              val filePath = fileDir + fileName 
              //val fileSize = Await.result(processFile(filePath, formData),Duration.Inf)
              val fileOutput = new FileOutputStream(filePath)
              onComplete(processFile(fileOutput, formData))  { 
                case Success(fileSize) => {
                  fileOutput.close()
                  var md5_hash = computeHash(filePath)
                  val check = checkIfMD5exists(md5_hash)
                  val ext1 = FilenameUtils.getExtension(original)
                  val check1 = Await.result(check, Duration.Inf) 
                  if(check1 == true) {
                    deleteFile(filePath)
                    complete(HttpResponse(StatusCodes.OK, entity = s"File is already uploaded"))
                  }
                  else {
                    var newPath = "./uploadedFiles/MainFiles/" + md5_hash + "." + ext1 +"/"
                    var a = new File(filePath).toPath
                    var b = new File(newPath).toPath
                    Files.move(a,b,StandardCopyOption.REPLACE_EXISTING)
                    complete(UploadResponse(newPath, md5_hash).toJson)
                  }
                }
                case Failure(ex) => complete(ex.getMessage)
              }
          }
		} ~ 
		path("upload") {
		  (post & entity(as[Resource]) )  { resource =>
			complete {
			  create(resource).map(_.toJson)
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
          (get) {
            parameters('fileMD5)  { fileMD5  =>      
              complete {
                val file1 = "./uploadedFiles/MainFiles/"+fileMD5
                HttpEntity(MediaTypes.`application/octet-stream`,  FileIO.fromPath(Paths.get(file1), chunkSize = 10000))
              }
            }
          }
        }
      }
  }
  reloadSchema()
  Http().bindAndHandle(route, interface = "localhost", port = 8082)
}
