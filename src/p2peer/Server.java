/**
 * TODO(pkakaraparti/kai.zhang): Implement a server process that can accept 
 * incoming requests.
 */
package p2peer;

import java.io.IOException;
import java.net.ServerSocket;

public class Server extends Thread {
	ServerSocket srvSocket;
	Server(int port) throws IOException{
		srvSocket = new ServerSocket(port);
	}
	public void run() {
		while(!srvSocket.isClosed()) {
			Socket socket = srvSocket.accept();
			
		}
	}
	public void close() {
		srvSocket.close();
	}
}

class Handler extends PeerConnection {
	Handler(){
		// set up connection and peerConnection variables here.
		
		this.run();
	}
}
