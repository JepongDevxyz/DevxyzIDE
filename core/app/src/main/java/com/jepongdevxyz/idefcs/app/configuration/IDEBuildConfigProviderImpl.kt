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

package com.jepongdevxyz.idefcs.app.configuration

import android.os.Build
import com.google.auto.service.AutoService
import com.jepongdevxyz.idefcs.app.IDEApplication

/**
 * @author Akash Yadav
 */
@AutoService(IDEBuildConfigProvider::class)
@Suppress("UNUSED")
open class IDEBuildConfigProviderImpl : IDEBuildConfigProvider {

  override val cpuAbiName: String by lazy {
    val applicationInfo = IDEApplication.instance.applicationInfo!!

    // transform to valid ABI names
    return@lazy when (val abi = applicationInfo.nativeLibraryDir!!.substringAfterLast('/')) {
      "arm64" -> CpuArch.AARCH64.abi
      "arm" -> CpuArch.ARM.abi
      else -> abi
    }
  }

  override val cpuArch: CpuArch
    get() = CpuArch.forAbi(cpuAbiName)!!

  override val deviceArch: CpuArch
    get() = CpuArch.forAbi(Build.SUPPORTED_ABIS[0])!!

  override val supportedAbis: Array<String> by lazy {
    arrayOf(CpuArch.AARCH64.abi, CpuArch.ARM.abi, CpuArch.X86_64.abi)
  }
}