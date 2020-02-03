package com.example.leveldb

import akka.Done
import akka.actor.ActorSystem
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.persistence.query.scaladsl.EventsByTagQuery
import akka.persistence.query.{Offset, PersistenceQuery, Sequence, TimeBasedUUID}
import akka.stream.scaladsl.Sink
import com.github.apuex.events.play.{EventEnvelope, EventEnvelopeProto, EventsConfig}
import com.google.protobuf.any.Any
import javax.inject._
import scalapb.GeneratedMessage
import scalapb.json4s.JsonFormat.GenericCompanion
import scalapb.json4s._

import scala.concurrent.Future

@Singleton
class LeveldbConfig @Inject()(system: ActorSystem) extends EventsConfig {
  val eventTag: String = HelloActor.name

  // json parser and printer
  val messagesCompanions = MessagesProto.messagesCompanions ++ EventEnvelopeProto.messagesCompanions :+ Any
  val registry: TypeRegistry = messagesCompanions
    .foldLeft(TypeRegistry())((r, mc) => r.addMessageByCompanion(mc.asInstanceOf[GenericCompanion]))

  val printer = new Printer().withTypeRegistry(registry)

  val parser = new Parser().withTypeRegistry(registry)

  val readJournal: EventsByTagQuery = PersistenceQuery(system)
    .readJournalFor[LeveldbReadJournal](LeveldbReadJournal.Identifier)

  override def inbound: Sink[String, Future[Done]] = Sink.foreach(println)

  override def outbound(offset: Offset) = readJournal
    .eventsByTag(eventTag, offset)
    .filter(ee => ee.event.isInstanceOf[GeneratedMessage])
    .map(ee => EventEnvelope(
      ee.offset match {
        case Sequence(value) => value.toString
        case TimeBasedUUID(value) => value.toString
        case x => x.toString
      },
      ee.persistenceId,
      ee.sequenceNr,
      Some(Any.of(s"type.googleapis.com/${ee.event.getClass.getName}", ee.event.asInstanceOf[GeneratedMessage].toByteString)))
    )
    .map(printer.print(_))
}
