package com.github.apuex.events.play

import java.util.UUID

import akka.actor.ActorSystem
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.{Offset, PersistenceQuery}
import akka.stream.scaladsl.{Flow, Sink}
import com.google.protobuf.{Any, Message}
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}

@Singleton
class EventsController @Inject()(config: EventsConfig, cc: ControllerComponents, system: ActorSystem) extends AbstractController(cc) {
  val cassandraQueries = PersistenceQuery(system)
    .readJournalFor[CassandraReadJournal](config.queryPluginId)

  def events(offset: String): WebSocket = WebSocket.accept[String, String] { request =>
    // Draining input events by Log events to the console
    // just in case that large volumes of un-consumed messages from client side
    // that causing resource exhausting.
    // because only unidirectional events pushing is allowed.
    val in = Sink.foreach[String](println)
    val out = cassandraQueries
      .eventsByTag(config.eventTag, Offset.timeBasedUUID(UUID.fromString(offset)))
      .filter(ee => ee.event.isInstanceOf[Message])
      .map(ee => EventEnvelope
        .newBuilder()
        .setSequenceNr(ee.sequenceNr)
        .setOffset(ee.offset.toString)
        .setPersistenceId(ee.persistenceId)
        .setEvent(Any.pack(ee.event.asInstanceOf[Message]))
        .build()
      )
      .map(config.printer.print(_))

    Flow.fromSinkAndSource(in, out)
  }

}
