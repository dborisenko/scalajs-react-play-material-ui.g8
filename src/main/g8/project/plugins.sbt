// Scala.js, the Scala to JavaScript compiler
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.21")

// Play Framework
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.11")

// plugin which allows you to use Scala.js along with any sbt-web server
addSbtPlugin("com.vmunier" % "sbt-web-scalajs" % "1.0.6")

// build application packages in native formats
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.2")

// sbt-web plugin for adding checksum files for web assets
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.4")

// sbt-web plugin for gzip compressing web assets
addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.2")

// SBT plugin that can check maven repositories for dependency updates
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.3")

// Compiler plugin for making type lambdas (type projections) easier to write
addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4")

// sbt plugin adding support for source code formatting using Scalariform
addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.2")

// Module bundler for Scala.js projects that use NPM packages.
addSbtPlugin("ch.epfl.scala" % "sbt-web-scalajs-bundler" % "0.9.0")
