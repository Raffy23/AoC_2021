package aoc
import cats.effect.IO

import scala.collection.mutable

/**
 * Created by
 *
 * @author Raphael Ludwig
 * @version 23.12.21
 */
object Day23 extends IORunner {

  override def task1: IO[Any] =
    streamInputAsString("day23.task1")
      .compile
      .lastOrError
      .map(Environment(_))
      .map { _.moveAmphipodsToRooms(
        Map(
          Point2D(2,3) -> Amber , Point2D(3,3) -> Amber ,
          Point2D(2,5) -> Bronze, Point2D(3,5) -> Bronze,
          Point2D(2,7) -> Copper, Point2D(3,7) -> Copper,
          Point2D(2,9) -> Desert, Point2D(3,9) -> Desert,
        )
      )}

  override def task2: IO[Any] =
    streamInputAsString("day23.task2")
      .compile
      .lastOrError
      .map(Environment(_))
      .map { _.moveAmphipodsToRooms(
        Map(
          Point2D(2,3) -> Amber , Point2D(3,3) -> Amber , Point2D(4,3) -> Amber , Point2D(5,3) -> Amber ,
          Point2D(2,5) -> Bronze, Point2D(3,5) -> Bronze, Point2D(4,5) -> Bronze, Point2D(5,5) -> Bronze,
          Point2D(2,7) -> Copper, Point2D(3,7) -> Copper, Point2D(4,7) -> Copper, Point2D(5,7) -> Copper,
          Point2D(2,9) -> Desert, Point2D(3,9) -> Desert, Point2D(4,9) -> Desert, Point2D(5,9) -> Desert,
        )
      )}

  implicit object EnvironmentOrdering extends Ordering[(Environment, Int)] {
    override def compare(x: (Environment, Int), y: (Environment, Int)): Int = y._2 compare x._2
  }

  case class Point2D(y: Int, x: Int)
  case class Environment(amphipods: Map[Point2D, Amphipod]) {
    import Environment.{hallway, hallwayY}

    def enumeratePossibleMovements(goal: Map[Point2D, Amphipod]): Iterator[(Environment, Int)] = {
      val amphipodGoals = goal.groupMap(_._2  )(_._1)
      val amphipodRooms = goal.groupMap(_._1.x)(identity).view.mapValues(_.toMap)

      @inline def walk(from: Int, direction: Int): Iterator[Int] = Iterator.unfold(from + direction) {
        case x if x >= 12                                  => Option.empty
        case x if x <= 0                                   => Option.empty
        case x if amphipods.contains(Point2D(hallwayY, x)) => Option.empty
        case x                                             => Some(x, x + direction)
      }

      @inline def isRoomCorrect(point: Point2D): Boolean =
        amphipodRooms(point.x)
          .forall { case (target, pod) => amphipods.get(target).forall(_ == pod) }

      val moveAmphipodToHallway: (Point2D, Amphipod) => Set[(Point2D, Int)] = { case (pos@Point2D(y, x), pod) =>
        if (isRoomCorrect(pos)) Set.empty
        else if (((hallwayY + 1) until y).exists(y => amphipods.contains(Point2D(y, x)))) Set.empty
        else {
          val baseCost = y - hallwayY
          val left  = walk(x, direction = -1)
          val right = walk(x, direction =  1)

          (left ++ right)
            .map(xH => (Point2D(hallwayY, xH), baseCost + (x-xH).abs))
            .filter(hallway contains _._1)
            .to(Set)
        }
      }

      val moveAmphipodToRoom: (Point2D, Amphipod) => Set[(Point2D, Int)] = { case (pos@Point2D(y, x), pod) =>
        if (goal.get(pos).contains(pod)) Set.empty
        else {
          amphipodGoals(pod)
            .flatMap { point =>
              val direction = (point.x - x).sign
              val baseCost = point.y - hallwayY
              val points = walk(x, direction)
                .map(xH => (Point2D(hallwayY, xH), baseCost + (x-xH).abs))
                .toSet

              val isWayFree     = points.exists(_._1 == Point2D(hallwayY, point.x))
              val isRoomBlocked = (hallwayY to point.y).map(y => Point2D(y, point.x)).forall(!amphipods.contains(_))
              val isRestCorrect = {
                amphipodRooms(point.x)
                  .filter(_._1.y > point.y)
                  .forall { case (target, pod) => amphipods.get(target).contains(pod) }
              }

              if (!isWayFree || !isRoomBlocked || !isRestCorrect) {
                Set.empty
              } else {
                Set((point, baseCost + (point.x - x).abs))
              }
            }
            .toSet
        }
      }

      amphipods
        .iterator
        .map {
          case (position, amphipod) if hallway.contains(position) => (position, amphipod, moveAmphipodToRoom   )
          case (position, amphipod)                               => (position, amphipod, moveAmphipodToHallway)
        }
        .flatMap { case (position, amphipod, movement) =>
            movement(position, amphipod)
              .map { case (targetPos, steps) =>
                (
                  Environment(amphipods - position + (targetPos -> amphipod)),
                  amphipod.movementCost(steps)
                )
              }
        }
    }

    // Dijkstra
    def moveAmphipodsToRooms(goal: Map[Point2D, Amphipod]): Int = {
      val distance  = new mutable.HashMap[Environment, Int]()
      val prioQueue = new mutable.PriorityQueue[(Environment, Int)]()
      val prev      = new mutable.HashMap[Environment, Environment]()

      enumeratePossibleMovements(goal).foreach { case (neighbour, dist) =>
        distance.put(neighbour, dist)
        prev.put(neighbour, this)
        prioQueue.addOne((neighbour, dist))
      }

      while (prioQueue.nonEmpty) {
        val (current, _) = prioQueue.dequeue()
        val curDistance  = distance(current)

        current.enumeratePossibleMovements(goal).foreach { case (neighbour, dist) =>
          val newDistance = curDistance + dist
          val d = distance.get(neighbour)

          if (d.isEmpty || newDistance < d.get) {
            distance.put(neighbour, newDistance)
            prev.put(neighbour, current)
            prioQueue.addOne((neighbour, dist))
          }
        }
      }

      distance(Environment(goal))
    }

    override def toString: String = {
      def p(y: Int, x: Int) = amphipods.get(Point2D(y, x)).map(_.toString).getOrElse(".")
      def rows = (3 to amphipods.map(_._1.y).max).map { y =>
        s"  #${p(y, 3)}#${p(y, 5)}#${p(y, 7)}#${p(y, 9)}#"
      }.mkString("\n")


      s"""#############
        |#${(1 to 11).map(x => p(hallwayY, x)).mkString}#
        |###${p(2, 3)}#${p(2, 5)}#${p(2, 7)}#${p(2, 9)}###
        |${rows}
        |  #########"""
        .stripMargin
    }

  }

