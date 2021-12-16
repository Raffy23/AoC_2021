package aoc
import cats.effect.IO

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 16.12.21
 */
object Day16 extends IORunner {

  sealed trait BITSPacket {
    def version: Byte
    def sumVersion: Int
    def value: Long
  }
  case class Literal(version: Byte, value: Long) extends BITSPacket {
    override def sumVersion: Int = version.toInt
  }

  object Literal {
    def apply(version: Int, bitStream: Iterator[Char]): Literal = {
      var bitValues = List(bitStream.take(5).mkString)
      while (bitValues.head(0) != '0') {
        bitValues = bitStream.take(5).mkString :: bitValues
      }

      new Literal(
        version.toByte,
        java.lang.Long.parseLong(bitValues.reverse.map(_.drop(1)).mkString, 2)
      )
    }
  }

  case class Operator(version: Byte, `type`: Byte, op: List[BITSPacket] => Long, content: List[BITSPacket]) extends BITSPacket {
    override def sumVersion: Int = version.toInt + content.map(_.sumVersion).sum
    override def value: Long = op(content)
  }

  object Operator {
    def apply(version: Int, `type`: Int, bitStream: Iterator[Char]): Operator = {
      val lengthType = bitStream.take(1).next()

      val contents = lengthType match {
        case '0' =>
          val lengthInBits = Integer.parseInt(bitStream.take(15).mkString, 2)
          val (contentBitStream, _) = bitStream.take(lengthInBits).duplicate

          var contents = List.empty[BITSPacket]
          while (contentBitStream.nonEmpty)
            contents = BITSPacket(contentBitStream) :: contents

          contents.reverse

        case '1' =>
          val numberOfContents = Integer.parseInt(bitStream.take(11).mkString, 2)
          (0 until numberOfContents).map(_ => BITSPacket(bitStream)).toList
      }

      new Operator(
        version.toByte,
        `type`.toByte,
        `type` match {
          case 0 => sum
          case 1 => product
          case 2 => min
          case 3 => max
          case 5 => gt
          case 6 => lt
          case 7 => eq
        },
        contents
      )
    }

    private def sum(contents: List[BITSPacket]): Long = contents.map(_.value).sum
    private def product(contents: List[BITSPacket]): Long = contents.map(_.value).product
    private def min(contents: List[BITSPacket]): Long = contents.map(_.value).min
    private def max(contents: List[BITSPacket]): Long = contents.map(_.value).max
    private def gt(contents: List[BITSPacket]): Long = if (contents.head.value >  contents(1).value) 1L else 0L
    private def lt(contents: List[BITSPacket]): Long = if (contents.head.value <  contents(1).value) 1L else 0L
    private def eq(contents: List[BITSPacket]): Long = if (contents.head.value == contents(1).value) 1L else 0L

  }

  object BITSPacket {

    def apply(bitStream: Iterator[Char]): BITSPacket = {
      val version    = Integer.parseInt(bitStream.take(3).mkString, 2)
      val packetType = Integer.parseInt(bitStream.take(3).mkString, 2)

      packetType match {
        case 4 => Literal(version, bitStream)
        case t => Operator(version, t, bitStream)
      }
    }

    def apply(hexString: String): BITSPacket = {
      BITSPacket.apply(hexString.iterator.flatMap {
        case '0' => "0000"
        case '1' => "0001"
        case '2' => "0010"
        case '3' => "0011"
        case '4' => "0100"
        case '5' => "0101"
        case '6' => "0110"
        case '7' => "0111"
        case '8' => "1000"
        case '9' => "1001"
        case 'A' => "1010"
        case 'B' => "1011"
        case 'C' => "1100"
        case 'D' => "1101"
        case 'E' => "1110"
        case 'F' => "1111"
      })
    }
  }


  override def task1: IO[Int] =
    streamInputLines("day16.task1")
      .map(BITSPacket.apply)
      .map(_.sumVersion)
      .compile
      .lastOrError

  override def task2: IO[Long] =
    streamInputLines("day16.task1")
      .map(BITSPacket.apply)
      .map(_.value)
      .compile
      .lastOrError

  implicit class DrainableIterator[A](private val itr: Iterator[A]) extends AnyVal {
    def drain(): Unit = {
      while (itr.hasNext)
        itr.next()
    }
  }

}
