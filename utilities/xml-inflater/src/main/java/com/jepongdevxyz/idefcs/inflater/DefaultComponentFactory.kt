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

package com.jepongdevxyz.idefcs.inflater

import android.view.View
import android.view.ViewGroup
import com.jepongdevxyz.idefcs.inflater.internal.AttributeImpl
import com.jepongdevxyz.idefcs.inflater.internal.LayoutFile
import com.jepongdevxyz.idefcs.inflater.internal.ViewGroupImpl
import com.jepongdevxyz.idefcs.inflater.internal.ViewImpl

/** Default implementation of [IComponentFactory]. */
open class DefaultComponentFactory : IComponentFactory {

  override fun createView(file: LayoutFile, name: String, view: View): IView {
    return if (view is ViewGroup) {
      ViewGroupImpl(file, name, view)
    } else ViewImpl(file, name, view)
  }

  override fun createAttr(
    view: IView,
    namespace: INamespace?,
    name: String,
    value: String
  ): IAttribute {
    return AttributeImpl(namespace = namespace, name = name, value = value)
  }
}