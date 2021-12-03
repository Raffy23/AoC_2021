package aoc
import cats.effect.IO

import scala.annotation.tailrec

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 03.12.21
 */
object Day03 extends IORunner {

  case class DiagnosticOutput(ones: IndexedSeq[Int], lines: Int) {
    def modify(line: String): DiagnosticOutput = {
      DiagnosticOutput(
        line.zipAll(this.ones, '0', 0).map {
          case ('0', count) => count
          case ('1', count) => count + 1
        },
        this.lines + 1
      )
    }
  }
  object DiagnosticOutput {
    def initial: DiagnosticOutput = DiagnosticOutput(Vector.empty, 0)
  }

  case class PowerConsumption(gamma: Int, epsilon: Int)
  object PowerConsumption {
    def empty: PowerConsumption = PowerConsumption(0, 0)
  }

  override def task2: IO[Unit] = for {
    inputs <- streamInputLines("day03.task1").compile.toVector

    oxygen <- IO {
      Integer.valueOf(filterData(0, (ones, zeros) => if (ones >= zeros) '1' else '0', inputs).head, 2)
    }

    co2Scrubber <- IO {
      Integer.valueOf(filterData(0, (ones, zeros) => if (ones >= zeros) '0' else '1', inputs).head, 2)
    }

    _ <- IO.println(s"Task2: ${oxygen * co2Scrubber}")
  } yield ()

  override def task1: IO[Unit] =
    streamInputLines("day03.task1")
      .fold(DiagnosticOutput.initial) { case (output, line) =>
        output.modify(line)
      }
      .compile
      .lastOrError
      .flatMap { diagnostics =>
        val (gamma, epsilon) = diagnostics.ones.foldLeft((0, 0)) {
          case ((gamma, epsilon), value) if value > diagnostics.lines - value => (gamma << 1 | 1, epsilon << 1 | 0)
          case ((gamma, epsilon), value) if value < diagnostics.lines - value => (gamma << 1 | 0, epsilon << 1 | 1)
        }

        IO.println(s"Task1: ${gamma * epsilon}")
      }

  @tailrec
  private def filterData(index: Int, symbolExtractor: (Int, Int) => Char, list: Vector[String]): Vector[String] = {
    val ones = list.foldLeft(0) {
      case (output, line) if line(index) == '1' => output + 1
      case (output, _   )                       => output
    }

    val symbol  = symbolExtractor(ones, list.size - ones)
    val newList = list.filter(_(index) == symbol)

    if (newList.sizeCompare(1) == 0) newList
    else                             filterData(index + 1, symbolExtractor, newList)
  }

}
