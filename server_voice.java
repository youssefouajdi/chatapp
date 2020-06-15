package clientserverchat;



public class server_voice {

	public static boolean calling = false;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		server_fr fr= new server_fr();
		Client_voice vc= new Client_voice();
		vc.main(args);
		fr.setVisible(true);
		
		

	}

}
