/**
 * @kai
 * Implement a file wrapper that puts the file in memory,
 * can package segments of a file for sending, and can piece together a complete
 * file from segments received.
 */
package p2peer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import p2peer.Message.MessageType;

public class FileWrapper {
	/**
	 * 
	 * @param file		A file object defined by common.cfg.
	 * @param hasFile	Whether or not this process starts with this file.
	 * @param peer		The PeerConnection object for the peer that uses this FileWrapper 
	 */
	
	File file;
	Map fileMap = new HashMap<String, File[]>();
	boolean hasFile;
	PeerConnection peer;

	public FileWrapper() {}

	public FileWrapper(PeerConnection peer, File file, boolean hasFile)
	{
		this.peer = peer;
		this.file = file;
		this.hasFile = hasFile;
	}

	//some variables
	int fileSize = PeerConnection.commonCfg.get_file_size();
	int pieceSize = PeerConnection.commonCfg.get_piece_size();
	int pieceNum = fileSize/pieceSize + (fileSize%pieceSize==0?0:1);

	/**
	 * If a peer has a complete file, call this method to initialize.
	 * @param fileName
	 * @param file
	 * @param fileSize
	 * @param pieceSize
	 */
	public void initialization(String fileName, File file, int fileSize, int pieceSize){
		int pieceNum = fileSize/pieceSize + (fileSize%pieceSize==0?0:1);
		File[] subFiles = new File[pieceNum];
		fileMap.put(fileName, subFiles);
		//TODO Divide the file into pieces.
	}

	/**
	 * This method is called when a peer sends a subFile to others.
	 * Return the subFile to be sent.
	 * @param fileName
	 * @param pieceIndex
	 * @return
	 */
	public File getSubFile(String fileName, int pieceIndex) {
		if (fileMap.containsKey(fileName)) {
			File[] subFiles = (File[]) fileMap.get(fileName);
			return subFiles[pieceIndex];
		}
		else {
			return null;
		}
	}

	/**
	 * This method is called when a peer receives a subFile.
	 * @param fileName
	 * @param file
	 * @param pieceIndex
	 * @param pieceNum
	 */
	public void receiveSubFile(String fileName, File file, int pieceIndex, int pieceNum) {
		if (fileMap.containsKey(fileName)) {
			File[] subFiles = (File[]) fileMap.get(fileName);
			subFiles[pieceIndex] = file;
		}
		else {
			File[] subFiles = new File[pieceNum];
			subFiles[pieceIndex] = file;
			fileMap.put(fileName, subFiles);
		}
	}

	/**
	 * Construct a piece message and send it to the other peer
	 * @param fileName
	 * @param pieceIndex
	 */
	public void send_piece(String fileName, int pieceIndex)
	{	
		//get the appropriate subfile corresponding to pieceIndex
		File file = getSubFile(fileName, pieceIndex);
		try
		{	
			//the subfile as a byte array
			byte[] file_byte_array = Files.readAllBytes(file.toPath());
			
			//the piece message contains 4 bytes piece index followed by the piece data
			//4 bytes representing piece index
			byte[] piece_index_bytes = Protocol.intToBytes(pieceIndex);
			//the payload of the piece message
			byte[] piece_payload = new byte[file_byte_array.length + 4];
			int i;
			for(i = 0; i < 4; i++)
			{
				piece_payload[i] = piece_index_bytes[i];
			}
			for(i = 0; i < file_byte_array.length; i++)
			{
				piece_payload[i + 4] = file_byte_array[i];
			}

			//now to construct the piece message and send it
			Message piece_message = new Message(MessageType.piece, piece_payload);
			Protocol.putMessage(peer.out, piece_message);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
