package pw.ian.playground.cats

import cats._
import cats.implicits._

// runMain "pw.ian.playground.shapeless.KittensNullMonoid"
object NullTypeclass {

  case class Moments(ct: Int)
  implicit val momentsMonoid: Monoid[Moments] = Monoid[Moments]

  def main(args: Array[String]): Unit = {
    println(momentsMonoid) // prints "null"
  }

}
