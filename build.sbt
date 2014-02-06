name := "reactive_mongo_example"

version := "0.0.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)     

play.Project.playScalaSettings
