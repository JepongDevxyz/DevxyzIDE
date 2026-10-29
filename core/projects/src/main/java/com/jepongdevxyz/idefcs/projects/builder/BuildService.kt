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

package com.jepongdevxyz.idefcs.projects.builder

import com.jepongdevxyz.idefcs.lookup.Lookup
import com.jepongdevxyz.idefcs.lookup.Lookup.Key
import com.jepongdevxyz.idefcs.tooling.api.IProject
import com.jepongdevxyz.idefcs.tooling.api.messages.InitializeProjectParams
import com.jepongdevxyz.idefcs.tooling.api.messages.result.BuildCancellationRequestResult
import com.jepongdevxyz.idefcs.tooling.api.messages.result.InitializeResult
import com.jepongdevxyz.idefcs.tooling.api.messages.result.TaskExecutionResult
import com.jepongdevxyz.idefcs.tooling.api.models.ToolingServerMetadata
import java.util.concurrent.CompletableFuture

/**
 * A build service provides API to initialize project, execute builds, query a build, cancel running
 * builds, etc.
 *
 * @author Akash Yadav
 */
interface BuildService {

  companion object {

    /** Key that can be used to retrieve the [BuildService] instance using the [Lookup] API. */
    @JvmField
    val KEY_BUILD_SERVICE = Key<BuildService>()

    /**
     * Key that can be used to retrieve the instance of Tooling API's [IProject] model using the
     * [Lookup] API.
     */
    @JvmField
    val KEY_PROJECT_PROXY = Key<IProject>()
  }

  /** Whether a build is in progress or not. */
  val isBuildInProgress: Boolean

  /** Returns `true` if and only if the tooling API server has been started, `false` otherwise. */
  fun isToolingServerStarted(): Boolean

  /**
   * Returns the [ToolingServerMetadata] of the tooling API server.
   */
  fun metadata(): CompletableFuture<ToolingServerMetadata>

  /**
   * Initialize the project.
   *
   * @param params Parameters for the project initialization.
   * @return A [CompletableFuture] which returns an [InitializeResult] when the project
   *   initialization process finishes.
   */
  fun initializeProject(params: InitializeProjectParams): CompletableFuture<InitializeResult>

  /**
   * Execute the given tasks.
   *
   * @param tasks The tasks to execute. If the fully qualified path of the task is not specified,
   *   then it will be executed in the root project directory.
   * @return A [CompletableFuture] which returns a list of [TaskExecutionResult]. The result
   *   contains a list of tasks that were executed and the result of the whole execution.
   */
  fun executeTasks(vararg tasks: String): CompletableFuture<TaskExecutionResult>

  /** Cancel any running build. */
  fun cancelCurrentBuild(): CompletableFuture<BuildCancellationRequestResult>
}
