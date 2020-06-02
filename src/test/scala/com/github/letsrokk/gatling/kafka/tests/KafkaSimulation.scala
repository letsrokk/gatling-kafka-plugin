package com.github.letsrokk.gatling.kafka.tests

import java.util.UUID

import io.gatling.core.Predef._
import com.github.letsrokk.gatling.kafka.Predef._
import com.github.letsrokk.gatling.kafka.protocol.KafkaProtocol
import io.gatling.core.structure.ScenarioBuilder
import com.github.letsrokk.gatling.kafka.tests.common.{DefaultKafkaProtocol, TestContainers}
import org.testcontainers.containers.KafkaContainer

class KafkaSimulation extends Simulation with TestContainers with DefaultKafkaProtocol {

  val kafkaContainer: KafkaContainer = startKafkaTestContainer()
  after {
    kafkaContainer.stop()
  }

  val kafkaProtocol: KafkaProtocol = defaultKafkaProtocol(
    kafkaContainer.getBootstrapServers, "qa--all"
  )

  val basicScenario: ScenarioBuilder = scenario("BasicScenario")
    .exec(
      kafka("BasicReqeust")
        .send[String]("foo")
    )

  val basicScenarioWithKey: ScenarioBuilder = scenario("BasicScenarioWithKey")
    .exec(
      kafka("BasicReqeustWithKey")
        .send[String,String]("key", "foo")
    )

  val expressionsScenario: ScenarioBuilder = scenario("ExpressionsScenario")
    .exec(flattenMapIntoAttributes(_ => {
      Map(
        "key" -> s"key_${UUID.randomUUID().toString}",
        "message" -> s"message_${UUID.randomUUID().toString}"
      )}
    ))
    .exec(
      kafka("ExpressionRequest")
        .send[String,String]("${key}", "${message}")
    )

  val expressionsScenarioWithHeaders: ScenarioBuilder = scenario("ExpressionsScenarioWithHeaders")
    .exec(flattenMapIntoAttributes(_ => {
      Map(
        "key" -> s"${UUID.randomUUID().toString}",
        "message" -> s"${UUID.randomUUID().toString}",
        "header1" -> s"${UUID.randomUUID().toString}",
        "header2" -> s"${UUID.randomUUID().toString}"
      )}
    ))
    .exec(
      kafka("ExpressionRequestWithHeaders")
        .send[String, String]("${key}","${message}")
        .header("header1", "${header1}")
        .header("header2", "${header2}")
    )

  setUp(
    basicScenario.inject(atOnceUsers(1)),
    basicScenarioWithKey.inject(atOnceUsers(1)),
    expressionsScenario.inject(atOnceUsers(1)),
    expressionsScenarioWithHeaders.inject(atOnceUsers(1))
  ).protocols(kafkaProtocol)

}
