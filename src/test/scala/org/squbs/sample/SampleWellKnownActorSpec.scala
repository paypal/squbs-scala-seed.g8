package org.squbs.sample

import akka.testkit.ImplicitSender
import org.scalatest.{FlatSpecLike, Matchers}
import org.squbs.actorregistry.ActorLookup
import org.squbs.testkit.CustomTestKit

import scala.concurrent.duration._
import scala.language.postfixOps

class SampleWellKnownActorSpec extends CustomTestKit(resources = Seq.empty, withClassPath = true)
  with FlatSpecLike with Matchers with ImplicitSender {

  "SampleWellKnownActor" should "forward message to SampleActor and get a response from SampleActor" in {
    ActorLookup("sample") ! PingRequest("foo")
    expectMsg(1 second, PingResponse("Hello foo welcome to squbs!"))
  }
}