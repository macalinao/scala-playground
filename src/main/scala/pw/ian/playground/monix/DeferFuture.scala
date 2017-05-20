package pw.ian.playground.monix

import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.{ Observable, OverflowStrategy }
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration.Duration

import cats.implicits._
import monix.cats._


object DeferFuture {

  def main(args: Array[String]): Unit = {
    implicit val scheduler = Scheduler.computation(20)

    val tasks = List.fill(100)(()).map { _ =>
      Task.deferFuture {
        Future {
          println("done")
          Thread.sleep(500)
        }
      }
    }

    // These tasks will execute serially.
    Await.result(tasks.sequence.runAsync, Duration.Inf)

    // To run in parallel, use this:
    // Await.result(Task.gather(tasks).runAsync, Duration.Inf)
    // https://monix.io/docs/2x/tutorials/parallelism.html#observablemergemap
  }

}
