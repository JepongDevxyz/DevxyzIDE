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

package com.jepongdevxyz.idefcs.lookup;

import com.jepongdevxyz.idefcs.lookup.internal.DefaultLookup;
import com.jepongdevxyz.idefcs.utils.ServiceLoader;
import com.jepongdevxyz.idefcs.utils.VMUtils;

/**
 * Provides instance of {@link Lookup}.
 *
 * @author Akash Yadav
 */
class LookupProvider {

  private static Lookup sLookup;
  private static final Object lock = new Object();

  static synchronized Lookup lookupService() {
    synchronized (lock) {
      if (sLookup != null) {
        return sLookup;
      }

      if (VMUtils.isJvm()) {
        // When in a test environment, load the default lookup
        return sLookup = new DefaultLookup();
      }

      return sLookup = ServiceLoader.load(Lookup.class).findFirstOrThrow();
    }
  }
}
