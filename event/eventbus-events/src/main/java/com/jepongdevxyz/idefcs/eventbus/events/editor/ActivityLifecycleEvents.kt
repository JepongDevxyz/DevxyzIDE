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

package com.jepongdevxyz.idefcs.eventbus.events.editor

import com.jepongdevxyz.idefcs.eventbus.events.Event

abstract class EditorActivityLifecycleEvent : Event()

/**
 * Dispatched just after `EditorActivity`'s `onCreate` method.
 *
 * @author Akash Yadav
 */
class OnCreateEvent : EditorActivityLifecycleEvent()

/**
 * Dispatched just after `EditorActivity`'s `onStart` method.
 *
 * @author Akash Yadav
 */
class OnStartEvent : EditorActivityLifecycleEvent()

/**
 * Dispatched just after `EditorActivity`'s `onResume` method.
 *
 * @author Akash Yadav
 */
class OnResumeEvent : EditorActivityLifecycleEvent()

/**
 * Dispatched just before `EditorActivity`'s `onPause` method.
 *
 * @author Akash Yadav
 */
class OnPauseEvent : EditorActivityLifecycleEvent()

/**
 * Dispatched just before `EditorActivity`'s `onStop` method.
 *
 * @author Akash Yadav
 */
class OnStopEvent : EditorActivityLifecycleEvent()

/**
 * Dispatched just before `EditorActivity`'s `onDestroy` method.
 *
 * @author Akash Yadav
 */
class OnDestroyEvent : EditorActivityLifecycleEvent()
