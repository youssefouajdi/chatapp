package clientserverchat;

import java.io.*;
import java.net.*;

public class Client extends Thread {

    private String clientName = null;
    private DataInputStream inputStream = null;
    private PrintStream outputStream = null;
    private String secret=null;
    private Socket clientSocket = null;
    private final Client[] threads;
    private int clientsCount;

    public Client(Socket clientSocket, Client[] threads, String key) {
    	this.secret=key;
        this.clientSocket = clientSocket;
        this.threads = threads;
        clientsCount = threads.length;
    }

    public void run() {
        int clientsCount = this.clientsCount;
        Client[] threads = this.threads;

        try {
            //creation input et output streams pour client
            inputStream = new DataInputStream(clientSocket.getInputStream());
            outputStream = new PrintStream(clientSocket.getOutputStream());
            String name;
            
            while (true) {
                outputStream.println("Entrez votre nom d utilisateur.");
               
                name = AES.decrypt(inputStream.readLine().trim(), secret);
                if (name.indexOf('@') == -1) {
                    break;
                } else {
                    outputStream.println("le nom ne doit pas contenir '@' .");
                }
            }

            //dire bonjour
            outputStream.println("Bienvenue " + name + " a la salle de discussion.\n pourquitter taper /quitter .");
            synchronized (this) {
                for (int i = 0; i < clientsCount; i++) {
                    if (threads[i] != null && threads[i] == this) {
                        clientName = "@" + name;
                        break;
                    }
                }
                for (int i = 0; i < clientsCount; i++) {
                    if (threads[i] != null && threads[i] != this) {
                        threads[i].outputStream.println("*** un utilisateur  " + name + " est dans la salle de discussion !!! ***");
                    }
                }
            }
            //commencer la discussion
            while (true) {
                String line = inputStream.readLine();
                System.out.println(line);
                line=AES.decrypt(line, secret);
                //verification si c est quitter
                if (line.startsWith("/quitter")) {
                    break;
                }
                //verification message privee
                if (line.startsWith("@")) {
                    //if line starts with @ split the line on words[0] = nom and words[1] = message
                    String[] words = line.split("\\s", 2);
                    if (words.length > 1 && words[1] != null) {
                        words[1] = words[1].trim();
                        if (!words[1].isEmpty()) {
                            //make it synchronized because not more than one thread should not access it at the same time
                            synchronized (this) {
                                for (int i = 0; i < clientsCount; i++) {
                                    if (threads[i] != null && threads[i] != this && threads[i].clientName != null && threads[i].clientName.equals(words[0])) {
                                        threads[i].outputStream.println("<" + name + "> " + words[1]);
                                        //print this message to let the client know the private message was sent
                                        this.outputStream.println(">" + name + "< " + words[1] );
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                //message pour tous les clients
                else {
                    synchronized (this) {
                        for (int i = 0; i < clientsCount; i++) {
                            if (threads[i] != null && threads[i].clientName != null) {
                                threads[i].outputStream.println("<" + name + "> " + line);
                            }
                        }
                    }
                }
            }
            
            //notification des autres utilisateurs que un utilisateur a quitter
            synchronized (this) {
                for (int i = 0; i < clientsCount; i++) {
                    if (threads[i] != null && threads[i] != this && threads[i].clientName != null) {
                        threads[i].outputStream.println("*** l utilisateur " + name + " a quitter la salle!!! ***");
                    }
                }
            }
            outputStream.println("*** au revoir " + name + " ***");

            //liberer de l espace quand un utilisateur se deconnecte
            synchronized (this) {
                for (int i = 0; i < clientsCount; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
            }
            
            //arret de la connection
            inputStream.close();
            outputStream.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }
}