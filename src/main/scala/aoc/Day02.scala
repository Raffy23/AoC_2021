package aoc

import aoc.Day02.Direction.Direction
import cats.effect.IO

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 02.12.21
 */
object Day02 extends IORunner {

  private val inputPattern = """(\w+) (\d+)""".r

  object Direction extends Enumeration {
    type Direction = Value
    val forward, down, up = Value
  }

  case class Input(direction: Direction, value: Int)

  case class Position(horizontal: Int, depth: Int, aim: Int = 0) { def println: Position = { System.out.println(this); this } }
  object Position {
    def start: Position = Position(0, 0)
  }

  override protected  def task1: IO[Unit] =
    streamPositions("day02.task1")
      .fold(Position.start) {
        case (position, Input(Direction.forward, value)) => Position(position.horizontal + value, position.depth        )
        case (position, Input(Direction.down   , value)) => Position(position.horizontal        , position.depth + value)
        case (position, Input(Direction.up     , value)) => Position(position.horizontal        , position.depth - value)
      }
      .map(position => position.horizontal * position.depth)
      .evalTap(v => IO.println(s"Task1: $v"))
      .compile
      .drain

  override protected  def task2: IO[Unit] =
    streamPositions("day02.task1")
      .fold(Position.start) {
        case (position, Input(Direction.forward, value)) => Position(position.horizontal + value, position.depth + position.aim * value, position.aim)
        case (position, Input(Direction.down   , value)) => Position(position.horizontal        , position.depth                       , position.aim + value)
        case (position, Input(Direction.up     , value)) => Position(position.horizontal        , position.depth                       , position.aim - value)
      }
      .map(position => position.horizontal * position.depth)
      .evalTap(v => IO.println(s"Task2: $v"))
      .compile
      .drain


  private def streamPositions(file: String): fs2.Stream[IO, Input] =
    streamInputLines(file)
      .map {
        case inputPattern(direction, value) => Input(Direction.withName(direction), value.toInt)
        case l                              => throw new RuntimeException(s"Line $l does not match regex pattern")
      }

}
