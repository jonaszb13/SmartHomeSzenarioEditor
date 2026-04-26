package service;

import util.DebugLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class FileHandler {
    public static String generateFile(String filePath, String fileName, String fileEnding) {
        final String filePathWithName = filePath + File.separator + fileName + "_" + System.currentTimeMillis() + "." + fileEnding;
        try {
            Files.createDirectories(Paths.get(filePath));
            File file = new File(filePathWithName);
            if (!file.exists() && !file.createNewFile()) throw new FileNotFoundException();
            return filePathWithName;
        } catch (FileNotFoundException eFnF) {
            DebugLog.addError("Datei konnte nicht angelegt werden: " + eFnF.getMessage(), eFnF);
        } catch (IOException eIO) {
            DebugLog.addError("Fehler beim Erstellen einer Datei: " + eIO.getMessage(), eIO);
        }
        return null;
    }
}
