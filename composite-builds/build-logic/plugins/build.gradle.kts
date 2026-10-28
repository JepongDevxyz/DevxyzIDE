/*
 *  This file is part of DevxyzIDE.
 *
 *  DevxyzIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  DevxyzIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with DevxyzIDE.  If not, see <https://www.gnu.org/licenses/>.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `kotlin-dsl`
}

repositories {
  google()
  gradlePluginPortal()
  mavenCentral()
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "17"
}

dependencies {
  implementation(projects.buildLogic.common)
  implementation(projects.buildLogic.desugaring)
  implementation(projects.buildLogic.propertiesParser)

  implementation("com.android.tools.build:gradle:${libs.versions.agp.asProvider().get()}")
  implementation(libs.maven.publish)

  implementation(libs.common.jkotlin)
  implementation(libs.common.antlr4)
  implementation(libs.google.gson)
  implementation(libs.google.java.format)
}

gradlePlugin {
  plugins {
    create("com.jepongdevxyz.idefcs.build") {
      id = "com.jepongdevxyz.idefcs.build"
      implementationClass = "com.jepongdevxyz.idefcs.plugins.DevxyzIDEPlugin"
    }
    create("com.jepongdevxyz.idefcs.core-app") {
      id = "com.jepongdevxyz.idefcs.core-app"
      implementationClass = "com.jepongdevxyz.idefcs.plugins.DevxyzIDECoreAppPlugin"
    }
    create("com.jepongdevxyz.idefcs.build.propsparser") {
      id = "com.jepongdevxyz.idefcs.build.propsparser"
      implementationClass = "com.jepongdevxyz.idefcs.plugins.PropertiesParserPlugin"
    }
    create("com.jepongdevxyz.idefcs.build.lexergenerator") {
      id = "com.jepongdevxyz.idefcs.build.lexergenerator"
      implementationClass = "com.jepongdevxyz.idefcs.plugins.LexerGeneratorPlugin"
    }
  }
}
