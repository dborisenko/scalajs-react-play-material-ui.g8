inThisBuild(List(
  scalaVersion := Dependencies.Versions.scala,
  scalacOptions := Seq(
    "-encoding", "UTF-8",
    "-deprecation", // warning and location for usages of deprecated APIs
    "-feature", // warning and location for usages of features that should be imported explicitly
    "-unchecked", // additional warnings where generated code depends on assumptions
    "-Xlint", // recommended additional warnings
    "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver
    "-Ywarn-inaccessible",
    "-Ywarn-dead-code"
  )
))

lazy val macroParadiseSettings = Seq(
  resolvers += Resolver.sonatypeRepo("releases"),
  addCompilerPlugin(Dependencies.paradise cross CrossVersion.full)
)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.`circe-generic`.value,
      Dependencies.`circe-java8`.value
    ),
    macroParadiseSettings
  )
  .jsConfigure(_ enablePlugins ScalaJSWeb)
  .jsSettings(
    libraryDependencies ++= Def.setting(Seq(
      Dependencies.`scala-java-time`.value
    )).value
  )
lazy val `shared-jvm` = shared.jvm.settings(name := "shared-jvm")
lazy val `shared-js` = shared.js.settings(name := "shared-js")

lazy val client = (project in file("client"))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalaJSWeb)
  .enablePlugins(ScalaJSBundlerPlugin)
  .settings(
    libraryDependencies ++= Def.setting(Seq(
      Dependencies.`scalajs-react-core`.value,
      Dependencies.`scalajs-react-extra`.value,
      Dependencies.`scalacss-ext-react`.value,
      Dependencies.`scalajs-dom`.value,
      Dependencies.`circe-generic`.value,
      Dependencies.`circe-parser`.value,
      Dependencies.`circe-java8`.value,
      Dependencies.`scalajs-react-components`.value
    )).value,
    npmDependencies in Compile ++= Seq(
      Dependencies.react,
      Dependencies.`react-dom`,
      Dependencies.`material-ui`,
      Dependencies.`react-tap-event-plugin`
    ),
    emitSourceMaps := false
  )
  .dependsOn(`shared-js`)

lazy val server = (project in file("server"))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
  .enablePlugins(DockerPlugin)
  .enablePlugins(WebScalaJSBundlerPlugin)
  .settings(macroParadiseSettings)
  .settings(
    libraryDependencies ++= Def.setting(Seq(
      guice,
      Dependencies.`scala-guice`.value,
      Dependencies.ficus.value,
      Dependencies.`play-circe`.value,
      Dependencies.scalatest.value % Test
    )).value,
    // triggers scalaJSPipeline when using compile or continuous compilation
    compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
    // connect to the client project
    scalaJSProjects := Seq(client),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip)
  )
  .aggregate(client)
  .dependsOn(`shared-jvm`)
