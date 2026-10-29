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

package com.jepongdevxyz.idefcs.contributors

/**
 * A contributor.
 *
 * @author Akash Yadav
 */
interface Contributor {

  /**
   * Unique integer ID.
   */
  val id: Int

  /**
   * Unique username.
   */
  val username: String

  /**
   * URL for the contributor's avatar.
   */
  val avatarUrl: String

  /**
   * URL for the contributor's profile.
   */
  val profileUrl: String
}