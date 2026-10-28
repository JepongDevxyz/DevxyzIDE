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

package com.jepongdevxyz.idefcs.projects.models

import com.jepongdevxyz.idefcs.projects.util.StringSearch
import java.nio.file.Path
import java.time.Instant

/**
 * An active java document.
 *
 * @author Akash Yadav
 */
class ActiveJavaDocument(file: Path, content: String, version: Int, modified: Instant) :
  ActiveDocument(file = file, version = version, modified = modified, content = content) {

  val packageName: String = StringSearch.packageName(reader())
}
