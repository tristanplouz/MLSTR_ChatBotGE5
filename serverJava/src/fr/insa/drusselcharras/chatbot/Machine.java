package fr.insa.drusselcharras.chatbot;

import java.util.ArrayList;
import java.util.List;

public class Machine {
    public String name;
    private final List<Crenau> usedTime = new ArrayList<>();

    /**
     * Constructeur de la class machine
     *
     * @param name Nom de la machine
     */
    public Machine(String name) {
        this.name = name;
    }

    /**
     * Méthode pour vérifier si le créneau demandé est libre
     *
     * @param potCre Créneau potentiel demandé
     * @return True: si le créneau est utilisé, False si il est libre
     */
    public synchronized boolean checkCrenaux(Crenau potCre) {
        System.out.println(potCre);
        for (Crenau usedCrenaux : usedTime) {
            if (usedCrenaux.start.isBefore(potCre.end) && usedCrenaux.end.isAfter(potCre.end)) {
                System.out.println("pb1 chevAvant");
                return true;
            } else if (usedCrenaux.start.isBefore(potCre.start) && usedCrenaux.end.isAfter(potCre.start)) {
                System.out.println("pb2 chevApres");
                return true;
            } else if (usedCrenaux.start.isBefore(potCre.start) && usedCrenaux.end.isAfter(potCre.start)) {
                System.out.println("pb4 Inter");
                return true;
            } else if ((usedCrenaux.start.compareTo(potCre.start) >= 0)
                    && (usedCrenaux.end.compareTo(potCre.end)) <= 0) {
                System.out.println("pb3 Same or Englobant");
                return true;
            }
        }
        return false;

    }

    /**
     * Méthode pour réserver un créneaux
     *
     * @param c Le Créneaux à réserver
     */
    public synchronized void addCrenaux(Crenau c) {
        usedTime.add(c);
        //TODO trier la liste par date croissante
        c.machine = this;
    }

    /**
     * Méthode pour afficher les créneaux utilisés sur la machine
     *
     * @return String affichant les créneaux
     */
    public String showCrenaux() {
        if(usedTime.size()==0){
            return "Aucun créneau réservé pour le moment";
        }
        String str = "\n";
        for (Crenau crenau : usedTime) {
            str += crenau.toString();
            str += "\n";
        }
        System.out.println(str);
        return str;
    }

    /**
     * Méthode ToString de la classe
     */
    public String toString() {
        return this.name;

    }
}
