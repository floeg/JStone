package de.loegler.schule.netzwerk;

import de.loegler.schule.datenstrukturen.List;

import java.sql.*;

/**
 * Ein Objekt der Klasse DatabaseConnector ermöglicht die Abfrage und Manipulation einer relationalen Datenbank.
 * Beim Erzeugen des Objekts wird eine Datenbankverbindung aufgebaut.
 */
public class DatabaseConnector {
    public static boolean DEBUG = false;
    private QueryResult lastQueryResult;
    private String errorMessage;
    private Connection connection;

    /**
     * Ein Objekt vom Typ DatabaseConnector wird erstellt,
     * und eine Verbindung zur Datenbank wird aufgebaut.
     * @param pIP IP-Adresse des Datenbankservers
     * @param pPort Portnummer des Datenbankservers
     * @param pDatabase Name der Datenbank
     * @param pUsername Benutzername für die Datenbank
     * @param pPassword Passwort für die Datenbank
     */
    public DatabaseConnector(String pIP, int pPort, String pDatabase, String pUsername, String pPassword) {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://"
                    + pIP + ":" + pPort + "/" + pDatabase + "?serverTimezone=UTC", pUsername, pPassword);
        } catch (SQLException e) {
            this.errorMessage = e.getMessage();
            if (DEBUG)
                e.printStackTrace();
        }
    }

    /**
     * Sendet den SQL Befehl an die Datenbank. Ein mögliches Ergebnis kann über die Methode getCurrentQueryResult abgerufen werden.
     * Warnung: Es werden KEINE PreparedStatements verwendet. Dies kann zu SQL-Injections führen.
     * Eine Verwendung von PreparedStatements ist aufgrund der Vorgaben der Klassen des Abiturs nicht möglich.
     */
    public void executeStatement(String pSQLStatement) {
        this.errorMessage = null;
        this.lastQueryResult = null;
        try (Statement statement = connection.createStatement()) { //AutoCloseable
            if (statement.execute(pSQLStatement)) {
                ResultSet r = statement.getResultSet();
                this.lastQueryResult = this.createQueryResult(r);
            }
        } catch (SQLException e) {
            this.errorMessage = e.getMessage();
            if (DEBUG)
                e.printStackTrace();
        }
    }

    private QueryResult createQueryResult(ResultSet resultSet) {
        try {
            ResultSetMetaData meta = resultSet.getMetaData();
            int columnCount = meta.getColumnCount();
            int rows = 0;
            String[] columnTypes = new String[columnCount];
            String[] columnNames = new String[columnCount];
            for (int i = 0; i != columnCount; i++) {
                columnNames[i] = meta.getColumnLabel(i + 1);
                columnTypes[i] = meta.getColumnTypeName(i + 1);
            }
            String[][] data;
            List<String[]> result = new List<>();

            while (resultSet.next()) {
                String[] tmp = new String[columnCount];
                for (int i = 0; i != tmp.length; i++) {
                    tmp[i] = resultSet.getString(i + 1); //Start bei 1
                }
                rows++;
                result.append(tmp);
            }
            data = new String[rows][columnCount];
            result.toFirst();
            for (int i = 0; result.hasAccess(); i++, result.next()) {
                data[i] = result.getContent();
            }
            return new QueryResult(data, columnNames, columnTypes);
        } catch (SQLException e) {
            this.errorMessage = e.getMessage();
            if (DEBUG)
                e.printStackTrace();
        }
        return null;
    }

    /**
     * Die Anfrage liefert das Ergebnis des letzten geschickten
     * SQL-Befehls als Objekt vom Typ {@link QueryResult} zurück.
     * Liegt kein Ergebnis vor, wird null zurückgegeben.
     *
     * @return
     */
    public QueryResult getCurrentQueryResult() {
        return this.lastQueryResult;
    }

    /**
     * Die Anfrage liefert null oder eine Fehlermeldung, die sich jeweils auf die letzte zuvor ausgeführte Datenbankoperation bezieht.
     *
     * @return null oder eine Fehlermeldung der letzten Datenbankoperation
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Die Datenbankverbindung wird geschlossen.
     */
    public void close() {
        try {
            connection.close();
        } catch (SQLException throwables) {
            if (DEBUG)
                throwables.printStackTrace();

            this.errorMessage = throwables.getMessage();
        }
    }
}
