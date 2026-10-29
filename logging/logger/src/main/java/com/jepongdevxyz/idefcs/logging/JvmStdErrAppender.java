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

package com.jepongdevxyz.idefcs.logging;

import com.jepongdevxyz.idefcs.logging.utils.LogUtils;

/**
 * @author Akash Yadav
 */
public class JvmStdErrAppender extends StdErrAppender {

  public static final String PROP_JVM_STDERR_APPENDER_ENABLED = "ide.logging.jvmStdErrAppenderEnabled";

  private boolean isJvm = false; // JvmStdErrAppender is disabled by default

  @Override
  public void start() {
    this.isJvm = LogUtils.isJvm();
    super.start();
  }

  @Override
  public boolean isStarted() {
    return super.isStarted()
        && isJvm
        && Boolean.parseBoolean(System.getProperty(PROP_JVM_STDERR_APPENDER_ENABLED, "true"));
  }
}
