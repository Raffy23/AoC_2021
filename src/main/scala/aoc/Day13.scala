package aoc
import cats.effect.IO
import fs2.Chunk

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 13.12.21
 */
object Day13 extends IORunner {

  private val UP   = 'y'
  private val LEFT = 'x'

  case class Point(x: Int, y: Int)
  case class FoldInstr(direction: Char, index: Int)

  override def task1: IO[Int] =
    streamInputLines("day13.task1", filterNonEmpty = false)
      .toPointsAndFoldInstr
      .map { case (points, instr) =>
        instr.take(1).foldLeft(points)(foldPaper)
      }
      .map(_
        .points
        .size
      )

  override def task2: IO[String] =
    streamInputLines("day13.task1", filterNonEmpty = false)
      .toPointsAndFoldInstr
      .map { case (points, instr) =>
        instr.foldLeft(points)(foldPaper)
      }
      .map("\n" ++ _.toString())

  @inline
  private def foldPaper(paper: Paper, inst: FoldInstr): Paper = paper.fold(inst)

  case class Paper(points: Set[Point]) {

    // Can only fold the paper if the fold is in the middle
    def fold(inst: FoldInstr): Paper = inst match {
      case FoldInstr(LEFT, index) =>
        val max = 2*index
        Paper(
          points.map {
            case Point(x, y) if x > index => Point(max - x, y)
            case Point(x, y) if x < index => Point(x      , y)
          }
        )

      case FoldInstr(UP, index) =>
        val max = 2*index
        Paper(
          points.map {
            case Point(x, y) if y > index => Point(x, max - y)
            case Point(x, y) if y < index => Point(x, y      )
          }
        )
    }

    override def toString: String = {
      val maxX = points.maxBy(_.x).x
      val maxY = points.maxBy(_.y).y
      val sb   = new StringBuilder

      (0 to maxY).foldLeft(sb) { case (sb, y) =>
        (0 to maxX).foldLeft(sb) { case (sb, x) =>
          if (points.contains(Point(x,y))) sb.append('█')
          else                             sb.append('.')
        }
          .append('\n')
      }

      sb.toString()
    }
  }

  implicit class PointAndFoldInstrStream(private val stream: fs2.Stream[IO, String]) extends AnyVal {
    def toPointsAndFoldInstr: IO[(Paper, Chunk[FoldInstr])] =
      stream
        .groupAdjacentBy(_.nonEmpty)
        .compile
        .toVector
        .map { data =>
          val (_, pointsInput)  = data(0)
          val (_, foldingInput) = data(2)

          val pointRegex     = """(\d+),(\d+)""".r
          val foldInstrRegex = """fold along ([xy])=(\d+)""".r

          val points = pointsInput.map {
            case pointRegex(x, y) => Point(x.toInt, y.toInt)
            case string           => throw new RuntimeException(s"'$string' does not match point regex!")
          }.toList
           .toSet

          val foldInstrs = foldingInput.map {
            case foldInstrRegex(direction, index) => FoldInstr(direction(0), index.toInt)
            case string                           => throw new RuntimeException(s"'$string' does not match folding instruction regex!")
          }

          (Paper(points), foldInstrs)
        }
  }

}