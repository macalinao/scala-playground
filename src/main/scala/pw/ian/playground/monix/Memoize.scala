package pw.ian.playground.monix

import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random


object Memoize {

  def main(args: Array[String]): Unit = {
    val runs = 20
    implicit val scheduler = Scheduler.computation(runs)

    val memo = Task.evalOnce { println("yoloswag") }

    val result = Observable.range(0, 10)
      .mapAsync(parallelism = 100) { el =>
        memo
      }
      .completedL.runAsync
    Await.result(result, Duration.Inf)
  }


}
