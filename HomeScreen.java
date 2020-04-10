package clientserverchat;


import java.io.*;
import java.net.*;
import javax.swing.*;

public class HomeScreen implements Constants {

    public static void main(String[] args) throws IOException {
        
       
        boolean serverCreated = available(DEFAULT_PORT);
        final JPanel panel = new JPanel();
        
  
        Object[] options = {"Server", "Client"};
        String initial = "Server";
        String[] arguments;
        
        Object chosen = JOptionPane.showInputDialog(null, "Inscrivez vous: ", "Application de Chat ", JOptionPane.QUESTION_MESSAGE, null, options, initial);
        
        String key = JOptionPane.showInputDialog("Entrez votre cle ");
        if (chosen.equals("Server")) {
            //creation serveur
            arguments = new String[]{key};
            
            new Server().main(arguments);
        } else if (chosen.equals("Client")) {
            
            if (serverCreated) {
                JOptionPane.showMessageDialog(panel, "vous ne disposez pas de serveur", "Erreur", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }
            
            //creation client
            String IPServer = JOptionPane.showInputDialog("Entrez votre adresse IP");
           
            arguments = new String[]{IPServer};
            new Chat();
            Chat.secret=key;
          
            
            
            Chat.main(arguments);
        }

    }


	private static boolean available(int port) {
        Socket socket = null;
        try {
            socket = new Socket("localhost", port);
            return false;
        }
        catch (IOException ex) {
                return true;
        }
        
    }

}