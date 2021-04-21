scalaVersion := "2.12.12"

enablePlugins(org.nlogo.build.NetLogoExtension, org.nlogo.build.ExtensionDocumentationPlugin)

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xlint", "-Xfatal-warnings", "-encoding", "UTF8")

name := "rnd"

version := "3.0.1"

netLogoExtName := "rnd"

netLogoClassManager := "org.nlogo.extensions.rnd.RndExtension"

netLogoZipSources := false

netLogoTarget :=
    org.nlogo.build.NetLogoExtension.directoryTarget(baseDirectory.value)

resolvers      += "netlogo" at "https://dl.cloudsmith.io/public/netlogo/netlogo/maven/"
netLogoVersion := "6.2.0-d27b502"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.1" % "test",
  "org.ow2.asm" % "asm-all" % "5.0.4" % "test",
  "org.picocontainer" % "picocontainer" % "2.13.6" % "test",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)

val testDirectory = settingKey[File]("directory that extension is copied to for testing")

testDirectory := {
  baseDirectory.value / "extensions" / netLogoExtName.value
}

val copyToTestDir = taskKey[Unit]("copy to ./extension/{name} folder for running language tests")

copyToTestDir := {
  (packageBin in Compile).value
  NetLogoExtension
    .directoryTarget(testDirectory.value)
    .create(NetLogoExtension.netLogoPackagedFiles.value)
}

test in Test := {
  copyToTestDir.value
  (test in Test).value
  IO.delete(testDirectory.value)
}

// allow language tests to run from sbt
test in Test := {
  val _ = (packageBin in Compile).value
  (test in Test).value
}
