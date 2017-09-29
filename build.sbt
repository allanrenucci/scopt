import Dependencies._
import com.typesafe.sbt.pgp.PgpKeys._

// shadow sbt-scalajs' crossProject and CrossType until Scala.js 1.0.0 is released
import sbtcrossproject.{crossProject, CrossType}

def v: String = "3.7.0"

lazy val root = project.in(file(".")).
  aggregate(scoptJS, scoptJVM, scoptNative).
  settings(
    publish := {},
    publishLocal := {},
    skip in publish := true)

lazy val scopt = (crossProject(JSPlatform, JVMPlatform, NativePlatform) in file(".")).
  settings(
    inThisBuild(Seq(
      version := v,
      organization := "com.github.scopt",
      scalaVersion := scala212,
      crossScalaVersions := Seq(scala211, scala210, scala212, scala213),
      homepage := Some(url("https://github.com/scopt/scopt")),
      licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php"))
    )),
    name := "scopt",
    // site
    // to preview, preview-site
    // to push, ghpages-push-site
    site.settings,
    site.includeScaladoc(s"$v/api"),
    // ghpages.settings,
    // git.remoteRepo := "git@github.com:scopt/scopt.git",
    description := """a command line options parsing library""",
    libraryDependencies += specs2 % Test,
    scalacOptions ++= Seq("-language:existentials"),
    resolvers += "sonatype-public" at "https://oss.sonatype.org/content/repositories/public",
    // scaladoc fix
    unmanagedClasspath in Compile += Attributed.blank(new java.io.File("doesnotexist"))
  ).
  nativeSettings(
    scalaVersion := scala211,
    crossScalaVersions := Nil
  )

lazy val scoptJS = scopt.js
lazy val scoptJVM = scopt.jvm.settings(dottySettings)
lazy val scoptNative = scopt.native

lazy val nativeTest = project.in(file("nativeTest")).
  dependsOn(scoptNative).
  enablePlugins(ScalaNativePlugin).
  settings(scalaVersion := scala211)

lazy val dottySettings = List(
  scalaVersion := dottyLatestNightlyBuild.get,
  libraryDependencies := libraryDependencies.value.map(_.withDottyCompat()),
  scalacOptions := List("-language:Scala2")
)
