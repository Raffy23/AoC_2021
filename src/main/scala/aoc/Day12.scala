package aoc

import cats.effect.IO
import cats.effect.unsafe.implicits.global

import scala.annotation.tailrec

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 12.12.21
 */
object Day12 extends IORunner {

  private val edgeRegex = """([A-Za-z]+)-([A-Za-z]+)""".r

  override def task1: IO[Int] =
    streamInputLines("day12.task1")
      .toGraph
      .map { graph =>
        @inline def discover(node: String, visited: Set[String], path: List[String]): List[List[String]] = {
          if (visited.contains(node)) return List.empty
          if (node == "end")          return List(node :: path)

          val neighbours = graph(node)
          val newVisited = if (node.isLowerCase) visited.incl(node)
                           else                  visited

          neighbours.flatMap(child =>
            discover(child, newVisited, node :: path)
          )
        }

        discover("start", Set.empty, List.empty)
      }
      .map(_.size)

  override def task2: IO[Any] =
    streamInputLines("day12.task1")
      .toGraph
      .map { graph =>
        @inline def discover(node: String, visited: Set[String], path: List[String], twice: Option[String] = None): List[List[String]] = {
          if (visited.contains(node)) return List.empty
          if (node == "end")          return List(node :: path)

          val neighbours = graph(node)
          val newVisited = if (node.isLowerCase) visited.incl(node)
                           else                  visited

          val withoutTwice = neighbours.flatMap(child => discover(child, newVisited, node :: path, twice))
          val withTwice =
            if (twice.isEmpty && node.isLowerCase && node != "start" && node != "end")
              neighbours.flatMap(child => discover(child, visited, node :: path, Some(node)))
            else
              List.empty

          withTwice ::: withoutTwice
        }

        discover("start", Set.empty, List.empty)
      }
      .map(_.distinct.size)

  implicit class GraphStream(private val stream: fs2.Stream[IO, String]) extends AnyVal {
    def toGraph: IO[Map[String, List[String]]] = {
      stream
        .map {
          case edgeRegex(a, b) => List(a -> b, b -> a)
          case string          => throw new RuntimeException(s"'$string' does not match regex!")
        }
        .compile
        .toList
        .map { list => list.flatten.groupMap(_._1)(_._2) }
        .map { originalGraph => originalGraph + ("end" -> List.empty) }
    }
  }

  implicit class NodeName(private val string: String) extends AnyVal {
    def isUpperCase: Boolean = string.forall(_.isUpper)
    def isLowerCase: Boolean = string.forall(_.isLower)
  }

}
