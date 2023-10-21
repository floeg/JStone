package de.loegler.schule.datenstrukturen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListTest {

    @Test
    public void lTest() {
        List<String> strings = new List<>();
        strings.append("Test");
        strings.append("Test2");
        strings.append("Test3");
        strings.append("Test4");
        strings.toFirst();
        strings.remove();
        //Test wurde entfernt --> Nachfolger ist nun current
        assertEquals("Test2", strings.getContent());
        //Test2 --> weiter zu Test3 --> Test3 wird entfernt --> Nachfolger ist nun current
        strings.next();
        strings.remove();
        assertEquals("Test4", strings.getContent());

    }

    @Test
    public void insertTest() {
        List<String> strings = new List<>();
        strings.append("Test");
        strings.append("Test2");
        strings.toFirst();
        strings.insert("DAZWISCHEN");
        //Das aktuelle Objekt bleibt unverändert
        assertEquals("Test", strings.getContent());
        strings.toFirst();
        //DAZWISCHEN ist nun ganz vorne da vor dem 1. Element
        assertEquals("DAZWISCHEN", strings.getContent());
    }

    @Test
    public void concatTest() {
        List<String> strings = new List<>();
        strings.append("Test");
        strings.append("Test2");
        strings.toFirst();
        List<String> strings2 = new List<>();
        strings2.append("LLTest");
        strings2.append("LLTest2");

        strings2.toFirst();
        strings2.concat(strings);
        assertEquals("LLTest", strings2.getContent());
        strings2.next();
        strings2.next();
        assertEquals("Test", strings2.getContent());


    }

}