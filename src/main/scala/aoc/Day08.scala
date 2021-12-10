package aoc
import cats.effect.IO

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 08.12.21
 */
object Day08 extends IORunner {

  case class Input(wires: Seq[SevenSegments], display: Seq[SevenSegments])
  case class SevenSegments(pattern: String) {
    def numberOfEnabledSegments: Int = pattern.length
    override def toString: String = pattern.mkString
  }

  override def task1: IO[Int] =
    streamInput()
      .map { case Input(_, display) =>
        display.flatMap(_.numberOfEnabledSegments match {
          case 2 => Some(1)
          case 4 => Some(4)
          case 3 => Some(7)
          case 7 => Some(8)
          case _ => Option.empty
        }).size
      }
      .compile
      .toList
      .map(_.sum)

  override def task2: IO[Int] =
    streamInput()
      .map { case Input(wires, display) =>
        val sets    = wires.map(_.pattern.toSet)
        val numbers = sets.flatMap { d => d.size match {
          case 2 => Some(1 -> d)
          case 4 => Some(4 -> d)
          case 3 => Some(7 -> d)
          case 7 => Some(8 -> d)
          case _ => Option.empty
        }}.toMap

        val downLeftCorner = numbers(8).diff(numbers(4) ++ numbers(7))
        val sixOrZero = sets.filter(d => d.size == 6 && downLeftCorner.subsetOf(d))
        val six  = sixOrZero.filterNot(p => numbers(7).subsetOf(p)).head
        val zero = sixOrZero.filter(p => numbers(7).subsetOf(p)).head

        val middle = numbers(8).diff(zero)

        val twoOrThreeOrFive = sets.filter(_.size == 5)
        val two   = twoOrThreeOrFive.find(d => (downLeftCorner ++ middle).subsetOf(d)).get
        val three = twoOrThreeOrFive.find(d => numbers(7).subsetOf(d)).get
        val five  = twoOrThreeOrFive.find(p => p != two && p != three).get
        val nine  = sets.find(p => p.size == 6 && (five ++ numbers(7)).subsetOf(p)).get

        val decodedNumbers = numbers.map { case (key, value) => value -> key } ++ Seq(
          zero  -> 0,
          two   -> 2,
          three -> 3,
          five  -> 5,
          six   -> 6,
          nine  -> 9
        )

        display
          .map(segment => decodedNumbers(segment.pattern.toSet))
          .foldLeft(0) { case (number, digit) => number * 10 + digit }
      }
      .compile
      .toList
      .map(_.sum)

  private def streamInput(): fs2.Stream[IO, Input] =
    streamInputLines("day08.task1")
      .map(_
        .split("\\|")
        .map(_
          .split(" ")
          .filter(_.nonEmpty)
          .map(SevenSegments)
        )
      )
      .map(arr => Input(arr(0), arr(1)))

}
