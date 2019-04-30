package com.example.leveldb

import akka.actor.ActorLogging
import akka.persistence._
import akka.persistence.journal.Tagged

object HelloActor {
  val name = "hello-1.0.0"
}

class HelloActor
  extends PersistentActor
    with ActorLogging {
  val tag = Set(HelloActor.name)
  val snapShotInterval = 1000

  override def receiveRecover: Receive = {
    case SnapshotOffer(metadata, offeredSnapshot) =>
      log.debug("snapshot state: {}", offeredSnapshot)
    case RecoveryCompleted =>
      log.debug("recovery completed.")
    case x =>
      updateStateWithTag(x)
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
    case x: SayHelloEvent =>
      if (lastSequenceNr % snapShotInterval == 0 && lastSequenceNr != 0) {
        deleteSnapshots(SnapshotSelectionCriteria.Latest)
        saveSnapshot(lastSequenceNr)
      }
      sender() ! x
    case x => log.info("unknown event: {}", x)
  }
}
