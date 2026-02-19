package com.example.bookey.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Enum per le case editrici
 */
public enum BookPublisher {
    ADELPHI("Adelphi"),
    BOMPIANI("Bompiani"),
    EINAUDI("Einaudi"),
    FELTRINELLI("Feltrinelli"),
    MONDADORI("Mondadori"),
    RIZZOLI("Rizzoli"),
    SELLERIO("Sellerio"),
    GIUNTI("Giunti"),
    MARSILIO("Marsilio"),
    MINIMUM_FAX("Minimum Fax"),
    NN_EDITORE("NN Editore"),
    IPERBOREA("Iperborea"),
    LATERZA("Laterza"),
    IL_MULINO("Il Mulino"),
    BOLLATI_BORINGHIERI("Bollati Boringhieri"),
    PONTE_ALLE_GRAZIE("Ponte alle Grazie"),
    NERI_POZZA("Neri Pozza"),
    E_O("E/O"),
    SUR("Sur"),
    ALTRO("Altro");

    private final String displayName;

    BookPublisher(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Ottiene tutte le case editrici
     */
    public static List<BookPublisher> getAllPublishers() {
        List<BookPublisher> publishers = new ArrayList<>();
        for (BookPublisher publisher : BookPublisher.values()) {
            publishers.add(publisher);
        }
        return publishers;
    }

    /**
     * Trova una casa editrice dal nome (per compatibilità con i dati esistenti)
     */
    public static BookPublisher fromString(String publisherName) {
        if (publisherName == null) return null;

        String normalized = publisherName.toUpperCase().replace(" ", "_").replace("/", "_");
        try {
            return BookPublisher.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            // Cerca per display name
            for (BookPublisher publisher : BookPublisher.values()) {
                if (publisher.displayName.equalsIgnoreCase(publisherName)) {
                    return publisher;
                }
            }
            return ALTRO;
        }
    }
}

