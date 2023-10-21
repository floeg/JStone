package de.loegler.jstone.server.admin.sql;

import de.loegler.jstone.server.main.DatabaseManager;
import de.loegler.schule.netzwerk.QueryResult;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Methoden zum Kopieren und Laden von Tabellen
 * Wird benötigt um die Karteninformationen zu übernehmen.
 */
public class SQLSerialisierung {

    /**
     * Erstellt Einträge auf Grundlage einer csvDatei
     *
     * @param tableName Der Name der Tabelle in der Datenbank
     * @param csvFile   Der Pfad zur csv-Datei
     * @param autoIndex Der Index welcher automatisch erhöht werden soll.
     * @implNote Aktuell wird der automatische Index für Fremdschlüssel nicht unterstützt.
     */
    public void loadFromCSV(String tableName, Path csvFile, int autoIndex) {
        QueryResult queryResult = DatabaseManager.getInstance().fuehreSQLAus("SELECT * FROM " + tableName);
        String[] dataTypes = queryResult.getColumnTypes();
        String[] columnNames = queryResult.getColumnNames();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(csvFile.toFile()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] items = line.split(",");
                StringBuilder insert = new StringBuilder();
                for (int i = 0; i != items.length; i++) {
                    String dataType = dataTypes[i].toLowerCase(Locale.ROOT);
                    System.out.println(i + " i mit Typ: " + dataType);
                    if (dataType.contains("int") || dataType.contains("double") || items[i] == null) {
                        if (i != autoIndex)
                            insert.append(items[i]).append(", ");

                    } else {
                        insert.append("'").append(items[i]).append("', ");
                    }
                }
                int toSplit = insert.toString().lastIndexOf(',');
                insert = new StringBuilder("(" + insert.substring(0, toSplit) + ")");
                StringBuilder columns = new StringBuilder();
                for (int i = 0; i != columnNames.length; i++) {
                    if (i != autoIndex) {
                        columns.append(columnNames[i]).append(", ");
                    }
                }
                columns = new StringBuilder(columns.substring(0, columns.lastIndexOf(",")));
                columns = new StringBuilder("(" + columns + ")");
                String sql = "INSERT INTO " + tableName + columns + " VALUES " + insert;
                System.out.println(sql);
                DatabaseManager.getInstance().fuehreSQLAus(sql);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Speichert eine Tabelle als csv-Datei.
     *
     * @param tableName Der Name der Tabelle
     * @param csvFile   Der Pfad der csv-Datei.
     */
    public void saveTableToCSV(String tableName, Path csvFile) {
        String[][] data = DatabaseManager.getInstance().fuehreSQLAus("SELECT * FROM " + tableName).getData();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile.toFile(), false));
            for (String[] line : data) {
                StringBuilder tmp = new StringBuilder();
                for (int i = 0; i != line.length - 1; i++) {
                    tmp.append(line[i]).append(",");
                }
                tmp.append(line[line.length - 1]); //Kein, zum Schluss
                writer.write(tmp.toString());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void loadAllFromFolder(Path folder){
        QueryResult alleVorhandenenTabellen = getAllTables();
        int count = alleVorhandenenTabellen.getRowCount();
        for (int i = 0; i != count; i++) {
            String tableName = alleVorhandenenTabellen.getData()[i][0];
            Path tablePath = Paths.get(folder.toString(), tableName + ".jstone");
            if (Files.exists(tablePath)) {
                System.out.println("Lade Tabelle " + tableName);
                loadFromCSV(tableName, tablePath, -1);
            }
        }
    }

    public QueryResult getAllTables() {
        return DatabaseManager.getInstance().fuehreSQLAus
                    ("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_SCHEMA = 'jstone' ");
    }

}
