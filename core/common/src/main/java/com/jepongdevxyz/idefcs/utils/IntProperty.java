/************************************************************************************
 * This file is part of DevxyzIDE.
 *
 *
 *
 * DevxyzIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DevxyzIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DevxyzIDE.  If not, see <https://www.gnu.org/licenses/>.
 *
 **************************************************************************************/

package com.jepongdevxyz.idefcs.utils;

import android.util.Property;

/**
 * From AOSP source code.
 *
 * <p>Copied to support earlier android versions.
 */
public abstract class IntProperty<T> extends Property<T, Integer> {
  public IntProperty(String name) {
    super(Integer.class, name);
  }

  @Override
  public final void set(T object, Integer value) {
    setValue(object, value.intValue());
  }

  /**
   * A type-specific variant of {@link #set(Object, Integer)} that is faster when dealing with
   * fields of type <code>int</code>.
   */
  public abstract void setValue(T object, int value);
}
