/**
 * TODO(pkakaraparti/kai.zhang): Implement PeerProcess here.
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

public class peerProcess {

	Server server; // This peer's server connection
	ArrayList<PeerConnection> peers; // A list of connected peers.
	int peerID; // This peer's ID.
	FileWrapper filewrapper; // A FileWrapper object for the file of interest.
	File log; // A normal file object for this peer's log.
	
	public static void main(String[] args) throws IOException {
		throw new IOException();
	}

}
