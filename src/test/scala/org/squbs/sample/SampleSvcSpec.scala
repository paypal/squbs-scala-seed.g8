package org.squbs.sample

import akka.actor.{Actor, Props}
import org.scalatest.{FlatSpecLike, Matchers}
import org.squbs.testkit.TestRoute
import spray.http.{HttpEntity, HttpResponse}
import spray.routing.RequestContext
import spray.testkit.ScalatestRouteTest

class SampleSvcSpec extends FlatSpecLike with Matchers with ScalatestRouteTest{

  val route = TestRoute[SampleSvc]

  "route" should "handle request correctly" in {
    Get() ~> route ~> check {
      responseAs[String] should be("Hello world")
    }
  }

  "route" should "handle actor path correctly" in {
    // create actor
    system.actorOf(Props[MockActor], "squbs-seed")
    Get("/actor") ~> route ~> check {
      responseAs[String] should be ("mock")
    }
  }
}

class MockActor extends Actor {
  override def receive: Receive = {case _ =>}

  context.actorOf(Props(new Actor {
    override def receive: Actor.Receive = {
      case RequestContext(req, responder, _) => responder ! HttpResponse(entity = HttpEntity("mock"))
    }
  }), "sample")
}
