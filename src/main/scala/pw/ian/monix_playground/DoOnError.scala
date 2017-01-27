package pw.ian.monix_playground

import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable
import scala.concurrent.Await
import scala.concurrent.duration.Duration


object DoOnError {

  def main(args: Array[String]): Unit = {
    implicit val scheduler = Scheduler.computation(20)
    val run = Observable.repeat(()).mapAsync { _ =>
      taskFactory
    }.onErrorRecover {
      case _ => println("yolo")
    }

    Await.result(run.foreach(identity), Duration.Inf)
  }

  def taskFactory = {
    Task.eval {
      throw new Exception("yolo swag")
    }
  }


}
