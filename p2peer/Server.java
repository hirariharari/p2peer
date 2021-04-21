/**
 * @author cphang, kai
 * 
 * A server process that can accept incoming requests.
 */
package p2peer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server extends Thread {
	public static ArrayList<Handler> handlers = new ArrayList<Handler>();
	ServerSocket srvSocket;
	
	public Server(int port) throws IOException{
		srvSocket = new ServerSocket(port);
		this.start();
	}
	
	public void run() {
		while(!srvSocket.isClosed()) {
			try {
				handlers.add(new Handler(srvSocket.accept()));
			} catch (IOException e) {
				// Socket was closed.
			}
		}
	}
	
	public void close() {
		try {
			srvSocket.close();
		} catch (IOException e) {
			// We don't actually care if this fails, because the socket
			// would be closed.
		}
	}
	public boolean isClosed() {
		return srvSocket.isClosed();
	}
	public InetAddress getInetAddress() {
		return srvSocket.getInetAddress();
	}
	public int getPort() {
		return srvSocket.getLocalPort();
	}
	public boolean allComplete() {
        for (Handler h :
                handlers) {
            for (int i :
                    h.getNeighborBitfield()) {
                if (i != 1) {
                    return false;
                }
            }
        }
        return true;
    }
}




class Handler extends PeerConnection {
	Handler(Socket socket) throws IOException{
		// set up connection and peerConnection variables here.
		super(socket);
		run();
	}
	public void run() {
        //Need to get the peer id of the connecting process.
        try {
            info("Incoming connection. Reading...");
            this.otherPeerID = Protocol.getHandshake(in);
            //return the handshake
            Protocol.putHandshake(out, +myPeerID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Log our connection.
        logging.tcp_connect_from(
                myPeerID,
                otherPeerID);
        info("Handshake established to " + otherPeerID);

        while (!socket.isClosed()) {

            try {
                handleMsg(Protocol.getMessage(in));
            } catch (IOException e) {
                e.printStackTrace();
            }

            info("Closing connection to " + otherPeerID);
            close();
        }
	}
	@Override
    public void handleMsg(Message msg) {
        super.handleMsg(msg);
        if (msg.type == Message.MessageType.bitfield) {
            List<Integer> defectSubFiles = file_wrapper.getDefectSubFiles();
            int pieceNum = file_wrapper.pieceNum;
            if (defectSubFiles.size() < pieceNum) {
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
        }
    }
}
