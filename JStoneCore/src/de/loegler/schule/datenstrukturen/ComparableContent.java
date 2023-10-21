package de.loegler.schule.datenstrukturen;

public interface ComparableContent<ContentType> {

    /**
     * @param cc
     * @return
     */
    boolean isEqual(ContentType cc);

    boolean isLess(ContentType cc);

    boolean isGreater(ContentType cc);


}
