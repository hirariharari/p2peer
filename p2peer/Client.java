/**
 * @author cwphang, kai
 * 
 * The client interface for the project.
 */
package p2peer;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

public class Client extends PeerConnection {
	public Client(int peerID, String host, int port, boolean hasFile) throws UnknownHostException, IOException {
		//set up connection and peerProcess variables here.
		super(new Socket(host,port));
		this.otherPeerID = peerID;
		this.hasFile = hasFile;
		
		this.start();
	}
	public void run() {
		while(!socket.isClosed()) {
			// Start with a handshake.
			try {
				connInfo("Sending handshake...");
				Protocol.putHandshake(out, myPeerID);
				
				// Expect a handshake back. We already have the peerID.
				connInfo("Handshake sent. Receiving handshake...");
				Protocol.getHandshake(in);
				
				// Log our connection.
				logging.tcp_connect_to(
						myPeerID, otherPeerID);
				connInfo("Done with handshake.");
				
				//Send bitfield
				connInfo("Sending bitfield");
				sendBitField();
				connInfo("Done with sending bitfield.");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				try {
					handleMsg(Protocol.getMessage(in));
				} catch (IOException e) {
					e.printStackTrace();
				}
				close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void sendBitField() {
		List<Integer> defectSubFiles = file_wrapper.getDefectSubFiles();
		int pieceNum = file_wrapper.pieceNum;
		int len = 8 - (pieceNum % 8) + pieceNum;
		int lenOfByteArray = len / 8;
		byte[] msgPayload = new byte[lenOfByteArray];
		int[] temp = new int[len];
		Arrays.fill(temp, 1);
		for (int i :
				defectSubFiles) {
			temp[i] = 0;
		}
		for (int i = 0; i < lenOfByteArray; i++) {
			//int begin = i * 8;
			for (int j = 0; j < 8; j++) {
				msgPayload[i] = (byte) (msgPayload[i] + (1 << (7 - j)));
			}
		}
		Message sendMsg = new Message(Message.MessageType.bitfield, msgPayload);
		sendMsg(sendMsg);
	}
	
	@Override
	public void handleMsg(Message msg) {
		super.handleMsg(msg);
		if (msg.type == Message.MessageType.bitfield) {
			List<Integer> defectSubFiles = file_wrapper.getDefectSubFiles();
			int pieceNum = file_wrapper.pieceNum;
			byte[] msgPayload = new byte[msg.payload.remaining()];
			int[] serverBitField = new int[pieceNum];
			for (int i = 0; i < pieceNum; i++) {
				int index = i/8;
				int offside = 7-(i%8);
				serverBitField[i] = msgPayload[index] & (1 << offside);
			}
			boolean interested = false;
			for (int i :
					defectSubFiles) {
				if (serverBitField[i] == 1) {
					interested = true;
					break;
				}
			}
			sendInterestedMsg(interested);
		}
	}
	
}
