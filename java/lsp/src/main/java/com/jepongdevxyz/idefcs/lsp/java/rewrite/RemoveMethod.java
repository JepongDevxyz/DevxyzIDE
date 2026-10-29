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
package com.jepongdevxyz.idefcs.lsp.java.rewrite;

import androidx.annotation.NonNull;
import com.jepongdevxyz.idefcs.lsp.java.compiler.CompilerProvider;
import com.jepongdevxyz.idefcs.lsp.java.utils.EditHelper;
import com.jepongdevxyz.idefcs.lsp.java.utils.FindHelper;
import com.jepongdevxyz.idefcs.lsp.models.TextEdit;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import openjdk.source.util.Trees;

public class RemoveMethod extends Rewrite {

  final String className, methodName;
  final String[] erasedParameterTypes;

  public RemoveMethod(String className, String methodName, String[] erasedParameterTypes) {
    this.className = className;
    this.methodName = methodName;
    this.erasedParameterTypes = erasedParameterTypes;
  }

  @NonNull
  @Override
  public Map<Path, TextEdit[]> rewrite(CompilerProvider compiler) {
    Path file = compiler.findTypeDeclaration(className);
    if (file == CompilerProvider.NOT_FOUND) {
      return CANCELLED;
    }

    return compiler
        .compile(file)
        .get(
            task -> {
              final var methodElement =
                  FindHelper.findMethod(task, className, methodName, erasedParameterTypes);
              if (methodElement == null) {
                return CANCELLED;
              }

              final var methodTree = Trees.instance(task.task).getTree(methodElement);
              if (methodTree == null) {
                return CANCELLED;
              }

              TextEdit[] edits = {EditHelper.removeTree(task.task, task.root(), methodTree)};
              return Collections.singletonMap(file, edits);
            });
  }
}
