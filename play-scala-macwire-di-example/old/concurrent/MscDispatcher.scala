package common.concurrent

import java.util.concurrent.TimeUnit

import akka.dispatch._
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, FiniteDuration}


/**
  * Configurator for the custom Akka Dispatcher
  *
  * References:
  * - http://yanns.github.io/blog/2014/05/04/slf4j-mapped-diagnostic-context-mdc-with-play-framework/
  * - http://stevenskelton.ca/threadlocal-variables-scala-futures/
  * - https://www.playframework.com/documentation/2.5.x/ThreadPools
  */
class MscDispatcherConfigurator(config: Config, prerequisites: DispatcherPrerequisites) extends MessageDispatcherConfigurator(config, prerequisites) {

  private val instance = new MscDispatcher(
    this,
    config.getString("id"),
    config.getInt("throughput"),
    FiniteDuration(config.getDuration("throughput-deadline-time", TimeUnit.NANOSECONDS), TimeUnit.NANOSECONDS),
    configureExecutor(),
    FiniteDuration(config.getDuration("shutdown-timeout", TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS))

  override def dispatcher(): MessageDispatcher = instance
}

/**
  * Propagate contextual information in a Play application using a custom Akka Dispatcher
  */
class MscDispatcher(_configurator: MessageDispatcherConfigurator,
                    id: String,
                    throughput: Int,
                    throughputDeadlineTime: Duration,
                    executorServiceFactoryProvider: ExecutorServiceFactoryProvider,
                    shutdownTimeout: FiniteDuration)
  extends Dispatcher(_configurator, id, throughput, throughputDeadlineTime, executorServiceFactoryProvider, shutdownTimeout) {
  self =>

  override def prepare(): ExecutionContext = new ExecutionContext {
    val copy = MscContextHolder.copyValues

    def execute(task: Runnable) = self.execute(new Runnable {
      def run() = {
        val old = MscContextHolder.copyValues
        MscContextHolder.setValues(copy)
        try {
          task.run()
        } finally {
          MscContextHolder.setValues(old)
        }
      }
    })

    def reportFailure(t: Throwable) = self.reportFailure(t)
  }

}