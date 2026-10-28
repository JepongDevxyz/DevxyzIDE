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

package com.jepongdevxyz.idefcs.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources.Theme
import android.util.TypedValue

fun Context.isSystemInDarkMode(): Boolean {
  return this.resources.configuration.isSystemInDarkMode()
}

fun Configuration.isSystemInDarkMode(): Boolean {
  return (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}

@JvmOverloads
fun Context.resolveAttr(id: Int, resolveRefs: Boolean = true): Int {
  return theme.resolveAttr(id, resolveRefs)
}

@JvmOverloads
fun Theme.resolveAttr(id: Int, resolveRefs: Boolean = true): Int =
  TypedValue().let {
    resolveAttribute(id, it, resolveRefs)
    it.data
  }
