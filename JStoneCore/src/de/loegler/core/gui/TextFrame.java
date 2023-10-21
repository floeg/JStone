package de.loegler.core.gui;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Ein <code>TextFrame</code> ermoeglicht das Anzeigen einer Nachricht an den User.
 */
public class TextFrame implements Darkmodeable {
    public static int RETURN_ACCEPT = 1, RETURN_CANCEL = 0;
    private final JDialog frame;
    private JTextArea textArea;
    private final TextFrameOptions options;


    private int userInput = Integer.MIN_VALUE;
    private JButton cancelButton, acceptButton;
    private final JPanel top;
    private final JPanel center;
    private final JPanel buttonPanel;
    private final JPanel mainPanel;
    private JLabel titleLabel;
    private ArrayList<TextFrameListener> listenerList = new ArrayList<>();

    public void addTextFrameListener(TextFrameListener listener) {
        this.listenerList.add(listener);
    }

    private void fireEvent() {
        for (TextFrameListener listener : this.listenerList) {
            listener.execute(this.userInput);
        }
    }


    /**
     * Erstellt ein neues TextFrame im Darkmode-Design
     *
     * @param options
     * @param title
     * @param lines
     */
    public TextFrame(TextFrameOptions options, String title, String... lines) {
        frame = new JDialog();
        this.options = options;
        frame.setTitle(title);
        frame.setSize(500, 250);
        frame.setUndecorated(true);
        frame.setLocationRelativeTo(null);
        top = new JPanel();
        center = new JPanel();
        buttonPanel = new JPanel();
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());


