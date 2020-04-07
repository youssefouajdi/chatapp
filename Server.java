package clientserverchat;

import java.io.*;
import java.net.*;


public class Server implements Constants {
    //initialisation de la socket
    private static ServerSocket serverSocket = null;
    private static Socket socket = null;
    static String secret;

    //instanciation du nombre max des clients a chaque inscription de client
    private static final Client[] threads = new Client[MAX_CLIENTS];

    public static void main(String args[]) throws IOException {

        if (args.length < 1) {
            //port 8000
            System.out.println(  "Actuellement vous utilisez le port "  + DEFAULT_PORT);
            serverSocket = new ServerSocket(DEFAULT_PORT);
        } else {
            int entered_port = Integer.parseInt(args[0]);
            serverSocket = new ServerSocket(entered_port);
        }

        ////edcrmefl
        //creation connection avec client
        while (true) {
            try {
                socket = serverSocket.accept();
                int i = 0;
                for (i = 0; i < MAX_CLIENTS; i++) {
                    if (threads[i] == null) {
                    	System.out.println("serveur: "+secret);
                        (threads[i] = new Client(socket, threads)).start();
                        break;
                    }
                }
                if (i == MAX_CLIENTS) {
                    PrintStream os = new PrintStream(socket.getOutputStream());
                    os.println("Le nombre maximum de personne est atteint.");
                    os.close();
                    socket.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}