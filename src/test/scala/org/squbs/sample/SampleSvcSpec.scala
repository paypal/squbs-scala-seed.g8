package org.squbs.sample

import akka.actor.{Actor, Props}
import org.scalatest.{FlatSpecLike, Matchers}
import org.squbs.httpclient.json.Json4sJacksonNoTypeHintsProtocol
import org.squbs.testkit.TestRoute
import spray.http.{MessageChunk, StatusCodes}
import spray.testkit.ScalatestRouteTest

import scala.concurrent.duration._
import scala.language.postfixOps

class SampleSvcSpec extends FlatSpecLike with Matchers with ScalatestRouteTest {

  implicit val timeout = RouteTestTimeout(5 seconds)

  val route = TestRoute[SampleHttpSvc]

  system.actorOf(Props[MockSupervisor], "squbs-seed")

  behavior of "the sample route"

  it should "handle simple request correctly" in {
    Get() ~> route ~> check {
      responseAs[String] should be ("Hello!")
    }
  }

  it should "handle path correctly" in {
    Get("/hello") ~> route ~> check {
      responseAs[String] should be ("Hello anonymous welcome to squbs!")
    }
  }

  it should "handle path segment and serialization" in {
    import Json4sJacksonNoTypeHintsProtocol._
    Get("/hello/foo") ~> route ~> check {
      responseAs[PingResponse] should be (PingResponse("Hello foo welcome to squbs!"))
    }
  }

  it should "return bad request for path segment representing space" in {
    Get("/hello/%20") ~> route ~> check {
      status should be (StatusCodes.BadRequest)
    }
  }

  it should "handle path segment, chunking, with delay" in {
    Get("/hello/foo/500") ~> route ~> check {
      val expected = List("Hello ", "foo", " welcome ", "to ", "squbs!") map MessageChunk.apply
      chunks should be (expected)
    }
  }

  it should "handle path segment, chunking, no delay" in {
    Get("/hello/foo/0") ~> route ~> check {
      val expected = List("Hello ", "foo", " welcome ", "to ", "squbs!") map MessageChunk.apply
      chunks should be (expected)
    }
  }

  it should "handle post serialization and deserialization" in {
    import Json4sJacksonNoTypeHintsProtocol._
    Post("/hello", PingRequest("bar")) ~> route ~> check {
      responseAs[PingResponse] should be (PingResponse("Hello bar welcome to squbs!"))
    }
  }

  it should "return bad request for request with blank field" in {
    import Json4sJacksonNoTypeHintsProtocol._
    Post("/hello", PingRequest("")) ~> route ~> check {
      status should be (StatusCodes.BadRequest)
    }
  }
}

/**
  * The MockSupervisor mocks a cube supervisor and starts the target actor without starting the
  * squbs infrastructure.
  */
class MockSupervisor extends Actor {
  def receive = {case _ =>}
  context.actorOf(Props[SampleDispatcher], "sample")
}
