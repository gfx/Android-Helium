package com.github.gfx.helium;

import com.google.common.io.Files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestUtils {
    public static File getAssetFile(String name) throws FileNotFoundException {
        String[] appDirs = {".", "app"};
        for (String appDir : appDirs) {
            File file = new File(appDir, "src/test/assets/" + name);
            if (file.exists()) {
                return file;
            }
        }
        throw new FileNotFoundException("No resource file: " + name);
    }

    public static byte[] getAssetFileInBytes(String name) throws IOException {
        return Files.toByteArray(getAssetFile(name));
    }
}
