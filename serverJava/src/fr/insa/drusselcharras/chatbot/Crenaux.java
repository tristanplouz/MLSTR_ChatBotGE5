package fr.insa.drusselcharras.chatbot;

import java.time.LocalDateTime;

public class Crenaux {
	public LocalDateTime start;
	public LocalDateTime end;
	public Machine machine;
	public String user;
	public String info;

	/**
	 * Constructeur de la class Crénaux
	 * 
	 * @param start Date de début
	 * @param end   Date de fin
	 * @param user  Nom de l'utilisateur pour le créneau
	 */
	public Crenaux(LocalDateTime start, LocalDateTime end, String user) {
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
	 * Second constructeur de la class Crénaux
	 * 
	 * @param start Date de début, la date de fin est 30min plus tard
	 * @param user  Nom de l'utilisateur pour le créneau
	 */
	public Crenaux(LocalDateTime start, String user) {
		this.start = start;
		this.end = start.plusMinutes(30);
		this.user = user;
	}

	/**
	 * Setter de l'attribut info
	 * 
	 * @param info Information sur le créneau
	 */
	public void setInfo(String info) {
		this.info = info;
	}

	/**
	 * Méthode ToString de la class
	 */
	public String toString() {
		String str = "";
		str += this.machine;
		str += " used from ";
		str += this.start;
		str += " to ";
		str += this.end;
		str += " by ";
		str += user;
		if (info != null) {
			str += " for " + info;
		}
		return str;
	}
}
