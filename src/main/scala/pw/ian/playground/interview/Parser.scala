package pw.ian.playground.interview

import java.io._
import scala.concurrent.Await
import monix.execution.Scheduler.Implicits.global
import java.util.Date
import java.text.SimpleDateFormat
import scala.io.Source
import monix.reactive.Observable
import cats.implicits._
import scala.concurrent.duration.Duration

object Parser {

  val utc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

  val SESSION_INTERVAL = 30 * 60 * 1000

  case class Event(
    timestamp: Long,
    eventName: String,
    userId: String,
    channelId: String,
  )

  case class User(
    userId: String,
    sessions: List[Session] = List(),
  ) {
    def addEvent(event: Event): User = {
      copy(
        sessions = sessions match {
          case head :: tail => {
            if (event.timestamp - head.end > SESSION_INTERVAL) {
              Session(event) :: head :: tail
            } else {
              head.addEvent(event) :: tail
            }
          }
          case Nil => Session(event) :: Nil
        }
      )
    }
  }

  case class SessionResult(
    userId: String,
    start: String,
    end: String,
    mostUsedChannel: String,
    messageCount: Int,
  ) {
    def toJSON: String = {
      s"""
      {
        "userId": "${userId}",
        "start": "${start}",
        "end": "${end}",
        "mostUsedChannel": "${mostUsedChannel}",
        "messageCount": "${messageCount}"
      }
      """
    }
  }

  object SessionResult {
    def apply(session: Session): SessionResult = {
      SessionResult(
        userId = session.userId,
        start = utc.format(new Date(session.start)),
        end = utc.format(new Date(session.end)),
        mostUsedChannel = session.channels.toList.sortBy(_._2).reverse.head._1,
        messageCount = session.channels.values.toList.sum,
      )
    }
  }

  case class Session(
    userId: String,
    start: Long,
    channels: Map[String, Int] = Map(),
    end: Long = 0,
  ) {
    def addEvent(event: Event): Session = {
      copy(
        channels = channels |+| Map(event.channelId -> 1),
        end = event.timestamp,
      )
    }
  }

  object Session {
    def apply(event: Event): Session = {
      Session(
        userId = event.userId,
        start = event.timestamp).addEvent(event)
    }
  }

  object Event {
    def apply(elt: String): Event = {
      val arr = elt.split(",")
      Event(
        timestamp = utc.parse(arr(0)).getTime,
        eventName = arr(1),
        userId = arr(2),
        channelId = arr(3),
      )
    }
  }

  def main(args: Array[String]): Unit = {
    val lines = Source.fromFile("/Users/ian/sample.csv").getLines
    lines.next
    val result = Observable.fromIterator(lines)
      .map(Event.apply)
      .groupBy(_.userId)
      .mapAsync(10) { obs =>
        obs.foldLeftL(User(userId = obs.key)) { (acc, event) =>
          acc.addEvent(event)
        }
      }
      .toListL

    val task = result.map { users =>
      val sessions = users.flatMap { user =>
        user.sessions.map(SessionResult.apply)
      }
      val result = "[" + sessions.map(_.toJSON).mkString(",") + "]"
      val pw = new PrintWriter(new File("/Users/ian/parser_result.json" ))
      pw.write(result)
      pw.close
      ()
    }

    Await.result(task.runAsync, Duration.Inf)
  }

}
