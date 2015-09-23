package org.squbs.sample

import org.squbs.unicomplex.RouteDefinition
import org.webjars.WebJarAssetLocator
import spray.http.StatusCodes
import spray.routing._
import Directives._


class AssetsActor extends RouteDefinition{
  val assetsLocator = new WebJarAssetLocator()
  override def route: Route = path("lib" / Segment / Rest ) {(lib, rest) =>
    getFromResource(assetsLocator.getFullPath(lib, rest))
  } ~ path(Rest){rest =>
    getFromResource(assetsLocator.getFullPath(rest))
  }

  override def exceptionHandler: Option[ExceptionHandler] = Some(ExceptionHandler {
    case e: IllegalArgumentException => ctx =>
      ctx.complete(StatusCodes.NotFound, e.getMessage)
  })
}
