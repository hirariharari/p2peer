/**
 * TODO(cwphang): Implement PeerProcess here.
 * It should be able to:
 * read Common.cfg and set up initial variables
 * start its own listening server
 *  read PeerInfo.cfg and set up a list of peers to connect to
 *   connect to peers earlier than itself on the PeerInfo.cfg list
 */

package p2peer;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class PeerProcess {

	static Server server; // This peer's server connection
	static ArrayList<PeerConnection> connections; // A list of connected peers.
	static int peerID; // This peer's ID.
	static FileWrapper filewrapper; // A FileWrapper object for the file of interest.
	static Logging logging = new Logging();
	static ParseCommonConfig commonCfg = new ParseCommonConfig();
	static ParsePeerInfoConfig peerCfg = new ParsePeerInfoConfig();
	public static final boolean debug = true;
	
	public static void main(String[] args) {
		peerID = Integer.parseInt(args[0]);
		
		// Read peerCFG and start connecting to other peers.
		info("The peerCfg host list length is "+peerCfg.get_host_ids().length);
		for (int i = 0; i < peerCfg.get_host_ids().length; i++) {
			int otherPeerID = Integer.parseInt(peerCfg.get_host_ids()[i]);
			// When I find my own peerID, stop.
			info("This peerID is "+otherPeerID);
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
			Client client;
			try {
				client = new Client(otherPeerID, host, port, hasFile);
				connections.add(client);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		info("Done!");
	}
	
	static void info(String str) {
		if(debug)
			System.out.println(str);
	}
	
}