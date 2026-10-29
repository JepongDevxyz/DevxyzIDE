package com.jepongdevxyz.idefcs.build.config/*
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

import org.gradle.api.JavaVersion

/**
 * Build configuration for the IDE.
 *
 * @author Akash Yadav
 */
object BuildConfig {

  /** DevxyzIDE's package name. */
  const val packageName = "com.jepongdevxyz.idefcs"

  /** The compile SDK version. */
  const val compileSdk = 34

  /** The minimum SDK version. */
  const val minSdk = 26

  /** The target SDK version. */
  const val targetSdk = 28

  const val ndkVersion = "26.1.10909125"

  /** The source and target Java compatibility. */
  val javaVersion = JavaVersion.VERSION_11
}
