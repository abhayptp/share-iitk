name := "share-iitk"

version := "0.1.0"

scalaVersion := "2.11.6"
/* jerhjy
resolvers ++= Seq(
  Resolver.bintrayRepo("Typesafe Releases", "https://repo.typesafe.com/typesafe/releases")
  )
*/

libraryDependencies ++= Seq(
	"org.scalatest"      %% "scalatest"            % "2.2.6" % "test",
	"org.postgresql"      % "postgresql"           % "9.4-1200-jdbc41",
	"com.typesafe.slick" %% "slick"                % "3.2.0",
	"com.typesafe.akka"  %% "akka-http"            % "10.0.4",
	"com.typesafe.akka"  %% "akka-http-spray-json" % "10.0.4",
	"com.typesafe.slick" %% "slick-hikaricp"	   % "3.2.0",
	"org.flywaydb"		  % "flyway-core" 		   % "4.2.0",
	"com.typesafe.akka"  %% "akka-actor"           % "2.4.17"
  )


