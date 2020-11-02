package fr.insa.drusselcharras.chatbot;

import java.util.ArrayList;

public class MachineHandler {
	public ArrayList<Machine> machines = new ArrayList<Machine>();

	/**
	 * Méthode pour créer et ajouter au gestionnaire une nouvelle machine
	 * 
	 * @param name Nom de la machine
	 */
	public void addMachine(String name) {
		Machine newOne = new Machine(name);
		this.machines.add(newOne);
	}

	/**
	 * Méthode pour trouver une machine par son nom
	 * 
	 * @param name Nom de la machine recherchée
	 * @return La machine ou null si non trouvée
	 */
	public Machine getMachineByName(String name) {
		for (Machine machine : machines) {
			System.out.println(name + " " + machine.name);
			if (machine.name == name) {
				return machine;
			}
		}
		return null;
	}

	/**
	 * Méthode pour montrer toutes les machines
	 * 
	 * @return String de toutes les machines enregistrées
	 */
	public String showAllMachines() {
		String ret = "";
		for (Machine machine : machines) {
			ret += machine.name + "\n";
		}

		return ret.substring(0, ret.length() - 1);
	}

	/**
	 * Méthode pour réserver un créneau
	 * 
	 * @param MachineName Nom de la machine sur la quelle on veut reserver
	 * @param creneau     Créneau qu'on veut réserver
	 * @return 0: Machine reservée 1: Machine non trouvée 2: Créneau non libre
	 */
	public int bookCrenaux(String MachineName, Crenaux creneau) {
		System.out.println("Booking...");
		Machine mach = this.getMachineByName(MachineName);
		if (mach == null) {
			return 1;
		} else if (mach.checkCrenaux(creneau)) {
			return 2;
		} else {
			mach.addCrenaux(creneau);
			return 0;
		}

	}
}
