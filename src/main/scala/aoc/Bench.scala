package aoc

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import org.openjdk.jmh.annotations.{Benchmark, BenchmarkMode, Fork, Measurement, Mode, OutputTimeUnit, Scope, Setup, State, TearDown, Warmup}
import org.openjdk.jmh.infra.Blackhole

import java.util.concurrent.TimeUnit

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 10.12.21
 */

@Fork(value = 2)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
class Bench {

  private implicit var unsafeIORuntime: IORuntime = _

  @Setup
  def setup(): Unit = unsafeIORuntime = IORuntime.global

  @TearDown
  def tearDown(): Unit = unsafeIORuntime.shutdown()

  @Benchmark def Day01_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day01.task1.unsafeRunSync())
  @Benchmark def Day01_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day01.task2.unsafeRunSync())

  @Benchmark def Day02_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day02.task1.unsafeRunSync())
  @Benchmark def Day02_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day02.task2.unsafeRunSync())

  @Benchmark def Day03_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day03.task1.unsafeRunSync())
  @Benchmark def Day03_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day03.task2.unsafeRunSync())

  @Benchmark def Day04_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day04.task1.unsafeRunSync())
  @Benchmark def Day04_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day04.task2.unsafeRunSync())

  @Benchmark def Day05_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day05.task1.unsafeRunSync())
  @Benchmark def Day05_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day05.task2.unsafeRunSync())

  @Benchmark def Day06_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day06.task1.unsafeRunSync())
  @Benchmark def Day06_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day06.task2.unsafeRunSync())

  @Benchmark def Day07_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day07.task1.unsafeRunSync())
  @Benchmark def Day07_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day07.task2.unsafeRunSync())

  @Benchmark def Day08_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day08.task1.unsafeRunSync())
  @Benchmark def Day08_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day08.task2.unsafeRunSync())

  @Benchmark def Day09_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day09.task1.unsafeRunSync())
  @Benchmark def Day09_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day09.task2.unsafeRunSync())

  @Benchmark def Day10_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day10.task1.unsafeRunSync())
  @Benchmark def Day10_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day10.task2.unsafeRunSync())

  @Benchmark def Day11_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day11.task1.unsafeRunSync())
  @Benchmark def Day11_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day11.task2.unsafeRunSync())

  @Benchmark def Day12_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day12.task1.unsafeRunSync())
  @Benchmark def Day12_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day12.task2.unsafeRunSync())

  @Benchmark def Day13_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day13.task1.unsafeRunSync())
  @Benchmark def Day13_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day13.task2.unsafeRunSync())

  @Benchmark def Day14_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day14.task1.unsafeRunSync())
  @Benchmark def Day14_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day14.task2.unsafeRunSync())

  @Benchmark def Day15_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day15.task1.unsafeRunSync())
  @Benchmark def Day15_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day15.task2.unsafeRunSync())

  @Benchmark def Day16_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day16.task1.unsafeRunSync())
  @Benchmark def Day16_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day16.task2.unsafeRunSync())

  @Benchmark def Day17_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day17.task1.unsafeRunSync())
  @Benchmark def Day17_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day17.task2.unsafeRunSync())

  @Benchmark def Day18_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day18.task1.unsafeRunSync())
  @Benchmark def Day18_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day18.task2.unsafeRunSync())

  @Benchmark def Day19_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day19.task1.unsafeRunSync())
  @Benchmark def Day19_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day19.task2.unsafeRunSync())

  @Benchmark def Day20_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day20.task1.unsafeRunSync())
  @Benchmark def Day20_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day20.task2.unsafeRunSync())

  @Benchmark def Day21_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day21.task1.unsafeRunSync())
  @Benchmark def Day21_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day21.task2.unsafeRunSync())

  @Benchmark def Day22_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day22.task1.unsafeRunSync())
  @Benchmark def Day22_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day22.task2.unsafeRunSync())

  @Benchmark def Day23_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day23.task1.unsafeRunSync())
  @Benchmark def Day23_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day23.task2.unsafeRunSync())

  @Benchmark def Day24_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day24.task2.unsafeRunSync())
  @Benchmark def Day24_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day24.task2.unsafeRunSync())

  @Benchmark def Day25_Task1(blackhole: Blackhole): Unit = blackhole.consume(Day25.task1.unsafeRunSync())
  @Benchmark def Day25_Task2(blackhole: Blackhole): Unit = blackhole.consume(Day25.task2.unsafeRunSync())

  @Benchmark def AllDays(blackhole: Blackhole): Unit = {
    val tasks = List(
      Day01, Day02, Day03, Day04, Day05, Day06, Day07, Day08, Day09, Day10,
      Day11, Day12, Day13, Day14, Day15, Day16, Day17, Day18, Day19, Day20,
      Day21, Day22, Day23, Day24, Day25
    )

    import cats.implicits._
    val all = for {
     task1 <- tasks.map(_.task1.start).sequence
     task2 <- tasks.map(_.task2.start).sequence

      _ <- task1.map(_.joinWithNever).sequence
      _ <- task2.map(_.joinWithNever).sequence
    } yield ()

    blackhole.consume(all.unsafeRunSync())
  }

}

