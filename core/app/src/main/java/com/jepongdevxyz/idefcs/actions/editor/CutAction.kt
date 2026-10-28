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

package com.jepongdevxyz.idefcs.actions.editor

import android.content.Context
import com.jepongdevxyz.idefcs.actions.ActionData
import com.jepongdevxyz.idefcs.actions.BaseEditorAction

/** @author Akash Yadav */
class CutAction(context: Context, override val order: Int) : BaseEditorAction() {

  init {
    label = context.getString(android.R.string.cut)

    val arr = context.obtainStyledAttributes(intArrayOf(android.R.attr.actionModeCutDrawable))
    icon = arr.getDrawable(0)?.let { tintDrawable(context, it) }
    arr.recycle()
  }

  override fun prepare(data: ActionData) {
    super.prepare(data)

    if (!visible) {
      return
    }

    visible = getEditor(data)?.isEditable ?: false
    enabled = visible
  }

  override val id: String = "ide.editor.code.text.cut"
  override suspend fun execAction(data: ActionData): Boolean {
    val editor = getEditor(data) ?: return false
    editor.cutText()
    return true
  }
}
