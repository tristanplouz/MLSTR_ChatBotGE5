package fr.insa.drusselcharras.chatbot;

import java.util.ArrayList;

public class Machine {
	public String name;
	private ArrayList<Crenaux> usedTime = new ArrayList<Crenaux>();

	/**
	 * Construteur de la class machine
	 * 
	 * @param name Nom de la machine
	 */
	public Machine(String name) {
		this.name = name;
	}

	/**
	 * Méthode pour verifier si le crénaux demandé est libre
	 * 
	 * @param potCre Crénaux potentiel demandé
	 * @return True: si le crénaux est utilisé, False si il est libre
	 */
	public boolean checkCrenaux(Crenaux potCre) {
		System.out.println(potCre);
		for (Crenaux usedCrenaux : usedTime) {
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
	 * Méthode pour reserver un crénaux
	 * 
	 * @param c Le Crénaux à reserver
	 */
	public void addCrenaux(Crenaux c) {
		usedTime.add(c);
		c.machine = this;
	}

	/**
	 * Méthode pour afficher les crénaux utilisés sur la machine
	 * 
	 * @return String affichant les crénaux
	 */
	public String showCrenaux() {
		String str = "Créneaux de la machine:\n";

		for (Crenaux crenaux : usedTime) {
			str += crenaux.toString();
			str += "\n";
		}
		System.out.println(str);
		return str;
	}

	/**
	 * Méthode ToString de la class
	 */
	public String toString() {
		return this.name;

	}
}
