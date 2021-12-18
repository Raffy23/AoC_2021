package aoc

import cats.effect.IO

import scala.annotation.tailrec

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 18.12.21
 */
object Day18 extends IORunner {

  override def task1: IO[Long] =
    streamInputLines("day18.task1")
      .map(SnailFishNumber(_))
      .reduce(_ + _)
      .compile
      .lastOrError
      .map(_.magnitude)

  override def task2: IO[Long] =
    streamInputLines("day18.task1")
      .map(SnailFishNumber(_))
      .compile
      .toList
      .map(_
        .toSet
        .subsets(2)
        .flatMap { set =>
          val number1 = set.head
          val number2 = set.last

          Set(
            (number1 + number2).magnitude,
            (number2 + number1).magnitude
          )
        }
        .max
      )

  sealed trait SnailFishNumber {
    def magnitude: Long
    def +(other: SnailFishNumber): SnailFishNumber
  }

  object SnailFishNumber {
    def apply(line: String): SnailFishNumber = {
      @inline def parse(str: Iterator[Char]): SnailFishNumber = {
        str.nextOption() match {
          case Some('[') => PairNode(parse(str), parse(str))
          case Some(d) if d.isDigit => NumberNode(d.toInt - '0')
          case Some(',') => parse(str)
          case Some(']') => parse(str)
          case string => throw new RuntimeException(s"'$string' does not match pattern of a snailfish number!")
        }
      }

      parse(line.iterator).asInstanceOf[PairNode]
    }
  }


  case class PairNode(left: SnailFishNumber, right: SnailFishNumber) extends SnailFishNumber {
    override def magnitude: Long = left.magnitude * 3 + right.magnitude * 2

    override def toString: String = s"[$left,$right]"

    override def +(other: SnailFishNumber): SnailFishNumber = {
      val addition = Option[SnailFishNumber](PairNode(this, other))

      @inline def explode(number: SnailFishNumber): Option[SnailFishNumber] =
        findNodeToExplode(number).map(explodeOnNode(number, _))

      @inline def split(number: SnailFishNumber): Option[SnailFishNumber] =
        findNodeToSplit(number).map(splitOnNode(number, _))

      @inline def simplify(number: SnailFishNumber): Option[SnailFishNumber] =
        explode(number) orElse split(number)

      Iterator
        .iterate(addition) { case Some(number) => simplify(number) }
        .lastDefined
    }
  }
  case class NumberNode(value: Int) extends SnailFishNumber {
    override def magnitude: Long = value

    override def +(other: SnailFishNumber): SnailFishNumber =
      throw new RuntimeException("NumberNode + <something> not supported")

    override def toString: String = value.toString
  }

  private def findNodeToSplit(number: SnailFishNumber): Option[List[SnailFishNumber]] = {
    @inline def search(node: SnailFishNumber, path: List[SnailFishNumber]): Option[List[SnailFishNumber]] = node match {
      case NumberNode(value) if value >= 10 => Some(node :: path)
      case NumberNode(_)                    => Option.empty
      case PairNode(left, right)            =>
        search(left, node :: path).orElse(
          search(right, node :: path)
        )
    }

    search(number, List.empty)
  }

  private def splitOnNode(number: SnailFishNumber, splitPath: List[SnailFishNumber]): SnailFishNumber = {
    @inline def split(node: SnailFishNumber, path: List[SnailFishNumber]): SnailFishNumber = node match {
      case NumberNode(value) if splitPath === (node :: path) =>
        PairNode(
          NumberNode(math.floor(value.toDouble / 2).toInt),
          NumberNode(math.ceil(value.toDouble / 2).toInt)
        )
      case PairNode(left, right) => PairNode(split(left, node :: path), split(right, node :: path))
      case node => node
    }

    split(number, List.empty)
  }

  private def findNodeToExplode(number: SnailFishNumber): Option[List[SnailFishNumber]] = {
    @inline def search(node: SnailFishNumber, level: Int, path: List[SnailFishNumber]): Option[List[SnailFishNumber]] = node match {
      case NumberNode(_) => None
      case PairNode(NumberNode(_), NumberNode(_)) if level == 4 => Some(node :: path)
      case PairNode(left, right) =>
        search(left, level + 1, node :: path).orElse(
          search(right, level + 1, node :: path)
        )
    }

    search(number, 0, List.empty)
  }

  private def explodeOnNode(number: SnailFishNumber, explodePath: List[SnailFishNumber]): SnailFishNumber = {
    val leftOfNode  = SnailFishNumberTraverser.traverseUpLeft(explodePath)
    val rightOfNode = SnailFishNumberTraverser.traverseUpRight(explodePath)

    val (left, right) = explodePath.head match {
      case PairNode(NumberNode(left), NumberNode(right)) => (left, right)
    }

    @inline def explode(node: SnailFishNumber, path: List[SnailFishNumber]): SnailFishNumber = {
      node match {
        case NumberNode(value)      if leftOfNode.exists(_ === node :: path)  => NumberNode(value + left)
        case NumberNode(value)      if rightOfNode.exists(_ === node :: path) => NumberNode(value + right)
        case PairNode(_, _)         if explodePath === (node :: path)         => NumberNode(0)
        case PairNode(left, right) => PairNode(
          explode(left , node :: path),
          explode(right, node :: path)
        )
        case node => node
      }
    }

    explode(number, List.empty)
  }

  implicit class LastIterator[T](private val itr: Iterator[Option[T]]) extends AnyVal {
    def lastDefined: T = {
      val iterator = itr.takeWhile(_.isDefined)
      var value = iterator.next()

      while (iterator.hasNext) {
        value = iterator.next()
      }

      value.get
    }
  }

  implicit class StrictPathEquality(private val path: List[SnailFishNumber]) extends AnyVal {
    def ===(other: List[SnailFishNumber]): Boolean =
      path.sizeCompare(path) == 0 && path.zip(other).forall { case (l, r) => l eq r }
  }

  object SnailFishNumberTraverser {
    @tailrec def traverseDownRight(node: SnailFishNumber, path: List[SnailFishNumber]): List[SnailFishNumber] = node match {
      case NumberNode(_)      => node :: path
      case PairNode(_, right) => traverseDownRight(right, node :: path)
    }

    @tailrec def traverseDownLeft(node: SnailFishNumber, path: List[SnailFishNumber]): List[SnailFishNumber] = node match {
      case NumberNode(_)      => node :: path
      case PairNode(left, _)  => traverseDownLeft(left, node :: path)
    }

    @tailrec def traverseUpLeft(path: List[SnailFishNumber]): Option[List[SnailFishNumber]] = path match {
      case child :: PairNode(left, _) :: _ if left ne child => Some(traverseDownRight(left, path.tail))
      case _     :: Nil                                     => None
      case _     :: tail                                    => traverseUpLeft(tail)
    }

    @tailrec def traverseUpRight(path: List[SnailFishNumber]): Option[List[SnailFishNumber]] = path match {
      case child :: PairNode(_, right) :: _ if right ne child => Some(traverseDownLeft(right, path.tail))
      case _ :: Nil                                           => None
      case _ :: tail                                          => traverseUpRight(tail)
    }
  }

}
