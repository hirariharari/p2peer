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
		super(new Socket(host,port), peerID);
		this.run();
	}
}
