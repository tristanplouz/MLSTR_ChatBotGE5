package fr.insa.drusselcharras.chatbot;

import java.time.LocalDateTime;

public class Crenau {
    public LocalDateTime start;
    public LocalDateTime end;
    public Machine machine;
    public String user;
    public String info;

    /**
     * Constructeur de la class Crenau
     *
     * @param start Date de début
     * @param end   Date de fin
     * @param user  Nom de l'utilisateur pour le créneau
     */
    public Crenau(LocalDateTime start, LocalDateTime end, String user) {
        if (start.isBefore(end)) {
            this.start = start;
            this.end = end;
        } else {
            this.end = start;
            this.start = end;
        }
        this.user = user;
    }

    /**
     * Méthode ToString de la class
     */
    public String toString() {
        String str = "";
        str += "machine ";
        str += this.machine;
        str += " utilisée de ";
        str += this.start;
        str += " à ";
        str += this.end;
        str += " par ";
        str += user;
        if (info != null) {
            str += " for " + info;
        }
        return str;
    }
}
