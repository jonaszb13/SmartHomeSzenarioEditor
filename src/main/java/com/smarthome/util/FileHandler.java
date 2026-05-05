package com.smarthome.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class FileHandler {

    public static String generateFile(final String filePath, final String fileName, final String fileEnding) {
        final String filePathWithName = filePath + File.separator + fileName + "_" + System.currentTimeMillis() + "." + fileEnding;
        try {
            Files.createDirectories(Paths.get(filePath));
            final File file = new File(filePathWithName);
            if (!file.exists() && !file.createNewFile()) throw new FileNotFoundException();
            return filePathWithName;
        } catch (FileNotFoundException eFnF) {
            StatusLog.addError("Datei konnte nicht angelegt werden: " + eFnF.getMessage(), eFnF);
        } catch (IOException eIO) {
            StatusLog.addError("Fehler beim Erstellen einer Datei: " + eIO.getMessage(), eIO);
        }
        return null;
    }
}
