package aoc

import cats.effect.{ExitCode, IO, IOApp}
import fs2.io.file.{Files, Path}
import fs2.text

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 02.12.21
 */
trait IORunner extends IOApp {

  protected def task1: IO[Unit] = IO.println("Task1: Not implemented")
  protected def task2: IO[Unit] = IO.println("Task2: Not implemented")

  protected def streamInputLines(name: String): fs2.Stream[IO, String] =
    Files[IO]
      .readAll(Path(s"./inputs/$name"))
      .through(text.utf8.decode)
      .through(text.lines)
      .filter(_.nonEmpty)

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
