package fr.insa.drusselcharras.chatbotclient;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatBotClient {
	public static PrintWriter sender = null;
	public static Scanner clavier = null;
	public static BufferedReader receiver = null;
	public static Socket server = null;

	public static void main(String[] args) {
		clavier = new Scanner(System.in);// gestion de l'entrée clavier
		clavier.useDelimiter("\\n");
		String ip=null,port = null;
		while(port==null) {
			System.out.println("Veuillez saisir l'adresse et le port du serveur sous la forme xx.xx.xx.xx:port:");
			String rep = clavier.next();
			try {
				ip=rep.split(":")[0];
				port = rep.split(":")[1];
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			server = new Socket(ip, Integer.parseInt(port));// Connection au server
			// Création des flux d'entrée/sortie
			receiver = new BufferedReader(new InputStreamReader(server.getInputStream()));
			sender = new PrintWriter(server.getOutputStream(), true);

			// Thread dédié à l'envoi de message
			Thread tx = new Thread(new Runnable() {
				public void run() {
					System.out.println("Entrer un message:");
					while (true) {

						String rep = clavier.next();
						if (rep.equals("quit")) {
							try {
								server.close();
								clavier.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							send(rep);
						}
					}
				}
			});
			tx.start();
			// Thread dédié à la réception de message
			Thread rx = new Thread(new Runnable() {
				public void run() {
					while (true) {
						String str;
						try {
							str = receiver.readLine();
							if (str == null)
								break;
							System.out.println(str);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("Entrer un message:");
					}
					System.out.println("Déconnection du server");
					System.out.println("Fin du processus");
					System.exit(0);
				}
			});
			rx.start();
		} catch (Exception e) {
			e.printStackTrace();
			port=null;
		}
	}

	/**
	 * Méthode pour envoyer un message au serveur
	 * 
	 * @param data Les données à envoyer
	 */
	public static void send(String data) {
		sender.println(data);
		sender.flush();
	}
}
