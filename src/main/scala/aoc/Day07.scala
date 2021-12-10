package aoc
import cats.effect.IO
import aoc.utils.Math

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 07.12.21
 */
object Day07 extends IORunner {

  override def task1: IO[Int] =
   streamIntegers("day07.task1")
      .compile
      .toVector
      .map(Math.medianAndSortedList[Int])
      .map { case (list, median) =>
        list.foldLeft(0) { case (fuel, crab) => fuel + math.abs(median - crab) }
      }

  override def task2: IO[Int] =
    streamIntegers("day07.task1")
      .compile
      .toVector
      .map(Math.medianAndSortedList[Int])
      .map { case (list, median) =>
        // Search around the median for the solution, mean would be
        // better ... ? (This is a approximation, may not yield result!)
        val searchSpace = list.size / 4
        val min = math.max(list.min, median - searchSpace)
        val max = math.min(list.max, median + searchSpace)

        @inline def fuelBurned(target: Int, crab: Int): Int = {
          val n = math.abs(target - crab)
          (n * (n + 1)) / 2
        }

        val solutions = (min to max).map(position =>
          list.foldLeft(0) { case (fuel, crab) =>
            fuel + fuelBurned(position, crab)
          }
        )

        solutions.min
      }

}
