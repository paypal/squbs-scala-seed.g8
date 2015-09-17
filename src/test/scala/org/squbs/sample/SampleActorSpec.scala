package org.squbs.sample

import akka.actor.{Props, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{Matchers, FlatSpecLike}
import spray.http._
import spray.http.Uri.Path
import spray.routing.RequestContext
import concurrent.duration._

class SampleActorSpec extends TestKit(ActorSystem()) with FlatSpecLike with Matchers with ImplicitSender with ResponseHelper{

  "SampleActor" should "return chunked response" in {
    val user = "test-user"
    val ctx = RequestContext(HttpRequest(uri = s"?user=$user"), self, Path.Empty)

    val target = system.actorOf(Props[SampleActor])
    watch(target)
    target ! ctx

    verifyChunkResponse(user)

    expectTerminated(target, 2 seconds)
  }


  "SampleActor" should "not handle other messages" in {
    val target = system.actorOf(Props[SampleActor])
    watch(target)
    target ! "other"

    expectTerminated(target, 2 seconds)
  }
}
