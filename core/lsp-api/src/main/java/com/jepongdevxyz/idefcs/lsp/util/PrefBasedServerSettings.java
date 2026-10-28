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

package com.jepongdevxyz.idefcs.lsp.util;

import androidx.annotation.Nullable;
import com.jepongdevxyz.idefcs.app.BaseApplication;
import com.jepongdevxyz.idefcs.lsp.api.IServerSettings;
import com.jepongdevxyz.idefcs.managers.PreferenceManager;

/**
 * {@link IServerSettings} implementation which uses {@link
 * com.jepongdevxyz.idefcs.managers.PreferenceManager PreferencesManager} to read common settings.
 *
 * @author Akash Yadav
 */
public abstract class PrefBasedServerSettings extends DefaultServerSettings {

  private PreferenceManager prefs;

  @Override
  public boolean shouldMatchAllLowerCase() {
    final var prefs = getPrefs();
    if (prefs != null) {
      return prefs.getBoolean(KEY_COMPLETIONS_MATCH_LOWER, true);
    }

    return false;
  }

  @Nullable
  public PreferenceManager getPrefs() {
    if (prefs == null) {
      final var app = BaseApplication.getBaseInstance();
      if (app != null) {
        prefs = app.getPrefManager();
      }
    }
    return prefs;
  }
}
