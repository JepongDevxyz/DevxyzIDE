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

package com.jepongdevxyz.idefcs.uidesigner.undo

import com.jepongdevxyz.idefcs.inflater.IView
import com.jepongdevxyz.idefcs.uidesigner.models.UiAttribute

/**
 * Represents the action when the user updates an attribute in an [IView].
 *
 * @author Akash Yadav
 */
internal class AttrUpdatedAction(view: com.jepongdevxyz.idefcs.inflater.IView, attr: UiAttribute, private val oldValue: String) :
  AttrAction(view, attr) {

  override fun undo() {
    // NOTE : We need to provide the view instance so that the new attribute object created will be
    // an instance of UiAttribute
    view.updateAttribute(attr.copyAttr(view = view, value = oldValue))
  }

  override fun redo() {
    view.updateAttribute(attr)
  }
}
