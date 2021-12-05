package aoc
import cats.effect.IO

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 05.12.21
 */
object Day05 extends IORunner {

  case class Point(x: Int, y: Int)
  case class Line(start: Point, end: Point) {
    def points: fs2.Stream[IO, Point] = fs2.Stream.fromIterator[IO](start to end, 64)
    def isHorizontalOrVertical: Boolean = start.x == end.x || start.y == end.y
  }

  object Line {
    private val lineRegex = """(\d+),(\d+) -> (\d+),(\d+)""".r

    def apply(line: String): Line = line match {
      case lineRegex(sX, sY, eX, eY) => new Line(Point(sX.toInt, sY.toInt), Point(eX.toInt, eY.toInt))
      case _                         => throw new RuntimeException(s"'$line' does not match regex!")
    }
  }

  override def task2: IO[Unit] = {
    streamInputLines("day05.task1")
      .map(Line.apply)
      .groupByIntersections
      .flatMap(c => IO.println(s"Task2: $c"))
  }

  override def task1: IO[Unit] =
    streamInputLines("day05.task1")
      .map(Line.apply)
      .filter(_.isHorizontalOrVertical)
      .groupByIntersections
      .flatMap(c => IO.println(s"Task1: $c"))

  implicit class PointIterator(private val start: Point) extends AnyVal {

    def to(end: Point): Iterator[Point] = Iterator.single(start) ++ Iterator.unfold(start) { current =>
      val deltaX = end.x - current.x
      val deltaY = end.y - current.y
      val moveX  = if (deltaX != 0) math.signum(deltaX) else 0
      val moveY  = if (deltaY != 0) math.signum(deltaY) else 0

      if (moveX == 0 && moveY == 0) None
      else {
        val next = Point(current.x + moveX, current.y + moveY)
        Some((next, next))
      }
    }

  }

  implicit class LineStream(private val stream: fs2.Stream[IO, Line]) extends AnyVal {

    def groupByIntersections: IO[Int] =
      stream.flatMap(_.points)
        .compile
        .toList
        .map(_
          .groupMapReduce(identity)(_ => 1)(_ + _)
          .count(_._2 >= 2)
        )

  }

}
