package aoc
import cats.effect.IO

import scala.annotation.tailrec

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 04.12.21
 */
object Day04 extends IORunner {

  private final val BOARD_SIZE = 5

  case class BingBoard(id: Int, numbers: Map[Int, (Int, Int)], freeRow: IndexedSeq[Int], freeColumn: IndexedSeq[Int]) {

    def pickNumber(num: Int): BingBoard = {
      numbers.get(num).map { case (row, column) =>
        new BingBoard(
          id,
          this.numbers - num,
          freeRow.updated(row, freeRow(row) - 1),
          freeColumn.updated(column, freeColumn(column) - 1)
        )
      }.getOrElse(this)
    }

    def isWinning: Boolean = freeRow.contains(0) || freeColumn.contains(0)

  }
  object BingBoard {

    def apply(id: Int, itr: Seq[String]): BingBoard = new BingBoard(
      id,
      itr.zipWithIndex.flatMap { case (line, rowIndex) =>
        line.split("\\s+").filter(_.nonEmpty).map(_.toInt).zipWithIndex.map { case (value, columnIndex) =>
          value -> (rowIndex, columnIndex)
        }
      }.toMap,
      Array.fill(BOARD_SIZE)(BOARD_SIZE),
      Array.fill(BOARD_SIZE)(BOARD_SIZE)
    )

  }

  private val data = (for {
    input   <- streamInputLines("day04.task1").compile.toList

    numbers <- IO { input.head.split(",").map(_.toInt) }
    boards  <- IO { input.drop(1).grouped(BOARD_SIZE).zipWithIndex.map {
        case (data, index) => BingBoard(index, data)
      }.toSeq
    }
  } yield (numbers, boards)).memoize

  override def task1: IO[Unit] = for {
    input  <- data.flatMap(io => io)
    winner <- IO { computeWinner(input._1.toList, input._2) }
    score  <- IO { winner._2.numbers.keys.sum * winner._1 }

    _ <- IO.println(s"Task1: $score")
  } yield ()

  override def task2: IO[Unit] = for {
    input  <- data.flatMap(io => io)
    winner <- IO { computeWinnerSequence(input._1.toList, input._2, List.empty) }
    score  <- IO {
      val (num, winnerBoard) = winner.head
      winnerBoard.numbers.keys.sum * num
    }

    _ <- IO.println(s"Task2: $score")
  } yield ()


  @tailrec
  def computeWinner(numbers: List[Int], boards: Seq[BingBoard]): (Int, BingBoard) = {
    val currentNumber = numbers.head
    val newBords = boards.map(_.pickNumber(currentNumber))
    val winner = newBords.find(_.isWinning)

    if (winner.isDefined) (currentNumber, winner.get)
    else                  computeWinner(numbers.tail, newBords)
  }

  @tailrec
  def computeWinnerSequence(numbers: List[Int], boards: Seq[BingBoard], winners: List[(Int, BingBoard)]): List[(Int, BingBoard)] = {
    val currentNumber = numbers.head
    val newBords = boards.map(_.pickNumber(currentNumber))
    val winner = newBords.filter(_.isWinning)

    if (newBords.isEmpty || numbers.tail.isEmpty)
      return winners

    if (winner.nonEmpty) computeWinnerSequence(numbers.tail, newBords.filterNot(_.isWinning), winner.map(w => (currentNumber, w)).toList ::: winners)
    else                 computeWinnerSequence(numbers.tail, newBords, winners)
  }

}
