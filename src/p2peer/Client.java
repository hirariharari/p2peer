/**
 * TODO(kai.zhang/kai.zhang): Implement a client process that can connect 
 * to  a server.
 */
package p2peer;

public class Client extends PeerConnection {
	public Client(int peerID, String hostName, int port, boolean hasFile) {
		//set up connection and peerProcess variables here.
		
		this.run();
	}
}
