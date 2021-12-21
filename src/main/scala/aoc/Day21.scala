package aoc
import cats.effect.IO

import scala.collection.mutable
import Day18.LastIterator

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 21.12.21
 */
object Day21 extends IORunner {

  case class State(dice: Iterator[Int], player1: Player, player2: Player) {
    def scoreOfLosing: Long = {
      val diceRolls = dice.next() - 1
      if (player1.isFinished) player2.score * diceRolls
      else                    player1.score * diceRolls
    }
  }

  override def task1: IO[Any] =
    streamInputLines("day21.task1")
      .map(Player(_))
      .compile
      .toList
      .map { case player1 :: player2 :: Nil =>
        Iterator.iterate(Option(State(Iterator.from(1), player1, player2))) {
          case Some(State(_, player1, player2)) if player1.isFinished || player2.isFinished =>
            Option.empty[State]

          case Some(State(dice, player1, player2)) =>
            @inline def roll: Iterator[Int] =
              dice.take(3).map(_ % 100)

            val p1AfterRoll = player1.advance(roll)
            val p2AfterRoll =
              if (p1AfterRoll.isFinished) player2
              else                        player2.advance(roll)

            Some(State(dice, p1AfterRoll, p2AfterRoll))
        }.lastDefined
         .scoreOfLosing
      }

  object QuantumState {
    val Player1   = 0
    val Player2   = 1
    val DiracDice = 3
  }
  case class QuantumState(qRolls: Int, rollSum: Int, player1: Player, player2: Player, currentlyRolling: Int) {
    import QuantumState._

    def advance: Either[QuantumState, (BigInt, BigInt)] = currentlyRolling match {
      case Player1 =>
        val pAfterRoll = player1.advance(rollSum)

        Either.cond(
          pAfterRoll.score >= 21,
          (BigInt(1), BigInt(0)),
          QuantumState(DiracDice, 0, pAfterRoll, player2, Player2)
        )

      case Player2 =>
        val pAfterRoll = player2.advance(rollSum)

        Either.cond(
          pAfterRoll.score >= 21,
          (BigInt(0), BigInt(1)),
          QuantumState(DiracDice, 0, player1, pAfterRoll, Player1)
        )
    }
  }

  override def task2: IO[Any] =
    streamInputLines("day21.task1")
      .map(Player(_))
      .compile
      .toList
      .map { case player1 :: player2 :: Nil =>

        import Day21.QuantumState._
        val quantumCache = new mutable.HashMap[QuantumState, (BigInt, BigInt)]

        @inline def playQuantumGame(state: QuantumState): (BigInt, BigInt) = {
          quantumCache.getOrElseUpdate(state, state match {
            case qState@QuantumState(0, _, _, _, _) =>
              qState.advance.fold(playQuantumGame, identity)

            case QuantumState(left, rolls, player1, player2, rollingPlayer) =>
              (1 to 3)
                .map(roll => playQuantumGame(
                  QuantumState(left - 1, rolls + roll, player1, player2, rollingPlayer)
                ))
                .reduceLeft[(BigInt, BigInt)] { case ((player1A, player2A), (player1B, player2B)) =>
                  (player1A + player1B, player2A + player2B)
                }
          })
        }

        val (p1, p2) = playQuantumGame(QuantumState(DiracDice, 0, player1, player2, Player1))
        p1.max(p2)
      }

  case class Player(id: Int, position: Int, score: Int) {

    def advance(roll: Int): Player = {
      var newPosition = (position + roll) % 10
      if (newPosition == 0)
        newPosition = 10

      Player(id, newPosition, score + newPosition)
    }

    def advance(rolls: Iterator[Int]): Player = {
      var newPosition = (position + rolls.sum) % 10
      if (newPosition == 0)
        newPosition = 10

      Player(id, newPosition, score + newPosition)
    }

    def isFinished: Boolean = score >= Player.FINISH_SCORE
  }

  object Player {
    private val regex = """Player (\d+) starting position: (\d+)""".r

    val FINISH_SCORE = 1_000

    def apply(line: String): Player = line match {
      case regex(id, pos) => new Player(id.toInt, pos.toInt, 0)
      case string         => throw new RuntimeException(s"'$string' does not match Player regex!")
    }
  }

}
