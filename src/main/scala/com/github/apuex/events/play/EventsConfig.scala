package com.github.apuex.events.play

import akka.{Done, NotUsed}
import akka.persistence.query.Offset
import akka.persistence.query.scaladsl.EventsByTagQuery
import akka.stream.scaladsl.{Sink, Source}
import com.google.protobuf.util.JsonFormat

import scala.concurrent.Future

trait EventsConfig {
  def inbound: Sink[String, Future[Done]] = Sink.foreach[String](_ => {})
  def outbound(offset: Offset): Source[String, NotUsed]
}
