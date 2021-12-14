package aoc
import cats.effect.IO

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 14.12.21
 */
object Day14 extends IORunner {

  case class Input(polymer: String, polymerTemplate: Map[String, String])

  override def task1: IO[Int] =
    streamInputLines("day14.task1", filterNonEmpty = false)
      .toPolymerInput
      .map { case Input(polymer, template) =>
        val occurrences = (0 until 10).foldLeft(polymer) { case (polymer, _) =>
        polymer
            .sliding(2)
            .flatMap(pattern =>
              template
                .get(pattern)
                .map(n => pattern(0) + n)
                .getOrElse(pattern)
            ).mkString + polymer.last
        }
        .groupMapReduce(identity)(_ => 1)(_ + _)
        .values

        occurrences.max - occurrences.min
      }

  override def task2: IO[Long] =
    streamInputLines("day14.task1", filterNonEmpty = false)
      .toPolymerInput
      .map { case Input(polymer, template) =>
        val polymerMap  = polymer.sliding(2).toVector.groupMapReduce(identity)(_ => 1L)(_ + _)
        val occurrences =
          (0 until 40).foldLeft(polymerMap) { case (polymerMap, _) =>
            polymerMap.map[List[(String, Long)]] { case entry@(polymer, count) =>
              template.get(polymer).map { insert =>
                List(
                  (polymer(0) + insert) -> (count),
                  (insert + polymer(1)) -> (count)
                )
              }.getOrElse(List(entry))
            }.flatten
             .groupMapReduce(_._1)(_._2)(_ + _)
          }
          .map[List[(Char, Long)]] {
            case (polymer, count) => List(
              polymer(0) -> count,
              polymer(1) -> count
            )
          }.flatten
           .++(Iterator.single(polymer.last -> 1L))
           .++(Iterator.single(polymer.head -> 1L))
           .groupMapReduce(_._1)(_._2)(_ + _)
           .view
           .mapValues(_ / 2)
           .toMap
           .values

        occurrences.max - occurrences.min
      }

  private val polymerTemplateRegex = """([A-Z]+) -> ([A-Z])""".r
  implicit class InputStream(private val stream: fs2.Stream[IO, String]) extends AnyVal {

    def toPolymerInput: IO[Input] =
      stream
        .groupAdjacentBy(_.nonEmpty)
        .compile
        .toVector
        .map { data =>
          val inputString = data(0)._2.head.get
          val templates   = data(2)._2.map {
            case polymerTemplateRegex(pattern, output) => pattern -> output
            case string                                => throw new RuntimeException(s"'$string' does not match polymer template")
          }.toVector
           .toMap

          Input(inputString, templates)
        }
  }

}
