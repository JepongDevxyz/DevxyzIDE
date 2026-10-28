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

import com.jepongdevxyz.idefcs.utils.RecyclableObjectPool

/**
 * Returns this [RecyclableObjectPool.Recyclable] object to the pool from the [TreeSitterObjectPoolProvider].
 *
 * @see TreeSitterObjectPoolProvider.requirePool
 */
internal inline fun <reified T : RecyclableObjectPool.Recyclable> T.returnToPool() {
  TreeSitterObjectPoolProvider.requirePool<T>().recycle(this)
}

/**
 * Get an instance of [T] from the [RecyclableObjectPool] using [TreeSitterObjectPoolProvider].
 *
 * @see TreeSitterObjectPoolProvider.requirePool
 */
internal inline fun <reified T : RecyclableObjectPool.Recyclable> obtainFromPool(): T {
  return TreeSitterObjectPoolProvider.requirePool<T>().obtain()
}