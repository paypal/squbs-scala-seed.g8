package org.squbs.sample

import org.scalatest.{FlatSpecLike, Matchers}
import org.squbs.testkit.TestRoute
import spray.http.StatusCodes
import spray.testkit.ScalatestRouteTest

import scala.concurrent.duration._
import scala.language.postfixOps

class SampleSvcNegSpec extends FlatSpecLike with Matchers with ScalatestRouteTest {

  implicit val timeout = RouteTestTimeout(5 seconds)

  val route = TestRoute[SampleHttpSvc]

  "The route" should "timeout handling path" in {
    Get("/hello") ~> route ~> check {
      status should be(StatusCodes.BadRequest)
    }
  }
}