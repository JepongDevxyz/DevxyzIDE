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

package com.jepongdevxyz.idefcs.services.builder

import com.jepongdevxyz.idefcs.tasks.TaskExecutor
import com.jepongdevxyz.idefcs.utils.Environment
import java.io.File
import java.util.Collections
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

/**
 * Lets the IDE build projects that need any SDK platform: when a build fails
 * only because an Android platform is missing from the IDE's SDK, this
 * installs that platform in-app with the command-line tools the setup
 * already installed, reports progress into the build output, and restarts
 * the same build once. Attempted once per platform until a build succeeds,
 * so a failed install can never loop forever.
 */
object SdkPlatformInstaller {

  private const val MAX_TAIL_LINES = 500
  private const val INSTALL_TIMEOUT_MINUTES = 10L

  private val jarPattern = Regex("""platforms/android-(\d+)/android\.jar does not exist""")
  private val pathPattern = Regex("""platforms;android-(\d+)""")
  private val targetPattern = Regex("""target with hash string 'android-(\d+)'""")

  private val tail = ArrayDeque<String>()

  private val installAttempted: MutableSet<Int> =
    Collections.synchronizedSet(mutableSetOf<Int>())

  /** Feed every build output line here; keeps only a bounded tail. */
  fun onOutputLine(line: String) {
    try {
      synchronized(tail) {
        tail.addLast(line)
        while (tail.size > MAX_TAIL_LINES) tail.removeFirst()
      }
    } catch (e: Exception) {
      // Never break the build output stream.
    }
  }

  fun onBuildSucceeded() {
    try {
      synchronized(tail) { tail.clear() }
      installAttempted.clear()
    } catch (e: Exception) {
      // Best-effort state cleanup only.
    }
  }

  /**
   * Called when a build failed. If the failure is a missing SDK platform,
   * installs it and re-runs [tasks] through [service]; all progress is
   * written to the build output via the service's own log channel.
   */
  fun onBuildFailed(service: GradleBuildService, tasks: List<String?>) {
    try {
      val output = synchronized(tail) {
        val joined = tail.joinToString("\n")
        tail.clear()
        joined
      }
      val api = detectMissingPlatform(output) ?: return
      val taskArray = tasks.filterNotNull().toTypedArray()
      if (taskArray.isEmpty()) return
      if (!installAttempted.add(api)) return

      service.logOutput(
        "[DevxyzIDE] Kulang ang Android $api platform para sa project na ito. " +
          "Ini-install ko ito ngayon dito mismo sa app..."
      )
      installPlatform(api) { success, detail ->
        try {
          if (success) {
            service.logOutput(
              "[DevxyzIDE] Naka-install na ang Android $api platform. " +
                "Sisimulan ko ulit ang build."
            )
            // The daemon from the failed build cached the SDK without the
            // new platform; stop it so the restarted build rescans the SDK.
            try {
              val projectPath = try {
                com.jepongdevxyz.idefcs.projects.IProjectManager.getInstance()
                  .projectDirPath
              } catch (e: Exception) {
                null
              }
              if (projectPath != null) {
                stopGradleDaemons(projectPath)
              }
            } catch (e: Exception) {
              // Best-effort; the restart proceeds either way.
            }
            service.executeTasks(*taskArray)
          } else {
            service.logOutput(
              "[DevxyzIDE] Hindi ko na-install ang Android $api platform ($detail). " +
                "Manual na paraan sa Terminal: " + manualCommand(api)
            )
          }
        } catch (e: Exception) {
          // Reporting/restarting must never break the service.
        }
      }
    } catch (e: Exception) {
      // The auto-install is best-effort only.
    }
  }

  private fun detectMissingPlatform(output: String): Int? {
    return jarPattern.find(output)?.groupValues?.get(1)?.toIntOrNull()
      ?: pathPattern.find(output)?.groupValues?.get(1)?.toIntOrNull()
      ?: targetPattern.find(output)?.groupValues?.get(1)?.toIntOrNull()
  }

