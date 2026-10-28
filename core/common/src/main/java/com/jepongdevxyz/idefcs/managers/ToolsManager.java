/*
 * This file is part of DevxyzIDE.
 *
 * DevxyzIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DevxyzIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DevxyzIDE.  If not, see <https://www.gnu.org/licenses/>.
 *
 */
package com.jepongdevxyz.idefcs.managers;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.jepongdevxyz.idefcs.app.BaseApplication;
import com.jepongdevxyz.idefcs.app.configuration.IDEBuildConfigProvider;
import com.jepongdevxyz.idefcs.app.configuration.IJdkDistributionProvider;
import com.jepongdevxyz.idefcs.utils.Environment;

import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kotlin.io.ConstantsKt;
import kotlin.io.FilesKt;

public class ToolsManager {

  private static final Logger LOG = LoggerFactory.getLogger(ToolsManager.class);

  public static String COMMON_ASSET_DATA_DIR = "data/common";

  public static void init(@NonNull BaseApplication app, Runnable onFinish) {

    if (!IDEBuildConfigProvider.getInstance().supportsCpuAbi()) {
      LOG.error("Device not supported");
      return;
    }

    CompletableFuture.runAsync(() -> {
      // Copy the previous $HOME/.androidide setup into $HOME/.devxyzide on first run.
      migrateLegacyHomeDir();

      // Load installed JDK distributions
      IJdkDistributionProvider.getInstance().loadDistributions();

      writeNoMediaFile();
      extractAapt2();
      extractToolingApi();
      extractLocalPluginRepo();
      extractAndroidJar();
      extractColorScheme(app);
      writeInitScript();

      deleteIdeenv();
    }).whenComplete((__, error) -> {
      if (error != null) {
        LOG.error("Error extracting tools", error);
      }

      if (onFinish != null) {
        onFinish.run();
      }
    });
  }

