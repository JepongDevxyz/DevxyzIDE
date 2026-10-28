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

package com.jepongdevxyz.idefcs.xml.versions

import com.jepongdevxyz.idefcs.utils.ServiceLoader
import com.jepongdevxyz.idefcs.xml.registry.XmlRegistry

/**
 * Registry that reads information about the API versions of classes, their fields and methods.
 *
 * The following information is stored :
 * - Since
 * - Removed
 * - Deprecated
 * @author Akash Yadav
 */
interface ApiVersionsRegistry : XmlRegistry<ApiVersions> {

  companion object {

    private var sInstance: ApiVersionsRegistry? = null

    /** Get the default instance of [ApiVersionsRegistry]. */
    @JvmStatic
    fun getInstance(): ApiVersionsRegistry {
      val klass = ApiVersionsRegistry::class.java
      return sInstance ?: ServiceLoader.load(klass, klass.classLoader).findFirstOrThrow()
        .also { sInstance = it }
    }
  }
}
