package fr.insa.drusselcharras.chatbot;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;

public class CltProcess implements Runnable {
	private Socket sock;
	private BufferedReader in;
	private PrintWriter out;
	private Server server;
	private String name = "";

	/**
	 * Constructeur du gestionnaire de clients
	 * 
	 * @param client socket du client
	 * @param s      serveur, pour accéder aux autres clients
	 */
	public CltProcess(Socket client, Server s) {
		this.sock = client;
		this.server = s;
		try {
			this.in = new BufferedReader(new InputStreamReader(this.sock.getInputStream())); // Création du flux
																								// d'entrée
			this.out = new PrintWriter(this.sock.getOutputStream()); // Création du flux de sortie
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Méthode run du thread du gestionnaire de client
	 */
	@Override
	public void run() {
		this.name = Thread.currentThread().getName();// On initialise le nom du client avec le nom du thread
		InetSocketAddress remote = (InetSocketAddress) this.sock.getRemoteSocketAddress();// On récupere l'ip du client
		System.out.println("New Client on " + remote + " handled by " + this.name);
		while (true) {
			try {
				String ligne = this.in.readLine();
				if (ligne == null)
					break; // Si la ligne est vide, le client est déconnecté on quite la boucle
				System.out.println(this.name + " says: " + ligne);
				this.process(ligne);// On traite les données reçues
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		System.out.println("Déconnection de " + this.name);
		// Fin du thread
	}

	/**
	 * Méthode pour envoyer un message au client
	 * 
	 * @param data Donnée à envoyer
	 */
	public void send(String data) {
		this.out.println(data);
		this.out.flush();
	}

	/**
	 * Méthode pour traiter les données réçues du client
	 * 
	 * @param data les données à traiter
	 */
	private void process(String data) {
		if (data.startsWith("-name:")) {// Commande pour changer de nom
			this.name = data.substring(6);
			this.send("Bienvenue " + this.name);
			Thread.currentThread().setName(this.name);
		} else if (data.startsWith("-chat:")) {// Commande pour envoyer un chat à tous les clients connectés
			this.server.broadcast(this.name + ": " + data.substring(6));
		} else if (data.startsWith("-addM:")) {// Commande pour ajouter une machine
			this.server.machineHdl.addMachine(data.substring(6));
		} else if (data.startsWith("-addC/")) {// Commande pour ajouter un créneau -addC/MACHINENAME@Y-M-DTH:M:S
			String content = data.split("/")[1];
			System.out.println(content);
			LocalDateTime t = LocalDateTime.parse(content.split("@")[1]);
			Crenaux c = new Crenaux(t, this.name);
			this.server.machineHdl.bookCrenaux(content.split("@")[0], c);
		} else if (data.startsWith("-showM:")) {// Commande pour afficher les machines
			this.send(this.server.machineHdl.showAllMachines());
		} else if (data.startsWith("-showC:")) {// Commande pour afficher les créneaux TODO

			String nameM = data.substring(7);
			System.out.println(nameM);
			Machine m = this.server.machineHdl.getMachineByName(nameM);
			System.out.println(m);
			String c = m.showCrenaux();
			System.out.println(c);
			this.send(nameM);
			this.send(c);
		} else {
			// TODO
		}

	}
}
