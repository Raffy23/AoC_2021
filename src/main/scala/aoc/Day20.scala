package aoc
import cats.effect.IO
import fs2.Chunk
import scodec.bits.BitVector

import scala.collection.immutable.TreeMap

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 20.12.21
 */
object Day20 extends IORunner {

  override def task1: IO[Int] =
    streamInputLines("day20.task1", filterNonEmpty = false)
      .toTrenchMap
      .map(_
        .enhance(2)
        .image
        .values
        .count(identity)
      )

  override def task2: IO[Any] =
    streamInputLines("day20.task1", filterNonEmpty = false)
      .toTrenchMap
      .map(_
        .enhance(50)
        .image
        .values
        .count(identity)
      )

  case class TrenchMap(image: TreeMap[(Int, Int), Boolean], enhancementVec: BitVector) {

    def enhance(lit: Boolean = false): TrenchMap = {

      @inline def bitIndex(row: Int, col: Int): Int = {
        List(
          image.getOrElse((row - 1, col - 1), lit),
          image.getOrElse((row - 1, col    ), lit),
          image.getOrElse((row - 1, col + 1), lit),
          image.getOrElse((row    , col - 1), lit),
          image.getOrElse((row    , col    ), lit),
          image.getOrElse((row    , col + 1), lit),
          image.getOrElse((row + 1, col - 1), lit),
          image.getOrElse((row + 1, col    ), lit),
          image.getOrElse((row + 1, col + 1), lit),
        ).foldLeft(0) {
          case (number, bit) if bit => number << 1 | 1
          case (number, _)          => number << 1
        }
      }

      val (minRows, minCols) = image.keySet.min
      val (maxRows, maxCols) = image.keySet.max
      val overscan = 1

      TrenchMap(
        TreeMap.from[(Int, Int), Boolean](((minRows - overscan) to (maxRows + overscan)).flatMap(row =>
          ((minCols - overscan) to (maxCols + overscan)).map(col =>
            (row, col) -> enhancementVec(bitIndex(row, col))
          )
        )),
        enhancementVec
      )
    }

    def enhance(times: Int): TrenchMap = {
      if (enhancementVec(0)) (0 until times).foldLeft(this) { case (map, idx) => map.enhance(idx % 2 == 1) }
      else                   (0 until times).foldLeft(this) { case (map, idx) => map.enhance() }
    }

    override def toString: String = {
      val sb = new StringBuilder

      val maxWidth = image.keySet.max._2
      image.foldLeft(sb) { case (sb, ((x, y), pixel)) =>
        sb.append(
          if (pixel) '#' else '.'
        )

        if (y == maxWidth)
          sb.append('\n')

        sb
      }

      sb.toString()
    }

  }

  object TrenchMap {
    def apply(vector: Vector[(Boolean, Chunk[String])]): TrenchMap = {
      val imageData = vector(2)._2.toVector

      TrenchMap(
        TreeMap.from[(Int, Int), Boolean](
          imageData.zipWithIndex.flatMap { case (data, row) =>
            data.zipWithIndex.map { case (pixel, col) =>
              (row, col) -> (pixel == '#')
            }
          }
        ),
        BitVector.bits(vector(0)._2.head.get.map {
          case '.' => false
          case '#' => true
        })
      )
    }
  }

  implicit class TrenchMapStream(private val stream: fs2.Stream[IO, String]) extends AnyVal {
    def toTrenchMap: IO[TrenchMap] =
      stream
        .groupAdjacentBy(_.nonEmpty)
        .compile
        .toVector
        .map(TrenchMap(_))
  }


}
