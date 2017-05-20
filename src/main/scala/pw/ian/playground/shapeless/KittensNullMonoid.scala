package pw.ian.playground.shapeless

import cats._
import cats.implicits._
import cats.derived._, monoid._, legacy._

// runMain "pw.ian.playground.shapeless.KittensNullMonoid"
object KittensNullMonoid {

  case class Moments(ct: Int)
  implicit val momentsMonoid: Monoid[Moments] = Monoid[Moments]

  def main(args: Array[String]): Unit = {
    println(momentsMonoid)
  }

}
