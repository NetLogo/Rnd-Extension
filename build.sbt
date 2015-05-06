scalaVersion := "2.9.3"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings", "-encoding", "UTF8")

libraryDependencies ++= Seq(
  "org.nlogo" % "NetLogo" % "5.2.0" from "http://ccl.northwestern.edu/netlogo/5.2.0/NetLogo.jar"
)

name := "rnd"

NetLogoExtension.settings

NetLogoExtension.classManager := "org.nlogo.extensions.rnd.RndExtension"
