package clientserverchat;

import java.io.*;
import java.net.*;

public class Client extends Thread {

    private String clientName = null;
    
    private DataInputStream inputStream = null;
    private PrintStream outputStream = null;
   
    private Socket clientSocket = null;
    
    private final Client[] threads;
    private int clientsCount;

    public Client(Socket clientSocket, Client[] threads) {
    	
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
               
                name = AES.decrypt(inputStream.readLine().trim(), "a");//null
                if (name.indexOf('@') == -1) {
                    break;
                } else {
                    outputStream.println("Tle nom ne doit pas contenir '@' .");
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
                
                //verification si c est quitter
                if (line.startsWith("/quitter")) {
                    break;
                }
                //check if message is private
                if (line.startsWith("@")) {
                    //if line starts with @ split the line on words[0] = name and words[1] = message
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
                                        this.outputStream.println(">" + name + "< " + words[1]);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                //the message is visible to all clients
                else {
                    synchronized (this) {
                        for (int i = 0; i < clientsCount; i++) {
                            if (threads[i] != null && threads[i].clientName != null) {
                                threads[i].outputStream.println("<" + name + "> " + AES.decrypt(line, "a"));
                            }
                        }
                    }
                }
            }
            
            //notify other clients that client is leaving chat room
            synchronized (this) {
                for (int i = 0; i < clientsCount; i++) {
                    if (threads[i] != null && threads[i] != this && threads[i].clientName != null) {
                        threads[i].outputStream.println("*** l utilisateur " + name + " a quitter la salle!!! ***");
                    }
                }
            }
            outputStream.println("*** au revoir " + name + " ***");

            //set the value of current thread to null and make free place for other thread to start
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