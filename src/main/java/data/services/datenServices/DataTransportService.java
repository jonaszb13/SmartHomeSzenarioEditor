package data.services.datenServices;

import data.models.fachobjekte.Raum;
import data.services.objektServices.RaumObjektService;
import util.FileHandler;
import util.statusmeldungen.StatusLog;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class DataTransportService {

    public boolean exportData() {
        try {
            String data = generateAuszug();
            File file = new File(FileHandler.generateFile("", "Datenauszug", "csv"));
            return FileHandler.writeTextFile(file, data);
        } catch (SQLException e) {
            StatusLog.addError(e);
            return false;
        }
    }

    public boolean importData(String filePath) {
        try {
            StringBuilder builder = new StringBuilder();
            List<String> data = FileHandler.readTextFile(new File(filePath));
            String[] werte;
            int teil = 0;
            for (String line : data) {
                if (line.startsWith(";")) {
                    teil++;
                    writeImport(builder.toString());
                    builder = new StringBuilder();
                    switch (teil) {
                        case 0:
                            //language=SQL
                            builder.append("INSERT INTO RAEUME (ID, NAME) VALUES ");
                            break;
                        case 1:
                            //language=SQL
                            builder.append("INSERT INTO GERAETE (ID, NAME, RAUM, ART) VALUES ");
                            break;
                        case 2:
                            //language=SQL
                            builder.append("INSERT INTO GERAETE_WERTE (ID, GERAET, SCHLUESSEL, WERT) VALUES ");
                            break;
                        case 3:
                            //language=SQL
                            builder.append("INSERT INTO SZENARIEN (ID, NAME, STATUS, BESCHREIBUNG) VALUES ");
                            break;
                        case 4://language=SQL
                            builder.append("INSERT INTO SZENARIEN_INHALT (ID, AKTION, SZENARIO, GERAET, SCHLUESSEL, WERT, POSITION) VALUES ");
                            break;
                    }
                }
                switch (teil) {
                    case 0:
                        werte = line.split(";");
                        builder.append(", (").append(werte[0]).append(", ").append(werte[1]).append(")");
                        break;
                    case 1, 2:
                        werte = line.split(";");
                        builder.append(", (").append(werte[0]).append(", ").append(werte[1]).append(", ").append(werte[2])
                                .append(", ").append(werte[3]).append(")");
                        break;
                    case 3:
                        werte = line.split(";");
                        builder.append(", (").append(werte[0]).append(", ").append(werte[1]).append(", ").append(werte[2])
                                .append(", ").append(werte[3]).append(", ").append(werte[4]).append(", ").append(werte[5])
                                .append(", ").append(werte[6]).append(")");
                        break;
                }
                writeImport(builder.toString());
            }

        } catch (SQLException | FileNotFoundException e) {
            StatusLog.addError(e);
            return false;
        }
        return true;
    }

    public void writeImport(String sql) throws SQLException {
        DataAccess.getInstance().executeUpdate(sql);
    }


    private String generateAuszug() throws SQLException {
        return generateRaumauszug() + generateGeraetAuszug() + generateSzenarioAuszug();
    }

    private String generateRaumauszug() throws SQLException {
        StringBuilder builder = new StringBuilder();
        Map<UUID, Raum> raumMap = RaumObjektService.getInstance().getRaumMap();
        for (Raum entry : raumMap.values()) {
            builder.append(entry.getId()).append(";").append(entry.getName()).append("\n");
        }
        return builder.append(";\n").toString();
    }

    private String generateGeraetAuszug() throws SQLException {
        StringBuilder builder = new StringBuilder();
        //language=SQL
        String sqlGeraet = "select ID, NAME, RAUM, ART from GERAETE";
        CachedRowSet crs = DataAccess.getInstance().getData(sqlGeraet);
        while (crs.next()) {
            builder.append(crs.getObject(1)).append(";").append(crs.getString(2))
                    .append(crs.getObject(3)).append(";").append(crs.getString(4))
                    .append("\n");
        }
        builder.append(";\n");
        //language=SQL
        String sqlGeraetWerte = "select ID, GERAET, SCHLUESSEL, WERT FROM GERAETE_WERTE";
        crs = DataAccess.getInstance().getData(sqlGeraetWerte);
        while (crs.next()) {
            builder.append(crs.getObject(1)).append(";").append(crs.getObject(2))
                    .append(crs.getString(3)).append(";").append(crs.getString(4))
                    .append("\n");
        }
        return builder.append(";\n").toString();
    }

    private String generateSzenarioAuszug() throws SQLException {
        StringBuilder builder = new StringBuilder();
        //language=SQL
        String sqlSzenario = "select ID, NAME, STATUS, BESCHREIBUNG from SZENARIEN";
        CachedRowSet crs = DataAccess.getInstance().getData(sqlSzenario);
        while (crs.next()) {
            builder.append(crs.getObject(1)).append(";").append(crs.getString(2))
                    .append(crs.getString(3)).append(";").append(crs.getString(4))
                    .append("\n");
        }
        builder.append(";\n");
        //language=SQL
        String sqlSzenarioWerte = "select ID, AKTION, SZENARIO, GERAET, SCHLUESSEL, WERT, POSITION FROM SZENARIEN_INHALT";
        crs = DataAccess.getInstance().getData(sqlSzenarioWerte);
        while (crs.next()) {
            builder.append(crs.getObject(1)).append(";").append(crs.getString(2))
                    .append(crs.getObject(3)).append(";").append(crs.getObject(4))
                    .append(crs.getString(5)).append(";").append(crs.getString(6))
                    .append(crs.getInt(7)).append("\n");
        }
        return builder.append(";\n").toString();
    }
}
