package clientserverchat;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
 
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
public class Serpent {
	public static void encrypt(String originalString, String secretKey) {
	try{
	    SecureRandom sr = new SecureRandom(secretKey.getBytes());
	      KeyGenerator kg = KeyGenerator.getInstance("SERPENT");
	      kg.init(sr);
	      SecretKey myDesKey = kg.generateKey();
	    Cipher desCipher;
	    // Create the cipher 
	    desCipher = Cipher.getInstance("SERPENT");
	    // Initialize the cipher for encryption
	    desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
	    //sensitive information
	    byte[] text = originalString.getBytes();
	    System.out.println("Text : " + new String(text));
	    // Encrypt the text
	    byte[] textEncrypted = desCipher.doFinal(text);
	    System.out.println("Text Encryted : " + new String( textEncrypted));
	    // Initialize the same cipher for decryption
	    desCipher.init(Cipher.DECRYPT_MODE, myDesKey);
	    // Decrypt the text
	    byte[] textDecrypted = desCipher.doFinal(textEncrypted);
	    System.out.println("Text Decryted : " + new String(textDecrypted));
	}catch(Exception e){
		e.printStackTrace();
	}
	}
	public static void appSerpent(String text, String key) {
		System.out.println("*************************");
		System.out.println("algorithme SERPENT");
	    Serpent.encrypt(text, key) ;
		System.out.println("*************************");

	}
	
	
}