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

package com.jepongdevxyz.idefcs.templates.base

import com.jepongdevxyz.idefcs.templates.FileTemplate
import com.jepongdevxyz.idefcs.templates.FileTemplateRecipeResult
import com.jepongdevxyz.idefcs.templates.TemplateBuilder
import java.io.File

/**
 * [TemplateBuilder] implementation for building files templates.
 *
 * @property dir The directory in which the file must be created.
 * @author Akash Yadav
 */
class FileTemplateBuilder<R: FileTemplateRecipeResult>(val dir: File) : TemplateBuilder<R>() {

  override fun buildInternal(): FileTemplate<R> {
    return FileTemplate(templateName!!, thumb!!, widgets!!, recipe!!)
  }
}