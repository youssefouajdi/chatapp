package clientserverchat;


import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;




public class Client_fr extends JDialog {

	private final JPanel contentPanel = new JPanel();
	public int port_server= 8888;
	public String add_server="192.168.137.1";
	public static AudioFormat getaudioformat() {
		float sampleRate=8000.0F;
		int sampleSizeInbits =16;
		int channel =2;
		boolean signed= true;
		boolean bigEndian= false;
		return new AudioFormat(sampleRate, sampleSizeInbits, channel, signed,bigEndian);
		
	}
	TargetDataLine audio_in;
	
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Client_fr dialog = new Client_fr();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	JButton btnCommencer = new JButton("commencer");
	 JButton btnArreter = new JButton("Arreter");
	public Client_fr() {
		
		
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5)); 
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Appel Vocal");
		lblNewLabel.setBounds(177, 32, 143, 31);
		contentPanel.add(lblNewLabel);
		
		
		btnCommencer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				init_audio();
			}
		});
		btnCommencer.setBounds(72, 96, 89, 23);
		contentPanel.add(btnCommencer);
		
		
		btnArreter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Client_voice.calling=false;
				btnCommencer.setEnabled(true);
				btnArreter.setEnabled(false);
				
			}
		});
		btnArreter.setBounds(227, 96, 89, 23);
		contentPanel.add(btnArreter);
		
		JButton btnFinAppel = new JButton("Fin appel");
		btnFinAppel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		btnFinAppel.setBounds(156, 165, 89, 23);
		contentPanel.add(btnFinAppel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			
		}
	}
	public void init_audio(){
		try {
		AudioFormat format = getaudioformat();
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		if (!AudioSystem.isLineSupported(info)){
			System.out.println("N est pas supporter");
			System.exit(0);	
		}
		audio_in = (TargetDataLine) AudioSystem.getLine(info);
		
			audio_in.open(format);
			audio_in.start();
			recorder_thread r = new recorder_thread();
			InetAddress inet = InetAddress.getByName(add_server);
			r.audio_in= audio_in;
			r.dout= new DatagramSocket();
			r.server_ip= inet;
			r.server_port= port_server;
			Client_voice.calling=true;
			r.start();
			btnCommencer.setEnabled(false);
			btnArreter.setEnabled(true);
			
		} catch (LineUnavailableException | SocketException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
