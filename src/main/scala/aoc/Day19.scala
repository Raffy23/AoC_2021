package aoc
import cats.effect.IO
import fs2.Chunk
import org.apache.commons.math3.geometry.euclidean.threed.{Rotation, RotationConvention, RotationOrder, Vector3D}

import scala.annotation.tailrec

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 19.12.21
 */
object Day19 extends IORunner {

  override def task1: IO[Int] =
    streamInputLines("day19.task1")
      .toScannerList
      .map(liftScannersIntoSameSpace)
      .map(_.flatMap(_.points.map(_.toVector).toSet).size)


  override def task2: IO[Int] =
    streamInputLines("day19.task1")
      .toScannerList
      .map(liftScannersIntoSameSpace)
      .map(_
        .toList
        .combinations(2)
        .map { case List(l, r) => l.manhattanDistance(r) }
        .max
      )

  private def liftScannersIntoSameSpace(scanners: List[Scanner]): Set[Scanner] = {

    @inline def overlap(scanner0: Scanner, scanner1: Scanner): Option[Scanner] = {
      rotations.iterator.map { rot =>
        val s = scanner1.rotate(rot)

        scanner0.points.flatMap { point =>
          s.points.map(other =>
            ((other - point) * (-1)).toVector
          )
        }.groupMapReduce(identity)(_ => 1)(_ + _)
          .find(_._2 >= 12)
          .map { case (position, intersections) =>
            scanner1.moveTo(rot, position)
          }

      }.dropWhile(_.isEmpty)
        .nextOption()
        .flatten
    }

    @tailrec @inline def scan(toScan: List[Scanner], scanned: Set[Scanner]): Set[Scanner] = {
      toScan match {
        case Nil => scanned
        case head :: tail =>
          val overlapping    = tail.flatMap(overlap(head, _))
          val overlappingIDs = overlapping.map(_.id).toSet
          val notOverlapping = tail.filterNot(s => overlappingIDs.contains(s.id))

          scan(overlapping ++ notOverlapping, scanned + head)
      }
    }

    scan(scanners, Set.empty)
  }

  implicit class ComparableVector3D(private val vec: Vector3D) extends AnyVal {

    @inline def -(other: Vector3D): Vector3D = vec.subtract(other)

    @inline def *(scalar: Double): Vector3D = vec.scalarMultiply(scalar)

    @inline def toVector: Vector[Int] = vec.toArray.map(_.round.toInt).toVector

    def ~==(other: Vector3D): Boolean =
      (vec.getX - other.getX).abs < 0.00001 &&
      (vec.getY - other.getY).abs < 0.00001 &&
      (vec.getZ - other.getZ).abs < 0.00001
  }

  object Vector3D {
    private val regex = """(-?\d+),(-?\d+),(-?\d+)""".r

    def apply(line: String): Vector3D = line match {
      case regex(x, y, z) => new Vector3D(x.toDouble, y.toDouble, z.toDouble)
      case string         => throw new RuntimeException(s"'$string' does not match Point3D regex!")
    }
  }

  case class Scanner(id: Int, points: Vector[Vector3D], position: Vector[Int] = Vector(0,0,0)) {

    def rotate(rotation: Rotation): Scanner = Scanner(id, points.map(rotation.applyTo), position)

    def overlaps(other: Scanner): Option[(Vector[Int], Int)] = {
      rotations.iterator.map { rot =>
        val s = other.rotate(rot)

        this.points.flatMap { point =>
          s.points.map(other =>
            ((other - point) * (-1)).toVector
          )
        }.groupMapReduce(identity)(_ => 1)(_ + _)
         .find(_._2 >= 12)

      }.dropWhile(_.isEmpty)
       .next()
    }

    def beacons(rotation: Rotation, position: Vector[Int]): Vector[Vector[Int]] = {
      val vecPos = new Vector3D(position(0), position(1), position(2))
      points.map(vec => rotation.applyTo(vec).add(vecPos).toVector)
    }

    def moveTo(rotation: Rotation, position: Vector[Int]): Scanner = {
      val vecPos = new Vector3D(position(0), position(1), position(2))
      val newPoints = points.map(vec => rotation.applyTo(vec).add(vecPos))

      new Scanner(id, newPoints, position)
    }

    def manhattanDistance(other: Scanner): Int =
      position.zip(other.position).map { case (l, r) => (l-r).abs }.sum

  }

  object Scanner {
    private val regex = """--- scanner (\d+) ---""".r

    def apply(chunk: Chunk[(Boolean, Chunk[String])]): Scanner = {
      new Scanner(
        chunk.head.get._2.head.get match {
          case regex(id) => id.toInt
          case string    => throw new RuntimeException(s"'$string' does not match Scanner regex!")
        },
        chunk.last.get._2.map(Vector3D(_)).toVector
      )
    }
  }

  implicit class ScannerStream(private val stream: fs2.Stream[IO, String]) extends AnyVal {
    def toScannerList: IO[List[Scanner]] =
      stream
        .groupAdjacentBy(_.startsWith("--- scanner"))
        .sliding(2, 2)
        .map(Scanner(_))
        .compile
        .toList
  }

  private lazy val rotations = Vector(                                 //               X                    Y                    Z
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians(  0), math.toRadians(  0), math.toRadians(  0)),
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians( 90), math.toRadians(  0), math.toRadians(  0)),
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians(180), math.toRadians(  0), math.toRadians(  0)),
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians(270), math.toRadians(  0), math.toRadians(  0)),
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians(  0), math.toRadians(  0), math.toRadians( 90)),
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians( 90), math.toRadians(  0), math.toRadians( 90)),
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians(180), math.toRadians(  0), math.toRadians( 90)),
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians(270), math.toRadians(  0), math.toRadians( 90)),
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians(  0), math.toRadians(  0), math.toRadians(180)),
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians( 90), math.toRadians(  0), math.toRadians(180)),
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians(180), math.toRadians(  0), math.toRadians(180)),
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians(270), math.toRadians(  0), math.toRadians(180)),
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians(  0), math.toRadians(  0), math.toRadians(270)),
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians( 90), math.toRadians(  0), math.toRadians(270)),
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians(180), math.toRadians(  0), math.toRadians(270)),
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians(270), math.toRadians(  0), math.toRadians(270)),
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians(  0), math.toRadians( 90), math.toRadians(  0)),
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians( 90), math.toRadians( 90), math.toRadians(  0)),
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians(180), math.toRadians( 90), math.toRadians(  0)),
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians(270), math.toRadians( 90), math.toRadians(  0)),
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians(  0), math.toRadians(270), math.toRadians(  0)),
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians( 90), math.toRadians(270), math.toRadians(  0)),
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians(180), math.toRadians(270), math.toRadians(  0)),
    new Rotation(RotationOrder.ZXY, RotationConvention.VECTOR_OPERATOR, math.toRadians(270), math.toRadians(270), math.toRadians(  0)),
  )

}
