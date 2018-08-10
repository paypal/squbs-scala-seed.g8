package $base_package$

import akka.actor.ActorSystem
import org.squbs.metrics.MetricsFlow
import org.squbs.pipeline.{Context, PipelineFlow, PipelineFlowFactory}

/**
  * Default flow definition that uses squbs provided MetricsFlow
  */
class DefaultFlow extends PipelineFlowFactory {

  override def create(context: Context)(implicit system: ActorSystem): PipelineFlow = {
    MetricsFlow(context.name)
  }

}
