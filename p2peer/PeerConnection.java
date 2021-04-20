/**
 * @author kai
 * Implement Describe the behaviors of peers to send messages and how to handle all the messages. Also how to update file and bitfields.
 */

package p2peer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class PeerConnection extends Thread{
	// Static members for common details.
	public static int myPeerID; // This peer's ID.
	public int otherPeerID; // The peer ID of the OTHER peerProcess.
	public static Logging logging = new Logging();
	public static ParseCommonConfig commonCfg = new ParseCommonConfig();
	public static ParsePeerInfoConfig peerCfg = new ParsePeerInfoConfig();
	public static boolean debug = false;


	Socket socket; // Should be a unique socket for this connection.
	boolean hasFile; //Whether the other peer has the file.
	public static FileWrapper file_wrapper; //FileWrapper object for this peer

	// TODO initialize these variables during the peer process
    // requestingPieceNums and neighbors should be shared by clients and server in the same machine.
	public static List<Integer> requestingPieceNums = new ArrayList<Integer>();
	public static Map<Integer,Long> neighborRates = new HashMap<Integer, Long>();// Value in this map is the time used
	public static List<Integer> preferredNeighbors = new ArrayList<Integer>();
	public static List<Integer> interestedNeighbors = new ArrayList<Integer>();
	public static int optNeighbor;
	private long startTime;

    public int[] getNeighborBitfield() {
        return neighborBitfield;
    }

    private int[] neighborBitfield = new int[file_wrapper.pieceNum];

	public BufferedInputStream in;
	public BufferedOutputStream out;

	public PeerConnection(Socket socket) throws IOException {
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

	// handleMsg and sendMsg are general methods for server and client
	public void handleMsg(Message msg) {
		if (msg.type == Message.MessageType.choke) {
			logging.choked_by_neighbor(myPeerID, otherPeerID);
		}
		else if (msg.type == Message.MessageType.unchoke) {
			logging.unchoked_by_neighbor(myPeerID, otherPeerID);
			List<Integer> defectSubFiles = file_wrapper.getDefectSubFiles();
			// Suppose peer A receives a unchoke message from peer B. Peer A selects a piece randomly among the pieces that peer B has and peer A does not have and also has not requested yet.
			if (requestingPieceNums.size() < defectSubFiles.size()) {
				List<Integer> candidatePieceIndexList = new ArrayList<>();
				for (int i : neighborBitfield) {
					if (defectSubFiles.contains(i) && !requestingPieceNums.contains(i)) {
						candidatePieceIndexList.add(i);
					}
				}
				if (candidatePieceIndexList.size()>0) {
					int index = new Random().nextInt(candidatePieceIndexList.size()-1);
					int pieceIndex = candidatePieceIndexList.get(index);
                    Message sendMsg = new Message(Message.MessageType.request, Protocol.intToBytes(pieceIndex));
                    sendMsg(sendMsg);
                }
			}
		}
		else if (msg.type == Message.MessageType.interested) {
			// Update interestedNeighbors of PeerConnection.
			PeerConnection.interestedNeighbors.add(otherPeerID);
			logging.receive_interested(myPeerID, otherPeerID);
		}
		else if (msg.type == Message.MessageType.notInterested) {
			// Update interestedNeighbors of PeerConnection.
			if (interestedNeighbors.contains(otherPeerID)) {
				PeerConnection.interestedNeighbors.remove(PeerConnection.interestedNeighbors.indexOf(otherPeerID));
			}
			logging.receive_not_interested(myPeerID, otherPeerID);
		}
		else if (msg.type == Message.MessageType.bitfield) {
            byte[] msgPayload = new byte[msg.payload.remaining()];
            int pieceNum = file_wrapper.pieceNum;
            for (int i = 0; i < pieceNum; i++) {
                int index = i/8;
                int offside = 7-(i%8);
                neighborBitfield[i] = msgPayload[index] & (1 << offside);
            }
        }
		else if (msg.type == Message.MessageType.have) {
			int pieceIndex = msg.payload.getInt();
			logging.receive_have(myPeerID, otherPeerID, pieceIndex);
			neighborBitfield[pieceIndex] = 1;
			boolean interested = false;
			for (int i :
					file_wrapper.getDefectSubFiles()) {
				if (neighborBitfield[i] == 1) {
					interested = true;
					break;
				}
			}
			sendInterestedMsg(interested);
		}
		else if (msg.type == Message.MessageType.request) {
			// If choked and not opt, stop sending pieces.
			if (preferredNeighbors.contains(otherPeerID) || optNeighbor == otherPeerID) {
				int pieceIndex = msg.payload.getInt();
				file_wrapper.send_piece_message(pieceIndex);
			}
		}
		else if (msg.type == Message.MessageType.piece) {
			long endTime = System.currentTimeMillis();
			// Update neighborRates.
			neighborRates.put(otherPeerID, endTime - startTime);
			file_wrapper.receive_piece_message(msg);
			int pieceIndex = msg.payload.getInt();
			neighborRates.entrySet().stream().forEach(e -> {
				Message sendMsg = new Message(Message.MessageType.have, Protocol.intToBytes(pieceIndex));
				sendMsg(sendMsg);
			});
			if (requestingPieceNums.contains(pieceIndex)) {
				requestingPieceNums.remove(requestingPieceNums.indexOf(pieceIndex));
			}
			logging.download(myPeerID, otherPeerID, pieceIndex, file_wrapper.pieceNum - file_wrapper.getDefectSubFiles().size());
			if (file_wrapper.hasFile) {
				logging.download_complete(myPeerID);
			}
			else {
				// Send request again if can get more.
				for (int i :
						file_wrapper.getDefectSubFiles()) {
					if (neighborBitfield[i] == 1) {
						Message sendMsg = new Message(Message.MessageType.request, Protocol.intToBytes(i));
						sendMsg(sendMsg);
					}
				}
			}
		}
	}

	protected void sendInterestedMsg(boolean interested) {
		if (interested) {
			Message sendMsg = new Message(Message.MessageType.interested);
			sendMsg(sendMsg);
		}
		else {
			Message sendMsg = new Message(Message.MessageType.notInterested);
			sendMsg(sendMsg);
		}
	}

	public void sendMsg(Message msg) {
	    sendMsg(msg, false, null);
    }

	/**
	 *
	 * @param msg
	 * @param isLastMsg Used for choking message log, if it is the last message, then log it.
	 * @param peerList Used for choking message log, current peer has sent choke message to the peers in the list.
	 */
	public void sendMsg(Message msg, boolean isLastMsg, List<Integer> peerList) {
		sendMsg(msg, isLastMsg, peerList, false);
	}


	/**
	 * Utility function for logging to this peerProcess.
	 * @param str
	 */
	public static void info(String str) {
		if(debug)
			System.out.println("["+myPeerID+"] "+str);
	}

	/**
	 * Utility function for logging to a specific thread.
	 * @param str
	 */
	public void connInfo(String str) {
		if(debug)
			System.out.println("["+myPeerID+" -> "+otherPeerID+"] "+str);
	}

    public void sendMsg(Message msg, boolean isLastMsg, List<Integer> peerList, boolean chooseOpt) {
        try {
            Protocol.putMessage(out, msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO log
        if (msg.type == Message.MessageType.choke) {

        }
        else if (msg.type == Message.MessageType.unchoke) {
            if (isLastMsg) {
                logging.preferred_neighbors(myPeerID, peerList.stream().mapToInt(Integer::intValue).toArray());
            }
            if (chooseOpt) {
                logging.opt_unchoked_neighbor(myPeerID, otherPeerID);
            }
        }
        else if (msg.type == Message.MessageType.request) {
        	startTime = System.currentTimeMillis();
            requestingPieceNums.add(msg.payload.getInt());
        }
    }
}