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

package com.jepongdevxyz.idefcs.inflater.utils

import com.android.aaptcompiler.AaptResourceType
import com.android.aaptcompiler.ResourceFile.Type.ProtoXml
import com.android.aaptcompiler.XmlProcessor
import com.android.aaptcompiler.extractPathData
import com.jepongdevxyz.idefcs.aapt.logging.IDELogger
import com.jepongdevxyz.idefcs.inflater.IAttribute
import com.jepongdevxyz.idefcs.inflater.IComponentFactory
import com.jepongdevxyz.idefcs.inflater.ILayoutInflater
import com.jepongdevxyz.idefcs.inflater.ILayoutInflater.Companion
import com.jepongdevxyz.idefcs.inflater.INamespace
import com.jepongdevxyz.idefcs.inflater.IView
import com.jepongdevxyz.idefcs.inflater.InflateException
import com.jepongdevxyz.idefcs.inflater.internal.AttributeImpl
import com.jepongdevxyz.idefcs.lookup.Lookup
import com.jepongdevxyz.idefcs.projects.IProjectManager
import com.jepongdevxyz.idefcs.projects.android.AndroidModule
import java.io.File

/** Get the [ILayoutInflater] registered with [Lookup]. */
fun lookupLayoutInflater(): ILayoutInflater? {
  return Lookup.getDefault().lookup(Companion.LOOKUP_KEY)
}

/** Get the [IComponentFactory] registered with [Lookup]. */
fun lookupComponentFactory(): IComponentFactory? {
  return Lookup.getDefault().lookup(IComponentFactory.LAYOUT_INFLATER_COMPONENT_FACTORY_KEY)
}

@JvmOverloads
fun newAttribute(
  view: IView? = null,
  attribute: IAttribute,
  namespace: INamespace? = null,
  name: String? = null,
  value: String? = null
): IAttribute {
  return newAttribute(
    view = view,
    namespace = namespace ?: attribute.namespace,
    name = name ?: attribute.name,
    value = value ?: attribute.value
  )
}

@JvmOverloads
fun newAttribute(
  view: IView? = null,
  namespace: INamespace? = INamespace.ANDROID,
  name: String,
  value: String
): IAttribute {
  val componentFactory = lookupComponentFactory()
  if (componentFactory != null && view != null) {
    return componentFactory.createAttr(view, namespace, name, value)
  }
  return AttributeImpl(namespace, name, value)
}

/**
 * Processes the XML file using [XmlProcessor].
 *
 * @param file The file to process.
 * @param expectedType The expected [type][AaptResourceType] for the XML file.
 * @return The pair of [XmlProcessor] (processed) instance and the [AndroidModule] instance for the
 *   given file.
 */
fun processXmlFile(file: File, expectedType: com.android.aaptcompiler.AaptResourceType): Pair<XmlProcessor, AndroidModule> {
  val pathData = extractPathData(file)
  if (pathData.type != expectedType) {
    throw InflateException("File is not a layout file.")
  }

  if (IProjectManager.getInstance().getWorkspace() == null) {
    throw InflateException("GradleProject is not initialized!")
  }

  val module =
    IProjectManager.getInstance().getWorkspace()?.findModuleForFile(file, false) as? AndroidModule
      ?: throw InflateException("Cannot find module for given file. Is the project initialized?")
  val resFile =
    com.android.aaptcompiler.ResourceFile(
      com.android.aaptcompiler.ResourceName(module.namespace, pathData.type!!, pathData.name),
      pathData.config,
      pathData.source,
      ProtoXml
    )

  val processor = XmlProcessor(pathData.source, com.android.aaptcompiler.BlameLogger(IDELogger))
  processor.process(resFile, file.inputStream())
  return processor to module
}
