package com.github.letsrokk.gatling.kafka.action

import java.util

import com.github.letsrokk.gatling.kafka.protocol.KafkaProtocol
import com.github.letsrokk.gatling.kafka.request.builder.KafkaAttributes
import io.gatling.core.action.{Action, ExitableAction}
import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.session._
import io.gatling.commons.util.DefaultClock
import io.gatling.commons.validation.{Failure, Success, Validation}
import io.gatling.core.CoreComponents
import io.gatling.core.stats.StatsEngine
import io.gatling.core.util.NameGen
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.internals.RecordHeader

import collection.JavaConverters._


class KafkaRequestAction[K,V]( val producer: KafkaProducer[K,V],
                               val kafkaAttributes: KafkaAttributes[K,V],
                               val coreComponents: CoreComponents,
                               val kafkaProtocol: KafkaProtocol,
                               val throttled: Boolean,
                               val next: Action )
  extends ExitableAction with NameGen {

  val statsEngine: StatsEngine = coreComponents.statsEngine
  val clock = new DefaultClock
  override val name: String = genName("kafkaRequest")

  override def execute(session: Session): Unit = recover(session) {

    kafkaAttributes requestName session flatMap { requestName =>

      val outcome =
        sendRequest(
          requestName,
          producer,
          kafkaAttributes,
          throttled,
          session)

      outcome.onFailure(
        errorMessage =>
          statsEngine.reportUnbuildableRequest(session, requestName, errorMessage)
      )

      outcome

    }

  }

  private def sendRequest( requestName: String,
                           producer: Producer[K,V],
                           kafkaAttributes: KafkaAttributes[K,V],
                           throttled: Boolean,
                           session: Session ): Validation[Unit] = {
    val topic = kafkaProtocol.topic
    val partition: Int = kafkaAttributes.partition match {
      case Some(partition) =>
        partition(session) match {
          case Success(p) => p.toInt
          case Failure(e) => null.asInstanceOf[Int]
        }
      case None => null.asInstanceOf[Int]
    }
    val key: K = kafkaAttributes.key match {
      case Some(key) =>
        key(session) match {
          case Success(k) => k.asInstanceOf[K]
          case Failure(e) => null.asInstanceOf[K]
        }
      case None => null.asInstanceOf[K]
    }
    val payload: V = kafkaAttributes.payload(session) match {
      case Success(p) => p.asInstanceOf[V]
      case Failure(e) => null.asInstanceOf[V]
    }
    val headers: util.List[Header] = mapToHeaders(session, kafkaAttributes.headers)

    val record = new ProducerRecord[K, V](topic, partition, key, payload, headers)

    val requestStartDate = clock.nowMillis

    producer.send(record, (m: RecordMetadata, e: Exception) => {
      val requestEndDate = clock.nowMillis
      statsEngine.logResponse(
        session,
        requestName,
        startTimestamp = requestStartDate,
        endTimestamp = requestEndDate,
        if (e == null) OK else KO,
        None,
        if (e == null) None else Some(e.getMessage)
      )
      if (throttled) {
        coreComponents.throttler.throttle(session.scenario, () => next ! session)
      } else {
        next ! session
      }
    })

    Validation.unit
  }

  private def mapToHeaders(session: Session, headers: Option[List[(String, Expression[String])]]): util.List[Header] = {
    headers match {
      case Some(h) =>
        h.toStream
          .map(e => {
            val name = e._1
            val value = e._2(session).toOption.get.getBytes()
            new RecordHeader(name, value).asInstanceOf[Header]
          }).toList.asJava
      case None =>
        List().asJava
    }
  }

}
