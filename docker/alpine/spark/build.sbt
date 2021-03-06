prpLookup += baseDirectory.value.getParentFile -> "alpine"

imageNames in docker := Seq(ImageName(prp.value("spark")))

val hadoop = (project in file("..")/"hadoop").enablePlugins(MosaicoDockerPlugin)

dockerfile in docker := {
  Def.sequential(
    download.toTask(s" @spark.url spark.tgz"),
    download.toTask(s" @spark.slf4j.url slf4j-api.jar")
  ).value
  val base = baseDirectory.value
  new Dockerfile {
    from((docker in hadoop).value.toString)
    add(base/"run.sh", "/services/spark/run")
    add(base/"spark.tgz", "/usr")
    runRaw("ln -sf /usr/spark-* /usr/spark ; chmod +x /usr/spark/bin/* /usr/spark/sbin/* /services/spark/run")
    add(base/"slf4j-api.jar", "/usr/spark/jars")
  }
}
