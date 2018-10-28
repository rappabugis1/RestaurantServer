name := """play-java-seed"""
organization := "atlantbh"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

lazy val myProject = (project in file("."))
  .enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.12.6"

libraryDependencies += guice
libraryDependencies += javaJdbc
libraryDependencies += evolutions

libraryDependencies += "org.postgresql" % "postgresql" % "42.2.5"

libraryDependencies += "org.pac4j" %% "play-pac4j" % "6.1.0"
libraryDependencies += "org.pac4j" % "pac4j-jwt" % "3.2.0"
libraryDependencies += "org.pac4j" % "pac4j-http" % "3.2.0"