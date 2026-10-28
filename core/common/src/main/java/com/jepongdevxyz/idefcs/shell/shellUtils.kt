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

package com.jepongdevxyz.idefcs.shell

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 * Execute a process (command) using [ProcessBuilder] asynchronously.
 *
 * @param configure Function to configure the process builder.
 * @return The started process.
 */
fun executeProcessAsync(configure: IProcessBuilder.() -> Unit): Process {
  return ProcessBuilderImpl().apply(configure).startAsync()
}

/**
 * Execute a process (command) using [ProcessBuilder].
 *
 * @param configure Function to configure the process builder.
 * @return The started process.
 */
suspend fun executeProcess(
  coroutineContext: CoroutineContext = Dispatchers.IO,
  configure: IProcessBuilder.() -> Unit
): Int {
  return ProcessBuilderImpl().apply(configure).start(coroutineContext)
}