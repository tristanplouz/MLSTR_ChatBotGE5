package fr.insa.drusselcharras.chatbot;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.UnknownHostException;
import java.util.ArrayList;

public class Server {
	private int port = 12345;
	private String IP = "localhost";
	private ServerSocket server = null;
	private int clientLimit = 5;
	private ArrayList<CltProcess> clients = new ArrayList<CltProcess>();
	public MachineHandler machineHdl = new MachineHandler();

	public Server() {
		this.machineHdl.addMachine("CNC");
		try {
			this.server = new ServerSocket(this.port, this.clientLimit, InetAddress.getByName(this.IP));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Méthode pour démarrer le serveur
	 */
	public void start() {
		Server s = this;
		Thread t = new Thread(new Runnable() {// On le démarre dans un thread particulier
			public void run() {
				System.out.print("Server started on: ");
				System.out.println(s.server.getInetAddress());
				while (true) {// Boucle infinie en attendant des clients
					try {
						Socket client = s.server.accept();
						s.clients.add(new CltProcess(client, s));
						new Thread(s.clients.get(s.clients.size() - 1)).start();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		t.setName("Serveur");
		t.start();
	}

	/**
	 * Méthode pour envoyer un message à tous les clients
	 * 
	 * @param data Données à envoyer
	 */
	public void broadcast(String data) {
		for (CltProcess c : clients) {
			c.send(data);
		}

	}
}
