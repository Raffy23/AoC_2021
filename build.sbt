name         := "Advent of Code 2021"
version      := "1.0"
scalaVersion := "2.13.7"

enablePlugins(JmhPlugin)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect"    % "3.3.0",
  "co.fs2"        %% "fs2-io"         % "3.2.2",

  "org.apache.commons" % "commons-math3" % "3.6.1"
)