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

import android.graphics.Typeface.DEFAULT
import android.widget.Switch
import com.jepongdevxyz.idefcs.annotations.inflater.ViewAdapter
import com.jepongdevxyz.idefcs.annotations.uidesigner.IncludeInDesigner
import com.jepongdevxyz.idefcs.annotations.uidesigner.IncludeInDesigner.Group.WIDGETS
import com.jepongdevxyz.idefcs.inflater.AttributeHandlerScope
import com.jepongdevxyz.idefcs.inflater.models.UiWidget
import com.jepongdevxyz.idefcs.resources.R.drawable
import com.jepongdevxyz.idefcs.resources.R.string

/**
 * Attribute adapter for [Switch].
 *
 * @author Akash Yadav
 */
@ViewAdapter(Switch::class)
@IncludeInDesigner(group = WIDGETS)
open class SwitchAdapter<T : Switch> : CompoundButtonAdapter<T>() {
  override fun createAttrHandlers(create: (String, AttributeHandlerScope<T>.() -> Unit) -> Unit) {
    super.createAttrHandlers(create)
    create("showText") { view.showText = parseBoolean(value) }
    create("splitTrack") { view.splitTrack = parseBoolean(value) }
    create("switchMinWidth") { view.switchMinWidth = parseDimension(context, value, 0) }
    create("switchPadding") { view.switchPadding = parseDimension(context, value, 0) }
    create("textOff") { view.textOff = parseString(value) }
    create("textOn") { view.textOn = parseString(value) }
    create("textStyle") { view.setSwitchTypeface(DEFAULT, parseTextStyle(value)) }
    create("thumb") { view.thumbDrawable = parseDrawable(context, value) }
    create("thumbTextPadding") { view.thumbTextPadding = parseDimension(context, value, 0) }
    create("thumbTint") { view.thumbTintList = parseColorStateList(context, value) }
    create("thumbTintMode") { view.thumbTintMode = parsePorterDuffMode(value) }
    create("track") { view.trackDrawable = parseDrawable(context, value) }
    create("trackTint") { view.trackTintList = parseColorStateList(context, value) }
    create("trackTintMode") { view.trackTintMode = parsePorterDuffMode(value) }
    create("typeface") { view.setSwitchTypeface(parseTypeface(value)) }
  }

  override fun createUiWidgets(): List<UiWidget> {
    return listOf(UiWidget(Switch::class.java, string.widget_switch, drawable.ic_widget_switch))
  }
}
