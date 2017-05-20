package pw.ian.playground.shapeless

import cats._
import cats.implicits._
import cats.data.NonEmptyList
import cats.derived._, monoid._, legacy._

// runMain "pw.ian.playground.shapeless.KittensMonoidDerivation"
object KittensMonoidDerivation {

  trait Dragon
  case object Infernal extends Dragon

  case class Moments(ct: Int, sum: Int, sumSq: Int)

  case class Wrapper(dragon: Dragon, data: Option[Moments])

  case class All(wrappers: Seq[Wrapper])

  implicit val momentsMonoid = Monoid[Moments]

  implicit val wrapperSemi = new Semigroup[Wrapper] {
    def combine(a: Wrapper, b: Wrapper): Wrapper = {
      a.copy(data = a.data |+| b.data)
    }
  }

  implicit val sortedSeqMonoid = new Monoid[Seq[Wrapper]] {

    def combine(a: Seq[Wrapper], b: Seq[Wrapper]): Seq[Wrapper] = {
      val al = a.toList
      val bl = b.toList
      val groups = al.groupBy(_.dragon) |+| bl.groupBy(_.dragon)
      groups
        .mapValues(x => NonEmptyList.fromList(x).map(_.reduce))
        .values.flatten.toList
    }

    def empty = List()

  }

  def main(args: Array[String]): Unit = {

    val a = All(
      wrappers = Seq(
        Wrapper(
          dragon = Infernal,
          data = Moments(1, 2, 3).some,
        ),
      ),
    )
    val b = All(
      wrappers = Seq(
        Wrapper(
          dragon = Infernal,
          data = Moments(4, 5, 6).some,
        ),
      ),
    )

    println(a |+| b) // throws exception
  }

}
