
scalaVersion := "2.12.4"

name := "redshift-fake-proxy"

organization := "com.github.vertical-blank"

version := "1.0.0"

fork in run := true

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature"
)

libraryDependencies ++= Seq(
//  "org.postgresql" %  "postgresql" % "9.4.1211",
  "jp.ne.opt"      %% "redshift-fake-driver" % "1.0.11",
  "org.scalatest"  %% "scalatest"            % "3.0.0"
)
