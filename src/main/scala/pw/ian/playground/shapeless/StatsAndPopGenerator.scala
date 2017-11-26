package pw.ian.playground.shapeless

import cats.implicits._
import shapeless._

trait StatsAndPopGenerator[M, S, P] {
  def generate(in: M): (S, P)
}

object StatsAndPopGenerator {

  case class Moments()
  case class Stat()
  case class Population()

  def computeStatsAndPopulation(in: Map[Int, Moments]): (Map[Int, Stat], Population) = {
    ???
  }

  def apply[M, S, P](fn: M => (S, P)): StatsAndPopGenerator[M, S, P] = {
    new StatsAndPopGenerator[M, S, P] {
      def generate(in: M): (S, P) = {
        fn(in)
      }
    }
  }

  implicit val hnilGenerator: StatsAndPopGenerator[HNil, Map[Int, HNil], HNil] =
    apply(_ => (Map(), HNil))


  implicit def hlistGenerator[
    M <: HList, S <: HList, P <: HList,
  ](
    implicit tailSPG: StatsAndPopGenerator[M, Map[Int, S], P],
  ): StatsAndPopGenerator[
    Map[Int, Moments] :: M,
    Map[Int, Option[Stat] :: S],
    Option[Population] :: P,
  ] = apply { case (moments :: rest) =>
    val (statsHead, popHead) = computeStatsAndPopulation(moments)
    val (statsTail, popTail) = tailSPG.generate(rest)
    (
      statsTail.transform { case (key, value) =>
        statsHead.get(key) :: value
      },
      popHead.some :: popTail,
    )
  }

  def genericGenerator[
    MG, SG, PG,
    M <: HList, S <: HList, P <: HList,
  ](
    implicit genm: Generic.Aux[MG, M],
    gens: Generic.Aux[SG, S],
    genp: Generic.Aux[PG, P],
    generator: StatsAndPopGenerator[M, Map[Int, S], P],
  ): StatsAndPopGenerator[
    MG, Map[Int, SG], PG,
  ] = apply { m =>
    val (statsL, popL) = generator.generate(genm.to(m))
    (
      statsL.mapValues { statsGen =>
        gens.from(statsGen)
      },
      genp.from(popL),
    )
  }

  case class ScM(
    a: Map[Int, Moments],
  )

  case class ScS(
    a: Option[Stat],
  )

  case class ScP(
    a: Option[Population],
  )

  implicit val mgen = Generic[ScM]
  implicit val cgen = Generic[ScS]
  implicit val pgen = Generic[ScP]

  val acsdGenerator: StatsAndPopGenerator[
    ScM, Map[Int, ScS], ScP,
  ] = genericGenerator

}