  private fun manualCommand(api: Int): String {
    val manager = findSdkManager()
    return if (manager != null) {
      "yes | ${manager.absolutePath} --sdk_root=${Environment.ANDROID_HOME.absolutePath} " +
        "\"platforms;android-$api\""
    } else {
      "sdkmanager --sdk_root=${Environment.ANDROID_HOME.absolutePath} \"platforms;android-$api\""
    }
  }

  private fun isPlatformInstalled(api: Int): Boolean {
    return try {
      File(Environment.ANDROID_HOME, "platforms/android-$api/android.jar").isFile
    } catch (e: Exception) {
      false
    }
  }

  private fun findSdkManager(): File? {
    val toolsRoot = File(Environment.ANDROID_HOME, "cmdline-tools")
    if (!toolsRoot.isDirectory) {
      return null
    }
    val candidates = listOf(
      File(toolsRoot, "latest/bin/sdkmanager"),
      File(toolsRoot, "tools/bin/sdkmanager"),
      File(toolsRoot, "bin/sdkmanager")
    )
    for (candidate in candidates) {
      if (candidate.isFile) {
        return candidate
      }
    }
    return toolsRoot.walkTopDown().maxDepth(4).firstOrNull {
      it.isFile && it.name == "sdkmanager"
    }
  }

  private fun installPlatform(api: Int, onDone: (Boolean, String) -> Unit) {
    TaskExecutor.executeAsync(
      Callable { installBlocking(api) }
    ) { result ->
      val (success, detail) = result ?: (false to "walang resulta ang installer")
      onDone(success, detail)
    }
  }

  private fun installBlocking(api: Int): Pair<Boolean, String> {
    if (isPlatformInstalled(api)) {
      return true to "naka-install na"
    }

    val manager = findSdkManager()
      ?: return false to "hindi makita ang sdkmanager sa cmdline-tools ng IDE"

    return try {
      writeLicenses()

      val androidHome = Environment.ANDROID_HOME.absolutePath
      val processBuilder = ProcessBuilder(
        "sh", manager.absolutePath,
        "--sdk_root=$androidHome",
        "platforms;android-$api"
      )
      val env = processBuilder.environment()
      env["JAVA_HOME"] = Environment.JAVA_HOME.absolutePath
      env["ANDROID_HOME"] = androidHome
      env["ANDROID_SDK_ROOT"] = androidHome
      env["HOME"] = Environment.HOME.absolutePath
      // The bundled JDK is a Termux build: its java cannot even start unless
      // the linker can see the prefix libraries (libz.so.1 for libjli.so).
      // Mirror the environment the IDE's own ToolingServerRunner uses.
      env["LD_LIBRARY_PATH"] = Environment.PREFIX.absolutePath + "/lib"
      env["TMPDIR"] = Environment.TMP_DIR.absolutePath
      env["CURL_CA_BUNDLE"] = Environment.PREFIX.absolutePath + "/etc/tls/cert.pem"
      env["SSL_CERT_FILE"] = Environment.PREFIX.absolutePath + "/etc/tls/cert.pem"
      env["PATH"] = Environment.JAVA_HOME.absolutePath + "/bin:" +
        Environment.BIN_DIR.absolutePath + ":" + (env["PATH"] ?: "/system/bin")
      processBuilder.directory(Environment.ANDROID_HOME)
      processBuilder.redirectErrorStream(true)

      val process = processBuilder.start()

      val stdinThread = Thread {
        try {
          process.outputStream.bufferedWriter().use { writer ->
            while (process.isAlive) {
              writer.write("y\n")
              writer.flush()
              Thread.sleep(500)
            }
          }
        } catch (e: Exception) {
          // The process ended; nothing more to answer.
        }
      }
      stdinThread.isDaemon = true
      stdinThread.start()

      val output = StringBuilder()
      val stdoutThread = Thread {
        try {
          process.inputStream.bufferedReader().use { reader ->
            while (true) {
              val line = reader.readLine() ?: break
              synchronized(output) {
                if (output.length < 8000) output.append(line).append('\n')
              }
            }
          }
        } catch (e: Exception) {
          // Stream closed.
        }
      }
      stdoutThread.isDaemon = true
      stdoutThread.start()

      val finished = process.waitFor(INSTALL_TIMEOUT_MINUTES, TimeUnit.MINUTES)
      if (!finished) {
        process.destroyForcibly()
        return false to "nag-timeout ang pag-install matapos ang $INSTALL_TIMEOUT_MINUTES minuto"
      }
      stdoutThread.join(2000)

      if (isPlatformInstalled(api)) {
        true to "na-install"
      } else {
        val tailLine = synchronized(output) { output.toString() }.trim().lines()
          .lastOrNull { it.isNotBlank() }
        false to "exit ${process.exitValue()}" + (if (tailLine != null) ": $tailLine" else "")
      }
    } catch (e: Exception) {
      false to (e.message ?: "nagkaroon ng error ang installer")
    }
  }

