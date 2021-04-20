/**
 * @author cwphang, kai
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
import java.time.Period;
import java.util.*;

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
		PeerConnection.myPeerID = peerID; //TODO Compile Error
		
		
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

			//set some variables
			int file_size = PeerConnection.commonCfg.get_file_size();
			int piece_size = PeerConnection.commonCfg.get_piece_size();
			int num_pieces = (int)Math.ceil(file_size/piece_size);
			
			info("Starting client for "+otherPeerID);
			try {
				connections.add((PeerConnection)new Client(otherPeerID, host, port, hasFile));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		// While not all machines have complete file, select unchoke neighbors.
		while (PeerConnection.file_wrapper.getDefectSubFiles().size() > 0
				|| (!server.allComplete()) ||
				connections.stream().anyMatch(conn -> {
					for (int i :
							conn.getNeighborBitfield()) {
						if (i != 1) {
							return true;
						}
					}
					return false;
				}) ) {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					List<Integer> peerIdByOrder = new ArrayList<Integer>();
					PeerConnection.neighborRates.entrySet().stream()
							.sorted(Map.Entry
									.comparingByValue())
							.forEachOrdered(b->peerIdByOrder.add(b.getKey()));
					int preferredSize = PeerConnection.commonCfg.get_number_of_preferred_neighbors();
					List<Integer> chokeList = new ArrayList<Integer>();
					List<Integer> unchokeList = new ArrayList<Integer>();
					for (int i = 0; i < preferredSize; i++) {
						if (!PeerConnection.preferredNeighbors.contains(peerIdByOrder.get(i))) {
							unchokeList.add(peerIdByOrder.get(i));
						}
					}
					for (int i :
							PeerConnection.preferredNeighbors) {
						if (peerIdByOrder.indexOf(i) == -1 || peerIdByOrder.indexOf(i) >= preferredSize) {
							chokeList.add(i);
						}
					}
					int countPreferred = 1;
					for (PeerConnection conn :
							connections) {
						if (sendChokeMsg(conn, unchokeList, chokeList, countPreferred) == 1){
							countPreferred++;
						}
					}
					for (PeerConnection conn :
							Server.handlers) {
						if (sendChokeMsg(conn, unchokeList, chokeList, countPreferred) == 1){
							countPreferred++;
						}
					}
					PeerConnection.preferredNeighbors.clear();
					// Update preferred neighbors.
					for (int i = 0; i < preferredSize; i++) {
						PeerConnection.preferredNeighbors.add(peerIdByOrder.get(i));
					}
				}
			}, PeerConnection.commonCfg.get_unchoking_interval() * 1000);
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					List<Integer> candidate = new ArrayList<Integer>();
					PeerConnection.neighborRates.entrySet().stream().filter(
							e -> (!PeerConnection.preferredNeighbors.contains(e.getKey())) &&
									PeerConnection.interestedNeighbors.contains(e.getKey())
					).forEach(e -> candidate.add(e.getKey()));
					PeerConnection.optNeighbor = candidate.get(new Random().nextInt(candidate.size()-1));
					for (PeerConnection conn :
							connections) {
						if (conn.otherPeerID == PeerConnection.optNeighbor){
							conn.sendMsg(new Message(Message.MessageType.unchoke), false, null, true);
							break;
						}
					}
					for (PeerConnection conn :
							Server.handlers) {
						if (conn.otherPeerID == PeerConnection.optNeighbor){
							conn.sendMsg(new Message(Message.MessageType.unchoke), false, null, true);
							break;
						}
					}
				}
			}, PeerConnection.commonCfg.get_optimistic_unchoking_interval() * 1000);
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
	
	/**
	 *
	 * @param conn
	 * @param unchokeList
	 * @param chokeList
	 * @param countPreferred
	 * @return 1 for unchoke, 0 for choke, -1 for nothing.
	 */
	private static int sendChokeMsg(PeerConnection conn, List<Integer> unchokeList, List<Integer> chokeList, int countPreferred) {
		if (unchokeList.contains(conn.otherPeerID)) {
			Message sendMsg = new Message(Message.MessageType.unchoke);
			conn.sendMsg(sendMsg, countPreferred == unchokeList.size(), unchokeList);
			return 1;
		}
		else if (chokeList.contains(conn.otherPeerID)) {
			//TODO Stop sending pieces.
			Message sendMsg = new Message(Message.MessageType.choke);
			conn.sendMsg(sendMsg);
			return 0;
		}
		return -1;
	}
	
	public static void info(String str) {
		PeerConnection.info(str);
	}
}