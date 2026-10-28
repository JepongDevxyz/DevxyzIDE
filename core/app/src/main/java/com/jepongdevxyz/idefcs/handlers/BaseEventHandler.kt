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

package com.jepongdevxyz.idefcs.handlers

import android.content.Context
import com.jepongdevxyz.idefcs.activities.editor.EditorHandlerActivity
import com.jepongdevxyz.idefcs.eventbus.events.Event
import com.jepongdevxyz.idefcs.eventbus.events.EventReceiver
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Base class for event handlers.
 *
 * @author Akash Yadav
 */
abstract class BaseEventHandler : EventReceiver {

  companion object {

    @JvmStatic
    protected val log: Logger = LoggerFactory.getLogger(BaseEventHandler::class.java)
  }

  protected open fun checkIsEditorActivity(event: Event): Boolean {
    return event.get(Context::class.java) is EditorHandlerActivity
  }

  protected open fun logCannotHandle(event: Event) {
    log.warn("Context is not EditorActivity. Cannot handle {} event.", event.javaClass.simpleName)
  }
}
