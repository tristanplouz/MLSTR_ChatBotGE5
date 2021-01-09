package fr.insa.drusselcharras.chatbot;

import java.time.LocalDateTime;

/**
 * Classe représentant un message normalisé
 */
public class Message {
    public String category;
    public LocalDateTime startTime = null;
    public LocalDateTime endTime = null;
    public String name = null;

    /**
     * Constructeur pour un message quelconque
     *
     * @param cat Catégorie du message
     */
    public Message(String cat) {
        this.category = cat;
    }

    /**
     * Constructeur pour un message de réservation d'un créneau précis
     *
     * @param cat   Catégorie du message
     * @param start Date de début
     * @param end   Date de fin
     */
    public Message(String cat, LocalDateTime start, LocalDateTime end) {
        this.category = cat;
        if (start != null && end != null) {
            this.startTime = start;
            this.endTime = end;
        } else if (start != null) {
            this.startTime = start;
            this.endTime = start.plusHours(2);
        } else {
            this.category = "R";
        }

    }

    /**
     * //TODO
     * Constructeur pour un message de définition du nom
     *
     * @param cat  Catégorie du message
     * @param name Nom de l'utilisateur
     */
    public Message(String cat, String name) {
        this.category = cat;
        this.name = name;
    }
}
