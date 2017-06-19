package models

import slick.jdbc.PostgresProfile.api._
//import slick.driver.PostgresDriver.api._


	final case class Resource(
		MD5: String,
		User: String,
		Course: String,
		Year: String,
		Sem: Int,
		Id: Long,
		Res_Type: String,
		Path: String)

	final class ResourceTable(tag: Tag) extends Table[Resource](tag, "resource") {

		def md5			= column[String]("MD5", O.PrimaryKey)
		def user		= column[String]("User")
		def course		= column[String]("Course")
		def year		= column[String]("Year")
		def sem 		= column[Int]("Sem")
		def id			= column[Long]("Id", O.AutoInc)
		def res_Type	= column[String]("Res_Type")
		def path		= column[String]("Path")

		def * = (md5, user, course, year, sem, id, res_Type, path).mapTo[Resource]
	}
