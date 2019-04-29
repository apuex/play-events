package com.example.leveldb

import akka.actor.ActorRef
import akka.pattern._
import akka.util._
import javax.inject.{Inject, Named}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent._
import scala.concurrent.duration._


class HelloController @Inject()
(
  @Named("hello-1.0.0") helloActor: ActorRef,
  cc: ControllerComponents
)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  implicit val timeout: Timeout = FiniteDuration(60, SECONDS)

  def sayHello(to: String) = Action.async { _ =>
    helloActor.ask(
      SayHelloCommand
        .newBuilder()
        .setTo(to)
        .build()
    ).mapTo[SayHelloEvent].map {
      case x: SayHelloEvent => Ok(x.getMessage)
    }
  }

}
