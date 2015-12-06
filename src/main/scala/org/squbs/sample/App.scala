package org.squbs.sample

import akka.actor._

import scala.concurrent.duration._

// Messages for interacting with this app.
case class PingRequest(who: String)
case class PingResponse(message: String)
case class ChunkRequest(who: String, delay: FiniteDuration)
case object ChunkEnd
case object EmptyRequest

/**
  * The dispatcher serves as a singleton registered entry point. It creates/manages actors to handle
  * the actual request and allows multiple access methods to this service. Only HTTP is shown
  * but it would be rather simple to add other access methods like messaging/streams, etc.
  * We could use actors with routers or any other method that has a static entry point, instead.
  */
class SampleDispatcher extends Actor with ActorLogging {
  def receive = {
    case request =>
      context.actorOf(Props[SampleActor]) forward request
  }
}

/**
  * This is the actor that handles the request messages.
  */
class SampleActor extends Actor with ActorLogging {
  val system = context.system
  import system.dispatcher

  case class SampleAck(remaining: Int)

  def receive = {

    case PingRequest(who) =>
      if (who.trim.nonEmpty) sender() ! PingResponse(s"Hello $who welcome to squbs!")
      else sender() ! EmptyRequest
      context.stop(self)

    case ChunkRequest(who, delay) =>
      val requester = sender() // Save the requester for use in the scheduler.
      val responses = Iterator("Hello ", who, " welcome ", "to ", "squbs!")
      if (delay.toMillis > 0) {
        val scheduler = context.system.scheduler.schedule(delay, delay) {
          if (responses.hasNext) requester ! PingResponse(responses.next())
          else {
            requester ! ChunkEnd
            self ! ChunkEnd
          }
        }
        context.become(cancelReceive(scheduler))
      }
      else {
        responses foreach { requester ! PingResponse(_) }
        requester ! ChunkEnd
        context.stop(self)
      }

    case _ =>
      context.stop(self)
  }

  def cancelReceive(scheduler: Cancellable): Receive = {
    case ChunkEnd =>
      scheduler.cancel()
      context.stop(self)
  }
}
