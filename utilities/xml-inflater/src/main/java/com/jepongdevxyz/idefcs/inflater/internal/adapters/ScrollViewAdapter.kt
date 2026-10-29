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

import android.widget.ScrollView
import com.jepongdevxyz.idefcs.annotations.uidesigner.IncludeInDesigner
import com.jepongdevxyz.idefcs.annotations.uidesigner.IncludeInDesigner.Group.LAYOUTS
import com.jepongdevxyz.idefcs.inflater.AttributeHandlerScope
import com.jepongdevxyz.idefcs.inflater.IView
import com.jepongdevxyz.idefcs.inflater.IViewGroup
import com.jepongdevxyz.idefcs.inflater.models.UiWidget
import com.jepongdevxyz.idefcs.resources.R

/**
 * View adapter for [ScrollView].
 *
 * @author Akash Yadav
 */
@com.jepongdevxyz.idefcs.annotations.inflater.ViewAdapter(
  forView = ScrollView::class)
@IncludeInDesigner(group = LAYOUTS)
open class ScrollViewAdapter<T : ScrollView> : FrameLayoutAdapter<T>() {

  override fun createAttrHandlers(
    create: (String, AttributeHandlerScope<T>.() -> Unit) -> Unit
  ) {
    super.createAttrHandlers(create)
    create("fillViewPort") { view.isFillViewport = parseBoolean(value, false) }
  }

  override fun createUiWidgets(): List<UiWidget> {
    return listOf(UiWidget(ScrollView::class.java, R.string.widget_scrollview,
      R.drawable.ic_widget_scroll_view))
  }

  override fun canAcceptChild(view: IViewGroup, child: IView?, name: String
  ): Boolean {
    // scrollview can have only one child
    return view.childCount == 0
  }
}
