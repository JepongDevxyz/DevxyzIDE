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

package com.jepongdevxyz.idefcs.plugins.util

import com.jepongdevxyz.idefcs.build.config.BuildConfig
import com.android.build.api.variant.AndroidComponentsExtension
import java.io.File

/**
 * @author Akash Yadav
 */
object SdkUtils {

  fun AndroidComponentsExtension<*, *, *>.getAndroidJar(assertExists: Boolean = true): File {
    val sdkDirectory = sdkComponents.sdkDirectory
    val androidJar = File(sdkDirectory.get().asFile,
      "platforms/android-${BuildConfig.compileSdk}/android.jar")

    if (assertExists) {
      check(androidJar.exists() && androidJar.isFile) {
        "$androidJar does not exist or is not a file."
      }
    }

    return androidJar
  }
}