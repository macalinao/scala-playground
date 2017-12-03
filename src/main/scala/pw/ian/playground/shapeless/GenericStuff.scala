package pw.ian.playground.shapeless

import shapeless._
import shapeless.record._
import shapeless.Witness._
import shapeless.syntax.singleton._
import shapeless.labelled.FieldType
import cats.implicits._
import cats.Show

object GenericStuff {

  case class Book(author: String, title: String, id: Int, price: Double)

  trait Thingie[T] {
    def derive(in: T): List[String]
  }

  def cinst[T](fn: T => List[String]): Thingie[T] = new Thingie[T] {
    def derive(in: T): List[String] = {
      fn(in)
    }
  }

  implicit val hnilDeriver: Thingie[HNil] = cinst { _ => List() }

  implicit def hlistDeriver[K <: Symbol, H, T <: HList](
    implicit witness: Witness.Aux[K],
    hEncoder: Lazy[Show[H]],
    tEncoder: Thingie[T],
  ): Thingie[FieldType[K, H] :: T] = cinst { case (h :: t) =>
      val fieldName: String = witness.value.name
      s"${fieldName} ${hEncoder.value.show(h)}" :: tEncoder.derive(t)
  }

  def genericDeriver[G, Repr <: HList](
    implicit generic: LabelledGeneric.Aux[G, Repr],
    deriver: Thingie[Repr],
  ): Thingie[G] = cinst { obj =>
    deriver.derive(generic.to(obj))
  }


  def main(args: Array[String]): Unit = {
    val deriver: Thingie[Book] = genericDeriver
  }

}
