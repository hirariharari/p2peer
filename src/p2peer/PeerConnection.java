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
	Socket socket; // Should be a unique socket for this connection.
	int peerID; // The peer ID of the OTHER peerProcess.
	boolean hasFile;
	
	public BufferedInputStream in;
	public BufferedOutputStream out;
	
	PeerConnection(Socket socket) throws IOException {
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
}
