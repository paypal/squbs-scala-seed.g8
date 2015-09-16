package org.squbs.sample

import akka.testkit.TestKitBase
import org.scalatest.Matchers
import spray.http.{ChunkedMessageEnd, MessageChunk, ChunkedResponseStart, Confirmed}
import concurrent.duration._
import scala.language.postfixOps

trait ResponseHelper {
  this: TestKitBase with Matchers=>


  def verifyChunkResponse(user: String): Unit = {
    var msgs = ""

    fishForMessage(5 seconds, "collecting message chunks") {
      case Confirmed(s: ChunkedResponseStart, ack) =>
        lastSender ! ack
        false
      case Confirmed(chunk: MessageChunk, ack) =>
        msgs += chunk.data.asString
        lastSender ! ack
        false
      case _: ChunkedMessageEnd => true
    }

    msgs should include(user)
  }
}
