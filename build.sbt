scalaVersion := "2.11.7"

enablePlugins(org.nlogo.build.NetLogoExtension)

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xlint", "-Xfatal-warnings", "-encoding", "UTF8")

name := "rnd"

netLogoExtName := "rnd"

netLogoClassManager := "org.nlogo.extensions.rnd.RndExtension"

netLogoZipSources := false

netLogoTarget :=
    org.nlogo.build.NetLogoExtension.directoryTarget(baseDirectory.value)

netLogoVersion := "6.0.0-M8"
