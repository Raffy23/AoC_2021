package aoc

import cats.effect.IO

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 11.12.21
 */
package object utils {

  case class Matrix(m: IndexedSeq[Array[Int]]) {

    override def toString: String =
      m.map(_
        .map(_.toString)
        .mkString
      ).mkString("\n")

  }

  implicit class MatrixStreamReader(private val stream: fs2.Stream[IO, String]) extends AnyVal {

    def toMatrix: IO[Matrix] =
      stream
        .map(_.toArray.map(c => (c - '0').toInt))
        .compile
        .toVector
        .map(Matrix)

  }

}
