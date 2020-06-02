# gatling-kafka-plugin [ ![Download](https://api.bintray.com/packages/letsrokk/github/gatling-kafka-plugin/images/download.svg) ](https://bintray.com/letsrokk/github/adaptavist-jira-parent/_latestVersion)

## Introduction

An unofficial Gatling 3.3.1 stress test plugin for Apache Kafka 2.4.x protocol.

This plugin supports the Kafka producer API only and doesn't support the Kafka consumer API.

# Usage

## Getting Started

`gatling-kafka-plugin` is currently available for Scala 2.12

You may include plugin as dependency in project with your tests. Write 

```scala
libraryDependencies += "com.github.letsrokk" %% "gatling-kafka-plugin" % <version> % Test
```

## Example

Simulation example can be found here: [KafkaSimulation](./src/test/scala/com/github/letsrokk/gatling/kafka/tests/KafkaSimulation.scala)

## Execution

```bash
% sbt gatling:test
```