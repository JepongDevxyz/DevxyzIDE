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

package com.jepongdevxyz.idefcs.inflater.utils

import com.jepongdevxyz.idefcs.projects.IProjectManager
import com.jepongdevxyz.idefcs.projects.android.AndroidModule
import java.io.File

private var currentModule: AndroidModule? = null
var isParsing: Boolean = false
  private set

val module: AndroidModule
  get() =
    currentModule ?: throw IllegalStateException("You must call startParse(AndroidModule) first")

fun startParse(file: File) {
  if (isParsing) {
    return
  }
  (IProjectManager.getInstance().getWorkspace()?.findModuleForFile(file, false) as? AndroidModule)?.let {
    startParse(it)
  }
}

fun startParse(m: AndroidModule) {
  currentModule = m
  isParsing = true
}

fun endParse() {
  currentModule = null
  isParsing = false
}
