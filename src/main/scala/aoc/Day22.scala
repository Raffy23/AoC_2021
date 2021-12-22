package aoc
import cats.effect.IO

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 22.12.21
 */
object Day22 extends IORunner {

  override def task1: IO[Any] =
    streamInputLines("day22.task1")
      .map(Cuboid(_))
      .filter(isInsideRegion)
      .fold(Set.empty[Point3D]) {
        case (state, step@Cuboid(true , _, _)) => state ++ step.points
        case (state, step@Cuboid(false, _, _)) => state -- step.points
      }
      .compile
      .lastOrError
      .map(_.size)

  override def task2: IO[Any] =
    streamInputLines("day22.task1")
      .map(Cuboid(_))
      .compile
      .toVector
      .map { steps =>
        steps
          .foldLeft(List.empty[Cuboid]) { case (usedCubes, cuboid) =>
            usedCubes.foldLeft(List.empty[Cuboid]) {
              case (result, cube) if !cube.intersects(cuboid) => cube :: result
              case (result, cube) if  cube.intersects(cuboid) => cube :: cube.intersect(cuboid, invertStatus = true) :: result
            } ::: (
              if (cuboid.on) List(cuboid)
              else           List.empty
            )
          }
          .map(_.size)
          .sum
      }

  private def isInsideRegion(step: Cuboid): Boolean =
    step.start.x >= -50 && step.end.x <= 50 &&
    step.start.y >= -50 && step.end.y <= 50 &&
    step.start.z >= -50 && step.end.z <= 50

  case class Point3D(x: Int, y: Int, z: Int)
  case class Cuboid(on: Boolean, start: Point3D, end: Point3D) {
    def points: LazyList[Point3D] = {
      @inline def range(start: Int, end: Int) =
        LazyList.from(start).takeWhile(_ <= end)

      range(start.x, end.x).flatMap { x =>
        range(start.y, end.y).flatMap { y =>
          range(start.z, end.z).map { z =>
            Point3D(x,y,z)
          }
        }
      }
    }

    def size: BigInt = {
      val rangeX = BigInt(end.x - start.x + 1)
      val rangeY = BigInt(end.y - start.y + 1)
      val rangeZ = BigInt(end.z - start.z + 1)

      val value = rangeX * rangeY * rangeZ
      if (on) value
      else    value * -1
    }

    def intersects(other: Cuboid): Boolean =
      this.start.x <= other.end.x && this.end.x >= other.start.x &&
      this.start.y <= other.end.y && this.end.y >= other.start.y &&
      this.start.z <= other.end.z && this.end.z >= other.start.z

    def intersect(other: Cuboid, invertStatus: Boolean = false): Cuboid =
      new Cuboid(
        if (invertStatus) !on else on,
        Point3D(
          math.max(this.start.x, other.start.x),
          math.max(this.start.y, other.start.y),
          math.max(this.start.z, other.start.z)
        ),
        Point3D(
          math.min(this.end.x, other.end.x),
          math.min(this.end.y, other.end.y),
          math.min(this.end.z, other.end.z)
        )
      )

    override def toString: String =
      s"${if (on) "on" else "off"} x=${start.x}..${end.x},y=${start.y}..${end.y},z=${start.z}..${start.z}"

  }
  object Cuboid {
    private val regex = """(on|off) x=(-?\d+)\.\.(-?\d+),y=(-?\d+)\.\.(-?\d+),z=(-?\d+)\.\.(-?\d+)""".r

    def apply(line: String): Cuboid = line match {
      case regex("on", xS, xE, yS, yE, zS, zE)  => Cuboid(on = true, Point3D(xS.toInt, yS.toInt, zS.toInt), Point3D(xE.toInt, yE.toInt, zE.toInt))
      case regex("off", xS, xE, yS, yE, zS, zE) => Cuboid(on = false, Point3D(xS.toInt, yS.toInt, zS.toInt), Point3D(xE.toInt, yE.toInt, zE.toInt))
      case string                               => throw new RuntimeException(s"'$string' does not match Step regex!")
    }
  }

}
