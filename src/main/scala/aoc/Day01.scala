package aoc

import cats.effect.{ExitCode, IO, IOApp}
import fs2.io.file.{Files, Path}
import fs2.text

/**
 * Created by
 * @author Raphael Ludwig
 * @version 01.12.21
 */
object Day01 extends IOApp {

  case class Counter(lastElement: Long, count: Int)
  object Counter {
    def empty: Counter = Counter(-1L, 0)
  }

  private def task1: IO[Unit] =
    readInput("./inputs/day01.task1")
      .fold(Counter.empty)(countBiggerElements)
      .evalTap(counter => IO.println(s"Task1: ${counter.count}"))
      .compile
      .drain

  private def task2: IO[Unit] =
    readInput("./inputs/day01.task1")
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
    Files[IO]
      .readAll(Path(input))
      .through(text.utf8.decode)
      .through(text.lines)
      .filter(_.nonEmpty)
      .map(_.toLong)

  override def run(args: List[String]): IO[ExitCode] = for {
    startTime  <- IO.monotonic

    f1 <- task1.start
    f2 <- task2.start

    _ <- f1.joinWithNever
    _ <- f2.joinWithNever

    endTime <- IO.monotonic
    _       <- IO.println(s"Time needed: ${(endTime - startTime).toMillis}ms")

  } yield ExitCode.Success

}
