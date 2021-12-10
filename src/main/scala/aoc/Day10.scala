package aoc
import aoc.utils.Math
import cats.effect.IO

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 10.12.21
 */
object Day10 extends IORunner {

  private def matchParentheses(content: List[Char], errors: List[Char], correction: List[Char]): (List[Char], List[Char], List[Char]) = {

    @inline def matchClosing(closingP: Char): (List[Char], List[Char], List[Char]) = {
      val (newContent, newErrors, newCorrections) = matchParentheses(content.tail, errors, correction)
      newContent.headOption match {
        case Some(`closingP`) => matchParentheses(newContent.tail, newErrors, newCorrections)
        case Some (illegal  ) => matchParentheses(newContent.tail, illegal :: newErrors, newCorrections)
        case None             => (newContent, newErrors, closingP :: newCorrections)
      }
    }

    content.headOption match {
      case Some('(') => matchClosing(')')
      case Some('[') => matchClosing(']')
      case Some('{') => matchClosing('}')
      case Some('<') => matchClosing('>')
      case Some(')') => (content, errors, correction)
      case Some(']') => (content, errors, correction)
      case Some('}') => (content, errors, correction)
      case Some('>') => (content, errors, correction)
      case None      => (List.empty, errors, correction)
    }
  }

  override def task1: IO[Unit] =
    streamInputLines("day10.task1")
      .map(str => matchParentheses(str.toList, List.empty, List.empty))
      .filter(_._2.nonEmpty)
      .map { case (_, errors, _) => errors.map {
        case ')' => 3
        case ']' => 57
        case '}' => 1197
        case '>' => 25137
      }}
      .compile
      .toVector
      .map(_.flatten.sum)
      .flatMap(points => IO.println(s"Task1: $points"))

  override def task2: IO[Unit] =
    streamInputLines("day10.task1")
      .map(str => matchParentheses(str.toList, List.empty, List.empty))
      .filter(_._2.isEmpty)
      .map { case (_, _, corrections) =>
        corrections.foldRight(0L) {
          case (')', score) => score * 5 + 1
          case (']', score) => score * 5 + 2
          case ('}', score) => score * 5 + 3
          case ('>', score) => score * 5 + 4
        }
      }
      .compile
      .toVector
      .map(Math.median[Long])
      .flatMap(result => IO.println(s"Task2: ${result._2}"))

}
