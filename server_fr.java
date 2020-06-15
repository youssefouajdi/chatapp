package clientserverchat;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.net.DatagramSocket;
import java.net.SocketException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
public class server_fr extends JDialog {

	private final JPanel contentPanel = new JPanel();

	
	public int port=8888;
	
	public static AudioFormat getaudioformat() {
		float sampleRate=8000.0F;
		int sampleSizeInbits =16;
		int channel =2;
		boolean signed= true;
		boolean bigEndian= false;
		return new AudioFormat(sampleRate, sampleSizeInbits, channel, signed, bigEndian);
		
	}
	public SourceDataLine audio_out;
	public static void main(String[] args) {
		try {
			server_fr dialog = new server_fr();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	JButton btnAppeler = new JButton("Appeler");
	public server_fr() {
		
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		btnAppeler.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				init_audio();
				
			}
		});
		
		
		btnAppeler.setBounds(156, 149, 89, 23);
		contentPanel.add(btnAppeler);
		
		JLabel lblServeurDAppel = new JLabel("Serveur d appel vocal");
		lblServeurDAppel.setBounds(141, 35, 159, 14);
		contentPanel.add(lblServeurDAppel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			
		}
	}
	public void init_audio(){
		AudioFormat format = getaudioformat();
		DataLine.Info info_out = new DataLine.Info(SourceDataLine.class, format);
		if(!AudioSystem.isLineSupported(info_out)){
			System.out.println("n est pas supporter");
			System.exit(0);
		}
		try {
			audio_out  = (SourceDataLine)AudioSystem.getLine(info_out);
			audio_out.open(format);
			audio_out.start();
			player_thread p = new player_thread();
			p.din= new DatagramSocket(port);
			p.audio_out = audio_out;
			server_voice.calling = true;
			p.start();
			btnAppeler.setEnabled(false);
		} catch (LineUnavailableException | SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
