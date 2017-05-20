package pw.ian.playground.monix

import monix.eval.Task
import monix.execution.Scheduler
import scala.concurrent.Await
import scala.concurrent.duration.Duration


object TaskErrorHandling {

  def main(args: Array[String]): Unit = {
    implicit val scheduler = Scheduler.computation(20)
    val task = Task.eval {
      throw new Exception("fuck")
    }.onErrorHandle { _ =>
      println("we good")
    }.map { _ =>
      println("fo real tho")
    }
    Await.result(task.runAsync, Duration.Inf)

    val task2 = Task.eval {
      throw new Exception("fuck")
    }.map { _ =>
      println("should not print")
    }
    Await.result(task2.runAsync, Duration.Inf)
  }


}
