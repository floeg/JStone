package de.loegler.jstone.server.admin.gui;

import de.loegler.core.gui.TextFrame;
import de.loegler.jstone.server.admin.sql.SQLSerialisierung;
import de.loegler.jstone.server.main.DatabaseManager;
import de.loegler.schule.netzwerk.QueryResult;
import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * GUI zur Steuerung der SQL-Serialisierung
 */
public class SQLSerialisierungGUI {
    private JFrame frame;
    private JPanel panel;
    private JButton exportToFile;
    private JButton importFromFile;
    private JCheckBox[] tableNames;
    private SQLSerialisierung sqlSerialisierung;

    /**
     * Erstellt und zeigt eine neue GUI zur SQL Serialisierung an
     * @param sqlSerialisierung
     */
    public SQLSerialisierungGUI(SQLSerialisierung sqlSerialisierung) {
        frame = new JFrame("JSTONE - Admin-Serialisierung ");
        this.sqlSerialisierung = sqlSerialisierung;
        panel = new JPanel();
        frame.setContentPane(panel);
        exportToFile = new JButton("Exportiere zu Datei");
        importFromFile = new JButton("Importiere von Datei");
        exportToFile.addActionListener(e -> createExportMenu());
        importFromFile.addActionListener(e -> createImportMenu());
        panel.add(importFromFile);
        panel.add(exportToFile);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(350, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void createExportMenu() {
        panel.setLayout(new GridLayout(0, 2));
        panel.removeAll();
        QueryResult alleVorhandenenTabellen = DatabaseManager.getInstance().fuehreSQLAus
                ("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_SCHEMA = 'jstone' ");

        int count = alleVorhandenenTabellen.getRowCount();
        tableNames = new JCheckBox[count];
        for (int i = 0; i != count; i++) {
            String tableName = alleVorhandenenTabellen.getData()[i][0];
            tableNames[i] = new JCheckBox(tableName);
            panel.add(tableNames[i]);
        }
        if (count % 2 != 0) //Für das Layout
            panel.add(new JLabel());
        JButton serialisieren = new JButton("Serialisieren");
        serialisieren.addActionListener(e -> {
            LookAndFeel current = UIManager.getLookAndFeel();
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                JFileChooser fileChooser = new JFileChooser(Paths.get("/").toFile());
                fileChooser.setDialogTitle("Bitte wähle einen Ordner zum Speichern.");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int res = fileChooser.showDialog(null, "Nutze Ordner");
                if (res == JFileChooser.APPROVE_OPTION) {
                    Path directory = fileChooser.getSelectedFile().toPath();
                    for (JCheckBox tableName : this.tableNames) {
                        if (tableName.isSelected()) {
                            String tName = tableName.getText();
                            System.out.println("Speichere Tabelle " + tName);
                            Path filePath = Paths.get(directory.toString(), tName + ".jstone");
                            sqlSerialisierung.saveTableToCSV(tName, filePath);
                        }
                    }
                    new TextFrame(new TextFrame.TextFrameOptions().enableAccept(), "Erfolgreich", "Serialisierung erfolgreich!");
                }
                UIManager.setLookAndFeel(current);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException classNotFoundException) {
                classNotFoundException.printStackTrace();
            }
        });
        panel.add(serialisieren);
        frame.revalidate();
        frame.repaint();
    }

    public void createImportMenu() {
        panel.removeAll();
        panel.setLayout(new GridLayout());
        try {
            LookAndFeel current = UIManager.getLookAndFeel();
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFileChooser fileChooser = new JFileChooser(Paths.get("/").toFile());
            fileChooser.setDialogTitle("Wähle den Ordner mit Dateien");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int ans = fileChooser.showDialog(null, "Speicherordner");
            if (ans == JFileChooser.APPROVE_OPTION) {
                this.sqlSerialisierung.loadAllFromFolder(fileChooser.getSelectedFile().toPath());
                new TextFrame(new TextFrame.TextFrameOptions().enableAccept(), "Erfolg!", "Dateien wurden erfolgreich eingelesen!");
            }
            UIManager.setLookAndFeel(current);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        frame.revalidate();
        frame.repaint();
    }
}
