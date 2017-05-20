package pw.ian.playground.shapeless

import cats._
import cats.implicits._
import cats.data.NonEmptyList
import cats.derived._, monoid._, legacy._

// runMain "pw.ian.playground.shapeless.KittensMonoidDerivation"
object KittensMonoidDerivation {

  trait MatchSumSeqElement[T] extends Semigroup[T] {
    def identify(t: T): Any
  }

  object MatchSumSeqElement {

    def instance[T](id: T => Any)(combineFunc: (T, T) => T): MatchSumSeqElement[T] = new MatchSumSeqElement[T] {
      def combine(a: T, b: T): T = combineFunc(a, b)
      def identify(t: T): Any = id(t)
    }

  }

  trait Dragon
  case object Infernal extends Dragon

  case class Moments(ct: Int, sum: Int, sumSq: Int)

  case class Wrappa(id: Dragon, data: Option[Moments])

  case class All(wrappas: Seq[Wrappa])

  implicit val momentsMonoid: Monoid[Moments] = Monoid[Moments]

  implicit val wrappaElt =
    MatchSumSeqElement.instance[Wrappa](_.id) { (a, b) =>
      a.copy(data = a.data |+| b.data)
    }

  implicit def sortedSeqMonoid[T](implicit mse: MatchSumSeqElement[T]): Monoid[Seq[T]] = new Monoid[Seq[T]] {

    def combine(a: Seq[T], b: Seq[T]): Seq[T] = {
      val al = a.toList
      val bl = b.toList
      val groups = al.groupBy(mse.identify) |+| bl.groupBy(mse.identify)
      groups
        .mapValues(x => NonEmptyList.fromList(x).map(_.reduce))
        .values.flatten.toList
    }

    def empty = List()

  }

  def main(args: Array[String]): Unit = {

    val a = All(
      wrappas = Seq(
        Wrappa(
          id = Infernal,
          data = Moments(1, 2, 3).some,
        ),
      ),
    )
    val b = All(
      wrappas = Seq(
        Wrappa(
          id = Infernal,
          data = Moments(4, 5, 6).some,
        ),
      ),
    )

    println(a |+| b)
  }

}
