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
import android.view.View
import android.widget.PopupMenu
import androidx.core.widget.PopupMenuCompat.getDragToOpenListener
import com.jepongdevxyz.idefcs.actions.ActionData
import com.jepongdevxyz.idefcs.actions.ActionItem
import com.jepongdevxyz.idefcs.actions.ActionsRegistry
import com.jepongdevxyz.idefcs.actions.FillMenuParams

/**
 * Utility class to show a popup menu with [com.jepongdevxyz.idefcs.actions.ActionsRegistry].
 *
 * @author Akash Yadav
 */
object ActionMenuUtils {

  @JvmStatic
  @JvmOverloads
  fun createMenu(
    context: Context,
    anchor: View,
    location: ActionItem.Location,
    dragToOpen: Boolean = false
  ): PopupMenu {
    return PopupMenu(context, anchor).apply {
      val data = ActionData()
      data.put(Context::class.java, context)
      ActionsRegistry.getInstance().fillMenu(FillMenuParams(data, location, menu))

      if (dragToOpen) {
        anchor.setOnTouchListener(getDragToOpenListener(this))
      }
    }
  }
}
