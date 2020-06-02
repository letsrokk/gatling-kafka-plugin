package com.github.letsrokk.gatling.kafka.request.builder

import com.github.letsrokk.gatling.kafka.action.KafkaRequestActionBuilder
import io.gatling.core.session._



case class KafkaRequestBuilder(requestName: Expression[String]) {

  def send[V](payload: Expression[V]): KafkaRequestActionBuilder[_,V] = send(payload, None)

  def send[K,V](key: Expression[K], payload: Expression[V]): KafkaRequestActionBuilder[K,V] = send(payload, Some(key))

  private def send[K,V](payload: Expression[V], key: Option[Expression[K]]) =
    new KafkaRequestActionBuilder(KafkaAttributes(requestName, key, payload, None, None))

}
