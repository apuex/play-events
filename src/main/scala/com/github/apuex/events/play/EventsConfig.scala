package com.github.apuex.events.play

import akka.persistence.query.Offset
import akka.stream.scaladsl.{Sink, Source}
import akka.{Done, NotUsed}

import scala.concurrent.Future

trait EventsConfig {
  def inbound: Sink[String, Future[Done]] = Sink.foreach[String](_ => {})

  def outbound(offset: Offset): Source[String, NotUsed]
}
