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

package com.jepongdevxyz.idefcs.treesitter.api

import com.itsaky.androidide.treesitter.TSTree
import com.jepongdevxyz.idefcs.utils.DefaultRecyclable
import com.jepongdevxyz.idefcs.utils.RecyclableObjectPool

/**
 * @author Akash Yadav
 */
class TreeSitterTree @JvmOverloads internal constructor(
  pointer: Long = 0
) : TSTree(pointer), RecyclableObjectPool.Recyclable by DefaultRecyclable() {

  private var ownerThread: Thread? = null

  companion object {

    @JvmStatic
    fun obtain(pointer: Long): TreeSitterTree {
      return obtainFromPool<TreeSitterTree>().apply {
        this.nativeObject = pointer
        this.ownerThread = Thread.currentThread()
      }
    }
  }

  override fun close() {
    super.close()
    this.nativeObject = 0
    recycle()
  }

  override fun recycle() {
    this.ownerThread = null
    if (!canAccess()) {
      returnToPool()
    }
  }
}