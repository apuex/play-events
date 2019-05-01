package com.github.apuex.events.play

import akka.Done
import akka.persistence.query.scaladsl.EventsByTagQuery
import akka.stream.scaladsl.Sink
import com.google.protobuf.Message
import com.google.protobuf.util.JsonFormat

import scala.concurrent.Future

trait EventsConfig {
  def eventTag: String
  def printer: JsonFormat.Printer
  def parser: JsonFormat.Parser
  def readJournal: EventsByTagQuery
  def inbound: Sink[Message, Future[Done]] = Sink.foreach[Message](_ => {})
}
