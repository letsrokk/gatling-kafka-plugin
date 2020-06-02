package com.github.letsrokk.gatling.kafka.examples

import com.github.letsrokk.gatling.kafka.Predef._
import com.github.letsrokk.gatling.kafka.protocol.KafkaProtocol
import io.gatling.core.Predef._
import io.gatling.core.feeder.BatchableFeederBuilder
import io.gatling.core.structure.ScenarioBuilder
import org.apache.kafka.clients.producer.ProducerConfig._

class ExampleSimulation extends Simulation {

  val kafkaProtocol: KafkaProtocol = kafka
    .topic("example--topic")
    .properties(Map(
      ACKS_CONFIG -> "all",
      BOOTSTRAP_SERVERS_CONFIG -> "192.168.0.19:9092",
      KEY_SERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringSerializer",
      VALUE_SERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringSerializer"
    ))

  val messagesFeeder: BatchableFeederBuilder[String]#F =
    csv("feeder/kafka-messages.csv").circular

  val exampleScenario: ScenarioBuilder = scenario("ExampleScenario")
    .repeat(5) {
      feed(messagesFeeder).exec(
        kafka("ExampleMessage")
          .send[String, String]("${key}","${message}")
          .header("header1", "${header1}")
          .header("header2", "${header2}")
      )
    }

//  setUp(
//    exampleScenario.inject(atOnceUsers(1))
//  ).protocols(kafkaProtocol)

}
