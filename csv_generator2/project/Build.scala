import sbt._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "csv_generator2"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      jdbc,
      "mysql" % "mysql-connector-java" % "5.1.18",
      "org.scalaquery" % "scalaquery_2.9.0" % "0.9.4",
      "org.xerial" % "sqlite-jdbc" % "3.6.20",
      "com.typesafe" %% "play-plugins-mailer" % "2.1-RC2"
      //"com.typesafe" %% "play-plugins-mailer" % "2.1.0"

    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}
