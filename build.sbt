import NativePackagerKeys._

//packageArchetype.java_application

name := "share-iitk"

version := "0.1.0"

scalaVersion := "2.12.5"

import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
enablePlugins(JavaAppPackaging)

resolvers ++= Seq(
  Resolver.bintrayRepo("Typesafe Releases", "https://repo.typesafe.com/typesafe/releases")
  )


libraryDependencies ++= Seq(
    "org.apache.commons" % "commons-io" % "1.3.2",
	/*"org.scalatest"      %% "scalatest"            % "2.2.6" % "test",*/
	"ch.megard" %% "akka-http-cors" % "0.2.1",
    "org.postgresql"      % "postgresql"           % "9.4-1200-jdbc41",
	"com.typesafe.slick" %% "slick"                % "3.2.0",
	"com.typesafe.akka"  %% "akka-http"            % "10.0.8",
	"com.typesafe.akka"  %% "akka-http-spray-json" % "10.0.8",
    "com.typesafe.akka"  %% "akka-stream"          % "2.5.3",
	"com.typesafe.slick" %% "slick-hikaricp"	   % "3.2.0",
	"org.flywaydb"		  % "flyway-core" 		   % "4.2.0",
	"com.typesafe.akka"  %% "akka-actor"           % "2.5.3"
  )
