package clientserverchat;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Observable;
import java.util.Observer;

public class Chat implements Constants {
    public static String secret;
   
    //classe observable
    private static class ChatAccess extends Observable {

        private Socket socket;
        private OutputStream outputStream;

        @Override
        public void notifyObservers(Object arg) {
            super.setChanged();
            super.notifyObservers(arg);
        }

        //notifier les autres thread du message
        public void init(String server, int port) throws IOException {
            socket = new Socket(server, port);
            outputStream = socket.getOutputStream();

            new Thread() {
                @Override
                public void run() {
                    try {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            notifyObservers(line);
                        }
                    } catch (IOException ex) {
                        notifyObservers(ex);
                    }
                }
            }.start();
            
        }

        private static final String newLine = "\r\n";
        public void send(String text) {
            try {
            	System.out.println("chat "+secret);
                outputStream.write((AES.encrypt(text, secret) + newLine).getBytes());
                outputStream.flush();
            } catch (IOException ex) {
                notifyObservers(ex);
            }
        }

        //close sockets
        public void close() {
            try {
                socket.close();
            } catch (IOException ex) {
                notifyObservers(ex);
            }
        }
    }

    //GUI 
    static class GUI extends JFrame implements Observer {

        private JTextArea textArea;
        private JTextField inputTextField;
        private JButton sendButton;
        private ChatAccess chatAccess;

        public GUI(ChatAccess chatAccess) {
            this.chatAccess = chatAccess;
            chatAccess.addObserver(this);
            build();
        }

        private void build() {
            textArea = new JTextArea(20, 50);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            add(new JScrollPane(textArea), BorderLayout.CENTER);
         
            Box box = Box.createHorizontalBox();
            add(box, BorderLayout.SOUTH);
            inputTextField = new JTextField();
            sendButton = new JButton("Envoie");
            box.add(inputTextField);
            box.add(sendButton);

            // action input et envoie
            ActionListener sendListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String str = inputTextField.getText();
                  
                    if (str != null && str.trim().length() > 0) {
                    	
                    	 
                        chatAccess.send(str);
                    }
                    inputTextField.selectAll();
                    inputTextField.requestFocus();
                    inputTextField.setText("");
                }
            };
            inputTextField.addActionListener(sendListener);
            sendButton.addActionListener(sendListener);

            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    chatAccess.close();
                }
            });
        }

        //mise a jour des info clients
        public void update(Observable o, Object arg) {
            final Object finalArg = arg;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    textArea.append(finalArg.toString());
                    textArea.append("\n");
                }
            });
        }
    }

    public static void main(String[] args) {
        String server = args[0];//127.0.0.1
       
        ChatAccess access = new ChatAccess();

        JFrame frame = new GUI(access);
        frame.setTitle("Chat application SERVER: " + server + ", PORT: " + DEFAULT_PORT);
     // menubar 
        JMenuBar mb; 
        // JMenu 
        JMenu x; 
        // Menu items 
         JMenuItem m1, m2, m3,m4,m5; 
     // create a menubar 
        mb = new JMenuBar(); 
  
        // create a menu 
        x = new JMenu("Menu"); 
  
        // create menuitems 
        m1 = new JMenuItem("APPEL"); 
        m2 = new JMenuItem("TRANSFERT FICHIER "); 
        m3 = new JMenuItem("transfer image"); 
        m4 = new JMenuItem("transfer video/audio"); 
        m5 = new JMenuItem("A propos"); 
  
        //action appel
        m1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new server_voice();
				server_voice.main(args);
			}
		});
        
        //transfert fichier
        m2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

				int returnValue = jfc.showOpenDialog(null);
				// int returnValue = jfc.showSaveDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = jfc.getSelectedFile();
					String a=selectedFile.getAbsolutePath();
				FileClient fc = new FileClient("127.0.0.1", 1988, a);}
			}
		});
        
         //action transfert image
        m3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

				int returnValue = jfc.showOpenDialog(null);
				// int returnValue = jfc.showSaveDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = jfc.getSelectedFile();
					String a=selectedFile.getAbsolutePath();
				FileClient fc = new FileClient("127.0.0.1", 1988, a);}
			}
		});
        
        //transfer video/audio
        m4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

				int returnValue = j.showOpenDialog(null);
				// int returnValue = jfc.showSaveDialog(null);
				System.out.println("jgnk,");
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = j.getSelectedFile();
					String a=selectedFile.getAbsolutePath();
				FileClient fc = new FileClient("127.0.0.1", 1990, a);}
			}
		});
        
        // ajout des item au menu
        x.add(m1); 
        x.add(m2); 
        x.add(m3);
        x.add(m4);
        x.add(m5);
        
        // ajout a la barre de menu
        mb.add(x);
        frame.setJMenuBar(mb); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
     

        try {
            access.init(server, DEFAULT_PORT);
        } catch (IOException ex) {
            System.out.println("Impossible de se connecter " + server + ":" + DEFAULT_PORT);
            ex.printStackTrace();
            System.exit(0);
        }
    }
}