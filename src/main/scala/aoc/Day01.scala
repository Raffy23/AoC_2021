package aoc

import cats.effect.IO

/**
 * Created by
 * @author Raphael Ludwig
 * @version 01.12.21
 */
object Day01 extends IORunner {

  case class Counter(lastElement: Long, count: Int)
  object Counter {
    def empty: Counter = Counter(-1L, 0)
  }

  override protected  def task1: IO[Unit] =
    readInput("day01.task1")
      .fold(Counter.empty)(countBiggerElements)
      .evalTap(counter => IO.println(s"Task1: ${counter.count}"))
      .compile
      .drain

  override protected def task2: IO[Unit] =
    readInput("day01.task1")
      .sliding(3, 1)
      .map(_.foldLeft(0L)(_ + _))
      .fold(Counter.empty)(countBiggerElements)
      .evalTap(counter => IO.println(s"Task2: ${counter.count}"))
      .compile
      .drain

  private def countBiggerElements(counter: Counter, element: Long): Counter = {
    if (counter.lastElement == -1)          Counter(element, 0)
    else if (element > counter.lastElement) Counter(element, counter.count + 1)
    else                                    Counter(element, counter.count)
  }

  private def readInput(input: String): fs2.Stream[IO, Long] =
    streamInputLines(input).map(_.toLong)

}
