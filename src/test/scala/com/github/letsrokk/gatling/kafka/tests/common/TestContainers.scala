package com.github.letsrokk.gatling.kafka.tests.common

import org.slf4j.{Logger, LoggerFactory}
import org.testcontainers.containers.KafkaContainer

trait TestContainers {

  def logger: Logger = LoggerFactory.getLogger("TestContainers")

  def startKafkaTestContainer(): KafkaContainer = {
    val kafkaContainer: KafkaContainer = new KafkaContainer()
    logger.info("Starting Kafka test container")
    kafkaContainer.start()
    logger.info(s"Container started: ${kafkaContainer.getBootstrapServers}")
    kafkaContainer
  }

}
