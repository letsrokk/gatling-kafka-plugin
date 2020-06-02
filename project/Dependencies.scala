import sbt._

object Dependencies {

  lazy val kafka: Seq[ModuleID] = Seq(
    ("org.apache.kafka" % "kafka-clients" % "2.4.1")
      .exclude("org.slf4j", "slf4j-api"))

  lazy val gatling: Seq[ModuleID] = Seq(
    "io.gatling"            % "gatling-core"
  ).map(_ % "3.3.1" % "provided")

  lazy val gatlingTest: Seq[ModuleID] = Seq(
    "io.gatling"            % "gatling-test-framework",
    "io.gatling.highcharts" % "gatling-charts-highcharts"
  ).map(_ % "3.3.1" % "test")

  lazy val testcontainers: Seq[ModuleID] = Seq(
    "com.dimafeng" %% "testcontainers-scala-scalatest",
    "com.dimafeng" %% "testcontainers-scala-kafka"
  ).map(_ % "0.37.0" % "test")

}
