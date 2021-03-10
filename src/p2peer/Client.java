/**
 * TODO(kai.zhang/kai.zhang): Implement a client process that can connect 
 * to  a server.
 */
package p2peer;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends PeerConnection {
	public Client(int peerID, String host, int port, boolean hasFile) throws UnknownHostException, IOException {
		//set up connection and peerProcess variables here.
		super(new Socket(host,port));
		this.peerID = peerID;
		this.hasFile = hasFile;
		
		this.start();
	}
	public void run() {
		while(!socket.isClosed()) {
			// Start with a handshake.
			try {
				PeerProcess.info("Sending handshake...",peerID);
				Protocol.putHandshake(out, PeerProcess.peerID);
				
				// Expect a handshake back. We already have the peerID.
				PeerProcess.info("Handshake sent. Receiving handshake...",peerID);
				Protocol.getHandshake(in);
				
				// Log our connection.
				PeerProcess.logging.tcp_connect_to(
						String.valueOf(PeerProcess.peerID), String.valueOf(peerID));
				
				// That's it for the demo. Close.
				PeerProcess.info("All done.",peerID);
				close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