  /**
   * Stops the Gradle daemon(s) for this IDE's Gradle user home: the
   * daemon caches the SDK platform list for its whole lifetime, so a
   * platform installed after the failed build stays invisible to the
   * restarted build unless the daemon is stopped first. Best-effort.
   */
  fun stopGradleDaemons(projectPath: String) {
    try {
      val projectDir = File(projectPath)
      val gradlew = File(projectDir, "gradlew")
      val command: List<String>? = when {
        gradlew.isFile -> listOf("sh", gradlew.absolutePath, "--stop")
        else -> findGradleDistribution()?.let {
          listOf("sh", File(it, "bin/gradle").absolutePath, "--stop")
        }
      }
      if (command == null) {
        return
      }
      val processBuilder = ProcessBuilder(command)
      processBuilder.directory(projectDir)
      val env = processBuilder.environment()
      env["JAVA_HOME"] = Environment.JAVA_HOME.absolutePath
      env["ANDROID_HOME"] = Environment.ANDROID_HOME.absolutePath
      env["ANDROID_SDK_ROOT"] = Environment.ANDROID_HOME.absolutePath
      env["GRADLE_USER_HOME"] = Environment.GRADLE_USER_HOME.absolutePath
      env["HOME"] = Environment.HOME.absolutePath
      env["LD_LIBRARY_PATH"] = Environment.PREFIX.absolutePath + "/lib"
      env["TMPDIR"] = Environment.TMP_DIR.absolutePath
      env["PATH"] = Environment.JAVA_HOME.absolutePath + "/bin:" +
        Environment.BIN_DIR.absolutePath + ":" + (env["PATH"] ?: "/system/bin")
      processBuilder.redirectErrorStream(true)
      val process = processBuilder.start()
      try {
        process.outputStream.close()
      } catch (e: Exception) {
        // No input needed.
      }
      Thread {
        try {
          process.inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
          // Stream closed.
        }
      }.apply { isDaemon = true }.start()
      val finished = process.waitFor(60, TimeUnit.SECONDS)
      if (!finished) {
        process.destroyForcibly()
      }
    } catch (e: Exception) {
      // Best-effort only.
    }
  }

  /** Finds an unpacked Gradle distribution in the IDE's Gradle user home. */
  private fun findGradleDistribution(): File? {
    return try {
      val dists = File(Environment.GRADLE_USER_HOME, "wrapper/dists")
      if (!dists.isDirectory) {
        return null
      }
      dists.walkTopDown().maxDepth(4).firstOrNull {
        it.isDirectory && File(it, "bin/gradle").isFile
      }
    } catch (e: Exception) {
      null
    }
  }

  private fun writeLicenses() {
    val licensesDir = File(Environment.ANDROID_HOME, "licenses")
    if (!licensesDir.exists()) {
      licensesDir.mkdirs()
    }
    val sdkLicense = File(licensesDir, "android-sdk-license")
    if (!sdkLicense.exists()) {
      sdkLicense.writeText(
        "\n8933bad161af4178b1185d1a37fbf41ea5269c55\n" +
          "d56f5187479451eabf01fb78af6dfcb131a6481e\n" +
          "24333f8a63b6825ea9c5514f83c2829b004d1fee\n"
      )
    }
  }
}
