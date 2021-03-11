/** 
 * TODO (any) 
 * Implement something that describes a connection to a peer and can send 
 * messages to and from one. Should be a thread so it can start and end, 
 * notifying the client or server socket that spawned it.
 */

package p2peer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class PeerConnection extends Thread{
	// Static members for common details.
	public static int myPeerID; // This peer's ID.
	int otherPeerID; // The peer ID of the OTHER peerProcess.
	public static Logging logging = new Logging();
	public static ParseCommonConfig commonCfg = new ParseCommonConfig();
	public static ParsePeerInfoConfig peerCfg = new ParsePeerInfoConfig();
	public static boolean debug = false;
	
	
	Socket socket; // Should be a unique socket for this connection.
	boolean hasFile; //Whether the other peer has the file.
	
	public BufferedInputStream in;
	public BufferedOutputStream out;
	
	public PeerConnection(Socket socket) throws IOException {
		this.socket = socket;
		this.in = new BufferedInputStream(socket.getInputStream());
		this.out = new BufferedOutputStream(socket.getOutputStream());
	}
	public void close() {
		try {
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			// we don't actually care if this fails because the socket would
			// be closed.
		}
	}
	public boolean isClosed() {
		return socket.isClosed();
	}
	
	/**
	 * Utility function for logging to this peerProcess.
	 * @param str
	 */
	public static void info(String str) {
		if(debug)
			System.out.println("["+myPeerID+"] "+str);
	}
	
	/**
	 * Utility function for logging to a specific thread.
	 * @param str
	 */
	public void connInfo(String str) {
		if(debug)
			System.out.println("["+myPeerID+" -> "+otherPeerID+"] "+str);	
	}
}