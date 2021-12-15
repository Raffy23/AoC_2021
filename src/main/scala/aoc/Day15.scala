package aoc
import cats.effect.IO
import utils.{Matrix, MatrixStreamReader}

import scala.collection.mutable

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 15.12.21
 */
object Day15 extends IORunner {

  case class Point(x: Int, y: Int)
  case class Path(risk: Int, path: List[Point])

  override def task1: IO[Int] =
    streamInputLines("day15.task1")
      .toMatrix
      .map { matrix =>
        val path = dijkstra(matrix, Point(0,0), Point(matrix.m.size-1, matrix.m(0).length-1))
        path.risk
      }

  override def task2: IO[Int] =
    streamInputLines("day15.task1")
      .toMatrix
      .map(_.grow(5))
      .map { matrix =>
        val path = dijkstra(matrix, Point(0,0), Point(matrix.m.size-1, matrix.m(0).length-1))
        path.risk
      }

  case class PointRisk(risk: Int, point: Point)
  implicit object PointRiskOrdering extends Ordering[PointRisk] {
    override def compare(x: PointRisk, y: PointRisk): Int = y.risk compare x.risk
  }

  private def dijkstra(matrix: Matrix, start: Point, end: Point): Path = {
    val distance  = new mutable.HashMap[Point, Int]()
    val prioQueue = new mutable.PriorityQueue[PointRisk]()
    val prev      = new mutable.HashMap[Point, PointRisk]()

    matrix.neighbours(start).foreach { neighbour =>
      val risk = matrix(neighbour)

      distance.put(neighbour, risk)
      prev.put(neighbour, PointRisk(0, start))
      prioQueue.addOne(PointRisk(risk, neighbour))
    }

    while (prioQueue.nonEmpty) {
      val currentPoint = prioQueue.dequeue()
      val curPointRisk = distance(currentPoint.point)

      matrix.neighbours(currentPoint.point).foreach { neighbour =>
        val newRisk = curPointRisk + matrix(neighbour)
        val d = distance.get(neighbour)

        if (d.isEmpty || newRisk < d.get) {
          distance.put(neighbour, newRisk)
          prev.put(neighbour, currentPoint)
          prioQueue.addOne(PointRisk(newRisk, neighbour))
        }
      }
    }

    var endPoint = prev(end)
    var result   = List(end)
    while (endPoint.point != start) {
      result = endPoint.point :: result
      endPoint = prev(endPoint.point)
    }

    Path(prev(end).risk + matrix(end), start :: result)
  }

  implicit class RichMatrix(private val matrix: Matrix) extends AnyVal {

    def apply(point: Point): Int =
      matrix.m(point.x)(point.y)

    def neighbours(point: Point): List[Point] = {
      val m = matrix.m
      val r = matrix.m(0)

      var result = List.empty[Point]
      if (point.x-1 > -1      ) result = Point(point.x-1, point.y  ) :: result
      if (point.x+1 < m.size  ) result = Point(point.x+1, point.y  ) :: result
      if (point.y-1 > -1      ) result = Point(point.x  , point.y-1) :: result
      if (point.y+1 < r.length) result = Point(point.x  , point.y+1) :: result

      result
    }

    def grow(size: Int): Matrix = {
      val original = matrix.m
      val rows = original.size

      Matrix(
        (0 until (original.size * size)).map { index =>
          val rowAcc      = index / rows
          val originalRow = original(index % rows).view.map { value =>
            if ((value + rowAcc) >= 10) (value+rowAcc) - 9
            else value + rowAcc
          }

          (0 until size).flatMap { index =>
            originalRow.map { value =>
              if ((value + index) >= 10) (value+index) - 9
              else value + index
            }
          }.toArray
        }
      )

    }

    def toStringWithPath(path: List[Point]): String = {
      val sb = new StringBuilder

      matrix.m.zipWithIndex.foldLeft(sb) { case (sb, (row, x)) =>
        row.zipWithIndex.foldLeft(sb) { case (sb, (cell, y)) =>
          if (path.contains(Point(x, y))) sb.append(cell)
          else                            sb.append('.')
        }.append('\n')
      }.toString()
    }

  }

}
