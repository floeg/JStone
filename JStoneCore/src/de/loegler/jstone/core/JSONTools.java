package de.loegler.jstone.core;

import de.loegler.jstone.core.event.DrawCardEvent;
import de.loegler.schule.datenstrukturenExtensions.ListX;
import de.loegler.schule.datenstrukturenExtensions.NTree;

/**
 * Die Klasse JSONTools ermöglicht das Umwandeln eines {@link JSONObject}s zu JSON sowie das Umwandeln
 * eines JSON-Strings zu einem {@link JSONObject}.
 */
public class JSONTools {
    private String json;
    private String scannedJSON;

    public JSONTools(String json) {
        this.json = json;
    }


    /**
     * Wandelt einen String in ein {@link JSONObject} um.
     *
     * @param json JSONObject als String dargestellt
     * @return JSONObject welches durch json dargestellt wurde, oder null, falls json null ist.
     * @throws JSONMalformedException Sofern unbekannte Token vorkommen oder sich die Token in einer falschen Reihenfolge befinden.
     */
    public static JSONObject fromJSON(String json) throws JSONMalformedException {
        if (json == null)
            return null;
        JSONTools tmp = new JSONTools(json);
        return tmp.wandleInJSONObjectUm(tmp.parse(tmp.scanJSON()));
    }

    /**
     * Stellt ein JSONObject als verschachtelten JSON-String dar.
     *
     * @param jsonObject Das {@link JSONObject}, welches als String dargestellt werden soll
     * @return Das JSONObject als String
     */
    public static String getJSON(JSONObject jsonObject) {
        StringBuilder builder = new StringBuilder("{");
        jsonObject.data.forEach((key, value) -> {
            if (value instanceof String) {
                builder.append("\"").append(key).append("\":\"").append(value).append("\", ");
            } else {
                if (value instanceof JSONObject) {
                    JSONObject jsonObject1 = (JSONObject) value;
                    String verschachteltJSON = getJSON(jsonObject1);
                    builder.append("\"").append(key).append("\":").append(verschachteltJSON).append(", ");
                }
            }
        });
        return builder.substring(0, builder.lastIndexOf(",")) + "}";
    }

 /*
        Scanner: Text wird in seine Tokens zerlegt
        Parser: Passen diese Tokens sinnvoll zusammen/Umwandlung in Baum/Graph - anschließend Umwandlung in JSONObject
        Tokens: {,},",Bez,Val,','
        --> "Key":"VAL" wird zu Bez  Val  --> "": wird als Bezeichner gekennzeichnet - "" ohne : als Val

Grammatik:
        S -> {A}
        A -> BezB
        B -> Val,B | Val | S

        {A} -> { Bez B } -> { Bez Val }

         */

    ListX<Token> scanJSON() throws JSONMalformedException {

        /*
        Tokens { } werden einfach hinzugefügt
        Eingabe " Es wird nach dem nächsten " gesucht. Dazwischen liegt der Wert. Ist hinter dem 2. " ein :,
        so ist es ein Bezeichner, andernfalls ein Value
        JSON wird Zeichen für Zeichen durchgegangen (bez. siehe oben teilweise mehrere Zeichen). Diese Zeichen werden dann aus dem JSON entfernt.
         */

        String jsonCopy = json;
        ListX<Token> tokenList = new ListX<>();
        while (jsonCopy.length() > 0) {
            char firstChar = jsonCopy.charAt(0);
            jsonCopy = jsonCopy.substring(1); //firstChar wurde bereits entfernt!
            switch (firstChar) {
                case '{':
                    tokenList.append(new Token("{", "{"));
                    break;
                case '}':
                    tokenList.append(new Token("}", "}"));
                    break;
                case ' ':
                    break; //Ignorieren von Leerzeichen
                case ',':
                    tokenList.append(new Token("Separator", ","));
                    break;
                case '"': {
                    int schliessendeIndex = jsonCopy.indexOf('\"');
                    if (schliessendeIndex == -1)
                        throw new JSONMalformedException("Fehlende schließende Anführungszeichen!");
                    else {

                        String innerhalb = jsonCopy.substring(0, schliessendeIndex);
                        jsonCopy = jsonCopy.substring(schliessendeIndex + 1);
                        while (jsonCopy.length() != 0 && jsonCopy.charAt(0) == ' ') {
                            jsonCopy = jsonCopy.substring(1); //Erlaubt key: , key :, key   :,...
                        }


                        if (jsonCopy.charAt(0) == ':') {
                            tokenList.append(new Token("Bez", innerhalb));
                            jsonCopy = jsonCopy.substring(1);
                        } else {
                            tokenList.append(new Token("Val", innerhalb));
                        }
                    }
                }
                break;
                default:
                    throw new JSONMalformedException("Unerwartetes Zeichen an Stelle" + (json.length() - jsonCopy.length()) + "(" + firstChar + ")");
                    //Gesamt-Noch übrige länge = Index 0 der copy des Originals
            }
        }
        //Debug
        // tokenList.forEach(System.out::println);
        return tokenList;
    }


