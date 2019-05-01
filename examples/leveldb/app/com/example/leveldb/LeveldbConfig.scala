package com.example.leveldb

import akka.actor.ActorSystem
import akka.persistence.query.PersistenceQuery
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.persistence.query.scaladsl.EventsByTagQuery
import com.github.apuex.events.play.{EventEnvelopeProto, EventsConfig}
import com.google.protobuf.util.JsonFormat
import javax.inject._

@Singleton
class LeveldbConfig @Inject()(system: ActorSystem) extends EventsConfig {
  override def eventTag: String = HelloActor.name

  val registry = JsonFormat.TypeRegistry
    .newBuilder
    .add(EventEnvelopeProto.getDescriptor.getMessageTypes)
    .add(MessagesProto.getDescriptor.getMessageTypes)
    .build

  override def printer: JsonFormat.Printer = {
    JsonFormat.printer().usingTypeRegistry(registry)
  }

  override def parser: JsonFormat.Parser = {
    JsonFormat.parser().usingTypeRegistry(registry)
  }

  override def readJournal: EventsByTagQuery = PersistenceQuery(system)
    .readJournalFor[LeveldbReadJournal](LeveldbReadJournal.Identifier)
}
