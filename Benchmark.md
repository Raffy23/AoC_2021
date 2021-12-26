Benchmark results
=================
**REMEMBER**: The numbers below are just data. To gain reusable insights, you need to follow up on
why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
experiments, perform baseline and negative tests that provide experimental control, make sure
the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
Do not assume the numbers tell you what you want them to tell.

The Benchmark was run in sbt with `Jmh / run` in OpenJDK 11.0.13 on an 
AMD Ryzen 5800X @ 4,4Ghz.

| Benchmark         | Mode | Cnt |     Score |     |    Error | Units |
|:------------------|:----:|:---:|----------:|-----|---------:|-------|
| Bench.AllDays     | avgt | 10  | 10392,619 | ±   | 2133,868 | ms/op |
| Bench.Day01_Task1 | avgt | 10  |     0,402 | ±   |    0,067 | ms/op |
| Bench.Day01_Task2 | avgt | 10  |    30,883 | ±   |    1,163 | ms/op |
| Bench.Day02_Task1 | avgt | 10  |     0,502 | ±   |    0,087 | ms/op |
| Bench.Day02_Task2 | avgt | 10  |     0,555 | ±   |    0,105 | ms/op |
| Bench.Day03_Task1 | avgt | 10  |     0,561 | ±   |    0,114 | ms/op |
| Bench.Day03_Task2 | avgt | 10  |     0,328 | ±   |    0,044 | ms/op |
| Bench.Day04_Task1 | avgt | 10  |     1,241 | ±   |    0,232 | ms/op |
| Bench.Day04_Task2 | avgt | 10  |     1,799 | ±   |    0,219 | ms/op |
| Bench.Day05_Task1 | avgt | 10  |    74,585 | ±   |   26,400 | ms/op |
| Bench.Day05_Task2 | avgt | 10  |   140,212 | ±   |   54,352 | ms/op |
| Bench.Day06_Task1 | avgt | 10  |     0,594 | ±   |    0,208 | ms/op |
| Bench.Day06_Task2 | avgt | 10  |     0,843 | ±   |    0,226 | ms/op |
| Bench.Day07_Task1 | avgt | 10  |     1,856 | ±   |    0,357 | ms/op |
| Bench.Day07_Task2 | avgt | 10  |     3,743 | ±   |    0,495 | ms/op |
| Bench.Day08_Task1 | avgt | 10  |     0,586 | ±   |    0,132 | ms/op |
| Bench.Day08_Task2 | avgt | 10  |     3,138 | ±   |    0,531 | ms/op |
| Bench.Day09_Task1 | avgt | 10  |     0,825 | ±   |    0,137 | ms/op |
| Bench.Day09_Task2 | avgt | 10  |     3,325 | ±   |    0,328 | ms/op |
| Bench.Day10_Task1 | avgt | 10  |     0,568 | ±   |    0,136 | ms/op |
| Bench.Day10_Task2 | avgt | 10  |     0,593 | ±   |    0,188 | ms/op |
| Bench.Day11_Task1 | avgt | 10  |     4,833 | ±   |    0,469 | ms/op |
| Bench.Day11_Task2 | avgt | 10  |    10,838 | ±   |    0,210 | ms/op |
| Bench.Day12_Task1 | avgt | 10  |     2,708 | ±   |    0,487 | ms/op |
| Bench.Day12_Task2 | avgt | 10  |   126,984 | ±   |   41,351 | ms/op |
| Bench.Day13_Task1 | avgt | 10  |     0,609 | ±   |    0,098 | ms/op |
| Bench.Day13_Task2 | avgt | 10  |     1,016 | ±   |    0,110 | ms/op |
| Bench.Day14_Task1 | avgt | 10  |     6,276 | ±   |    0,691 | ms/op |
| Bench.Day14_Task2 | avgt | 10  |     1,063 | ±   |    0,133 | ms/op |
| Bench.Day15_Task1 | avgt | 10  |     3,893 | ±   |    0,139 | ms/op |
| Bench.Day15_Task2 | avgt | 10  |   191,243 | ±   |   53,321 | ms/op |
| Bench.Day16_Task1 | avgt | 10  |     0,689 | ±   |    0,231 | ms/op |
| Bench.Day16_Task2 | avgt | 10  |     0,692 | ±   |    0,202 | ms/op |
| Bench.Day17_Task1 | avgt | 10  |     8,575 | ±   |    1,528 | ms/op |
| Bench.Day17_Task2 | avgt | 10  |   198,713 | ±   |   25,164 | ms/op |
| Bench.Day18_Task1 | avgt | 10  |    28,934 | ±   |    3,565 | ms/op |
| Bench.Day18_Task2 | avgt | 10  |   432,271 | ±   |    5,069 | ms/op |
| Bench.Day19_Task1 | avgt | 10  |  1508,340 | ±   |   65,456 | ms/op |
| Bench.Day19_Task2 | avgt | 10  |  1494,319 | ±   |   13,900 | ms/op |
| Bench.Day20_Task1 | avgt | 10  |    23,680 | ±   |    0,544 | ms/op |
| Bench.Day20_Task2 | avgt | 10  |  1254,316 | ±   |    6,899 | ms/op |
| Bench.Day21_Task1 | avgt | 10  |     0,283 | ±   |    0,047 | ms/op |
| Bench.Day21_Task2 | avgt | 10  |   162,664 | ±   |   35,423 | ms/op |
| Bench.Day22_Task1 | avgt | 10  |   683,332 | ±   |  168,880 | ms/op |
| Bench.Day22_Task2 | avgt | 10  |   129,655 | ±   |   54,884 | ms/op |
| Bench.Day23_Task1 | avgt | 10  |  1595,700 | ±   |  172,661 | ms/op |
| Bench.Day23_Task2 | avgt | 10  |  3164,861 | ±   |  908,757 | ms/op |
| Bench.Day24_Task1 | avgt | 10  |  1489,653 | ±   |   61,398 | ms/op |
| Bench.Day24_Task2 | avgt | 10  |  1506,329 | ±   |  131,012 | ms/op |
| Bench.Day25_Task1 | avgt | 10  |  2711,002 | ±   |  148,920 | ms/op |
| Bench.Day25_Task2 | avgt | 10  |     0,007 | ±   |    0,001 | ms/op |