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


import com.jepongdevxyz.idefcs.build.config.BuildConfig

plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
}



android {
  namespace = "${BuildConfig.packageName}.templates"
}

dependencies {
  api(libs.composite.javapoet)

  api(projects.core.common)
  api(projects.core.resources)
  api(projects.logging.logger)
  api(projects.xml.dom)
  api(projects.xml.utils)

  api(libs.aapt2.common)
  api(libs.androidx.annotation)
  api(libs.androidx.appcompat)
  api(libs.google.material)
}