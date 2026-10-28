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

package com.jepongdevxyz.idefcs.utils;

/**
 * @author Akash Yadav
 */
@SuppressWarnings("unused")
public class LogTagUtils {

  public static String trimTagIfNeeded(String tag, int maxLength) {
    final var sb = new StringBuilder(tag);
    final var length = tag.length();
    if (length > maxLength) {
      final var start = length - maxLength;
      sb.delete(0, start);

      // When the tag is long enough, prefix the tag with '..'
      sb.setCharAt(0, '.');
      sb.setCharAt(1, '.');
    }

    return sb.toString();
  }
}
