name := "monix-playground"
organization := "pw.ian"
scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "io.monix" %% "monix" % "2.2.3",
  "io.monix" %% "monix-cats" % "2.2.3",
  "org.typelevel" %% "cats" % "0.9.0"
)
