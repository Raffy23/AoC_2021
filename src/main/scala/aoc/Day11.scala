package aoc
import aoc.utils.MatrixStreamReader
import cats.effect.IO

import scala.annotation.tailrec

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 11.12.21
 */
object Day11 extends IORunner {

  override def task1: IO[Int] =
    streamInputLines("day11.task1")
      .toMatrix
      .map(matrix => new OctopusField(matrix.m.map(_.toIndexedSeq)))
      .map { initialField =>
        (1 to 100).foldLeft((0, initialField)) { case ((counter, field), _) =>
          val step = field.step()
          (counter + step._1, step._2)
        }
      }
      .map(_._1)

  override def task2: IO[Int] =
    streamInputLines("day11.task1")
      .toMatrix
      .map(matrix => new OctopusField(matrix.m.map(_.toIndexedSeq)))
      .map { initialField =>
        val size = initialField.size

        @tailrec @inline def iterate(iteration: Int, field: OctopusField): Int = {
          val (counter, result) =  field.step()

          if (counter == size) iteration
          else                 iterate(iteration + 1, result)
        }

        iterate(1, initialField)
      }

  class OctopusField(private val m: IndexedSeq[IndexedSeq[Int]]) {

    def neighbors(x: Int, y: Int): List[(Int, Int)] = {
      var result = List.empty[(Int, Int)]

      val r = m(x)
      if (x-1 > -1      ) result = (x-1, y  ) :: result
      if (x+1 < m.size  ) result = (x+1, y  ) :: result
      if (y-1 > -1      ) result = (x  , y-1) :: result
      if (y+1 < r.length) result = (x  , y+1) :: result

      if (x-1 > -1     && y-1 > -1      ) result = (x-1, y-1) :: result
      if (x+1 < m.size && y-1 > -1      ) result = (x+1, y-1) :: result
      if (x-1 > -1     && y+1 < r.length) result = (x-1, y+1) :: result
      if (x+1 < m.size && y+1 < r.length) result = (x+1, y+1) :: result

      result
    }

    def step(): (Int, OctopusField) = {

      @tailrec @inline def propagateFlashes(field: IndexedSeq[IndexedSeq[Int]], flashed: Set[(Int, Int)]): IndexedSeq[IndexedSeq[Int]] = {

        // Check which octopus have reached flash level and note modifications
        val modifications = field.zipWithIndex.foldLeft(List.empty[(Int, Int)]) { case (result, (row, rowIndex)) =>
          row.zipWithIndex.foldLeft(List.empty[(Int, Int)]) { case (result, (cell, columnIndex)) =>
            val notFlashed = !flashed.contains((rowIndex, columnIndex))

            if (cell > 9 && notFlashed) neighbors(rowIndex, columnIndex) ::: result
            else                        result
          } ::: result
        }

        if (modifications.isEmpty)
          return field

        // Apply modifications to matrix
        var result = field
        for ( (x,y) <- modifications) {
          val r = result(x)
          result = result.updated(x, r.updated(y, r(y) + 1))
        }

        // Check which octopus are going to flash in this iterations
        val shouldFlash = field.zipWithIndex.flatMap { case (row, rowIndex) =>
          row.zipWithIndex.map { case (cell, colIndex) =>
            if (cell > 9) Some((rowIndex, colIndex))
            else None
          }
        }.flatten

        propagateFlashes(result, flashed ++ shouldFlash)
      }

      val updated = propagateFlashes(m.map(_.map(_ + 1)), Set.empty).map(_.map {
        case num if num > 9 => 0
        case num            => num
      })

      val numOfFlashes = updated.flatten.foldLeft(0) {
        case (counter, 0) => counter + 1
        case (counter, _) => counter
      }

      (numOfFlashes, new OctopusField(updated))
    }

    def size: Int = m.flatten.size

  }

}
