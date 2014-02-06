import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "reactive_mongo_example"
  val appVersion      = "0.0.1"

  val appDependencies = Seq(
    //"org.reactivemongo" %% "reactivemongo" % "0.10.0"
		  //"websocket_plugin" % "websocket_plugin_2.10" % "0.3.1",
      "reactive_mongo_plugin" %% "reactive_mongo_plugin" % "0.0.1"

  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    (Seq(

      routesImport += "se.radley.plugin.salat.Binders._",
      //templatesImport += "org.bson.types.ObjectId",
      // debug
      resolvers += "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
      resolvers += Resolver.url("TPTeam Snapshots", url("http://tpteam.github.io/snapshots/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("TPTeam Repository", url("http://tpteam.github.io/releases/"))(Resolver.ivyStylePatterns),
      resolvers += Opts.resolver.sonatypeReleases,
      resolvers += Resolver.sonatypeRepo("snapshots"))): _*)


}
