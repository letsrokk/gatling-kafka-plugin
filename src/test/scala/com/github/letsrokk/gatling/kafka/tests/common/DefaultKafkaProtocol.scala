package com.github.letsrokk.gatling.kafka.tests.common

import io.gatling.core.Predef._

import com.github.letsrokk.gatling.kafka.Predef._
import com.github.letsrokk.gatling.kafka.protocol._
import org.apache.kafka.clients.producer.ProducerConfig

trait DefaultKafkaProtocol {

  def defaultKafkaProtocol(bootstrapServers: String, topic: String): KafkaProtocol = {
    kafka
      .topic(topic)
      .properties(Map(
        ProducerConfig.ACKS_CONFIG -> "all",
        // list of Kafka broker hostname and port pairs
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> bootstrapServers,
        // in most cases, StringSerializer or ByteArraySerializer
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG ->
          "org.apache.kafka.common.serialization.StringSerializer",
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG ->
          "org.apache.kafka.common.serialization.StringSerializer"
      ))
  }

}
