package com.example.cassandra

import akka.Done
import akka.actor.ActorSystem
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.{Offset, PersistenceQuery}
import akka.persistence.query.scaladsl.EventsByTagQuery
import akka.stream.scaladsl.Sink
import com.github.apuex.events.play.{EventEnvelope, EventEnvelopeProto, EventsConfig}
import com.google.protobuf.{Any, Message}
import com.google.protobuf.util.JsonFormat
import javax.inject._

import scala.concurrent.Future

@Singleton
class CassandraConfig @Inject()(system: ActorSystem) extends EventsConfig {
  val eventTag: String = HelloActor.name

  val registry = JsonFormat.TypeRegistry
    .newBuilder
    .add(EventEnvelopeProto.getDescriptor.getMessageTypes)
    .add(MessagesProto.getDescriptor.getMessageTypes)
    .build

  val printer: JsonFormat.Printer = JsonFormat.printer().usingTypeRegistry(registry)

  val parser: JsonFormat.Parser = JsonFormat.parser().usingTypeRegistry(registry)

  val readJournal: EventsByTagQuery = PersistenceQuery(system)
    .readJournalFor[CassandraReadJournal](CassandraReadJournal.Identifier)

  override def inbound: Sink[String, Future[Done]] = Sink.ignore

  override def outbound(offset: Offset) = readJournal
    .eventsByTag(eventTag, offset)
    .filter(ee => ee.event.isInstanceOf[Message])
    .map(ee => EventEnvelope
      .newBuilder()
      .setSequenceNr(ee.sequenceNr)
      .setOffset(ee.offset.toString)
      .setPersistenceId(ee.persistenceId)
      .setEvent(Any.pack(ee.event.asInstanceOf[Message]))
      .build()
    )
    .map(printer.print(_))
}
