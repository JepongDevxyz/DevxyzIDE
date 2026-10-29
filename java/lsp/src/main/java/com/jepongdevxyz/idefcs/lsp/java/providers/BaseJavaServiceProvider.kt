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

package com.jepongdevxyz.idefcs.lsp.java.providers

import com.jepongdevxyz.idefcs.lookup.Lookup
import com.jepongdevxyz.idefcs.lsp.api.IServerSettings
import com.jepongdevxyz.idefcs.lsp.java.compiler.JavaCompilerService
import com.jepongdevxyz.idefcs.progress.ICancelChecker
import java.nio.file.Path

/**
 * Base class for java service providers.
 *
 * @author Akash Yadav
 */
abstract class BaseJavaServiceProvider(
  protected val file: Path,
  protected val compiler: JavaCompilerService,
  protected val settings: IServerSettings
) {

  /** Abort the completion if cancelled. */
  fun abortCompletionIfCancelled() {
    val checker = Lookup.getDefault().lookup(ICancelChecker::class.java)
    checker?.abortIfCancelled()
  }
}
