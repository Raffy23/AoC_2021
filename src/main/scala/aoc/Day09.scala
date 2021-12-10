package aoc
import cats.effect.IO

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 09.12.21
 */
object Day09 extends IORunner {

  case class Matrix(m: IndexedSeq[Array[Int]]) {

    def isLowPoint(x: Int, y: Int): Boolean = {
      val currentValue = m(x)(y)
      if (currentValue == 9)
        return false

      val r = m(x)
      val up    = if (x-1 > -1      ) m(x-1)(y  ) > currentValue else true
      val down  = if (x+1 < m.size  ) m(x+1)(y  ) > currentValue else true
      val left  = if (y-1 > -1      ) m(x  )(y-1) > currentValue else true
      val right = if (y+1 < r.length) m(x  )(y+1) > currentValue else true

      up & down & left & right
    }

    def lowPoints: Seq[(Int, Int, Int)] =
      m.zipWithIndex.flatMap { case (rows, x) =>
        rows.zipWithIndex.map { case (cell, y) =>
          (x, y, cell, isLowPoint(x, y))
        }
      }.filter(_._4)
       .map { case (x, y, cell, _) => (x,y,cell) }

    def basinOf(x: Int, y: Int): List[((Int, Int), Int)] = {
      var marked = Set((x,y))
      var queue  = List((x,y))
      var result = List.empty[((Int, Int), Int)]

      while (queue.nonEmpty) {
        val element = queue.head
        queue  = queue.tail

        val x = element._1
        val y = element._2
        val value = m(x)(y)

        if (value < 9) {
          result = (element, value) :: result

          if (x-1 > -1          && !marked.contains((x-1, y))) { queue = (x-1, y) :: queue; marked = marked.incl((x-1, y)) }
          if (x+1 < m.size      && !marked.contains((x+1, y))) { queue = (x+1, y) :: queue; marked = marked.incl((x+1, y)) }
          if (y-1 > -1          && !marked.contains((x, y-1))) { queue = (x, y-1) :: queue; marked = marked.incl((x, y-1)) }
          if (y+1 < m(x).length && !marked.contains((x, y+1))) { queue = (x, y+1) :: queue; marked = marked.incl((x, y+1)) }
        }
      }

      result
    }

    override def toString: String =
      m.map(_
        .map(_.toString)
        .mkString
      ).mkString("\n")

  }

  override def task1: IO[Int] =
    readInputMatrix
      .map(_.lowPoints.map(_._3 + 1).sum)

  override def task2: IO[Int] =
    readInputMatrix.map(matrix =>
      matrix
        .lowPoints
        .map { case (x,y, _) => matrix.basinOf(x,y).size }
        .sorted
        .takeRight(3)
        .product
    )

  private def readInputMatrix: IO[Matrix] =
    streamInputLines("day09.task1")
      .map(_.toArray.map(c => (c - '0').toInt))
      .compile
      .toVector
      .map(Matrix)

}
