package com.example.cassandra

import akka.actor.ActorLogging
import akka.persistence.PersistentActor
import akka.persistence.journal.Tagged

object HelloActor {
  val name = "hello-1.0.0"
}

class HelloActor
  extends PersistentActor
    with ActorLogging {
  val tag = Set(HelloActor.name)

  override def receiveRecover: Receive = {
    case _ =>
  }

  override def receiveCommand: HelloActor.this.Receive = {
    case x: SayHelloCommand =>
      persist(
        Tagged(
          SayHelloEvent
            .newBuilder()
            .setTo(x.getTo)
            .setMessage(s"Hello, ${x.getTo}!\n\t-- A warm welcome from ${HelloActor.name}.")
            .build(),
          tag
        )
      )(updateStateWithTag)
    case x =>
      log.info("unknown command: {}", x)
  }

  override def persistenceId: String = HelloActor.name

  private def updateStateWithTag: (Any => Unit) = {
    case Tagged(x, _) => updateState(x)
    case x => updateState(x)
  }

  private def updateState: (Any => Unit) = {
    case x =>
      sender() ! x
      log.info("apply event to state: {}", x)
  }
}
