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
package com.jepongdevxyz.idefcs.tooling.events.internal

import com.jepongdevxyz.idefcs.tooling.events.OperationDescriptor

/** @author Akash Yadav */
class DefaultOperationDescriptor(override val name: String, override val displayName: String) :
  OperationDescriptor() {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is DefaultOperationDescriptor) return false

    if (name != other.name) return false
    if (displayName != other.displayName) return false

    return true
  }

  override fun hashCode(): Int {
    var result = name.hashCode()
    result = 31 * result + displayName.hashCode()
    return result
  }
}
