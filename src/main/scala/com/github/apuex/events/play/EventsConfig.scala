package com.github.apuex.events.play

import com.google.protobuf.util.JsonFormat

trait EventsConfig {
  def eventTag: String
  def printer: JsonFormat.Printer
  def queryPluginId: String
}