  object Environment {

    private val hallwayY = 1
    private val hallway = Set(
      Point2D(1,1), Point2D(1,2), Point2D(1,4), Point2D(1,6), Point2D(1,8), Point2D(1,10), Point2D(1,11)
    )

    private val regexA =
      """#############
        |#...........#
        |###(A|B|C|D)#(A|B|C|D)#(A|B|C|D)#(A|B|C|D)###
        |  #(A|B|C|D)#(A|B|C|D)#(A|B|C|D)#(A|B|C|D)#
        |  #########"""
        .stripMargin
        .r

    private val regexB =
      """#############
        |#...........#
        |###(A|B|C|D)#(A|B|C|D)#(A|B|C|D)#(A|B|C|D)###
        |  #(A|B|C|D)#(A|B|C|D)#(A|B|C|D)#(A|B|C|D)#
        |  #(A|B|C|D)#(A|B|C|D)#(A|B|C|D)#(A|B|C|D)#
        |  #(A|B|C|D)#(A|B|C|D)#(A|B|C|D)#(A|B|C|D)#
        |  #########"""
        .stripMargin
        .r

    def apply(input: String): Environment = input match {
      case regexA(a1, b1, c1, d1, a2, b2, c2, d2) => new Environment(
        Map(
          Point2D(2,3) -> Amphipod(a1), Point2D(2,5) -> Amphipod(b1), Point2D(2,7) -> Amphipod(c1), Point2D(2,9) -> Amphipod(d1),
          Point2D(3,3) -> Amphipod(a2), Point2D(3,5) -> Amphipod(b2), Point2D(3,7) -> Amphipod(c2), Point2D(3,9) -> Amphipod(d2),
        )
      )

      case regexB(a1, b1, c1, d1, a2, b2, c2, d2, a3, b3, c3, d3, a4, b4, c4, d4) => new Environment(
        Map(
          Point2D(2,3) -> Amphipod(a1), Point2D(2,5) -> Amphipod(b1), Point2D(2,7) -> Amphipod(c1), Point2D(2,9) -> Amphipod(d1),
          Point2D(3,3) -> Amphipod(a2), Point2D(3,5) -> Amphipod(b2), Point2D(3,7) -> Amphipod(c2), Point2D(3,9) -> Amphipod(d2),
          Point2D(4,3) -> Amphipod(a3), Point2D(4,5) -> Amphipod(b3), Point2D(4,7) -> Amphipod(c3), Point2D(4,9) -> Amphipod(d3),
          Point2D(5,3) -> Amphipod(a4), Point2D(5,5) -> Amphipod(b4), Point2D(5,7) -> Amphipod(c4), Point2D(5,9) -> Amphipod(d4),
        )
      )

      case string => throw new RuntimeException(s"'$string' does not match regex")
    }

  }
  
  trait Amphipod {
    def energy: Int
    def movementCost(steps: Int): Int = energy * steps
  }

  object Amphipod {
    def apply(pod: String): Amphipod = pod.head match {
      case 'A' => Amber
      case 'B' => Bronze
      case 'C' => Copper
      case 'D' => Desert
    }
  }

  case object Amber  extends Amphipod { override def energy: Int = 1    ; override def toString(): String = "A" }
  case object Bronze extends Amphipod { override def energy: Int = 10   ; override def toString(): String = "B" }
  case object Copper extends Amphipod { override def energy: Int = 100  ; override def toString(): String = "C" }
  case object Desert extends Amphipod { override def energy: Int = 1000 ; override def toString(): String = "D" }

}
