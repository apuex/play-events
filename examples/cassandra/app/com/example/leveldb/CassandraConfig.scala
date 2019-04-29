package com.example.leveldb

import akka.actor.ActorSystem
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.PersistenceQuery
import akka.persistence.query.scaladsl.EventsByTagQuery
import com.github.apuex.events.play.{EventEnvelopeProto, EventsConfig}
import com.google.protobuf.util.JsonFormat
import javax.inject._

@Singleton
class CassandraConfig @Inject()(system: ActorSystem) extends EventsConfig {
  override def eventTag: String = HelloActor.name

  override def printer: JsonFormat.Printer = {
    val registry = JsonFormat.TypeRegistry
      .newBuilder
      .add(EventEnvelopeProto.getDescriptor.getMessageTypes)
      .add(MessagesProto.getDescriptor.getMessageTypes)
      .build
    JsonFormat.printer().usingTypeRegistry(registry)
  }

  override def readJournal: EventsByTagQuery = PersistenceQuery(system)
    .readJournalFor[CassandraReadJournal](CassandraReadJournal.Identifier)
}