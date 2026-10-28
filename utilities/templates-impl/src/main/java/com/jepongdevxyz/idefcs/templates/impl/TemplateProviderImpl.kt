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

package com.jepongdevxyz.idefcs.templates.impl

import com.google.auto.service.AutoService
import com.google.common.collect.ImmutableList
import com.jepongdevxyz.idefcs.templates.ITemplateProvider
import com.jepongdevxyz.idefcs.templates.Template
import com.jepongdevxyz.idefcs.templates.impl.basicActivity.basicActivityProject
import com.jepongdevxyz.idefcs.templates.impl.bottomNavActivity.bottomNavActivityProject
import com.jepongdevxyz.idefcs.templates.impl.composeActivity.composeActivityProject
import com.jepongdevxyz.idefcs.templates.impl.emptyActivity.emptyActivityProject
import com.jepongdevxyz.idefcs.templates.impl.navDrawerActivity.navDrawerActivityProject
import com.jepongdevxyz.idefcs.templates.impl.noActivity.noActivityProjectTemplate
import com.jepongdevxyz.idefcs.templates.impl.noAndroidXActivity.noAndroidXActivityProject
import com.jepongdevxyz.idefcs.templates.impl.tabbedActivity.tabbedActivityProject

/**
 * Default implementation of the [ITemplateProvider].
 *
 * @author Akash Yadav
 */
@Suppress("unused")
@AutoService(ITemplateProvider::class)
class TemplateProviderImpl : ITemplateProvider {

  private val templates = mutableMapOf<String, Template<*>>()

  init {
    initializeTemplates()
  }

  private fun templates() =
    //@formatter:off
    arrayOf(
      noActivityProjectTemplate(),
      emptyActivityProject(),
      basicActivityProject(),
      navDrawerActivityProject(),
      bottomNavActivityProject(),
      tabbedActivityProject(),
      noAndroidXActivityProject(),
      composeActivityProject()
    )

  private fun initializeTemplates() {
    templates().forEach { template ->
      templates[template.templateId] = template
    }
  }
  //@formatter:on

  override fun getTemplates(): List<Template<*>> {
    return ImmutableList.copyOf(templates.values)
  }

  override fun getTemplate(templateId: String): Template<*>? {
    return templates[templateId]
  }

  override fun reload() {
    release()
    initializeTemplates()
  }

  override fun release() {
    templates.forEach { it.value.release() }
    templates.clear()
  }
}