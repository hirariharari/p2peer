/**
 * @author cwphang 
 * 
 * peerProcess implementation.
 * It is able to:
 *   read Common.cfg and PeerInfo.cfg and set up initial variables
 *   start its own listening server
 *   read PeerInfo.cfg and set up a list of peers to connect to
 *   connect to peers earlier than itself on the PeerInfo.cfg list
 *   Log completed handshakes.
 */

import java.io.IOException;
import java.util.ArrayList;
import p2peer.*;

public class peerProcess {
	
	public static boolean debug = false;
	
	public static void main(String[] args) {
		Server server = null; // This peer's server connection
		ArrayList<PeerConnection> connections = 
				new ArrayList<PeerConnection>(); // A list of connected peers.
		// FileWrapper filewrapper; // A FileWrapper object for the file of interest.

		
		if(args.length == 2)
			PeerConnection.debug = true;
		int peerID = Integer.parseInt(args[0]);
		PeerConnection.myPeerID = peerID;
		
		
		info("Starting peerProcess "+peerID);
		
		// Start the server
		try {
			info("Starting server...");
			server = new Server(Integer.parseInt(PeerConnection.peerCfg.get_host_values(peerID)[1]));
			info("Server started at "+
			server.getInetAddress().getHostName()+':'+
			server.getPort());
		} catch (NumberFormatException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Read peerCFG and start connecting to other peers.
		info("The peerCfg host list length is " + 
				PeerConnection.peerCfg.get_host_ids().length);
		
		for (int i = 0; i < PeerConnection.peerCfg.get_host_ids().length; i++) {
			int otherPeerID = Integer.parseInt(
					PeerConnection.peerCfg.get_host_ids()[i]);
			// When I find my own peerID, stop.
			info("Reading config for peerID "+otherPeerID);
			if (otherPeerID == peerID) {
				info("This is my peerID!");
				break;
			}
			String [] values = PeerConnection.peerCfg.get_host_values(otherPeerID);
			
			// convert values to appropriate form
			String host = values[0];
			int port = Integer.parseInt(values[1]);
			boolean hasFile = (values[2].equals("1") ? true : false);
			
			info("Starting client for "+otherPeerID);
			try {
				connections.add((PeerConnection)new Client(otherPeerID, host, port, hasFile));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		info("press enter to close this demo:");
		try {
			System.in.read();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
				
		info("Closing server...");
		try {
			server.close();
			server.join();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		info("Waiting for all connections to close...");
		for (PeerConnection conn : connections) {
			try {
				conn.close();
				conn.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		info("All done!");
	}

	public static void info(String str) {
		PeerConnection.info(str);
	}
}