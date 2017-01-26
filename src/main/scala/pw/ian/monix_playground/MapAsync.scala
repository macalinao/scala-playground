package pw.ian.monix_playground

import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Random


object MapAsync {

  def main(args: Array[String]): Unit = {
    val runs = 20
    implicit val scheduler = Scheduler.computation(runs)

    val rands = (0 to (runs * 2)).map { _ => Random.nextInt(1000) }

    println(s"max: ${rands.max}")

    val start = System.currentTimeMillis()
    val run = Observable.fromIterable(rands).mapAsync(runs) { duration =>
      Task.eval {
        println(s"start ${duration}")
        Thread.sleep(duration)
        println(s"end ${duration}")
      }
    }

    Await.result(run.foreach(identity), Duration.Inf)

    println(System.currentTimeMillis() - start)
  }


}
