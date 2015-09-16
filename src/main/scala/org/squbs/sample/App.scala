package org.squbs.sample

import akka.actor._
import org.squbs.unicomplex.RouteDefinition
import spray.http.HttpHeaders.`Content-Type`
import spray.http._
import spray.routing.Route
import spray.routing._
import Directives._

class SampleSvc extends RouteDefinition {
  override def route: Route = path("actor") {ctx =>
    // actor path = /user/ + cube-shortname + / + actor name
    context.actorSelection("/user/squbs-seed/sample") ! ctx
  } ~ {
    // otherwise go to default response
    complete("Hello world")
  }
}

class SampleDispatcher extends Actor with ActorLogging {
  override def receive: Receive = {
    case ctx: RequestContext =>
      context.actorOf(Props[SampleActor]) forward ctx
  }
}

class SampleActor extends Actor with ActorLogging {
  val system = context.system
  import system.dispatcher
  import system.scheduler
  import concurrent.duration._

  case class SampleAck(remaining: Int)

  override def receive: Receive = {
    case ctx:RequestContext =>
      val user = ctx.request.uri.query.get("user").getOrElse("there")
      val messages = s"Hello $user, welcome to Squbs!".split("")
      context.become(chunkResponse(ctx.responder, messages))
      // Have to use text/html for some specific browser like Safari
      // https://code.google.com/p/chromium/issues/detail?id=156023
      ctx.responder ! ChunkedResponseStart(HttpResponse().withHeaders(`Content-Type`(MediaTypes.`text/html`)))
                        .withAck(SampleAck(messages.length))

    case other =>
      log.warning(s"Get unexpected message: $other")
      context.stop(self)
  }

  def chunkResponse(responder: ActorRef, messages: Array[String]): Actor.Receive = {
    case SampleAck(0) =>
      responder ! ChunkedMessageEnd
      log.info(s"No remaining messages, stop the actor")
      context.stop(self)

    case SampleAck(remaining) => scheduler.scheduleOnce(50 millis) {
      responder ! MessageChunk(messages(messages.length - remaining)).withAck(SampleAck(remaining - 1))
    }

  }
}
