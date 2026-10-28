/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jepongdevxyz.idefcs.plugins

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.jepongdevxyz.idefcs.build.config.BuildConfig
import com.jepongdevxyz.idefcs.build.config.downloadVersion
import com.jepongdevxyz.idefcs.plugins.tasks.AddAndroidJarToAssetsTask
import com.jepongdevxyz.idefcs.plugins.tasks.AddFileToAssetsTask
import com.jepongdevxyz.idefcs.plugins.tasks.GenerateInitScriptTask
import com.jepongdevxyz.idefcs.plugins.tasks.GradleWrapperGeneratorTask
import com.jepongdevxyz.idefcs.plugins.tasks.SetupAapt2Task
import com.jepongdevxyz.idefcs.plugins.util.SdkUtils.getAndroidJar
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.publish.maven.tasks.GenerateMavenPom
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.bundling.Jar
import org.gradle.configurationcache.extensions.capitalized

/**
 * Handles asset copying and generation.
 *
 * @author Akash Yadav
 */
class AndroidIDEAssetsPlugin : Plugin<Project> {

  override fun apply(target: Project) {
    target.run {
      // :tooling:plugin is not a dependency of the app, so without this it
      // is evaluated after the app project. Its jar and pom tasks must
      // already exist when the app variants are configured, otherwise
      // looking them up fails with "Task with name 'jar' not found".
      evaluationDependsOn(":tooling:plugin")

      val wrapperGeneratorTaskProvider = tasks.register("generateGradleWrapper",
        GradleWrapperGeneratorTask::class.java)

      val androidComponentsExtension = extensions.getByType(
        ApplicationAndroidComponentsExtension::class.java)

      val setupAapt2TaskTaskProvider = tasks.register("setupAapt2", SetupAapt2Task::class.java)

      val addAndroidJarTaskProvider = tasks.register("addAndroidJarToAssets",
        AddAndroidJarToAssetsTask::class.java) {
        androidJar = androidComponentsExtension.getAndroidJar(assertExists = true)
      }

      androidComponentsExtension.onVariants { variant ->

        val variantNameCapitalized = variant.name.capitalized()

        variant.sources.jniLibs?.addGeneratedSourceDirectory(setupAapt2TaskTaskProvider,
          SetupAapt2Task::outputDirectory)

        variant.sources.assets?.addGeneratedSourceDirectory(wrapperGeneratorTaskProvider,
          GradleWrapperGeneratorTask::outputDirectory)

        variant.sources.assets?.addGeneratedSourceDirectory(addAndroidJarTaskProvider,
          AddAndroidJarToAssetsTask::outputDirectory)

        // Init script generator
        val generateInitScript = tasks.register("generate${variantNameCapitalized}InitScript",
          GenerateInitScriptTask::class.java) {
          mavenGroupId.set(BuildConfig.packageName)
          downloadVersion.set(this@run.downloadVersion)
        }

        variant.sources.assets?.addGeneratedSourceDirectory(generateInitScript,
          GenerateInitScriptTask::outputDir)

        // Tooling API JAR copier
        val copyToolingApiJar = tasks.register("copy${variantNameCapitalized}ToolingApiJar",
          AddFileToAssetsTask::class.java) {
          val implPath = ":tooling:impl"
          val toolingApi = checkNotNull(rootProject.findProject(implPath)) {
            "Cannot find the Tooling Impl module with project path: '$implPath'"
          }
          dependsOn(toolingApi.tasks.getByName("copyJar"))

          val toolingApiJar = toolingApi.layout.buildDirectory.file("libs/tooling-api-all.jar")

          inputFile.set(toolingApiJar)
          baseAssetsPath.set("data/common")
        }

        variant.sources.assets?.addGeneratedSourceDirectory(copyToolingApiJar,
          AddFileToAssetsTask::outputDirectory)

        // Local Gradle plugin JAR/POM copiers. The plugin jar and its generated
        // pom are bundled as assets so the IDE can resolve the build plugin
        // from the device instead of a remote repository.
        // Use lazy task providers for the plugin tasks, which exist by now
        // because :tooling:plugin is evaluated before the app (see above).
        val localPluginProject = checkNotNull(rootProject.findProject(":tooling:plugin")) {
          "Cannot find the Tooling Plugin module with project path: ':tooling:plugin'"
        }
        val localPluginJarTask = localPluginProject.tasks.named("jar", Jar::class.java)
        // The java-gradle-plugin names its publication "pluginMaven", so
        // its generated pom task is generatePomFileForPluginMavenPublication,
        // not generatePomFileForMavenPublication.
        val localPluginPomTask = localPluginProject.tasks.named(
          "generatePomFileForPluginMavenPublication", GenerateMavenPom::class.java)

        val copyLocalPluginJar = tasks.register("copy${variantNameCapitalized}LocalPluginJar",
          AddFileToAssetsTask::class.java) {
          dependsOn(localPluginJarTask)
          inputFile.set(localPluginJarTask.flatMap { it.archiveFile })
          fileName.set("gradle-plugin.jar")
          baseAssetsPath.set("data/common/local-plugin")
        }

        variant.sources.assets?.addGeneratedSourceDirectory(copyLocalPluginJar,
          AddFileToAssetsTask::outputDirectory)

        // GenerateMavenPom.destination is a plain File, not a file property,
        // so wrap the lazy task output with the project layout to keep the
        // pom location lazy.
        val localPluginPomFile = localPluginProject.layout.file(
          localPluginPomTask.map { it.destination })

        val copyLocalPluginPom = tasks.register("copy${variantNameCapitalized}LocalPluginPom",
          AddFileToAssetsTask::class.java) {
          dependsOn(localPluginPomTask)
          inputFile.set(localPluginPomFile)
          fileName.set("gradle-plugin.pom")
          baseAssetsPath.set("data/common/local-plugin")
        }

        variant.sources.assets?.addGeneratedSourceDirectory(copyLocalPluginPom,
          AddFileToAssetsTask::outputDirectory)

        // Local plugin version file. The version is the same downloadVersion
        // used for the init script classpath, not a hardcoded value.
        val writeLocalPluginVersion = tasks.register(
          "write${variantNameCapitalized}LocalPluginVersion",
          LocalPluginVersionTask::class.java) {
          version.set(this@run.downloadVersion)
        }

        variant.sources.assets?.addGeneratedSourceDirectory(writeLocalPluginVersion,
          LocalPluginVersionTask::outputDirectory)
      }
    }
  }
}

/**
 * Writes the local plugin version to assets as data/common/local-plugin-version.txt.
 * The version is supplied by the caller (the same downloadVersion used for the
 * init script classpath) and is written with no extra content.
 */
abstract class LocalPluginVersionTask : DefaultTask() {

  @get:Input
  abstract val version: Property<String>

  @get:OutputDirectory
  abstract val outputDirectory: DirectoryProperty

  @TaskAction
  fun write() {
    val outFile = outputDirectory.file("data/common/local-plugin-version.txt").get().asFile
      .also { it.parentFile.mkdirs() }
    outFile.writeText(version.get())
  }
}
