scalaVersion := "2.9.3"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings", "-encoding", "UTF8")

libraryDependencies ++= Seq(
  "org.nlogo" % "NetLogoLite" % "5.0.4" from "http://ccl.northwestern.edu/netlogo/5.0.4/NetLogoLite.jar"
)

name := "rnd"

NetLogoExtension.settings

NetLogoExtension.classManager := "org.nlogo.extensions.rnd.RndExtension"
