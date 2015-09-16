package org.squbs.sample

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKitBase}
import org.scalatest.exceptions.TestFailedException
import org.scalatest.{FlatSpecLike, Matchers}
import org.squbs.unicomplex.RouteDefinition
import spray.http.{HttpEntity, HttpResponse}
import spray.routing._
import spray.testkit.{RouteTest, TestFrameworkInterface}

class SampleSvcMockSpec extends {
  override implicit val system: ActorSystem = ActorSystem()
} with TestKitBase with FlatSpecLike with Matchers with RouteTest with TestFrameworkInterface with ImplicitSender{

  "SampleSvc" should "Return hello world" in {
    // mockup
    system.actorOf(Props(new Actor {
      val route = RouteDefinition.startRoutes{new SampleSvc}.route
      val mockActor = context.actorOf(Props(new Actor {
        override def receive: Actor.Receive = {
          case ctx: RequestContext => ctx.responder ! HttpResponse(entity = HttpEntity("test"))
        }
      }), "sample")

      override def receive: Receive = {
        case _ => sender ! route // return route
      }
    }), "squbs-seed") ! "give me route"

    val route = expectMsgType[Route]
    Get("/actor") ~> route ~> check {
      responseAs[String] should be("test")
    }

    Get() ~> route ~> check {
      responseAs[String] should be ("Hello world")
    }
  }


  override def failTest(msg: String): Nothing = throw new TestFailedException(msg, 11)
}
