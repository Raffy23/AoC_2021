package aoc

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
}

