package com.github.apuex.events.play

import java.util.UUID

import akka.actor.ActorSystem
import akka.persistence.query.Offset
import akka.stream.scaladsl.Flow
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}

@Singleton
class EventsController @Inject()(config: EventsConfig, cc: ControllerComponents, system: ActorSystem)
  extends AbstractController(cc) {
  val log = play.Logger.of("application")

  def events(offset: Option[String]): WebSocket = WebSocket.accept[String, String] { request =>
    val startPos = offset
      .map(x => {
        if (x.matches("^[\\+\\-]{0,1}[0-9]+$")) Offset.sequence(x.toLong)
        else Offset.timeBasedUUID(UUID.fromString(x))
      })
      .getOrElse(Offset.noOffset)

    Flow.fromSinkAndSource(config.inbound, config.outbound(startPos))
  }

}
