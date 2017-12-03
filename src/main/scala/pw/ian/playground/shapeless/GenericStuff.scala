package pw.ian.playground.shapeless

import shapeless._

object GenericStuff {

  case class Book(author: String, title: String, id: Int, price: Double)

  def main(args: Array[String]): Unit = {
    val bookGen = LabelledGeneric[Book]
    val tapl = Book("Benjamin Pierce", "Types and Programming Languages", 262162091, 44.11)
    val rec = bookGen.to(tapl)
  }

}
