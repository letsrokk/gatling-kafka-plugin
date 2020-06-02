ThisBuild / organization := "com.github.letsrokk"
ThisBuild / scalaVersion := "2.12.8"

ThisBuild / publishMavenStyle := true

ThisBuild / scmInfo := Some(
  ScmInfo(url("https://github.com/letsrokk/gatling-kafka-plugin"),
          "git@github.com:letsrokk/gatling-kafka-plugin.git"))

ThisBuild / developers := List(
  Developer(
    id = "letsrokk",
    name = "Dmitry Mayer",
    email = "mayer.dmitry@gmail.com",
    url = url("https://github.com/letsrokk")
  )
)

ThisBuild / description := "Plugin to support kafka performance testing in Gatling(3.x.x)."
ThisBuild / licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
ThisBuild / homepage := Some(url("https://github.com/letsrokk/gatling-kafka-plugin"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ =>
  false
}
