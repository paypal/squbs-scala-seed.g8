package org.squbs.sample

import akka.actor.{Stash, ActorRef, Props, Actor}
import akka.pattern._
import akka.util.Timeout
import org.squbs.httpclient.json.Json4sJacksonNoTypeHintsProtocol
import org.squbs.unicomplex.RouteDefinition
import spray.http.HttpHeaders.`Content-Type`
import spray.http._
import spray.routing.Directives._
import spray.routing._

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Success

/**
  * Just a place to hold actor paths.
  */
object ActorPaths {
  // actor path = /user/ + cube-shortname + / + actor name
  val pingActorPath = "/user/squbs-seed/sample"
}

/**
  * The route definition.
  */
class SampleHttpSvc extends RouteDefinition {

  import context.dispatcher

  implicit val timeout: Timeout = 3 seconds

  import ActorPaths._

  override def route: Route =
    get {
      path("hello") {
        onComplete(context.actorSelection(pingActorPath) ? PingRequest("anonymous")) {
          case Success(PingResponse(message)) => complete(message)
          case _ => complete(StatusCodes.BadRequest)

        }
      } ~
      path("hello" / Segment) { who =>
        import Json4sJacksonNoTypeHintsProtocol._
        onComplete(context.actorSelection(pingActorPath) ? PingRequest(who)) {
          case Success(response: PingResponse) => complete(response)
          case _ => complete(StatusCodes.BadRequest)
        }
      } ~
      path("hello" / Segment / IntNumber) { (who, delay) => ctx =>
        context.actorOf(Props[ChunkingActor]) ! (PingRequest(who), delay, ctx.responder)
      }
    } ~
    post {
      path("hello") {
        import Json4sJacksonNoTypeHintsProtocol._
        entity(as[PingRequest]) { request =>
          onComplete(context.actorSelection(pingActorPath) ? request) {
            case Success(response: PingResponse) => complete(response)
            case _ => complete(StatusCodes.BadRequest)
          }
        }
      }
    } ~
    complete("Hello!")
}

/**
  * Warning: Advanced Topic - response chunking.
  * The chunking actor. We only need this one in case of chunking.
  */
class ChunkingActor extends Actor with Stash {

  import ActorPaths._

  case object ChunkAck

  /**
    * Receiving initial requests
    * @return This receive
    */
  def receive = {
    case (PingRequest(who), delay: Int, responder: ActorRef) =>
      context.actorSelection(pingActorPath) ! ChunkRequest(who, delay milliseconds)
      responder ! ChunkedResponseStart(HttpResponse()
        .withHeaders(`Content-Type`(MediaTypes.`text/plain`)))
        .withAck(ChunkAck)
      context.become(ackAwait(chunkEmitter(responder)))
  }

  /**
    * Get into ack-await state where we don't do anything but await for the ack.
    * @param sendAwait The receive to become
    * @return This receive
    */
  def ackAwait(sendAwait: Receive): Receive = {

    case ChunkAck =>
      unstashAll()
      context.become(sendAwait, discardOld = false)

    case _ =>
      stash()
  }

  /**
    * Get into emit state when acks are satisfied.
    * @param responder The responder to send the messages to
    * @return This receive
    */
  def chunkEmitter(responder: ActorRef): Receive = {

    case PingResponse(message) =>
      responder ! MessageChunk(message).withAck(ChunkAck)
      context.unbecome()

    case ChunkEnd =>
      responder ! ChunkedMessageEnd
      context.stop(self)
  }
}
