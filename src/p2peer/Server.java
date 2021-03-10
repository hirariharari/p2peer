/**
 * TODO(pkakaraparti/kai.zhang): Implement a server process that can accept 
 * incoming requests.
 */
package p2peer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread {
	public static ArrayList<Handler> handlers;
	ServerSocket srvSocket;
	Server(int port) throws IOException{
		srvSocket = new ServerSocket(port);
	}
	public void run() {
		while(!srvSocket.isClosed()) {
			try {
				Socket socket = srvSocket.accept();
				handlers.add(new Handler(socket));
			} catch (IOException e) {
				// Socket was closed.
			}
			
		}
	}
	public void close() {
		try {
			srvSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class Handler extends PeerConnection {
	Handler(Socket socket){
		// set up connection and peerConnection variables here.
		super(socket);
		this.run();
	}
	public void run() {
		//Need to get the peer id of the connecting process.
		try {
			BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
			BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
			peerID = Protocol.getHandshake(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
