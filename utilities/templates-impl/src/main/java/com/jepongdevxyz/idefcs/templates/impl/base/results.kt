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

package com.jepongdevxyz.idefcs.templates.impl.base

import com.jepongdevxyz.idefcs.templates.ModuleTemplateData
import com.jepongdevxyz.idefcs.templates.ModuleTemplateRecipeResult
import com.jepongdevxyz.idefcs.templates.ProjectTemplateData
import com.jepongdevxyz.idefcs.templates.ProjectTemplateRecipeResult
import com.jepongdevxyz.idefcs.templates.base.ModuleTemplateBuilder
import com.jepongdevxyz.idefcs.templates.base.ProjectTemplateBuilder

data class ProjectTemplateRecipeResultImpl(
  override val data: ProjectTemplateData
) : ProjectTemplateRecipeResult

data class ModuleTemplateRecipeResultImpl(override val data: ModuleTemplateData
) : ModuleTemplateRecipeResult


internal fun ProjectTemplateBuilder.recipeResult(): ProjectTemplateRecipeResult {
  return ProjectTemplateRecipeResultImpl(data)
}

internal fun ModuleTemplateBuilder.recipeResult(): ModuleTemplateRecipeResult {
  return ModuleTemplateRecipeResultImpl(data)
}