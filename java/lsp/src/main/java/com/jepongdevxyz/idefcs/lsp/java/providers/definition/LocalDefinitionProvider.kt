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

package com.jepongdevxyz.idefcs.lsp.java.providers.definition

import com.jepongdevxyz.idefcs.lsp.api.IServerSettings
import com.jepongdevxyz.idefcs.lsp.java.compiler.JavaCompilerService
import com.jepongdevxyz.idefcs.lsp.java.utils.FindHelper
import com.jepongdevxyz.idefcs.models.Location
import com.jepongdevxyz.idefcs.models.Position
import com.jepongdevxyz.idefcs.progress.ICancelChecker
import jdkx.lang.model.element.Element
import openjdk.source.util.Trees
import java.nio.file.Path

/**
 * Provides definition for local elements.
 *
 * @author Akash Yadav
 */
class LocalDefinitionProvider(
  position: Position,
  completingFile: Path,
  compiler: JavaCompilerService,
  settings: IServerSettings, cancelChecker: ICancelChecker,
) : IJavaDefinitionProvider(position, completingFile, compiler, settings, cancelChecker) {

  override fun doFindDefinition(element: Element): List<Location> {
    return compiler.compile(file).get {
      val trees = Trees.instance(it.task)
      val path = trees.getPath(element)
      if (path == null) {
        log.error("TreePath of element is null. Cannot find definition. Element is {}", element)
        return@get emptyList<Location>()
      }

      var name = element.simpleName
      if (name.contentEquals("<init>")) {
        name = element.enclosingElement.simpleName
      }

      abortIfCancelled()
      return@get listOf(FindHelper.location(it, path, name))
    }
  }
}
