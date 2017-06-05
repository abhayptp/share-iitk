name := "share-iitk"

version := "0.1.0"

scalaVersion := "2.11.8"

resolvers ++= Seq(
  Resolver.bintrayRepo("Typesafe Releases", "https://repo.typesafe.com/typesafe/releases")
  )

libraryDependencies ++= Seq(
  "org.scalatest"     %% "scalatest"            % "2.2.6" % "test",
  "com.typesafe.akka" %% "akka-http"            % "10.0.4",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.4",
  "com.typesafe.akka" %% "akka-actor"           % "2.4.17"
  )
