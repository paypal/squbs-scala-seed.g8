package $base_package$

import akka.actor._
import akka.stream.scaladsl.Source
import akka.stream.{Attributes, DelayOverflowStrategy}

import scala.concurrent.duration._

// Messages for interacting with this app.
case class PingRequest(who: String)
case class PingResponse(message: String)
case class ChunkRequest(who: String, delay: FiniteDuration)
case class ChunkSourceMessage(source: Source[PingResponse, Any])
case object EmptyRequest

/**
  * The well-known-actor serves as a singleton registered entry point. It creates/manages actors to handle
  * the actual request and allows multiple access methods to this service. Only HTTP is shown
  * but it would be rather simple to add other access methods like messaging/streams, etc.
  * We could use actors with routers or any other method that has a static entry point, instead.
  *
  * What makes it well-known is being referenced in `squbs-meta.conf` and letting squbs to create the actor.
  * The path to this actor is "/user/{cube short name}/{name specified in squbs-meta.conf}"
  */
class SampleWellKnownActor extends Actor with ActorLogging {
  val sampleActor = context.actorOf(Props[SampleActor])
  def receive = {
    case request => sampleActor forward request

  }
}

/**
  * This is the actor that handles the request messages.
  */
class SampleActor extends Actor with ActorLogging {

  def receive = {

    case PingRequest(who) =>
      if (who.trim.nonEmpty) sender() ! PingResponse(s"Hello \$who welcome to squbs!")
      else sender() ! EmptyRequest

    case ChunkRequest(who, delay) =>
      val source =
        Source(PingResponse("Hello ") ::
          PingResponse(who) ::
          PingResponse(" welcome ") ::
          PingResponse("to ") ::
          PingResponse("squbs!") ::
          PingResponse("LastChunk") :: Nil)
          .delay(delay, DelayOverflowStrategy.backpressure)
          .withAttributes(Attributes.inputBuffer(initial = 1, max = 1))

      sender() ! ChunkSourceMessage(source)
  }
}
