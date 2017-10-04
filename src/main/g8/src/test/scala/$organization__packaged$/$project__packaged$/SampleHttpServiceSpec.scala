package $organization$.$project$

import akka.http.scaladsl.model.HttpEntity.{Chunk, LastChunk}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.RouteTestTimeout
import org.json4s.{DefaultFormats, native}
import org.scalatest.{FlatSpecLike, Matchers}
import org.squbs.testkit.{CustomRouteTestKit, TestRoute}

import scala.concurrent.duration._
import scala.language.postfixOps

class SampleHttpServiceSpec extends CustomRouteTestKit(resources = Seq.empty, withClassPath = true)
  with FlatSpecLike with Matchers {

  implicit val timeout = RouteTestTimeout(5 seconds)

  val route = TestRoute[SampleHttpService]

  behavior of "SampleHttpService"

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
    import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
    implicit val serialization = native.Serialization
    implicit val formats = DefaultFormats
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
      val expected = Chunk("Hello ") ::
        Chunk("foo") ::
        Chunk(" welcome ") ::
        Chunk("to ") ::
        Chunk("squbs!") ::
        LastChunk :: Nil
      chunks should be (expected)
    }
  }

  it should "handle path segment, chunking, no delay" in {
    Get("/hello/foo/0") ~> route ~> check {
      val expected = Chunk("Hello ") ::
        Chunk("foo") ::
        Chunk(" welcome ") ::
        Chunk("to ") ::
        Chunk("squbs!") ::
        LastChunk :: Nil
      chunks should be (expected)
    }
  }

  it should "handle post serialization and deserialization" in {
    import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
    implicit val serialization = native.Serialization
    implicit val formats = DefaultFormats
    Post("/hello", PingRequest("bar")) ~> route ~> check {
      responseAs[PingResponse] should be (PingResponse("Hello bar welcome to squbs!"))
    }
  }

  it should "return bad request for request with blank field" in {
    import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
    implicit val serialization = native.Serialization
    implicit val formats = DefaultFormats
    Post("/hello", PingRequest("")) ~> route ~> check {
      status should be (StatusCodes.BadRequest)
    }
  }
}
