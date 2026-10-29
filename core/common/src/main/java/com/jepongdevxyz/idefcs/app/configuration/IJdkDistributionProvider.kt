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

package com.jepongdevxyz.idefcs.app.configuration

import androidx.annotation.WorkerThread
import com.jepongdevxyz.idefcs.models.JdkDistribution
import com.jepongdevxyz.idefcs.utils.ServiceLoader

/**
 * Provides information about various JDK distributions installed on the device.
 *
 * @author Akash Yadav
 */
interface IJdkDistributionProvider {

  /**
   * The list of JDK distributions installed on the device.
   */
  val installedDistributions: List<JdkDistribution>

  /**
   * Reloads the installed JDK distributions. This function is synchronous and should not be called
   * on the UI thread.
   */
  @WorkerThread
  fun loadDistributions()

  /**
   * Get the [JdkDistribution] instance for the given java version.
   *
   * @return The [JdkDistribution] instance for the given java version, or `null` if no such
   * distribution is found.
   */
  fun forVersion(javaVersion: String) : JdkDistribution? =
    installedDistributions.firstOrNull { it.javaVersion == javaVersion }


  /**
   * Get the [JdkDistribution] instance for the given java home.
   *
   * @return The [JdkDistribution] instance for the given java home, or `null` if no such
   * distribution is found.
   */
  fun forJavaHome(javaHome: String) : JdkDistribution? =
    installedDistributions.firstOrNull { it.javaHome == javaHome }

  companion object {

    /**
     * The default java version.
     */
    const val DEFAULT_JAVA_VERSION = "17"

    private val _instance by lazy {
      ServiceLoader.load(
        IJdkDistributionProvider::class.java,
        IJdkDistributionProvider::class.java.classLoader
      ).findFirstOrThrow()
    }

    /**
     * Get instance of [IJdkDistributionProvider].
     */
    @JvmStatic
    fun getInstance(): IJdkDistributionProvider = _instance
  }
}