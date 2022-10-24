/*
 * FileLoader
 * Copyright (c) 2018-2019 Federico Berti
 * Last modified: 01/07/19 16:01
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package m68k.util;


import org.slf4j.Logger;

import javax.swing.filechooser.FileFilter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

public class FileUtil {

    private static final Logger LOG = LogHelper.getLogger(FileUtil.class.getSimpleName());

    private static final int[] EMPTY = new int[0];

    private static final String SNAPSHOT_VERSION = "SNAPSHOT";
    private static final String MANIFEST_RELATIVE_PATH = "/META-INF/MANIFEST.MF";

    public static byte[] readFileSafe(Path file) {
        byte[] rom = new byte[0];
        try {
            rom = Files.readAllBytes(file);
        } catch (IOException e) {
            LOG.error("Unable to load file: {}", file.getFileName());
        }
        return rom;
    }

    public static String readFileContentAsString(String fileName) {
        return String.join("\n", readFileContent(fileName));
    }

    public static List<String> readFileContent(String fileName) {
        Path pathObj = Paths.get(".", fileName);
        return readFileContent(pathObj);
    }

    public static List<String> readFileContent(Path pathObj) {
        List<String> lines = Collections.emptyList();
        String fileName = pathObj.getFileName().toString();
        if (pathObj.toFile().exists()) {
            try {
                lines = Files.readAllLines(pathObj);
            } catch (IOException e) {
                LOG.error("Unable to load {}, from path: {}", fileName, pathObj);
            }
            return lines;
        }
        String classPath = getCurrentClasspath();
        if (isRunningInJar(classPath)) {
            return loadFileContentFromJar(fileName);
        }
        LOG.warn("Unable to load: {}", fileName);
        return lines;
    }

    private static List<String> loadFileContentFromJar(String fileName) {
        List<String> lines = Collections.emptyList();
        try (
                InputStream inputStream = FileUtil.class.getResourceAsStream("/" + fileName);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            lines = reader.lines().collect(Collectors.toList());
        } catch (Exception e) {
            LOG.error("Unable to load {}, from path: {}", fileName, fileName);
        }
        return lines;
    }

    private static byte[] readBinaryFile(Path file, FileFilter fileFilter) {
        byte[] data = new byte[0];
        String fileName = file.toAbsolutePath().toString();
        try {
            if (fileFilter.accept(file.toFile())) {
                data = readBinaryFile(file);
            } else {
                throw new RuntimeException("Unexpected file: " + fileName);
            }
        } catch (Exception e) {
            LOG.error("Unable to load: {}", fileName, e);
        }
        return data;
    }

    public static byte[] readBinaryFile(Path file, String... ext) {
        String fileName = file.toAbsolutePath().toString();
        byte[] data = new byte[0];
        if (ZipUtil.isZipFile.test(fileName)) {
            data = ZipUtil.readZipFileContents(file, ext);
        } else if (ZipUtil.isGZipFile.test(fileName)) {
            data = ZipUtil.readGZipFileContents(file);
        } else {
            data = readFileSafe(file);
        }
        return data;
    }

    public static String loadVersionFromManifest() {
        String version = SNAPSHOT_VERSION;
        String classPath = getCurrentClasspath();
        if (!isRunningInJar(classPath)) {
            // Class not from JAR
            LOG.info("Not running from a JAR, using version: {}", version);
            return version;
        }
        String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) +
                MANIFEST_RELATIVE_PATH;
        try (InputStream is = new URL(manifestPath).openStream()) {
            Manifest manifest = new Manifest(is);
            Attributes attr = manifest.getMainAttributes();
            version = attr.getValue("Implementation-Version");
            LOG.info("Using version from Manifest: {}", version);
        } catch (Exception e) {
            LOG.error("Unable to load manifest file: {}", manifestPath);
        }
        return version;
    }

    private static String getCurrentClasspath() {
        Class<?> clazz = FileUtil.class;
        String className = clazz.getSimpleName() + ".class";
        return clazz.getResource(className).toString();
    }

    private static boolean isRunningInJar(String classPath) {
        return classPath.startsWith("jar");
    }

    //Handles symLinks
    public static String getFileName(Path rom) {
        String n = rom.getFileName().toString();
        try {
            //follows symLink, might throw
            String m = rom.toFile().getCanonicalFile().getName();
            if (!n.equalsIgnoreCase(m)) {
                n = m + " (" + n + ")";
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return n;
    }
}
