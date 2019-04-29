package com.github.apuex.events.play

import java.util.UUID

import akka.actor.ActorSystem
import akka.persistence.query.Offset
import akka.stream.scaladsl.{Flow, Sink}
import com.google.protobuf.{Any, Message}
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}

@Singleton
class EventsController @Inject()(config: EventsConfig, cc: ControllerComponents, system: ActorSystem)
  extends AbstractController(cc) {
  val log = play.Logger.of("application")

  def events(offset: Option[String]): WebSocket = WebSocket.accept[String, String] { request =>
    // Draining input events by Log events to the console
    // just in case that large volumes of un-consumed messages from client side
    // that causing resource exhausting.
    // because only unidirectional events pushing is allowed.
    val startPos = offset
      .map(x => {
        if (x.matches("^[\\+\\-]{0,1}[0-9]+$")) Offset.sequence(x.toLong)
        else Offset.timeBasedUUID(UUID.fromString(x))
      })
      .getOrElse(Offset.noOffset)
    val in = Sink.foreach[String](x => log.debug("[{}] from [{}]", x, request.remoteAddress))
    val out = config.readJournal
      .eventsByTag(config.eventTag, startPos)
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
