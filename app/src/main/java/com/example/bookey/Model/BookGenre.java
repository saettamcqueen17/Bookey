package com.example.bookey.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enum per la gerarchia dei generi letterari
 */
public enum BookGenre {
    // Generi principali
    ROMANZO("Romanzo", null),
    GIALLO("Giallo", null),
    NARRATIVA("Narrativa", null),
    SAGGISTICA("Saggistica", null),
    POESIA("Poesia", null),
    TEATRO("Teatro", null),
    FANTASY("Fantasy", null),
    FANTASCIENZA("Fantascienza", null),

    // Sottogeneri di ROMANZO
    ROMANZO_STORICO("Romanzo Storico", ROMANZO),
    ROMANZO_FANTASY("Romanzo Fantasy", ROMANZO),
    ROMANZO_POLITICO("Romanzo Politico", ROMANZO),
    ROMANZO_PSICOLOGICO("Romanzo Psicologico", ROMANZO),
    ROMANZO_AVVENTURA("Romanzo d'Avventura", ROMANZO),
    ROMANZO_FORMAZIONE("Romanzo di Formazione", ROMANZO),

    // Sottogeneri di GIALLO
    GIALLO_NOIR("Giallo Noir", GIALLO),
    GIALLO_POLIZIESCO("Giallo Poliziesco", GIALLO),
    GIALLO_THRILLER("Thriller", GIALLO),

    // Sottogeneri di SAGGISTICA
    SAGGISTICA_STORICA("Saggistica Storica", SAGGISTICA),
    SAGGISTICA_SCIENTIFICA("Saggistica Scientifica", SAGGISTICA),
    SAGGISTICA_FILOSOFICA("Saggistica Filosofica", SAGGISTICA),
    SAGGISTICA_POLITICA("Saggistica Politica", SAGGISTICA),

    // Sottogeneri di FANTASY
    FANTASY_EPICO("Fantasy Epico", FANTASY),
    FANTASY_URBANO("Fantasy Urbano", FANTASY),

    // Sottogeneri di FANTASCIENZA
    FANTASCIENZA_HARD("Fantascienza Hard", FANTASCIENZA),
    FANTASCIENZA_DISTOPICA("Fantascienza Distopica", FANTASCIENZA);

    private final String displayName;
    private final BookGenre parent;

    BookGenre(String displayName, BookGenre parent) {
        this.displayName = displayName;
        this.parent = parent;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BookGenre getParent() {
        return parent;
    }

    public boolean isParent() {
        return parent == null;
    }

    /**
     * Ottiene tutti i sottogeneri (figli diretti) di questo genere
     */
    public List<BookGenre> getChildren() {
        List<BookGenre> children = new ArrayList<>();
        for (BookGenre genre : BookGenre.values()) {
            if (genre.parent == this) {
                children.add(genre);
            }
        }
        return children;
    }

    /**
     * Ottiene tutti i sottogeneri (figli, nipoti, ecc.) di questo genere ricorsivamente
     */
    public List<BookGenre> getAllDescendants() {
        List<BookGenre> descendants = new ArrayList<>();
        for (BookGenre child : getChildren()) {
            descendants.add(child);
            descendants.addAll(child.getAllDescendants());
        }
        return descendants;
    }

    /**
     * Ottiene tutti i generi principali (senza parent)
     */
    public static List<BookGenre> getMainGenres() {
        List<BookGenre> mainGenres = new ArrayList<>();
        for (BookGenre genre : BookGenre.values()) {
            if (genre.isParent()) {
                mainGenres.add(genre);
            }
        }
        return mainGenres;
    }

    /**
     * Trova un genere dal nome (per compatibilità con i dati esistenti)
     */
    public static BookGenre fromString(String genreName) {
        if (genreName == null) return null;

        String normalized = genreName.toUpperCase().replace(" ", "_");
        try {
            return BookGenre.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            // Cerca per display name
            for (BookGenre genre : BookGenre.values()) {
                if (genre.displayName.equalsIgnoreCase(genreName)) {
                    return genre;
                }
            }
            return null;
        }
    }

    /**
     * Ottiene tutti i nomi dei generi che dovrebbero matchare quando si filtra per questo genere
     * (include se stesso e tutti i discendenti)
     */
    public List<String> getAllMatchingGenreNames() {
        List<String> names = new ArrayList<>();
        names.add(this.name());
        names.add(this.displayName);

        for (BookGenre descendant : getAllDescendants()) {
            names.add(descendant.name());
            names.add(descendant.displayName);
        }

        return names;
    }
}

