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

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

@SuppressLint("SdCardPath")
public final class Environment {

  public static final String PROJECTS_FOLDER = "DevxyzIDEProjects";
  private static final Logger LOG = LoggerFactory.getLogger(Environment.class);
  public static File ROOT;
  public static File PREFIX;
  public static File HOME;
  public static File DEVXYZIDE_HOME;
  public static File DEVXYZIDE_UI;
  public static File JAVA_HOME;
  public static File ANDROID_HOME;
  public static File TMP_DIR;
  public static File BIN_DIR;
  public static File LIB_DIR;
  public static File PROJECTS_DIR;
  public static File REALM_DB_DIR;

  /**
   * Used by Java LSP until the project is initialized.
   */
  public static File ANDROID_JAR;

  public static File TOOLING_API_JAR;

  public static File INIT_SCRIPT;
  public static File GRADLE_USER_HOME;
  public static File AAPT2;
  public static File JAVA;
  public static File BASH_SHELL;
  public static File LOGIN_SHELL;

  public static void init(Context context) {
    ROOT = context.getFilesDir();
    PREFIX = mkdirIfNotExits(new File(ROOT, "usr"));
    HOME = mkdirIfNotExits(new File(ROOT, "home"));
    DEVXYZIDE_HOME = mkdirIfNotExits(new File(HOME, ".devxyzide"));
    TMP_DIR = mkdirIfNotExits(new File(PREFIX, "tmp"));
    BIN_DIR = mkdirIfNotExits(new File(PREFIX, "bin"));
    LIB_DIR = mkdirIfNotExits(new File(PREFIX, "lib"));
    PROJECTS_DIR = mkdirIfNotExits(new File(FileUtil.getExternalStorageDir(), PROJECTS_FOLDER));
    ANDROID_JAR = mkdirIfNotExits(new File(DEVXYZIDE_HOME, "android.jar"));
    TOOLING_API_JAR = new File(mkdirIfNotExits(new File(DEVXYZIDE_HOME, "tooling-api")),
      "tooling-api-all.jar");
    AAPT2 = new File(DEVXYZIDE_HOME, "aapt2");
    DEVXYZIDE_UI = mkdirIfNotExits(new File(DEVXYZIDE_HOME, "ui"));
    REALM_DB_DIR = mkdirIfNotExits(new File(ROOT, "realm-dbs"));

    INIT_SCRIPT = new File(mkdirIfNotExits(new File(DEVXYZIDE_HOME, "init")), "init.gradle");
    GRADLE_USER_HOME = new File(HOME, ".gradle");

    ANDROID_HOME = new File(HOME, "android-sdk");
    JAVA_HOME = new File(PREFIX, "opt/openjdk");

    JAVA = new File(JAVA_HOME, "bin/java");
    BASH_SHELL = new File(BIN_DIR, "bash");
    LOGIN_SHELL = new File(BIN_DIR, "login");

    setExecutable(JAVA);
    setExecutable(BASH_SHELL);

    System.setProperty("user.home", HOME.getAbsolutePath());
  }

  public static File mkdirIfNotExits(File in) {
    if (in != null && !in.exists()) {
      FileUtils.createOrExistsDir(in);
    }

    return in;
  }

  public static void setExecutable(@NonNull final File file) {
    if (!file.setExecutable(true)) {
      LOG.error("Unable to set executable permissions to file: {}", file);
    }
  }

  public static void setProjectDir(@NonNull File file) {
    PROJECTS_DIR = new File(file.getAbsolutePath());
  }

  public static void putEnvironment(Map<String, String> env, boolean forFailsafe) {

    env.put("HOME", HOME.getAbsolutePath());
    env.put("ANDROID_HOME", ANDROID_HOME.getAbsolutePath());
    env.put("ANDROID_SDK_ROOT", ANDROID_HOME.getAbsolutePath());
    env.put("ANDROID_USER_HOME", HOME.getAbsolutePath() + "/.android");
    env.put("JAVA_HOME", JAVA_HOME.getAbsolutePath());
    env.put("GRADLE_USER_HOME", GRADLE_USER_HOME.getAbsolutePath());
    env.put("SYSROOT", PREFIX.getAbsolutePath());
    env.put("PROJECTS", PROJECTS_DIR.getAbsolutePath());

    // add user envs for non-failsafe sessions
    if (!forFailsafe) {
      // No mirror select
      env.put("TERMUX_PKG_NO_MIRROR_SELECT", "true");
    }
  }

    /**
     * Copies the previous per-project IDE directory ({@code .androidide}) into the new
     * per-project directory ({@code .devxyzide}) on first use, so an existing project
     * setup survives the rename. The legacy directory is never deleted. The copy only
     * runs when the new directory does not already contain any file, so it never
     * overwrites an existing setup; a failure is logged and the new directory is
     * returned anyway.
     */
    private static void migrateLegacyProjectCacheDir(File projectDir, File cacheDir) {
        try {
            File legacyCacheDir = new File(projectDir, ".androidide");
            if (!legacyCacheDir.isDirectory()) {
                return;
            }
            if (hasAnyFile(cacheDir)) {
                return;
            }
            copyDirectory(legacyCacheDir, cacheDir);
            LOG.info("Migrated legacy per-project IDE directory {} to {}", legacyCacheDir, cacheDir);
        } catch (Exception e) {
            LOG.warn("Failed to migrate legacy per-project IDE directory, continuing", e);
        }
    }

    private static boolean hasAnyFile(File dir) {
        if (dir == null || !dir.isDirectory()) {
            return false;
        }
        File[] children = dir.listFiles();
        if (children == null) {
            return false;
        }
        for (File child : children) {
            if (Files.isSymbolicLink(child.toPath()) || child.isFile() || hasAnyFile(child)) {
                return true;
            }
        }
        return false;
    }

    private static void copyDirectory(File source, File target) throws IOException {
        if (Files.isSymbolicLink(source.toPath())) {
            return;
        }
        if (source.isDirectory()) {
            if (!target.exists() && !target.mkdirs()) {
                throw new IOException("Failed to create directory: " + target);
            }
            File[] children = source.listFiles();
            if (children != null) {
                for (File child : children) {
                    copyDirectory(child, new File(target, child.getName()));
                }
            }
            return;
        }
        if (!target.exists()) {
            File parent = target.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            Files.copy(source.toPath(), target.toPath());
        }
    }

  public static File getProjectCacheDir(File projectDir) {
    File cacheDir = new File(projectDir, ".devxyzide");
    migrateLegacyProjectCacheDir(projectDir, cacheDir);
    return cacheDir;
  }

  @NonNull
  public static File createTempFile() {
    var file = newTempFile();
    while (file.exists()) {
      file = newTempFile();
    }

    return file;
  }

  @NonNull
  private static File newTempFile() {
    return new File(TMP_DIR, "temp_" + UUID.randomUUID().toString().replace('-', 'X'));
  }
}
