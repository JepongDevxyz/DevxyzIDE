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

package com.jepongdevxyz.idefcs.lsp.java.edits

import com.jepongdevxyz.idefcs.editor.api.ILspEditor
import com.jepongdevxyz.idefcs.lsp.edits.DefaultEditHandler
import com.jepongdevxyz.idefcs.lsp.models.Command
import io.github.rosemoe.sora.widget.CodeEditor

/**
 * Implementation of [DefaultEditHandler] which avoids reflection in
 * [DefaultEditHandler.executeCommand].
 *
 * @author Akash Yadav
 */
open class BaseJavaEditHandler : DefaultEditHandler() {

  override fun executeCommand(editor: CodeEditor, command: Command?) {
    if (editor is ILspEditor) {
      editor.executeCommand(command)
      return
    }
    super.executeCommand(editor, command)
  }
}
