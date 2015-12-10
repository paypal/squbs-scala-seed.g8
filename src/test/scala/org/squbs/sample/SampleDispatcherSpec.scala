package org.squbs.sample

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{FlatSpecLike, Matchers}

import scala.concurrent.duration._
import scala.language.postfixOps

class SampleDispatcherSpec extends TestKit(ActorSystem("SampleDispatcherSpec")) with FlatSpecLike with Matchers
with ImplicitSender {

  "SampleDispatcher" should "forward message to SampleActor and get a response from SampleActor" in {
    system.actorOf(Props[SampleDispatcher], "SampleDispatcher") ! PingRequest("foo")
    expectMsg(10 seconds, PingResponse("Hello foo welcome to squbs!"))
    lastSender.path.toString should fullyMatch regex """akka://SampleDispatcherSpec/user/SampleDispatcher/\$\w"""
  }
}