/** 
 * TODO (any) 
 * Implement something that describes a connection to a peer and can send 
 * messages to and from one. Should be a thread so it can start and end, 
 * notifying the client or server socket that spawned it.
 */

package p2peer;

import java.io.File;
import java.net.Socket;

public class PeerConnection extends Thread{
	Socket socket; // Should be a unique socket for this connection.
	int id; // The peer ID of the OTHER peerProcess.
	PeerConnection(Socket socket, int peerID) {
		this.socket = socket;
		this.id = peerID;
	}
	public void run() {
		
	}
	public void close() {
	}
}
