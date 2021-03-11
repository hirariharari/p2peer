/**
 * @author cwphang
 * 
 * The client interface for the project.
 */
package p2peer;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends PeerConnection {
	public Client(int peerID, String host, int port, boolean hasFile) throws UnknownHostException, IOException {
		//set up connection and peerProcess variables here.
		super(new Socket(host,port));
		this.otherPeerID = peerID;
		this.hasFile = hasFile;
		
		this.start();
	}
	public void run() {
		while(!socket.isClosed()) {
			// Start with a handshake.
			try {
				connInfo("Sending handshake...");
				Protocol.putHandshake(out, myPeerID);
				
				// Expect a handshake back. We already have the peerID.
				connInfo("Handshake sent. Receiving handshake...");
				Protocol.getHandshake(in);
				
				// Log our connection.
				logging.tcp_connect_to(
						myPeerID, otherPeerID);
				
				// That's it for the demo. Close.
				connInfo("Done with handshake.");
				close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
