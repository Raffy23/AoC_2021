package aoc
import cats.effect.IO

import scala.annotation.tailrec

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 25.12.21
 */
object Day25 extends IORunner {

  type Sea = Map[(Long, Int), SeaCucumber]

  override def task1: IO[Any] =
    streamInputLines("day25.task1")
      .zipWithIndex
      .map { case (line, row) =>
        (line.length, line.zipWithIndex.flatMap { case (c, col) => SeaCucumber(c).map((row, col) -> _) })
      }
      .compile
      .toVector
      .map { rowData =>
        val rows = rowData.size
        val cols = rowData.head._1
        val env  = rowData.flatMap(_._2)

        @tailrec def moveAllUntilNothingChanges(round: Long, sea: Sea): Long = {

          @inline def moveAll(toMove: Sea, view: Sea): Sea = {
            @inline def moveSeaCucumber(row: Long, col: Int, c: SeaCucumber): (Long, Int) = c match {
              case RightSeaCucumber => (row           , (col+1) % cols)
              case DownSeaCucumber  => ((row+1) % rows, col           )
            }

            toMove.map {
              case original @ ((row, col), cucumber) =>
                val targetLocation = moveSeaCucumber(row, col, cucumber)
                if (view.contains(targetLocation)) original
                else                               targetLocation -> cucumber
            }
          }

          val afterRightSea = moveAll(sea.filter(_._2 == RightSeaCucumber), sea)
          val downMoving   = sea.filter(_._2 == DownSeaCucumber )
          val afterDownSea = moveAll(downMoving, afterRightSea ++ downMoving)
          val result = afterRightSea ++ afterDownSea
          val overlap = afterRightSea.keySet.intersect(afterDownSea.keySet)

          if (result == sea) round
          else moveAllUntilNothingChanges(round + 1, result)
        }

        moveAllUntilNothingChanges(1, env.toMap)
      }

  override def task2: IO[Any] = IO.unit

  private def printSeaToString(rows: Int, cols: Int, sea: Sea): String = {
    val sb = new StringBuilder()
    val stringSea = sea.view.mapValues(_.toString)

    (0 until rows).foldLeft(sb) { case (sb, row) =>
      (0 until cols).foldLeft(sb) { case (sb, col) =>
        sb.append(stringSea.getOrElse((row, col), "."))
      }.append("\n")
    }.toString()
  }

  trait SeaCucumber
  object SeaCucumber {
    def apply(c: Char): Option[SeaCucumber] = c match {
      case '.' => None
      case '>' => Some(RightSeaCucumber)
      case 'v' => Some(DownSeaCucumber)
      case  c  => throw new RuntimeException(s"'$c' unknown type of sea cucumber!")
    }
  }
  case object RightSeaCucumber extends SeaCucumber { override def toString: String = ">" }
  case object DownSeaCucumber  extends SeaCucumber { override def toString: String = "v" }

}
