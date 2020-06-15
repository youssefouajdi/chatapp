package clientserverchat;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer2 extends Thread {
	
	private ServerSocket ss;
	
	public FileServer2(int port) {
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void run() {
		while (true) {
			try {
				Socket clientSock = ss.accept();
				saveFile(clientSock);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void saveFile(Socket clientSock) throws IOException {
		DataInputStream dis = new DataInputStream(clientSock.getInputStream());
		FileOutputStream fos = new FileOutputStream("a.mp3");
		byte[] buffer = new byte[4096];
		
		int filesize = 15123;
		int read = 0;
		int totalRead = 0;
		int remaining = filesize;
		while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			totalRead += read;
			remaining -= read;
			System.out.println("l ensemble des bits du fichier sont " + totalRead);
			fos.write(buffer, 0, read);
			System.out.println("****");
		}
		System.out.println("tous est ok ");
		fos.close();
		dis.close();
	}
}
	