    /**
     * Wandelt die Tokenliste in eine Baumstruktur um, welche anschließend in {@link JSONObject}e umgewandelt werden kann.
     * Wirft einen Fehler, wenn der Baum nicht erstellt werden kann, da Tokens nicht in der richtigen Reihenfolge/Beziehung stehen.
     *
     * @param tokenList
     */
    public NTree<Token> parse(ListX<Token> tokenList) throws JSONMalformedException {
        tokenList.toFirst();
        NTree<Token> root;
        if (!tokenList.getContent().tokenType.equalsIgnoreCase("{"))
            throw new JSONMalformedException("JSON muss mit einem Object anfangen!");

        int klammernAuf = 1;

        root = new NTree<>(tokenList.getContent());
        NTree<Token> current = root;
        for (tokenList.next(); tokenList.hasAccess(); tokenList.next()) {
            if (klammernAuf == 0) {
                throw new JSONMalformedException("Kein Ende nach letzter schließenden Klammer!");
            }

            if (tokenList.getContent().tokenType.equalsIgnoreCase("Bez")) {
                if (current.getContent().tokenType.equalsIgnoreCase("{"))
                    current = current.addChild(tokenList.getContent());
                else
                    throw new JSONMalformedException("Key-Deklaration an einer unerwarteten Stelle!");
            }
            if (tokenList.getContent().tokenType.equalsIgnoreCase("Val")) {
                if (current.getContent().tokenType.equalsIgnoreCase("Bez")) {
                    current = current.addChild(tokenList.getContent());
                } else {
                    throw new JSONMalformedException("Value zuweisung ohne Key!");
                }
            }
            if (tokenList.getContent().tokenType.equals("{")) {
                if (current.getContent().tokenType.equalsIgnoreCase("Bez")) {
                    klammernAuf++;
                    current = current.addChild(tokenList.getContent());

                } else {
                    throw new JSONMalformedException("Inneres Objekt ohne Key!");
                }

            } else if (tokenList.getContent().tokenType.equals("}")) {
                klammernAuf--;
                if (klammernAuf < 0)
                    throw new JSONMalformedException("Unerwartete schließende Klammer!");
                //} am Ende; Zeiger zeigt auf Object welches geschlossen werden soll -> Nach oben: Zu Key außerhalb, noch einmal: zu Object außerhalb
                current = current.getParent().getParent();

            } else if (tokenList.getContent().tokenType.equalsIgnoreCase("Separator")) {
                if (current.getContent().tokenType.equalsIgnoreCase("Val") || current.getContent().tokenType.equalsIgnoreCase("{")) {
                    current = current.getParent().getParent(); //Zu Key, Zu Object
                } else {
                    throw new JSONMalformedException("Separator ohne Key-Value Bezug. Parent: " + current.getContent().toString());
                }
            }
        }
        return root;
    }

    public void gebeBaumAus(NTree<Token> tree) {
        System.out.println(tree.getContent().toString());
        tree.getChildren().forEach(this::gebeBaumAus);
    }

    public JSONObject wandleInJSONObjectUm(NTree<Token> tree) {
        JSONObject jsonObject = new JSONObject();
        tree.getChildren().forEach(children -> {
            String key = children.getContent().value;
            //key-Node kann nur ein Kind haben - Value
            NTree<Token> valueNode = children.getChildren().get(0);
            Object value = null;
            if (valueNode.getContent().tokenType.equalsIgnoreCase("{")) {
                value = wandleInJSONObjectUm(valueNode);
            } else
                value = valueNode.getContent().value;
            jsonObject.data.put(key, value);

        });
        return jsonObject;
    }


    public class JSONMalformedException extends Exception {
        public JSONMalformedException(String msg) {
            super("Fehler beim Scanner/Parser: " + msg);
        }
    }

    public class Token {
        private String tokenType;
        private String value;

        public Token(String tokenType, String value) {
            this.tokenType = tokenType;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Token: " +
                    "tokenType='" + tokenType + '\'' +
                    ", value='" + value + '\''
                    ;
        }
    }


}
