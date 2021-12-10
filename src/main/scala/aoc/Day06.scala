package aoc

import cats.effect.IO
import fs2.io.file.{Files, Path}
import fs2.text

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 06.12.21
 */
object Day06 extends IORunner  {

  private val REPROD_CYCLE = 6
  private val YOUNG_CYCLE  = 8
  private val DAYS = 256 //80

  private def calculateReproduction(file: String, days: Int): IO[Long] =
    Files[IO]
      .readAll(Path(file))
      .through(text.utf8.decode)
      .filter(_.nonEmpty)
      .map("""\d+""".r.findAllIn)
      .flatMap(x => fs2.Stream.fromIterator[IO](x, 32))
      .map(_.toInt)
      .compile
      .toList
      .map(_.groupMapReduce(identity)(_ => 1L)(_ + _))
      .map((0 until days).foldLeft(_) { case (map, _) =>
        map
          .map[List[(Int, Long)]] {
            case (0   , count) => List(REPROD_CYCLE -> count, YOUNG_CYCLE -> count)
            case (days, count) => List(days-1 -> count)
          }
          .flatten
          .groupMapReduce(_._1)(_._2)(_ + _)
      })
      .map(_.values.sum)

  override def task1: IO[Long] =
    calculateReproduction("./inputs/day06.task1", 80)

  override def task2: IO[Long] =
    calculateReproduction("./inputs/day06.task1", 256)

}


