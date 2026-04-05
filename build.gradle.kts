plugins {
  id("mod.base-conventions")
}

tasks {
  compileJava {
    options.encoding = "UTF-8"
  }
}

dependencies {
  paperweight.paperDevBundle(libs.versions.paper)

  compileOnly(libs.ignite)
  compileOnly(libs.mixin)
  compileOnly(libs.mixinExtras)

  annotationProcessor(libs.mixinExtras)
}
