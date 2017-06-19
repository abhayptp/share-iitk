package DAO
import models._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl._
import slick.dbio.{Effect, NoStream}
import slick.lifted.TableQuery
import slick.sql.{FixedSqlAction, SqlAction, FixedSqlStreamingAction}

import slick.driver.PostgresDriver.api._



import scala.concurrent.ExecutionContext
import scala.concurrent.{Await, Future}

trait Dao  {
	val resourceTable = TableQuery[ResourceTable]
	def db = Database.forConfig("database")
	implicit val session: Session = db.createSession()

	protected implicit def executeFromDb[A](action: SqlAction[A, NoStream, _ <: slick.dbio.Effect]): Future[A] = {
    db.run(action)
	}
	
	protected implicit def executeReadStreamFromDb[A](action: FixedSqlStreamingAction[Seq[A], A, _ <: slick.dbio.Effect]): Future[Seq[A]] = {
    db.run(action)
	}
}
/* Currently it handles requests for uploading single file only.
*/

object Base extends Dao {

	val connectionUrl = "jdbc:postgresql://localhost/"

	def returnWhole():  Future[Seq[Resource]] = {
		(for {
			resource <- resourceTable.filter(_.id >= 0L )
		} yield resource).result	
	}

	def findFile(md_5: String): Future[Resource] = {
		(for {
			resource <- resourceTable.filter(_.md5 === md_5)
		} yield resource).result.head
	}
	def create(resources: Resource) = {
		resourceTable+=resources

	}
	

}
