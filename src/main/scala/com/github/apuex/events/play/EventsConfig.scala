package com.github.apuex.events.play

import akka.persistence.query.scaladsl.EventsByTagQuery
import com.google.protobuf.util.JsonFormat

trait EventsConfig {
  def eventTag: String
  def printer: JsonFormat.Printer
  def readJournal: EventsByTagQuery
}
