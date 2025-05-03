import org.nlogo.build.{ NetLogoExtension, ExtensionDocumentationPlugin }

enablePlugins(NetLogoExtension, ExtensionDocumentationPlugin)

name := "rnd"
version := "3.0.1"
isSnapshot := true

scalaVersion := "2.13.16"
scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xlint", "-Xfatal-warnings", "-encoding", "UTF8", "-release", "11")

netLogoExtName := "rnd"
netLogoClassManager := "org.nlogo.extensions.rnd.RndExtension"
netLogoVersion := "7.0.0-internal1"
