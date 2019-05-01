package com.example.cassandra

import akka.actor._
import akka.pattern._
import akka.util._
import javax.inject._
import play.api.mvc._

import scala.concurrent._
import scala.concurrent.duration._


class HelloController @Inject()
(
  @Named("hello-1.0.0") helloActor: ActorRef,
  system: ActorSystem,
  cc: ControllerComponents
)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {
  val log = play.Logger.of("application")
  implicit val timeout: Timeout = FiniteDuration(60, SECONDS)

  def sayHello(to: String): Action[AnyContent] = Action.async { _ =>
    helloActor.ask(
      SayHelloCommand
        .newBuilder()
        .setTo(to)
        .build()
    ).mapTo[SayHelloEvent].map {
      case x: SayHelloEvent => Ok(x.getMessage)
    }
  }

  def takeSnapshot = Action { _ =>
    helloActor.tell(
      TakeSnapshotCommand
        .newBuilder()
        .build(),
      ActorRef.noSender
    )
    Ok("take snapshot command issued.")
  }

  def shutdownSystem: Action[AnyContent] = Action.async { _ =>
    helloActor.tell(
      ShutdownSystemCommand
        .newBuilder()
        .build(),
      ActorRef.noSender
    )
    system.terminate()
      .map(_ => Ok("shutdown system command issued."))
  }
}
