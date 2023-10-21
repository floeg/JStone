package de.loegler.schule.netzwerk;

/**
 * Ein Objekt der Klasse QueryResult stellt die Ergebnistabelle einer Datenbankanfrage mithilfe der Klasse  {@link DatabaseConnector}  dar.
 * Objekte dieser Klasse werden nur von der Klasse {@link DatabaseConnector} erstellt.
 * Die Klasse verfügt über keinen öffentlichen Konstruktor.
 *
 */
public class QueryResult {
    /**
     * Speicherung der Daten im Format [Zeile][Spalte]
     */
    private final String[][] data;
    private final String[] columnNames;
    private final String[] columnTypes;

    /**
     * Konstruktor, welcher nur von {@link DatabaseConnector} benutzt werden kann.
     */
    QueryResult(String[][] data, String[] columnNames, String[] columnTypes) {
        this.data = data;
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
    }

    /**
     * Die    Anfrage    liefert    die    Einträge    der    Ergebnistabelle
     * als zweidimensionales Feld vom Typ String.
     * Der erste Index des Feldes stellt     die     Zeile     und     der     zweite     die     Spalte     dar
     * (d.h. String[zeile][spalte])
     *
     * @return Spalteninhalte vom Typen String im Format [Zeile][Spalte]
     */
    public String[][] getData() {
        return data;
    }

    /**
     * Die  Anfrage  liefert  die  Bezeichner  der  Spalten  der Ergebnistabelle als Feld vom Typ String zurück.
     *
     * @return Alle Namen der Spalten im Format [Spalte]
     */
    public String[] getColumnNames() {
        return this.columnNames;
    }

    /**
     * Die    Anfrage    liefert    die    Typenbezeichnung    der    Spalten
     * der Ergebnistabelle als Feld vom Typ String zurück. Die Bezeichnungen entsprechen den Angaben in der Datenbank.
     *
     * @return Alle Datentypen der Spalten im Format [Spalte]
     */
    public String[] getColumnTypes() {
        return this.columnTypes;
    }

    /**
     * Die Anfrage liefert die Anzahl der Zeilen der Ergebnistabelle als int.
     *
     * @return
     */
    public int getRowCount() {
        return this.data.length;
    }

    /**
     * Die Anfrage liefert die Anzahl der Spalten der Ergebnistabelle als int.
     *
     * @return
     */
    public int getColumnCount() {
        return data.length > 0 ? data[0].length : 0;
    }
}
