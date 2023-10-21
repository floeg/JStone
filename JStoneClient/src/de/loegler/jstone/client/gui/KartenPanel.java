package de.loegler.jstone.client.gui;

import de.loegler.jstone.core.Diener;
import de.loegler.jstone.core.Karte;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;

/**
 * Grafische Darstellung einer Karte
 */
public class KartenPanel extends JLabel {

    private static final long serialVersionUID = 1794397244410247925L;
    public final String HAND = "Hand", BFIELD = "Battlefield";
    // Ein Klick auf das Bild des Dieners/Zaubers soll ihn auswählen
    private JButton imageButton;
    private JLabel manaLabel, nameLabel = new JLabel(), dienerAG, dienerLB;
    private Karte k;
    protected String currentPosition = HAND;

    public KartenPanel(Karte k) {
        this.setLayout(null);
        this.k = k;
        if (getKarte().getBild() != null && !getKarte().getBild().isEmpty()) {
            ImageIcon icon;
            try (InputStream in = KartenPanel.class.getResourceAsStream("/images/" + getKarte().getBild())) {
                if (in != null) { //Bild nicht vorhanden -> Icon wird nicht gesetzt
                    icon = new ImageIcon(ImageIO.read(in));
                    imageButton = new JButton(icon);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (imageButton == null)
            imageButton = new JButton();
        imageButton.setBounds(0, 20, 100, 160);
        manaLabel = new JLabel(k.getManakosten() + "");
        manaLabel.setBounds(80, 0, 20, 25);
        this.add(manaLabel);
        nameLabel.setBounds(0, 0, 90, 25);
        this.add(nameLabel);
        this.add(imageButton);
        if (k instanceof Diener) {
            this.diener();
        }
        imageButton.addMouseListener(new PanelListener());
    }

    protected KartenPanel(Karte k, Rectangle r) {
        this(k);
        this.setBounds(r);
        revalidate();
    }

    public String getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(String currentPosition) {
        this.currentPosition = currentPosition;
    }

    private void diener() {
        Diener d = (Diener) k;
        this.dienerAG = new JLabel(d.getAngriff() + "");
        this.dienerLB = new JLabel(d.getLeben() + "");
        dienerAG.setBounds(0, 180, 25, 20);
        dienerLB.setBounds(80, 185, 20, 15);
        this.add(this.dienerAG);
        this.add(this.dienerLB);
        if (d.hasEffect(Diener.DienerEffekt.SPOTT))
            imageButton.setBorder(new LineBorder(Color.darkGray, 3));
    }

    protected Karte getKarte() {
        return this.k;
    }

    public JButton getButton() {
        return this.imageButton;
    }

    public class PanelListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            if (SwingUtilities.isRightMouseButton(e)) {
                new KartenInformationsFenster(getKarte());
            }
        }
    }
}

