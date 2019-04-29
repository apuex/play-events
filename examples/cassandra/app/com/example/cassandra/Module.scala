package com.example.cassandra

import com.github.apuex.events.play.EventsConfig
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

class Module extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bind(classOf[EventsConfig]).to(classOf[CassandraConfig])
    bindActor[HelloActor](HelloActor.name)
  }
}
