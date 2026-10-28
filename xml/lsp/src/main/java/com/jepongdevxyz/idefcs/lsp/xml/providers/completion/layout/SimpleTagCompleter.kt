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

package com.jepongdevxyz.idefcs.lsp.xml.providers.completion.layout

import com.android.aaptcompiler.ResourcePathData
import com.jepongdevxyz.idefcs.lookup.Lookup
import com.jepongdevxyz.idefcs.lsp.api.ICompletionProvider
import com.jepongdevxyz.idefcs.lsp.models.CompletionItem
import com.jepongdevxyz.idefcs.lsp.models.CompletionParams
import com.jepongdevxyz.idefcs.lsp.models.CompletionResult
import com.jepongdevxyz.idefcs.lsp.xml.utils.XmlUtils.NodeType
import com.jepongdevxyz.idefcs.projects.ModuleProject
import com.jepongdevxyz.idefcs.utils.ClassTrie
import com.jepongdevxyz.idefcs.xml.widgets.WidgetTable
import org.eclipse.lemminx.dom.DOMDocument

/**
 * Completes platform widget names.
 *
 * @author Akash Yadav
 */
class SimpleTagCompleter(provider: ICompletionProvider) : LayoutTagCompletionProvider(provider) {

  override fun doComplete(
    params: CompletionParams,
    pathData: ResourcePathData,
    document: DOMDocument,
    type: NodeType,
    prefix: String
  ): CompletionResult {
    val widgets =
      Lookup.getDefault().lookup(WidgetTable.COMPLETION_LOOKUP_KEY)?.getAllWidgets()
        ?: return CompletionResult.EMPTY
    val result = mutableListOf<CompletionItem>()

    // Complete all tags which do not require fully qualified name
    for (widget in widgets) {
      val match = matchLevel(widget.simpleName, prefix)
      result.add(createTagCompletionItem(widget.simpleName, widget.qualifiedName, match, true))
    }

    // Complete the root package names if possible
    val module =
      Lookup.getDefault().lookup(ModuleProject.COMPLETION_MODULE_KEY) ?: return CompletionResult(result)

    // Add root packages from the compile classpath and source paths
    addFromTrie(module.compileClasspathClasses, prefix, result)
    addFromTrie(module.compileJavaSourceClasses, prefix, result)

    return CompletionResult(result)
  }

  private fun addFromTrie(trie: ClassTrie, prefix: String, result: MutableList<CompletionItem>) {
    trie.root.children.values.forEach {
      val match = matchLevel(it.name, prefix)
      result.add(createTagCompletionItem(it.name, it.qualifiedName, match))
    }
  }
}
