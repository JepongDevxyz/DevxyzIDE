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

package com.jepongdevxyz.idefcs.actions.build

import android.content.Context
import androidx.core.content.ContextCompat
import com.jepongdevxyz.idefcs.actions.ActionData
import com.jepongdevxyz.idefcs.actions.BaseBuildAction
import com.jepongdevxyz.idefcs.fragments.RunTasksDialogFragment
import com.jepongdevxyz.idefcs.resources.R

/** @author Akash Yadav */
class RunTasksAction(context: Context, override val order: Int) : BaseBuildAction() {
  override val id: String = "ide.editor.build.runTasks"
  private var dialog: RunTasksDialogFragment? = null

  init {
    label = context.getString(R.string.title_run_tasks)
    icon = ContextCompat.getDrawable(context, R.drawable.ic_run_tasks)
  }

  override suspend fun execAction(data: ActionData): Any {
    dialog?.dismiss()
    dialog = null
    dialog = RunTasksDialogFragment()
    return dialog!!
  }

  override fun postExec(data: ActionData, result: Any) {
    if (result !is RunTasksDialogFragment) {
      return
    }

    val activity = data.getActivity()!!
    result.show(activity.supportFragmentManager, this.id)
  }
  
  override fun destroy() {
    super.destroy()
    try {
      dialog?.dismiss()
    } catch (e: Exception) {
      // ignored
    }
    dialog = null
  }
}
