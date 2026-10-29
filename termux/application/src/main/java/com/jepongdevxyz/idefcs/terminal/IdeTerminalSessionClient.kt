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

package com.jepongdevxyz.idefcs.terminal

import com.jepongdevxyz.idefcs.activities.TerminalActivity
import com.termux.app.terminal.TermuxTerminalSessionActivityClient
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient

/**
 * [TerminalSessionClient] delegate for DevxyzIDE.
 *
 * @author Akash Yadav
 */
class IdeTerminalSessionClient(
  activity: TerminalActivity
) : TermuxTerminalSessionActivityClient(activity) {

  override fun onSessionFinished(finishedSession: TerminalSession) {
    val termuxSession = mActivity?.termuxService?.getTermuxSessionForTerminalSession(
      finishedSession)
    if (termuxSession != null && termuxSession is IdesetupSession) {
      // if the finished session was performing tools installation
      // then set the result code for the installation process
      mActivity.setResult(finishedSession.exitStatus)
    }

    super.onSessionFinished(finishedSession)
  }
}