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

package com.jepongdevxyz.idefcs.uidesigner.models

import android.view.View
import com.jepongdevxyz.idefcs.inflater.internal.LayoutFile
import java.io.File

/**
 * View which acts as a placeholder to show the position where the dragged view will be added.
 *
 * @author Akash Yadav
 */
internal class PlaceholderView(view: View) :
  UiView(LayoutFile(File(""), ""), View::class.qualifiedName!!, view) {
  override var includeInIndexComputation: Boolean = false
}
