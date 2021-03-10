/**
 * TODO(cwphang): Implement PeerProcess here.
 * It should be able to:
 * read Common.cfg and set up initial variables
 * start its own listening server
 *  read PeerInfo.cfg and set up a list of peers to connect to
 *   connect to peers earlier than itself on the PeerInfo.cfg list
 */

package p2peer;
import java.io.IOException;
import java.util.ArrayList;

public class PeerProcess {
	static int peerID; // This peer's ID.
	static Logging logging = new Logging();
	static ParseCommonConfig commonCfg = new ParseCommonConfig();
	static ParsePeerInfoConfig peerCfg = new ParsePeerInfoConfig();
	
	public static boolean debug = false;
	
	public static void main(String[] args) {
		Server server = null; // This peer's server connection
		ArrayList<PeerConnection> connections = 
				new ArrayList<PeerConnection>(); // A list of connected peers.
		// FileWrapper filewrapper; // A FileWrapper object for the file of interest.

		
		if(args.length == 2)
			debug = true;
		peerID = Integer.parseInt(args[0]);
		
		info("Starting peerProcess "+peerID);
		
		// Start the server
		try {
			info("Starting server...");
			server = new Server(Integer.parseInt(peerCfg.get_host_values(peerID)[1]));
			info("Server started at "+
			server.srvSocket.getInetAddress().getHostAddress()+':'+
			server.srvSocket.getLocalPort());
		} catch (NumberFormatException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Read peerCFG and start connecting to other peers.
		info("The peerCfg host list length is "+peerCfg.get_host_ids().length);
		for (int i = 0; i < peerCfg.get_host_ids().length; i++) {
			int otherPeerID = Integer.parseInt(peerCfg.get_host_ids()[i]);
			// When I find my own peerID, stop.
			info("Reading config for peerID "+otherPeerID);
			if (otherPeerID == peerID) {
				info("This is my peerID!");
				break;
			}
			String [] values = peerCfg.get_host_values(otherPeerID);
			
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
	
	static void info(String str) {
		if(debug)
			System.out.println("["+peerID+"] "+str);
	}
	public static void info(String str, int id) {
		if(debug)
			System.out.println("["+peerID+" -> "+id+"] "+str);	
	}
	
}