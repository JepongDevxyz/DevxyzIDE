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
package com.jepongdevxyz.idefcs.lsp.java.actions.diagnostics

import com.jepongdevxyz.idefcs.actions.ActionData
import com.jepongdevxyz.idefcs.actions.hasRequiredData
import com.jepongdevxyz.idefcs.actions.markInvisible
import com.jepongdevxyz.idefcs.actions.requireFile
import com.jepongdevxyz.idefcs.actions.requirePath
import com.jepongdevxyz.idefcs.lsp.java.JavaCompilerProvider
import com.jepongdevxyz.idefcs.lsp.java.actions.BaseJavaCodeAction
import com.jepongdevxyz.idefcs.lsp.java.models.DiagnosticCode
import com.jepongdevxyz.idefcs.lsp.java.rewrite.RemoveClass
import com.jepongdevxyz.idefcs.lsp.java.utils.CodeActionUtils.findPosition
import com.jepongdevxyz.idefcs.projects.IProjectManager
import com.jepongdevxyz.idefcs.resources.R
import org.slf4j.LoggerFactory

/** @author Akash Yadav */
class RemoveClassAction : BaseJavaCodeAction() {

  override val id: String = "ide.editor.lsp.java.diagnostics.removeClass"
  override var label: String = ""
  private val diagnosticCode = DiagnosticCode.UNUSED_CLASS.id

  override val titleTextRes: Int = R.string.action_remove_class

  companion object {

    private val log = LoggerFactory.getLogger(RemoveClassAction::class.java)
  }

  override fun prepare(data: ActionData) {
    super.prepare(data)

    if (!visible || !data.hasRequiredData(
        com.jepongdevxyz.idefcs.lsp.models.DiagnosticItem::class.java)
    ) {
      markInvisible()
      return
    }

    val diagnostic = data[com.jepongdevxyz.idefcs.lsp.models.DiagnosticItem::class.java]!!
    if (diagnosticCode != diagnostic.code) {
      markInvisible()
      return
    }
  }

  override suspend fun execAction(data: ActionData): Any {
    val diagnostic = data[com.jepongdevxyz.idefcs.lsp.models.DiagnosticItem::class.java]!!
    val compiler =
      JavaCompilerProvider.get(
        IProjectManager.getInstance().getWorkspace()?.findModuleForFile(data.requireFile(), false)
          ?: return Any()
      )
    val file = data.requirePath()

    return compiler.compile(file).get {
      RemoveClass(file, findPosition(it, diagnostic.range.start))
    }
  }

  override fun postExec(data: ActionData, result: Any) {
    if (result !is RemoveClass) {
      log.warn("Unable to remove class")
      return
    }

    performCodeAction(data, result)
  }
}
