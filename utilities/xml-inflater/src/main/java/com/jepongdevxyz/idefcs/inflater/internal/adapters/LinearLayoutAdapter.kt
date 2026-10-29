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

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jepongdevxyz.idefcs.annotations.inflater.ViewAdapter
import com.jepongdevxyz.idefcs.annotations.uidesigner.IncludeInDesigner
import com.jepongdevxyz.idefcs.annotations.uidesigner.IncludeInDesigner.Group.LAYOUTS
import com.jepongdevxyz.idefcs.inflater.AttributeHandlerScope
import com.jepongdevxyz.idefcs.inflater.INamespace
import com.jepongdevxyz.idefcs.inflater.IView
import com.jepongdevxyz.idefcs.inflater.IViewGroup
import com.jepongdevxyz.idefcs.inflater.LayoutStrategy
import com.jepongdevxyz.idefcs.inflater.internal.LayoutFile
import com.jepongdevxyz.idefcs.inflater.models.UiWidget
import com.jepongdevxyz.idefcs.inflater.utils.newAttribute
import com.jepongdevxyz.idefcs.resources.R.drawable
import com.jepongdevxyz.idefcs.resources.R.string

/**
 * Attribute adapter for [LinearLayout].
 *
 * @author Akash Yadav
 */
@ViewAdapter(LinearLayout::class)
@IncludeInDesigner(group = LAYOUTS)
open class LinearLayoutAdapter<T : LinearLayout> : ViewGroupAdapter<T>() {

  override fun createAttrHandlers(
    create: (String, AttributeHandlerScope<T>.() -> Unit) -> Unit
  ) {
    super.createAttrHandlers(create)

    create("baselineAligned") { view.isBaselineAligned = parseBoolean(value) }
    create("baselineAlignedChildIndex") {
      view.baselineAlignedChildIndex = parseInteger(value, view.childCount)
    }
    create("gravity") { view.gravity = parseGravity(value) }
    create("measureWithLargestChild") {
      view.isMeasureWithLargestChildEnabled = parseBoolean(value)
    }
    create("orientation") { view.orientation = parseOrientation(value) }
    create("weightSum") { view.weightSum = parseFloat(value) }
  }

  override fun createUiWidgets(): List<UiWidget> {
    val horizontal =
      LinearLayoutWidget(title = string.widget_linear_layout_horz,
        icon = drawable.ic_widget_linear_layout_horz, isVertical = false)
    val vertical = LinearLayoutWidget(title = string.widget_linear_layout_vert,
      icon = drawable.ic_widget_linear_layout_vert, isVertical = true)
    return listOf(horizontal, vertical)
  }

  override fun getLayoutStrategy(group: IViewGroup): LayoutStrategy {
    val orientation = group.findAttribute("orientation", INamespace.ANDROID.uri)
    return if (orientation?.value == "vertical") LayoutStrategy.VERTICAL else LayoutStrategy.HORIZONTAL
  }

  protected open fun parseOrientation(value: String): Int {
    return when (value) {
      "vertical" -> LinearLayout.VERTICAL
      "horizontal" -> LinearLayout.HORIZONTAL
      else -> LinearLayout.HORIZONTAL
    }
  }

  private class LinearLayoutWidget(@StringRes title: Int,
                                   @DrawableRes icon: Int,
                                   private val isVertical: Boolean
  ) : UiWidget(LinearLayout::class.java, title, icon) {

    companion object {

      private const val HORIZONTAL = "horizontal"
      private const val VERTICAL = "vertical"
    }

    override fun createView(context: Context, parent: ViewGroup,
                            layoutFile: LayoutFile
    ): IView {
      return super.createView(context, parent, layoutFile).apply {
        addAttribute(newAttribute(this, name = "orientation",
          value = if (isVertical) VERTICAL else HORIZONTAL))
      }
    }
  }
}
