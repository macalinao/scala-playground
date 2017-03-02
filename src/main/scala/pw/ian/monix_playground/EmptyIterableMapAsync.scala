package pw.ian.monix_playground

import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.{ Observable, OverflowStrategy }
import scala.concurrent.Await
import scala.concurrent.duration.Duration


object EmptyIterableMapAsync {

  def modEmpty(n: Long): Task[List[Long]] = {
    if (n % 2 == 0) {
      Task.now(List())
    } else {
      Task.now(List(n, n))
    }
  }

  def main(args: Array[String]): Unit = {
    implicit val scheduler = Scheduler.computation(20)

    val priorityStream: Observable[Long] = Observable
      .range(0, 100)
      .mapAsync(3)(n => modEmpty(n))
      .map { x => println("the list: " + x); x }
      .flatMap(Observable.fromIterable)

    Await.result(priorityStream.foreach(println), Duration.Inf)
  }

}
