package org.squbs.sample

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{FlatSpecLike, Matchers}

import scala.concurrent.duration._
import scala.language.postfixOps

class SampleActorSpec extends TestKit(ActorSystem("SampleActorSpec")) with FlatSpecLike
with Matchers with ImplicitSender {

  "SampleActor" should "emit single response for PingRequest" in {
    val target = system.actorOf(Props[SampleActor])
    watch(target)
    target ! PingRequest("foo")
    expectMsg(1 second, PingResponse("Hello foo welcome to squbs!"))
    expectTerminated(target, 1 seconds)
  }

  "SampleActor" should "emit multiple responses for ChunkRequest" in {

    val chunks = Seq("Hello ", "foo", " welcome ", "to ", "squbs!")

    val target = system.actorOf(Props[SampleActor])
    watch(target)
    target ! ChunkRequest("foo", 200 milliseconds)
    chunks foreach { chunk => expectMsg(1 second, PingResponse(chunk)) }
    expectMsg(1 second, ChunkEnd)
    expectTerminated(target, 1 seconds)

  }

  "SampleActor" should "not handle other messages" in {
    val target = system.actorOf(Props[SampleActor])
    watch(target)
    target ! "other"
    expectTerminated(target, 1 seconds)
  }
}