      /**
     * Copies the previous IDE home directory ({@code $HOME/.androidide}) into the new
     * home directory ({@code $HOME/.devxyzide}) on first run, so an existing setup
     * (tooling jar, init script, local-plugin-repo and other IDE files) survives the
     * rename. The legacy directory is never deleted. The copy only runs when the new
     * home does not already contain any file, so it never overwrites an existing
     * setup; a failure is logged and tool extraction continues without it.
     */
    private static void migrateLegacyHomeDir() {
        try {
            File legacyHome = new File(Environment.HOME, ".androidide");
            if (!legacyHome.isDirectory()) {
                return;
            }
            File newHome = Environment.DEVXYZIDE_HOME;
            if (hasAnyFile(newHome)) {
                return;
            }
            copyDirectory(legacyHome, newHome);
            LOG.info("Migrated legacy IDE home directory {} to {}", legacyHome, newHome);
        } catch (Exception e) {
            LOG.warn("Failed to migrate legacy IDE home directory, continuing with a fresh setup", e);
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

private static void extractColorScheme(final BaseApplication app) {
    final var defPath = "editor/schemes";
    final var dir = new File(Environment.DEVXYZIDE_UI, defPath);
    try {
      for (final String asset : app.getAssets().list(defPath)) {

        final var prop = new File(dir, asset + "/" + "scheme.prop");
        if (prop.exists() && !shouldExtractScheme(app, new File(dir, asset),
            defPath + "/" + asset)) {
          continue;
        }

        final File schemeDir = new File(dir, asset);
        if (schemeDir.exists()) {
          schemeDir.delete();
        }

        ResourceUtils.copyFileFromAssets(defPath + "/" + asset, schemeDir.getAbsolutePath());
      }
    } catch (IOException e) {
      LOG.error("Failed to extract color schemes", e);
    }
  }

  private static boolean shouldExtractScheme(final BaseApplication app, final File dir,
      final String path) throws IOException {

    final var schemePropFile = new File(dir, "scheme.prop");
    if (!schemePropFile.exists()) {
      return true;
    }

    final var files = app.getAssets().list(path);
    if (Arrays.stream(files).noneMatch("scheme.prop"::equals)) {
      // no scheme.prop file
      return true;
    }

    try {
      final var props = new Properties();
      Reader reader = new InputStreamReader(app.getAssets().open(path + "/scheme.prop"));
      props.load(reader);
      reader.close();

      final var version = Integer.parseInt(props.getProperty("scheme.version", "0"));
      if (version == 0) {
        return true;
      }

      props.clear();

      reader = new FileReader(schemePropFile);
      props.load(reader);
      reader.close();

      final var fileVersion = Integer.parseInt(props.getProperty("scheme.version", "0"));
      if (fileVersion < 0) {
        return true;
      }

      return version > fileVersion;
    } catch (Throwable err) {
      LOG.error("Failed to read color scheme version for scheme '{}'", path, err);
      return false;
    }
  }

  private static void writeNoMediaFile() {
    final var noMedia = new File(BaseApplication.getBaseInstance().getProjectsDir(), ".nomedia");
    if (!noMedia.exists()) {
      try {
        if (!noMedia.createNewFile()) {
          LOG.error("Failed to create .nomedia file in projects directory");
        }
      } catch (IOException e) {
        LOG.error("Failed to create .nomedia file in projects directory");
      }
    }
  }

  private static void extractAndroidJar() {
    if (!Environment.ANDROID_JAR.exists()) {
      ResourceUtils.copyFileFromAssets(getCommonAsset("android.jar"),
          Environment.ANDROID_JAR.getAbsolutePath());
    }
  }

  private static void deleteIdeenv() {
    final var file = new File(Environment.BIN_DIR, "ideenv");
    if (file.exists() && !file.delete()) {
      LOG.warn("Unable to delete file: {}", file);
    }
  }

  @NonNull
  @Contract(pure = true)
  public static String getCommonAsset(String name) {
    return COMMON_ASSET_DATA_DIR + "/" + name;
  }

  private static void extractAapt2() {
    if (!Environment.AAPT2.exists()) {
      final var context = BaseApplication.getBaseInstance();
      final var nativeLibraryDir = context.getApplicationInfo().nativeLibraryDir;
      final var sourceAapt2 = new File(nativeLibraryDir, "libaapt2.so");
      if (sourceAapt2.exists() && sourceAapt2.isFile()) {
        FilesKt.copyTo(sourceAapt2, Environment.AAPT2, true, ConstantsKt.DEFAULT_BUFFER_SIZE);
      } else {
        LOG.error("{} file does not exist! This can be problematic.", sourceAapt2);
      }
    }

    if (!Environment.AAPT2.canExecute() && !Environment.AAPT2.setExecutable(true)) {
      LOG.error("Cannot set executable permissions to AAPT2 binary");
    }
  }

  private static void extractToolingApi() {
    if (Environment.TOOLING_API_JAR.exists()) {
      FileUtils.delete(Environment.TOOLING_API_JAR);
    }

    ResourceUtils.copyFileFromAssets(getCommonAsset("tooling-api-all.jar"),
        Environment.TOOLING_API_JAR.getAbsolutePath());
  }

  /**
   * Extracts one bundled module's jar and pom into a local Maven
   * repository layout under HOME/.devxyzide/local-plugin-repo.
   * The module is skipped only when both files already exist at
   * exactly that version path.
   * See extractLocalPluginRepo for the modules that are extracted.
   */
  private static void extractLocalPluginModule(String group, String artifact, String version) {
    final var versionDir = new File(getLocalPluginRepoDir(),
        group.replace('.', '/') + "/" + artifact + "/" + version);
    final var jarFile = new File(versionDir, artifact + "-" + version + ".jar");
    final var pomFile = new File(versionDir, artifact + "-" + version + ".pom");
    if (jarFile.exists() && pomFile.exists()) {
      return;
    }
    if (!versionDir.exists() && !versionDir.mkdirs()) {
      LOG.error("Failed to create local plugin repository directory: {}", versionDir);
      return;
    }
    if (!jarFile.exists()) {
      ResourceUtils.copyFileFromAssets(getCommonAsset("local-plugin/" + artifact + ".jar"),
          jarFile.getAbsolutePath());
    }
    if (!pomFile.exists()) {
      ResourceUtils.copyFileFromAssets(getCommonAsset("local-plugin/" + artifact + ".pom"),
          pomFile.getAbsolutePath());
    }
  }

  /** Extracts the bundled plugin and its plugin-config and build-info dependencies, parsing the group, artifact and version from the init script classpath line. */ private static void extractLocalPluginRepo() { final Matcher matcher = Pattern.compile("classpath[(]'([^:]+):([^:]+):([^']+)'[)]").matcher(readInitScript()); if (!matcher.find()) { LOG.error("Could not find the Gradle plugin classpath in the init script"); return; } extractLocalPluginModule(matcher.group(1), matcher.group(2), matcher.group(3)); extractLocalPluginModule("com.jepongdevxyz.idefcs.tooling", "plugin-config", matcher.group(3)); extractLocalPluginModule("com.jepongdevxyz.idefcs.utilities", "build-info", matcher.group(3)); } private static File getLocalPluginRepoDir() {
    return new File(Environment.DEVXYZIDE_HOME, "local-plugin-repo");
  }

  private static String getLocalPluginRepoUri() {
    var uri = getLocalPluginRepoDir().toURI().toString();
    if (!uri.endsWith("/")) {
      uri += "/";
    }
    return uri;
  }

  private static void writeInitScript() {
    final var initScript = Environment.INIT_SCRIPT;
    final var initScriptBak = new File(initScript.getParentFile(), initScript.getName() + ".bak");
    final var contents = readInitScript().replace("@LOCAL_PLUGIN_REPO_URL@",
        getLocalPluginRepoUri());

    FilesKt.writeText(initScriptBak, contents, StandardCharsets.UTF_8);
    FilesKt.writeText(initScript, contents, StandardCharsets.UTF_8);
  }

  @NonNull
  private static String readInitScript() {
    return ResourceUtils.readAssets2String(getCommonAsset("androidide.init.gradle"));
  }

}
