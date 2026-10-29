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

package com.jepongdevxyz.idefcs.editor.ui

import com.jepongdevxyz.idefcs.eventbus.events.editor.DocumentChangeEvent
import com.jepongdevxyz.idefcs.eventbus.events.editor.DocumentCloseEvent
import com.jepongdevxyz.idefcs.eventbus.events.editor.DocumentEvent
import com.jepongdevxyz.idefcs.eventbus.events.editor.DocumentOpenEvent
import com.jepongdevxyz.idefcs.eventbus.events.editor.DocumentSaveEvent
import com.jepongdevxyz.idefcs.eventbus.events.editor.DocumentSelectedEvent
import com.jepongdevxyz.idefcs.projects.FileManager.onDocumentClose
import com.jepongdevxyz.idefcs.projects.FileManager.onDocumentContentChange
import com.jepongdevxyz.idefcs.projects.FileManager.onDocumentOpen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.slf4j.LoggerFactory
import java.util.concurrent.CancellationException
import java.util.concurrent.LinkedBlockingQueue

/**
 * Dispatches events for the editor.
 *
 * @author Akash Yadav
 */
class EditorEventDispatcher(
  var editor: IDEEditor? = null
) {

  private val eventQueue = LinkedBlockingQueue<DocumentEvent>()
  private var eventDispatcherJob: Job? = null

  companion object {

    private val log = LoggerFactory.getLogger(EditorEventDispatcher::class.java)
  }

  fun init(scope: CoroutineScope) {
    eventDispatcherJob = scope.launch(Dispatchers.Default) {
      while (isActive) {
        dispatchNextEvent()
      }
    }.also {
      it.invokeOnCompletion { error ->
        if (error != null && error !is CancellationException) {
          log.error("Failed to dispatch editor events", error)
        }
      }
    }
  }

  fun dispatch(event: DocumentEvent) {
    check(eventQueue.offer(event)) {
      "Failed to dispatch event: $event"
    }
  }

  private suspend fun dispatchNextEvent() {
    val event = withContext(Dispatchers.IO) {
      eventQueue.take()
    }

    if (editor?.isReleased != false) {
      return
    }

    when (event) {
      is DocumentOpenEvent -> dispatchOpen(event)
      is DocumentChangeEvent -> dispatchChange(event)
      is DocumentSaveEvent -> dispatchSave(event)
      is DocumentCloseEvent -> dispatchClose(event)
      is DocumentSelectedEvent -> dispatchSelected(event)
      else -> throw IllegalArgumentException("Unknown document event: $event")
    }
  }

  private fun dispatchOpen(event: DocumentOpenEvent) {
    onDocumentOpen(event)
    post(event)
  }

  private fun dispatchChange(event: DocumentChangeEvent) {
    onDocumentContentChange(event)
    post(event)
  }

  private fun dispatchSave(event: DocumentSaveEvent) {
    post(event)
  }

  private fun dispatchClose(event: DocumentCloseEvent) {
    onDocumentClose(event)
    post(event)
  }

  private fun dispatchSelected(event: DocumentSelectedEvent) {
    post(event)
  }

  private fun post(event: DocumentEvent) {
    EventBus.getDefault().post(event)
  }

  fun destroy() {
    editor = null
    eventDispatcherJob?.cancel(CancellationException("Cancellation requested"))
  }
}