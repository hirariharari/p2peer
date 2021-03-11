/**
 * @author cphang
 * 
 * A server process that can accept incoming requests.
 */
package p2peer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread {
	public static ArrayList<Handler> handlers = new ArrayList<Handler>();
	ServerSocket srvSocket;
	
	public Server(int port) throws IOException{
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
	public InetAddress getInetAddress() {
		return srvSocket.getInetAddress();
	}
	public int getPort() {
		return srvSocket.getLocalPort();
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
				info("Incoming connection. Reading...");
				this.otherPeerID = Protocol.getHandshake(in);
				//return the handshake
				Protocol.putHandshake(out, +myPeerID);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// Log our connection.
			logging.tcp_connect_from(
					myPeerID, 
					otherPeerID);
			
			// That's it for the demo. Close down this connection.
			info("Handshake established to "+otherPeerID);
			info("Closing connection to "+otherPeerID);
			close();
		}
	}
}
