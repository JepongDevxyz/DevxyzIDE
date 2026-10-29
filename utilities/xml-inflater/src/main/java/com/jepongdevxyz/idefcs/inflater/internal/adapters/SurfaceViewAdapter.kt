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

package com.jepongdevxyz.idefcs.inflater.internal.adapters

import android.view.SurfaceView
import com.jepongdevxyz.idefcs.annotations.uidesigner.IncludeInDesigner
import com.jepongdevxyz.idefcs.annotations.uidesigner.IncludeInDesigner.Group.WIDGETS
import com.jepongdevxyz.idefcs.inflater.models.UiWidget
import com.jepongdevxyz.idefcs.resources.R.drawable
import com.jepongdevxyz.idefcs.resources.R.string

/**
 * Attribute adapter for [SurfaceView].
 *
 * @author Akash Yadav
 */
@com.jepongdevxyz.idefcs.annotations.inflater.ViewAdapter(SurfaceView::class)
@IncludeInDesigner(group = WIDGETS)
open class SurfaceViewAdapter<T : SurfaceView> : ViewAdapter<T>() {
  override fun createUiWidgets(): List<UiWidget> {
    return listOf(
      UiWidget(SurfaceView::class.java, string.widget_surfaceview, drawable.ic_widget_surface_view)
    )
  }
}
