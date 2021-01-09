package fr.insa.drusselcharras.chatbot;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe gérant le serveur Socket
 */
public class Server {
    public MachineHandler machineHdl = new MachineHandler();
    public MLHandler mlHandler = new MLHandler();
    private final int port = 12345;
    private final String IP = "localhost";
    private ServerSocket server = null;
    private final int clientLimit = 5;
    private final List<CltProcess> clients = new ArrayList<>();

    /**
     * Constructeur de la classe Server
     */
    public Server() {
        this.machineHdl.addMachine("default");
        try {
            this.server = new ServerSocket(this.port, this.clientLimit, InetAddress.getByName(this.IP));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode pour démarrer le serveur
     */
    public void start() {
        Server s = this;
        // On le démarre dans un thread particulier
        Thread t = new Thread(() -> {
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