        if (title != null) {
            titleLabel = new JLabel(title);
            top.add(titleLabel);
        }
        if (lines != null) {
            StringBuilder output = new StringBuilder();
            for (String line : lines) {
                output.append(line).append("\n");
            }
            textArea = new JTextArea(output.toString());
            textArea.setEditable(false);
            textArea.setFont(textArea.getFont().deriveFont(13.0F));
            center.add(textArea);

        }

        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 100, 10, 100));

        if (options.cancel) {
            cancelButton = new JButton("Abbrechen");
            cancelButton.setMargin(new Insets(10, 25, 10, 25));
            buttonPanel.add(cancelButton, BorderLayout.WEST);
            cancelButton.addActionListener(e -> {
                this.userInput = TextFrame.RETURN_CANCEL;
                frame.dispose();
                this.fireEvent();
            });


        }
        if (options.accept) {
            acceptButton = new JButton("Akzeptieren");
            acceptButton.setMargin(new Insets(10, 25, 10, 25));
            acceptButton.addActionListener(e -> {
                this.userInput = TextFrame.RETURN_ACCEPT;
                frame.dispose();
                this.fireEvent();
            });

            buttonPanel.add(acceptButton, BorderLayout.EAST);

        }


        mainPanel.add(top, BorderLayout.PAGE_START);
        mainPanel.add(center, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);

        frame.setContentPane(mainPanel);
        this.toDarkmode();


        frame.setVisible(true);
        frame.repaint();
        frame.requestFocus();
        frame.setAlwaysOnTop(true);

    }

    /**
     * Erstellt ein TextFrame mit den TextFrameOptionen cancel und accept
     *
     * @param title
     * @param lines
     */
    public TextFrame(String title, String... lines) {
        this(new TextFrameOptions().enableAccept().enableCancel(), title, lines);

    }


    /**
     * Liefert die Rückgabe des Users. Hat der User noch keinen Wert ausgewählt, so wird der aktuelle Thread bis zur Usereingabe blockiert
     *
     * @return
     */
    public int waitForUserInput() {
        frame.setModalExclusionType(Dialog.ModalExclusionType.NO_EXCLUDE);

        while (userInput == Integer.MIN_VALUE) {
            Thread.yield();//Sonst Optimierung durch den Compiler
        }
        return userInput;
    }


    /**
     * Das aktuelle Element soll seine Anzeige zu Farben des Lightmodes ändern.
     */
    @Override
    public void toLightmode() {
        if (cancelButton != null)
            cancelButton.setBackground(LightmodeColor.BUTTON.getColor());
        if (acceptButton != null)
            acceptButton.setBackground(LightmodeColor.BUTTON.getColor());

        top.setBackground(ColorAdvices.LIGHTMODE_SECONDARY_TWO.getColor());
        center.setBackground(ColorAdvices.LIGHTMODE_BACKGROUND.getColor());
        buttonPanel.setBackground(ColorAdvices.LIGHTMODE_BACKGROUND.getColor());
        mainPanel.setBackground(ColorAdvices.LIGHTMODE_BACKGROUND.getColor());
        if (textArea != null) {
            textArea.setBackground(ColorAdvices.LIGHTMODE_BACKGROUND.getColor());
            textArea.setForeground(Color.BLACK);
            textArea.setForeground(ColorAdvices.LIGHTMODE_PRIMARY_ONE.getColor());
        }
        if (titleLabel != null)
            titleLabel.setForeground(Color.BLACK);
        if (options.mode == TextFrameOptions.ERROR_MODE || options.mode == TextFrameOptions.WARNING_MODE) {
            if (textArea != null) {
                textArea.setBackground(ColorAdvices.LIGHTMODE_ERROR.getColor());
                textArea.setForeground(Color.BLACK);
            }
            mainPanel.setBackground(ColorAdvices.LIGHTMODE_ERROR.getColor());
            center.setBackground(ColorAdvices.LIGHTMODE_ERROR.getColor());
            buttonPanel.setBackground(ColorAdvices.LIGHTMODE_ERROR.getColor());
            top.setBackground(Color.RED);
        }
    }

    /**
     * Das aktuelle Element soll seine Anzeige zu Farben des Darkmodes ändern.
     */
    @Override
    public void toDarkmode() {
        if (cancelButton != null)
            cancelButton.setBackground(DarkmodeColor.BUTTON.getColor());
        if (acceptButton != null)
            acceptButton.setBackground(DarkmodeColor.BUTTON.getColor());
        if (titleLabel != null)
            titleLabel.setForeground(Color.WHITE);
        top.setBackground(ColorAdvices.DARKMODE_ACCENT_TWO.getColor());
        center.setBackground(DarkmodeColor.BACKGROUND.getColor());
        buttonPanel.setBackground(DarkmodeColor.BACKGROUND.getColor());
        mainPanel.setBackground(DarkmodeColor.BACKGROUND.getColor());
        if (textArea != null) {
            textArea.setBackground(DarkmodeColor.BACKGROUND.getColor());
            textArea.setForeground(DarkmodeColor.BUTTON_SECOND.getColor());
            textArea.setForeground(ColorAdvices.DARKMODE_SECONDARY_ONE.getColor());

        }
        if (options.mode == TextFrameOptions.ERROR_MODE || options.mode == TextFrameOptions.WARNING_MODE) {
            if (textArea != null) {
                textArea.setBackground(ColorAdvices.DARKMODE_ERROR.getColor());
                textArea.setForeground(Color.BLACK);
            }
            mainPanel.setBackground(ColorAdvices.DARKMODE_ERROR.getColor());
            center.setBackground(ColorAdvices.DARKMODE_ERROR.getColor());
            buttonPanel.setBackground(ColorAdvices.DARKMODE_ERROR.getColor());
            top.setBackground(Color.red);
        }


    }

    public void close() {
        frame.dispose();
    }


    public interface TextFrameListener {
        /**
         * Methode wird aufgerufen, nachdem der Benutzer einen der Buttons angeklickt hat
         *
         * @param returnValue
         */
        void execute(int returnValue);

    }

    /**
     * Ermoeglicht das setzen einzelner Optionen, welche von einem {@link TextFrame} ausgewertet werden.
     * Die Klasse folgt dem Builder-Entwurfsmuster, sodass ein einfaches setzen der gewuenschten Parameter ueber die Methoden moeglich ist.
     */
    public static class TextFrameOptions {
        private boolean accept, cancel;
        private int mode = MESSAGE_MODE;

        public static final int MESSAGE_MODE = 1, ERROR_MODE = -1, WARNING_MODE = 0;

        public TextFrameOptions changeMode(int mode) {
            this.mode = mode;
            return this;
        }

        public TextFrameOptions enableAccept() {
            this.accept = true;
            return this;
        }

        /**
         * Aktiviert die Option "cancel"
         *
         * @return Die aktuelle Instanz
         */
        public TextFrameOptions enableCancel() {
            this.cancel = true;
            return this;
        }
    }
}