import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play-reactive-mongo"
  val appVersion      = "0.0.3"

  val appDependencies = Seq(
      "reactive_mongo_plugin" %% "reactive_mongo_plugin" % "0.0.15"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    (Seq(
      resolvers += "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
      resolvers += Resolver.url("TPTeam Repository", url("http://tpteam.github.io/releases/"))(Resolver.ivyStylePatterns),
      resolvers += Opts.resolver.sonatypeReleases,
      resolvers += Resolver.sonatypeRepo("snapshots"))): _*)


}
