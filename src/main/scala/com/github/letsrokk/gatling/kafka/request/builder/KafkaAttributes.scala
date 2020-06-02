package com.github.letsrokk.gatling.kafka.request.builder

import io.gatling.core.session.Expression

case class KafkaAttributes[K, V](requestName: Expression[String],
                                 key: Option[Expression[K]],
                                 payload: Expression[V],
                                 headers: Option[List[Tuple2[String, Expression[String]]]],
                                 partition: Option[Expression[String]])
