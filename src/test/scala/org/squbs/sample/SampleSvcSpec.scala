package org.squbs.sample

import org.scalatest.{Matchers, FlatSpecLike}
import org.squbs.lifecycle.GracefulStop
import org.squbs.testkit.SimpleTestKit
import org.squbs.unicomplex.{Unicomplex}
import spray.http.{HttpResponse, HttpRequest}

class SampleSvcSpec extends SimpleTestKit with FlatSpecLike with Matchers with ResponseHelper{

  "SampleSvc" should "handle request correctly" in {
    val routeActor = system.actorSelection("/user/squbs-seed/$a")

    routeActor ! HttpRequest()

    val response = expectMsgType[HttpResponse]
    response.entity.asString should be ("Hello world")
  }

  "SampleSvc" should "handle actor request correctly" in {
    val routeActor = system.actorSelection("/user/squbs-seed/$a")

    routeActor ! HttpRequest(uri = "/actor")

    verifyChunkResponse("there")
  }

  override protected def afterAll(): Unit = {
    Unicomplex(system).uniActor ! GracefulStop
  }
}
