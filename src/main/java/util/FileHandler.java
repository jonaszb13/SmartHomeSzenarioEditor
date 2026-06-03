package util;

import util.statusmeldungen.StatusLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public final class FileHandler {
    private FileHandler() {
    }

    public static String generateFile(final String filePath, final String fileName, final String fileEnding) {
        String filePathWithName = filePath + File.separator + fileName + "_" + System.currentTimeMillis() + "." + fileEnding;
        try {
            Files.createDirectories(Paths.get(filePath));
            final File file = new File(filePathWithName);
            if (!file.exists() && !file.createNewFile()) {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException eFnF) {
            StatusLog.addError("Datei konnte nicht angelegt werden: " + eFnF.getMessage(), eFnF);
            filePathWithName = null;
        } catch (IOException eIO) {
            StatusLog.addError("Fehler beim Erstellen einer Datei: " + eIO.getMessage(), eIO);
            filePathWithName = null;
        }
        return filePathWithName;
    }

    //Daten in Datei schreiben
    public static boolean writeTextFile(File file, String data)  {
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(data);
            fw.close();
        } catch (IOException eIO) {
            StatusLog.addError(eIO);
            return false;
        }
        return true;
    }

    //Datei auslesen
    public static ArrayList<String> readTextFile(File file) throws FileNotFoundException {
        Scanner reader = new Scanner(file);
        ArrayList<String> data = new ArrayList<>();
        while (reader.hasNextLine()) {
            data.add(reader.nextLine());
        }
        reader.close();
        return data;
    }
}
