package com.example.leveldb

import akka.Done
import akka.actor.ActorSystem
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.persistence.query.scaladsl.EventsByTagQuery
import akka.persistence.query.{Offset, PersistenceQuery}
import akka.stream.scaladsl.Sink
import com.github.apuex.events.play.{EventEnvelope, EventEnvelopeProto, EventsConfig}
import com.google.protobuf.util.JsonFormat
import com.google.protobuf.{Any, Message}
import javax.inject._

import scala.concurrent.Future

@Singleton
class LeveldbConfig @Inject()(system: ActorSystem) extends EventsConfig {
  val eventTag: String = HelloActor.name

  val registry = JsonFormat.TypeRegistry
    .newBuilder
    .add(EventEnvelopeProto.getDescriptor.getMessageTypes)
    .add(MessagesProto.getDescriptor.getMessageTypes)
    .build

  val printer = JsonFormat.printer().usingTypeRegistry(registry)

  val parser = JsonFormat.parser().usingTypeRegistry(registry)

  val readJournal: EventsByTagQuery = PersistenceQuery(system)
    .readJournalFor[LeveldbReadJournal](LeveldbReadJournal.Identifier)

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