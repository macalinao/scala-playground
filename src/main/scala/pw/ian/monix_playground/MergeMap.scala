package pw.ian.monix_playground

import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable


object MergeMap {

  def main(args: Array[String]): Unit = {
    implicit val scheduler = Scheduler.computation(20)
    val samples = 1000000
    val run = Observable.range(0, samples).mapAsync(10) { index =>
      Task.eval {
        val rand = Math.random()
        val x = rand * 2 - 1
        val y = rand * 2 - 1
        if (x*x + y*y <= 1) 1 else 0
      }
    }.reduce(_ + _).map(4.0 * _ / (samples - 1)).map(println).subscribe()
  }


}
