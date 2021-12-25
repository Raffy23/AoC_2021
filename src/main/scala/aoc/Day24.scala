package aoc
import cats.effect.IO

import scala.collection.mutable
import scala.language.implicitConversions

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 24.12.21
 */
object Day24 extends IORunner {

  override def task1: IO[Option[Long]] =
    streamInputLines("day24.task1")
      .map(Instruction(_))
      .compile
      .toList
      .map { instructions =>

        val badZ1 = new mutable.TreeSet[(Int, Long)]()
        def search(groups: List[List[Instruction]], number: Long, z1: Long): Option[Long] = groups match {
          case Nil                                          => Option.when(z1 == 0)(number)
          case _ :: tail if badZ1.contains((tail.size, z1)) => Option.empty
          case instructions :: tail =>

            @inline def calculateZ(number: String): Long =
              State(number, Map('z' -> z1))
                .run(instructions)
                .value('z')

            val result = (1 to 9)
              .reverse
              .map(w => (number * 10 + w, calculateZ(w.toString)))
              .filter  { case (_     , z) => z <= math.pow(26L, 5L)  }
              .flatMap { case (number, z) => search(tail, number, z) }
              .headOption

            if (result.isEmpty)
              badZ1.add((tail.size, z1))

            result
        }

        search(instructions.grouped(18).toList, 0, 0)
      }

/*
  inp w
  mul x 0 ~> x_0 = 0
  add x z    x_1 = z_0
  mod x 26   x_2 = x_1 % 26
  div z 1    z_1 = z_0 / A
  add x 14   x_3 = x_2 + B
  eql x w    x_4 = w == x_3
  eql x 0    x_5 = x_4 == 0     ~> E
  mul y 0    y_0 = 0
  add y 25   y_0 = 25
  mul y x    y_1 = y_0 * x_5
  add y 1    y_2 = y_1 + 1
  mul z y    z_2 = z_1 * y_2
  mul y 0    y_3 = 0
  add y w    y_3 = w
  add y 12   y_4 = y_3 + C
  mul y x    y_5 = y_4 * x_5
  add z y    z = z_2 + y_5

  E = w != ((z_0 % 26) + B))
  z = ((z_0 / A) * ((25 * E) + 1)) + ((w + C) * E)
*/

  override def task2: IO[Option[Long]] =
    streamInputAsString("day24.task1").map { str =>
      val regex = """inp w
                    |mul x 0
                    |add x z
                    |mod x 26
                    |div z (-?\d+)
                    |add x (-?\d+)
                    |eql x w
                    |eql x 0
                    |mul y 0
                    |add y 25
                    |mul y x
                    |add y 1
                    |mul z y
                    |mul y 0
                    |add y w
                    |add y (-?\d+)
                    |mul y x
                    |add z y
                    |""".stripMargin
                        .r

      val xs = regex
        .findAllMatchIn(str)
        .map(r => (r.group(1).toInt, r.group(2).toInt, r.group(3).toInt))
        .toList

      @inline def calculateZ(w: Int, a: Int, b: Int, c: Int, z1: Long): Long = {
        val E: Int  = w != ( (z1 % 26) + b )
        val z: Long = (z1/a) * (25L*E+1L) + (w+c)*E.toLong
        z
      }

      val badZ1 = new mutable.TreeSet[(Int, Long)]()
      def search(parameters: List[(Int, Int, Int)], number: Long, z1: Long): Option[Long] = parameters match {
        case Nil                                          => Option.when(z1 == 0)(number)
        case _ :: tail if badZ1.contains((tail.size, z1)) => Option.empty
        case (a, b, c) :: tail =>

          val result = (1 to 9)
            .map(w => (number * 10 + w, calculateZ(w, a, b, c, z1)))
            .filter  { case (_     , z) => z <= math.pow(26L, 5L)  }
            .flatMap { case (number, z) => search(tail, number, z) }
            .headOption

          if (result.isEmpty)
            badZ1.add((tail.size, z1))

          result
      }

      search(xs, 0, 0)
    }.compile
     .lastOrError

  type Variable = Char
  type Constant = Long
  case class State(input: String, variables: Map[Variable, Long]) {

    def value(variable: Char): Long = variables.getOrElse(variable, 0)

    def modify(target: Variable, value: Long): State = {
      State(input, variables + (target -> value))
    }

    def modify(target: Variable, op1: Either[Variable, Constant], f: (Long, Long) => Long): State = {
      State(input, variables + (target -> f(value(target), op1.fold(value, identity))))
    }

    def run(instructions: Iterable[Instruction]): State = {
      instructions.foldLeft(this) { case (state, inst) => inst.run(state) }
    }

  }

  sealed trait Instruction {
    def run(state: State): State
  }
  object Instruction {
    private val inputRegex     = """inp ([a-z])""".r
    private val operationRegex = """([a-z]+) ([a-z]) ([a-z])""".r
    private val constOperRegex = """([a-z]+) ([a-z]) (-?\d+)""".r

    def apply(line: String): Instruction = line match {
      case inputRegex(variable)        => Inp(variable.head)
      case operationRegex("add", a, b) => Add(a.head, Left(b.head))
      case operationRegex("mul", a, b) => Mul(a.head, Left(b.head))
      case operationRegex("div", a, b) => Div(a.head, Left(b.head))
      case operationRegex("mod", a, b) => Mod(a.head, Left(b.head))
      case operationRegex("eql", a, b) => Eql(a.head, Left(b.head))
      case constOperRegex("add", a, b) => Add(a.head, Right(b.toInt))
      case constOperRegex("mul", a, b) => Mul(a.head, Right(b.toInt))
      case constOperRegex("div", a, b) => Div(a.head, Right(b.toInt))
      case constOperRegex("mod", a, b) => Mod(a.head, Right(b.toInt))
      case constOperRegex("eql", a, b) => Eql(a.head, Right(b.toInt))
    }
  }

  case class Inp(a: Variable)              extends Instruction {
    override def run(state: State): State = State(
      state.input.drop(1),
      state.variables + (a -> (state.input.head - '0'))
    )
  }
  case class Add(a: Variable, b: Either[Variable, Constant]) extends Instruction { override def run(state: State): State = state.modify(a, b, _ + _)  }
  case class Mul(a: Variable, b: Either[Variable, Constant]) extends Instruction { override def run(state: State): State = state.modify(a, b, _ * _)  }
  case class Div(a: Variable, b: Either[Variable, Constant]) extends Instruction { override def run(state: State): State = state.modify(a, b, _ / _)  }
  case class Mod(a: Variable, b: Either[Variable, Constant]) extends Instruction { override def run(state: State): State = state.modify(a, b, _ % _)  }
  case class Eql(a: Variable, b: Either[Variable, Constant]) extends Instruction { override def run(state: State): State = state.modify(a, b, _ == _) }

  implicit def boolean2Integer(boolean: Boolean): Int = if (boolean) 1 else 0
  implicit def boolean2Long(boolean: Boolean): Long = if (boolean) 1 else 0

}
