package pw.ian.playground.monix

import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.{ Observable, OverflowStrategy }
import scala.concurrent.Await
import scala.concurrent.duration.Duration


object MergeMapNoAsync {

  def main(args: Array[String]): Unit = {
    implicit val scheduler = Scheduler.computation(20)
    val samples = 100L
    val run = Observable
      .range(0, samples)
      .groupBy(_ % 10)
      .mapAsync(10) { obs =>
        obs.mapTask { el =>
          Task { Thread.sleep(500); println(el) }
        }.foreachL(identity)
      }
      .foreach(identity)
    Await.result(run, Duration.Inf)
  }

  def main2(args: Array[String]): Unit = {
    implicit val scheduler = Scheduler.computation(20)
    val samples = 100L
    val run = Observable
      .range(0, samples)
      .groupBy(_ % 10)
      .mergeMap { obs =>
        obs.mapTask { el =>
          Task { Thread.sleep(500); println(el) }
        }
      }(OverflowStrategy.Unbounded)
      .foreach(identity)
    Await.result(run, Duration.Inf)
  }


}
