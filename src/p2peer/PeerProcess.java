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
import java.util.ArrayList;

public class PeerProcess {

	static Server server; // This peer's server connection
	static ArrayList<PeerConnection> peers; // A list of connected peers.
	static int peerID; // This peer's ID.
	static FileWrapper filewrapper; // A FileWrapper object for the file of interest.
	static Logging logging = new Logging();
	static ParseCommonConfig commonCfg = new ParseCommonConfig();
	static ParsePeerInfoConfig peerCfg = new ParsePeerInfoConfig();
	
	public static void main(String[] args) {
		peerID = Integer.parseInt(args[0]);
		for (int i = 0; i < peerCfg.get_host_ids().length; i++) {
			String hostID = peerCfg.get_host_ids()[i];
			int id = Integer.parseInt(hostID);
			System.out.println(hostID + " | " + peerCfg.get_host_values(id));
		}
	}

}
