package org.squbs.sample

import akka.actor.{Props, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{Matchers, FlatSpecLike}
import spray.http._
import spray.http.Uri.Path
import spray.routing.RequestContext
import concurrent.duration._

class SampleDispatcherSpec extends TestKit(ActorSystem()) with FlatSpecLike with Matchers with ImplicitSender with ResponseHelper {

  "SampleDispatcher" should "forward message to SampleActor" in {
    val user = "test-user"
    val ctx = RequestContext(HttpRequest(uri = s"?user=$user"), self, Path.Empty)

    val target = system.actorOf(Props[SampleDispatcher])
    target ! ctx

    verifyChunkResponse(user)
  }
}
