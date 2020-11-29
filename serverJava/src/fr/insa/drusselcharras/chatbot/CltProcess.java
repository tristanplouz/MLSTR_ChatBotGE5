package fr.insa.drusselcharras.chatbot;

import java.io.*;
import java.net.*;
import java.util.List;

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
     * Méthode pour traiter les données reçues du client
     *
     * @param data les données à traiter
     */
    private void process(String data) {
        List<Message> reps = this.server.mlHandler.processData(data);
        String anser = "";
        String prevCat = "";
        for (Message rep : reps) {
            switch (rep.category) {
                case "G":
                    if (prevCat.equals("G")) {
                        ;
                    } else {
                        anser += "Bonjour, comment allez vous? ";
                    }
                    prevCat = "G";
                    break;
                case "R":
                    if (prevCat.equals("R")) {
                        ;
                    } else {
                        anser += "Quel créneaux voulez vous? ";
                    }
                    prevCat = "R";
                    break;
                case "RD":
                    Crenau c = new Crenau(rep.startTime,rep.endTime,"defaut");
                    int state = server.machineHdl.bookCrenaux("Défaut",c);
                    if (state==2){
                        anser+="Le créneaux est déjà utilisé.";
                    }else if( state == 0){
                        anser += "Je vous réserve le créneaux:"+c+ ". ";
                    }
                    break;
                case "P":
                    anser += "Les pâtes c'est très important. ";
                    break;
                case "C":
                    anser += "Voici les créneaux déjà réservés:"+server.machineHdl.showAllCrenaux();
                    break;
                case "FIN":
                    anser += "Merci de m'avoir utilisé. ";
                    break;
                default:
                    anser += "Je n'ai pas compris votre demande. ";
                    break;

            }
        }
        this.send(anser);
    }

}
/*zdt    public static String process(List<Message> reps) {

        if (reps.size() <= 1){
            switch (reps.get(0).category) {
                case "G":
                    return "Bonjour très cher, comment puis-je vous aider?";
                case "R":
                    //getNextCrenau
                    return "Quel créneaux voulez vous?";
                case "RD":
                    //getNextCrenau
                    return "Je vous réserve un créneaux de "+reps.get(0).startTime+" à "+reps.get(0).endTime;
                case "P":
                    //getNextCrenau
                    return "Les pâtes c'est très important";
                case "FIN":
                    return "Merci de m'avoir utilisé";
                default:
                    return "Je n'ai pas compris votre demande";
            }
         }
         else{
            String prevCat = "";
            String anser = "";
            for (Message rep:reps) {
                switch (rep.category) {
                    case "G":
                        if(prevCat.equals("G")){
                            ;
                        }
                        else{
                            anser+="Bonjour, comment allez vous? ";
                        }
                        prevCat="G";
                        break;
                    case "R":

                        if(prevCat.equals("R")){
                            ;
                        }
                        else{
                            anser+="Quel créneaux voulez vous? ";
                        }
                        prevCat="R";
                        break;
                    case "RD":
                        //getNextCrenau
                        anser+= "Je vous réserve un créneaux de "+rep.startTime+" à "+rep.endTime+". ";
                        break;
                    case "P":
                        //getNextCrenau
                        anser+= "Les pâtes c'est très important. ";
                        break;
                    case "C":
                        //getNextCrenau
                        anser+= "Voici les créneaux déjà réservés:";
                        break;
                    case "FIN":
                        anser+= "Merci de m'avoir utilisé. ";
                        break;
                    default:
                        anser+= "Je n'ai pas compris votre demande. ";
                        break;
                }
            }
            return anser;
    }

    }*/