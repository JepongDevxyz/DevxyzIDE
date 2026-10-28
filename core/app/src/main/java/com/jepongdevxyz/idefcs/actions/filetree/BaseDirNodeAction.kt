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

package com.jepongdevxyz.idefcs.actions.filetree

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jepongdevxyz.idefcs.actions.ActionData
import com.jepongdevxyz.idefcs.actions.markInvisible
import com.jepongdevxyz.idefcs.actions.requireFile

/**
 * Base class for action items for directory nodes.
 *
 * @author Akash Yadav
 */
abstract class BaseDirNodeAction(context: Context,
  @StringRes labelRes: Int? = null,
  @DrawableRes iconRes: Int? = null) : BaseFileTreeAction(context, labelRes, iconRes) {

  override fun prepare(data: ActionData) {
    super.prepare(data)
    if (!data.hasFileTreeData()) {
      markInvisible()
      return
    }

    val file = data.requireFile()
    if (!file.isDirectory) {
      markInvisible()
      return
    }

    visible = true
    enabled = true
  }
}
