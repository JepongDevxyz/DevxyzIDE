/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/>.
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
import com.jepongdevxyz.idefcs.lsp.java.rewrite.RemoveException
import com.jepongdevxyz.idefcs.lsp.java.utils.CodeActionUtils
import com.jepongdevxyz.idefcs.projects.IProjectManager
import com.jepongdevxyz.idefcs.resources.R
import org.slf4j.LoggerFactory

/** @author Akash Yadav */
class RemoveUnusedThrowsAction : BaseJavaCodeAction() {

  override val id: String = "ide.editor.lsp.java.diagnostics.removeUnusedThrows"
  override var label: String = ""
  private val diagnosticCode = DiagnosticCode.UNUSED_THROWS.id

  override val titleTextRes: Int = R.string.action_remove_unused_throws

  companion object {

    private val log = LoggerFactory.getLogger(RemoveUnusedThrowsAction::class.java)
  }

  override fun prepare(data: ActionData) {
    super.prepare(data)

    if (
      !visible ||
      !data.hasRequiredData(com.jepongdevxyz.idefcs.lsp.models.DiagnosticItem::class.java)
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
    val d = data[com.jepongdevxyz.idefcs.lsp.models.DiagnosticItem::class.java]!!
    val compiler =
      JavaCompilerProvider.get(
        IProjectManager.getInstance().getWorkspace()?.findModuleForFile(data.requireFile(), false)
          ?: return Any()
      )
    val file = data.requirePath()
    return compiler.compile(file).get { task ->
      val notThrown = CodeActionUtils.extractNotThrownExceptionName(d.message)
      val methodWithExtraThrow = CodeActionUtils.findMethod(task, d.range)

      return@get RemoveException(
        methodWithExtraThrow.className,
        methodWithExtraThrow.methodName,
        methodWithExtraThrow.erasedParameterTypes,
        notThrown
      )
    }
  }

  override fun postExec(data: ActionData, result: Any) {
    if (result !is RemoveException) {
      log.warn("Unable to remove unused throws")
      return
    }

    performCodeAction(data, result)
  }
}
