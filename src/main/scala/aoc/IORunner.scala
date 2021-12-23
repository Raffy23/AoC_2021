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

  def task1: IO[Any]

  def task2: IO[Any]

  protected def streamInputLines(name: String, filterNonEmpty: Boolean = true): fs2.Stream[IO, String] = {
    val stream =
      Files[IO]
        .readAll(Path(s"./inputs/$name"))
        .through(text.utf8.decode)
        .through(text.lines)

    if (filterNonEmpty) stream.filter(_.nonEmpty)
    else                stream
  }

  protected def streamInputAsString(name: String): fs2.Stream[IO, String] =
    Files[IO]
      .readAll(Path(s"./inputs/$name"))
      .through(text.utf8.decode)

  protected def streamIntegers(name: String): fs2.Stream[IO, Int] =
    Files[IO]
      .readAll(Path(s"./inputs/$name"))
      .split(_ == ',')
      .map(chunk => String.valueOf(chunk.map(_.asInstanceOf[Char]).toArray).trim)
      .map(_.toInt)

  override def run(args: List[String]): IO[ExitCode] = for {
    startTime  <- IO.monotonic

    f1 <- task1.start
    f2 <- task2.start

    r1 <- f1.joinWithNever
    r2 <- f2.joinWithNever

    _ <- IO.println(s"Task1: $r1")
    _ <- IO.println(s"Task2: $r2")

    endTime <- IO.monotonic
    _       <- IO.println(s"Time needed: ${(endTime - startTime).toMillis}ms")

  } yield ExitCode.Success
}
