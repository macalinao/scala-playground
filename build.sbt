name := "playground"
organization := "pw.ian"
scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "io.monix" %% "monix" % "2.2.3",
  "io.monix" %% "monix-cats" % "2.2.3",
  "org.typelevel" %% "cats" % "0.9.0",
  "com.chuusai" %% "shapeless" % "2.3.2",
  "org.typelevel" %% "kittens" % "1.0.0-M9"
)
