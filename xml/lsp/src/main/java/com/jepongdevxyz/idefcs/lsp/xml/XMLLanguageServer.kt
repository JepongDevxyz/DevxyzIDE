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
package com.jepongdevxyz.idefcs.lsp.xml

import androidx.annotation.RestrictTo
import com.jepongdevxyz.idefcs.eventbus.events.editor.DocumentChangeEvent
import com.jepongdevxyz.idefcs.lsp.api.ICompletionProvider
import com.jepongdevxyz.idefcs.lsp.api.ILanguageClient
import com.jepongdevxyz.idefcs.lsp.api.ILanguageServer
import com.jepongdevxyz.idefcs.lsp.api.IServerSettings
import com.jepongdevxyz.idefcs.lsp.models.CodeFormatResult
import com.jepongdevxyz.idefcs.lsp.models.CompletionParams
import com.jepongdevxyz.idefcs.lsp.models.CompletionResult
import com.jepongdevxyz.idefcs.lsp.models.DefinitionParams
import com.jepongdevxyz.idefcs.lsp.models.DefinitionResult
import com.jepongdevxyz.idefcs.lsp.models.DiagnosticResult
import com.jepongdevxyz.idefcs.lsp.models.ExpandSelectionParams
import com.jepongdevxyz.idefcs.lsp.models.FormatCodeParams
import com.jepongdevxyz.idefcs.lsp.models.LSPFailure
import com.jepongdevxyz.idefcs.lsp.models.ReferenceParams
import com.jepongdevxyz.idefcs.lsp.models.ReferenceResult
import com.jepongdevxyz.idefcs.lsp.models.SignatureHelp
import com.jepongdevxyz.idefcs.lsp.models.SignatureHelpParams
import com.jepongdevxyz.idefcs.lsp.util.NoCompletionsProvider
import com.jepongdevxyz.idefcs.lsp.xml.models.XMLServerSettings
import com.jepongdevxyz.idefcs.lsp.xml.providers.AdvancedEditProvider.onContentChange
import com.jepongdevxyz.idefcs.lsp.xml.providers.CodeFormatProvider
import com.jepongdevxyz.idefcs.lsp.xml.providers.XmlCompletionProvider
import com.jepongdevxyz.idefcs.models.Range
import com.jepongdevxyz.idefcs.projects.IWorkspace
import com.jepongdevxyz.idefcs.utils.DocumentUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.nio.file.Path

/**
 * Language server implementation for XML files.
 *
 * @author Akash Yadav
 */
class XMLLanguageServer : ILanguageServer {

  @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
  override var client: ILanguageClient? = null
    private set

  private var settings: IServerSettings? = null

  override val serverId: String = SERVER_ID

  init {
    EventBus.getDefault().register(this)
  }

  override fun shutdown() {
    if (EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().unregister(this)
    }
  }

  override fun connectClient(client: ILanguageClient?) {
    this.client = client
  }

  override fun applySettings(settings: IServerSettings?) {
    this.settings = settings
  }

  override fun setupWorkspace(workspace: IWorkspace) {}
  override fun complete(params: CompletionParams?): CompletionResult {
    val completionProvider: ICompletionProvider = if (!getSettings().completionsEnabled()) {
      NoCompletionsProvider()
    } else {
      XmlCompletionProvider(getSettings())
    }
    return completionProvider.complete(params)
  }

  fun getSettings(): IServerSettings {
    if (settings == null) {
      settings = XMLServerSettings
    }
    return settings!!
  }

  override suspend fun findReferences(params: ReferenceParams): ReferenceResult {
    return ReferenceResult(emptyList())
  }

  override suspend fun findDefinition(params: DefinitionParams): DefinitionResult {
    return DefinitionResult(emptyList())
  }

  override suspend fun expandSelection(params: ExpandSelectionParams): Range {
    return params.selection
  }

  override suspend fun signatureHelp(params: SignatureHelpParams): SignatureHelp {
    return SignatureHelp(emptyList(), -1, -1)
  }

  override suspend fun analyze(file: Path): DiagnosticResult {
    return DiagnosticResult.NO_UPDATE
  }

  override fun formatCode(params: FormatCodeParams?): CodeFormatResult {
    return CodeFormatProvider().format(params)
  }

  @Subscribe(threadMode = ThreadMode.BACKGROUND)
  fun onDocumentChange(event: DocumentChangeEvent) {
    if (!DocumentUtils.isXmlFile(event.changedFile)) {
      return
    }
    onContentChange(event)
  }

  override fun handleFailure(failure: LSPFailure?): Boolean {
    return super<ILanguageServer>.handleFailure(failure)
  }

  companion object {

    const val SERVER_ID = "ide.lsp.xml"
  }
}