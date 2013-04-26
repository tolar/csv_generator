// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers ++= Seq("Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "sonatype-public" at "https://oss.sonatype.org/content/groups/public",
  "repo.codahale.com" at "http://repo.codahale.com"
)


// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.1.0")

addSbtPlugin("org.scalaxb" % "sbt-scalaxb" % "1.0.2")




