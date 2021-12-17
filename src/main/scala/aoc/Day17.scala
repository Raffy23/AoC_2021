package aoc
import cats.effect.IO

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 17.12.21
 */
object Day17 extends IORunner {

  case class Point(x: Int, y: Int)
  case class TargetArea(leftUp: Point, rightDown: Point) {
    def contains(point: Point): Boolean =
      point.x >= leftUp.x && point.x <= rightDown.x && point.y <= leftUp.y && point.y >= rightDown.y

    def hasMissed(point: Point): Boolean =
      point.x > rightDown.x || point.y < rightDown.y
  }

  object TargetArea {
    private val regex = """target area: x=(-?\d+)\.\.(-?\d+), y=(-?\d+)\.\.(-?\d+)""".r

    def apply(input: String): TargetArea = input match {
      case regex(x1, x2, y1, y2) => TargetArea(Point(x1.toInt, y2.toInt), Point(x2.toInt, y1.toInt))
      case string                => throw new RuntimeException(s"'$string' does not match target area regex!")
    }
  }

  override def task1: IO[Int] =
    streamInputLines("day17.task1")
      .compile
      .lastOrError
      .map(input =>
        enumerateThrows(Point(0,0), TargetArea(input), (0, 0), (100, 100))
          .map(_.maxBy(_.y).y)
          .max
      )

  override def task2: IO[Int] =
    streamInputLines("day17.task1")
      .compile
      .lastOrError
      .map(input =>
        enumerateThrows(Point(0,0), TargetArea(input), (0, -100), (500, 500))
          .size
      )

  private def enumerateThrows(start: Point, tA: TargetArea, from: (Int, Int), to: (Int, Int)): Iterable[Seq[Point]] =
    (from._1 to to._1).flatMap { x =>
      (from._2 to to._2).map { y =>
        simulateThrow(start, (x,y))
          .takeWhile(p => !tA.hasMissed(p))
          .toList
      }.filter(list => list.nonEmpty && tA.contains(list.last))
    }

  private def simulateThrow(start: Point, velocity: (Int, Int)): Iterator[Point] =
    Iterator.unfold((start, velocity)) { case (Point(x,y), (vX, vY)) =>
      val nextVX = vX match {
        case vX if vX > 0 => vX - 1
        case vX if vX < 0 => vX + 1
        case 0            => 0
      }

      val nextVY = vY - 1
      val nextP  = Point(x + vX, y + vY)

      Some(nextP, (nextP, (nextVX, nextVY)))
    }

  private def convertThrowToGraph(start: Point, target: TargetArea, points: List[Point]): String = {
    val p = (start :: target.leftUp :: target.rightDown :: points).toSet
    val minX = p.minBy(_.x).x
    val minY = p.minBy(_.y).y
    val maxX = p.maxBy(_.x).x
    val maxY = p.maxBy(_.y).y

    val throwPoints = points.toSet

    val sb = new StringBuilder
    (minY to maxY).reverse.foldLeft(sb) { case (sb, y) =>
      (minX to maxX).foldLeft(sb) { case (sb, x) =>
        val point = Point(x,y)

        if (target.contains(point) && !throwPoints.contains(point)) sb.append('T')
        else if (throwPoints.contains(point))                       sb.append('#')
        else if (point == start)                                    sb.append('S')
        else                                                        sb.append('.')
      }.append('\n')
    }.toString
  }

}
