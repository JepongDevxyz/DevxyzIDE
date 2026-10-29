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

package com.jepongdevxyz.idefcs.inflater

import com.android.SdkConstants.ANDROID_NS_NAME
import com.android.SdkConstants.ANDROID_URI
import com.jepongdevxyz.idefcs.inflater.internal.NamespaceImpl

/**
 * An XML namespace declaration.
 *
 * @author Akash Yadav
 */
interface INamespace {

  /** The prefix of the namespace. */
  val prefix: String

  /** The namespace URI. */
  val uri: String

  companion object {
    /** The Android namespace. */
    @JvmField val ANDROID: INamespace = NamespaceImpl(ANDROID_NS_NAME, ANDROID_URI)
  }
}
