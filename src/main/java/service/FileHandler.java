package service;

import util.Errorlog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHandler {
    public static File generateFile(String filePath, String fileName, String fileEnding) {
        try {
            Files.createDirectories(Paths.get(filePath));
            filePath += File.separator + fileName + "_" + System.currentTimeMillis() + "." + fileEnding;
            File file = new File(filePath);
            if (!file.exists()) {
                boolean createFileSuccessful = file.createNewFile();
                if (!createFileSuccessful) throw new FileNotFoundException();
            }
            return file;
        } catch (FileNotFoundException eFnF) {
            System.err.println("Datei konnte nicht angelegt werden: " + eFnF.getMessage());
            Errorlog.addError(eFnF);
            return null;
        } catch (IOException eIO) {
            System.err.println("Fehler bei erstellen einer Datei: " + eIO.getMessage());
            Errorlog.addError(eIO);
            return null;
        }
    }

    public static void writeTextFile(File file, String data) throws IOException {
        FileWriter fw = new FileWriter(file);
        fw.write(data);
        fw.close();
    }
}
