package de.loegler.core.gui;

import java.awt.*;

/**
 * Komponente, welche dieses Interface implementieren besitzen einen Dark- sowie Lightmode.
 */
public interface Darkmodeable {

    /**
     * Das aktuelle Element soll seine Anzeige zu Farben des Lightmodes ändern.
     */
    void toLightmode();

    /**
     * Das aktuelle Element soll seine Anzeige zu Farben des Darkmodes ändern.
     */
    void toDarkmode();

    /**
     * Farben, welche besonders häufig für ein Lighttheme verwendet werden
     *
     */
    enum LightmodeColor {
        BACKGROUND(216, 226, 230), BUTTON(39, 152, 227), BUTTON_SECOND(85, 217, 201);

        private final Color color;

        LightmodeColor(int r, int g, int b) {
            color = new Color(r, g, b);
        }

        public Color getColor() {
            return color;
        }

    }

    /**
     * Farben, welche besonders häufig für ein Darktheme verwendet werden
     *
     */
    enum DarkmodeColor {
        BACKGROUND(50, 50, 50), MENU_BAR(40, 40, 40), BUTTON(163, 204, 217), BUTTON_SECOND(103, 58, 183);


        private final Color color;

        DarkmodeColor(int r, int g, int b) {
            color = new Color(r, g, b);
        }

        public Color getColor() {
            return color;
        }
    }

    /**
     * Bereitstellung bestimmter Farben, welche besonders oft verwendet werden, um
     * ein Dark/Lighttheme aufzubauen
     *
     * @version 0.0.2
     * @see DarkmodeColor
     * @see LightmodeColor
     */
    enum ColorAdvices {

        DARKMODE_BACKGROUND(18, 18, 18),
        /**
         * Primäre Farbe - 1. Variante
         */
        DARMODE_PRIMARY_ONE(187, 134, 252),
        /**
         * Primäre Farbe - 2. Variante
         */
        DARKMODE_PRIMARY_TWO(54, 0, 179),

        DARKMODE_SECONDARY_ONE(187, 134, 252), DARKMODE_SECONDARY_TWO(3, 218, 196), DARKMODE_ACCEND_ONE(103, 58, 183),
        DARKMODE_ACCENT_TWO(179, 157, 219), DARKMODE_ERROR(207, 102, 121), LIGHTMODE_BACKGROUND(255, 255, 255),
        LIGHTMODE_PRIMARY_ONE(98, 0, 238), LIGHTMODE_PRIMARY_TWO(55, 0, 179), LIGHTMODE_SECONDARY_ONE(3, 218, 196),
        LIGHTMODE_SECONDARY_TWO(1, 135, 134), LIGHTMODE_ERROR(207, 102, 121);

        private final Color color;

        ColorAdvices(int r, int g, int b) {
            color = new Color(r, g, b);
        }

        public Color getColor() {
            return color;
        }

    }

}
