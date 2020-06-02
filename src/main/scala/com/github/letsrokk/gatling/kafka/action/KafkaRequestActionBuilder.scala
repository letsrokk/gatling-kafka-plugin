package com.github.letsrokk.gatling.kafka.action

import com.github.letsrokk.gatling.kafka.protocol.{KafkaComponents, KafkaProtocol}
import com.github.letsrokk.gatling.kafka.request.builder.KafkaAttributes
import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.Expression
import io.gatling.core.structure.ScenarioContext
import org.apache.kafka.clients.producer.KafkaProducer

import scala.collection.JavaConverters._


class KafkaRequestActionBuilder[K,V](kafkaAttributes: KafkaAttributes[K,V]) extends ActionBuilder {

  def header(name: String, value: Expression[String]): KafkaRequestActionBuilder[K, V] = {
    val header = Tuple2[String, Expression[String]](name, value)
    val headers: List[(String, Expression[String])] = kafkaAttributes.headers match {
      case Some(h) => header :: h
      case None => List(header)
    }
    copy(headers = Option(headers))
  }

  private def copy(requestName: Expression[String] = kafkaAttributes.requestName,
                   key: Option[Expression[K]] = kafkaAttributes.key,
                   payload: Expression[V] = kafkaAttributes.payload,
                   headers: Option[List[(String, Expression[String])]] = kafkaAttributes.headers,
                   partition: Option[Expression[String]] = kafkaAttributes.partition): KafkaRequestActionBuilder[K, V] = {
    new KafkaRequestActionBuilder[K,V](KafkaAttributes(requestName, key, payload, headers, partition))
  }

  override def build( ctx: ScenarioContext, next: Action ): Action = {
    import ctx.{protocolComponentsRegistry, coreComponents, throttled}

    val kafkaComponents: KafkaComponents = protocolComponentsRegistry.components(KafkaProtocol.KafkaProtocolKey)

    val producer = new KafkaProducer[K,V]( kafkaComponents.kafkaProtocol.properties.asJava )

    coreComponents.actorSystem.registerOnTermination(producer.close())

    new KafkaRequestAction(
      producer,
      kafkaAttributes,
      coreComponents,
      kafkaComponents.kafkaProtocol,
      throttled,
      next
    )

  }

}