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
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    namespace = "${BuildConfig.packageName}.xml.utils"
}

dependencies {
    kapt(libs.google.auto.service)

    api(projects.utilities.lookup)
    api(projects.xml.resourcesApi)

    implementation(libs.common.kotlin)
    implementation(libs.composite.jdt)
    implementation(libs.composite.jaxp)
    implementation(libs.google.auto.service.annotations)

    implementation(projects.core.common)
    implementation(projects.logging.logger)
    implementation(projects.xml.aaptcompiler)
    
    testImplementation(libs.tests.junit)
    testImplementation(libs.tests.robolectric)
    testImplementation(libs.tests.google.truth)
}