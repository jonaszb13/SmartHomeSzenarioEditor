package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHandler {
    public static void generateFile(String filePath, String fileName, String fileEnding) {
        try {
            Files.createDirectories(Paths.get(filePath));
            String filePathWithName = filePath + File.separator + fileName + "_" + System.currentTimeMillis() + "." + fileEnding;
            File file = new File(filePathWithName);
            if (!file.exists()) {
                boolean createFileSuccessful = file.createNewFile();
                if (!createFileSuccessful) throw new FileNotFoundException();
            }
        } catch (FileNotFoundException eFnF) {
            DebugLog.addError("Datei konnte nicht angelegt werden: " + eFnF.getMessage(), eFnF);
        } catch (IOException eIO) {
            DebugLog.addError("Fehler beim Erstellen einer Datei: " + eIO.getMessage(), eIO);
        }
    }
}
