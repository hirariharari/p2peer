/**
 * TODO(pkakaraparti/kai.zhang): Implement a server process that can accept 
 * incoming requests.
 */
package p2peer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread {
	public static ArrayList<Handler> handlers = new ArrayList<Handler>();
	ServerSocket srvSocket;
	
	Server(int port) throws IOException{
		srvSocket = new ServerSocket(port);
		this.start();
	}
	
	public void run() {
		while(!srvSocket.isClosed()) {
			try {
				handlers.add(new Handler(srvSocket.accept()));
			} catch (IOException e) {
				// Socket was closed.
			}
		}
	}
	
	public void close() {
		try {
			srvSocket.close();
		} catch (IOException e) {
			// We don't actually care if this fails, because the socket
			//would be closed.
		}
	}
	public boolean isClosed() {
		return srvSocket.isClosed();
	}
	
}




class Handler extends PeerConnection {
	Handler(Socket socket) throws IOException{
		// set up connection and peerConnection variables here.
		super(socket);
		run();
	}
	public void run() {
		while(!socket.isClosed()) {
			//Need to get the peer id of the connecting process.
			try {
				PeerProcess.info("Incoming connection. Reading...");
				peerID = Protocol.getHandshake(in);
				//return the handshake
				Protocol.putHandshake(out, PeerProcess.peerID);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// That's it for the demo. Close down this connection.
			PeerProcess.info("Handshake established to "+peerID);
			PeerProcess.info("Closing connection to "+peerID);
			close();
		}
	}
}
