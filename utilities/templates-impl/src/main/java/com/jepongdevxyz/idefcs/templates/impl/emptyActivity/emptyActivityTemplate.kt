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

package com.jepongdevxyz.idefcs.templates.impl.emptyActivity

import com.jepongdevxyz.idefcs.templates.ProjectTemplate
import com.jepongdevxyz.idefcs.templates.base.AndroidModuleTemplateBuilder
import com.jepongdevxyz.idefcs.templates.base.modules.android.defaultAppModule
import com.jepongdevxyz.idefcs.templates.base.util.AndroidModuleResManager.ResourceType.LAYOUT
import com.jepongdevxyz.idefcs.templates.base.util.SourceWriter
import com.jepongdevxyz.idefcs.templates.impl.R
import com.jepongdevxyz.idefcs.templates.impl.base.createRecipe
import com.jepongdevxyz.idefcs.templates.impl.base.emptyThemesAndColors
import com.jepongdevxyz.idefcs.templates.impl.base.writeMainActivity
import com.jepongdevxyz.idefcs.templates.impl.baseProjectImpl

fun emptyActivityProject(): ProjectTemplate = baseProjectImpl {
  templateName = R.string.template_empty
  thumb = R.drawable.template_empty_activity
  defaultAppModule {
    recipe = createRecipe {
      sources {
        writeEmptyActivity(this)
      }

      res {
        writeEmptyActivity()
      }
    }
  }
}

internal fun AndroidModuleTemplateBuilder.writeEmptyActivity() {
  res.apply {
    // layout/activity_main.xml
    writeXmlResource("activity_main", LAYOUT, source = ::emptyLayoutSrc)
    emptyThemesAndColors()
  }
}

internal fun AndroidModuleTemplateBuilder.writeEmptyActivity(
  writer: SourceWriter
) {
  writeMainActivity(writer, ::emptyActivitySrcKt, ::emptyActivitySrcJava)
}