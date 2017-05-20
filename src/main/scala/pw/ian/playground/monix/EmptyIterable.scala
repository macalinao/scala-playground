package pw.ian.monix_playground

import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.{ Observable, OverflowStrategy }
import scala.concurrent.Await
import scala.concurrent.duration.Duration


object EmptyIterable {

  def modEmpty(n: Long): Observable[Long] = {
    if (n % 2 == 0) {
      Observable.fromIterable(Vector())
    } else {
      Observable.fromIterable(Vector(n, n))
    }
  }

  def main(args: Array[String]): Unit = {
    implicit val scheduler = Scheduler.computation(20)
    val run = Observable
      .range(0, 100)
      .flatMap { n =>
        modEmpty(n)
      }
      .foreach(println)
    Await.result(run, Duration.Inf)
  }

}
