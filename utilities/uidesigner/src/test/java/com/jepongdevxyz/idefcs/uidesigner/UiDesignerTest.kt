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

package com.jepongdevxyz.idefcs.uidesigner

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.jepongdevxyz.idefcs.inflater.IComponentFactory.Companion.LAYOUT_INFLATER_COMPONENT_FACTORY_KEY
import com.jepongdevxyz.idefcs.inflater.internal.LayoutFile
import com.jepongdevxyz.idefcs.inflater.internal.ViewGroupImpl
import com.jepongdevxyz.idefcs.inflater.internal.ViewImpl
import com.jepongdevxyz.idefcs.inflater.internal.utils.ViewFactory
import com.jepongdevxyz.idefcs.inflater.viewGroup
import com.jepongdevxyz.idefcs.lookup.Lookup
import com.jepongdevxyz.idefcs.uidesigner.utils.UiInflaterComponentFactory
import org.robolectric.Robolectric
import java.io.File

private val activity by lazy { Robolectric.buildActivity(AppCompatActivity::class.java).get() }

fun requiresActivity(initComponentFactory: Boolean = true, block: AppCompatActivity.() -> Unit) {
  if (initComponentFactory) {
    Lookup.getDefault().apply {
      lookup(LAYOUT_INFLATER_COMPONENT_FACTORY_KEY)
        ?: register(LAYOUT_INFLATER_COMPONENT_FACTORY_KEY, UiInflaterComponentFactory())
    }
  }
  activity.block()
}

fun Context.createLayout(parent: com.jepongdevxyz.idefcs.inflater.IViewGroup? = null): ViewGroupImpl {
  return ViewGroupImpl(
      LayoutFile(File(""), ""),
      LinearLayout::class.qualifiedName!!,
      LinearLayout(this)
    )
    .apply { parent?.let { applyLayoutParams(it) } }
}

fun Context.createView(parent: com.jepongdevxyz.idefcs.inflater.IViewGroup? = null): ViewImpl {
  return ViewImpl(LayoutFile(File(""), ""), View::class.qualifiedName!!, View(this)).apply {
    parent?.let { applyLayoutParams(it) }
  }
}

fun ViewImpl.applyLayoutParams(parent: com.jepongdevxyz.idefcs.inflater.IViewGroup) {
  view.layoutParams = ViewFactory.generateLayoutParams(parent.viewGroup)
}
