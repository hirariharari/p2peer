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
	FileWrapper filewrapper; // should be a GLOBAL filewrapper for this file.
	File log;
	PeerProcess peer; // this peerProcess.
	int peerID; // The peer ID of the OTHER peerProcess.
	
	public void run() {
	}
	public void close() {
	}
}
