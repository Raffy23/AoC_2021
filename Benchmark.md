Benchmark results
=================
**REMEMBER**: The numbers below are just data. To gain reusable insights, you need to follow up on
why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
experiments, perform baseline and negative tests that provide experimental control, make sure
the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
Do not assume the numbers tell you what you want them to tell.

Benchmark was run in sbt with `Jmh / run` with OpenJDK 11.0.13 on an 
Intel(R) Core(TM) i5-4210H CPU @ 2.90GHz.

|Benchmark         | Mode | Cnt | Score    | ± | Error   | Units |
|------------------|------|-----|----------|---|---------|-------|
|Bench.Day01_Task1 | avgt |  10 |   3,179  | ± |   1,074 | ms/op |
|Bench.Day01_Task2 | avgt |  10 |  57,181  | ± |   7,478 | ms/op |
|Bench.Day02_Task1 | avgt |  10 |   3,706  | ± |   0,658 | ms/op |
|Bench.Day02_Task2 | avgt |  10 |   3,960  | ± |   1,117 | ms/op |
|Bench.Day03_Task1 | avgt |  10 |   3,283  | ± |   0,973 | ms/op |
|Bench.Day03_Task2 | avgt |  10 |   2,278  | ± |   0,610 | ms/op |
|Bench.Day04_Task1 | avgt |  10 |   7,866  | ± |   3,090 | ms/op |
|Bench.Day04_Task2 | avgt |  10 |  12,390  | ± |   3,825 | ms/op |
|Bench.Day05_Task1 | avgt |  10 | 245,811  | ± | 127,327 | ms/op |
|Bench.Day05_Task2 | avgt |  10 | 434,724  | ± |  95,714 | ms/op |
|Bench.Day06_Task1 | avgt |  10 |   9,315  | ± |   1,960 | ms/op |
|Bench.Day06_Task2 | avgt |  10 |  10,196  | ± |   4,006 | ms/op |
|Bench.Day07_Task1 | avgt |  10 |  18,414  | ± |  12,997 | ms/op |
|Bench.Day07_Task2 | avgt |  10 |  27,478  | ± |  10,760 | ms/op |
|Bench.Day08_Task1 | avgt |  10 |   7,642  | ± |   2,027 | ms/op |
|Bench.Day08_Task2 | avgt |  10 |  21,774  | ± |  10,927 | ms/op |
|Bench.Day09_Task1 | avgt |  10 |  12,902  | ± |  12,051 | ms/op |
|Bench.Day09_Task2 | avgt |  10 |  19,905  | ± |  13,285 | ms/op |
|Bench.Day10_Task1 | avgt |  10 |   5,892  | ± |   3,595 | ms/op |
|Bench.Day10_Task2 | avgt |  10 |   5,284  | ± |   2,118 | ms/op |
|Bench.Day11_Task1 | avgt |  10 |  17,024  | ± |   5,001 | ms/op |
|Bench.Day11_Task2 | avgt |  10 |  29,266  | ± |   8,547 | ms/op |
|Bench.Day12_Task1 | avgt |  10 |   9,309  | ± |   2,109 | ms/op |
|Bench.Day12_Task2 | avgt |  10 | 250,985  | ± |  59,407 | ms/op |
|Bench.Day13_Task1 | avgt |  10 |   5,688  | ± |   2,130 | ms/op |
|Bench.Day13_Task2 | avgt |  10 |   7,662  | ± |   2,340 | ms/op |
|Bench.Day14_Task1 | avgt |  10 |  17,843  | ± |   4,037 | ms/op |
|Bench.Day14_Task2 | avgt |  10 |  10,957  | ± |   4,397 | ms/op |
|Bench.Day15_Task1 | avgt |  10 |  14,533  | ± |   7,200 | ms/op |
|Bench.Day15_Task2 | avgt |  10 | 395,360  | ± |  60,482 | ms/op |
|Bench.Day16_Task1 | avgt |  10 |   5,576  | ± |   1,002 | ms/op |
|Bench.Day16_Task2 | avgt |  10 |   5,326  | ± |   1,529 | ms/op |
|Bench.Day17_Task1 | avgt |  10 |  20,989  | ± |   4,697 | ms/op |
|Bench.Day17_Task2 | avgt |  10 | 423,464  | ± |  45,964 | ms/op |