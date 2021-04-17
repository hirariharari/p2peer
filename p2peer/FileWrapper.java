/**
 * TODO(any): Implement a file wrapper that puts the file in memory,
 * can package segments of a file for sending, and can piece together a complete
 * file from segments received.
 */
package p2peer;

import java.io.File;
import java.nio.file.Files;

import p2peer.Message.MessageType;

public class FileWrapper {
	/**
	 * 
	 * @param file		A file object defined by common.cfg.
	 * @param hasFile	Whether or not this process starts with this file.
	 * @param peer		The PeerConnection object for the peer that uses this FileWrapper 
	 */
	
	File file;
	boolean hasFile;
	PeerConnection peer;
	
	//some variables
	int file_size = PeerConnection.commonCfg.get_file_size();
	int piece_size = PeerConnection.commonCfg.get_piece_size();
	int num_pieces = (int)Math.ceil(file_size/piece_size);

	/**
	 * Constructor for the FileWrapper
	 * @param peer
	 * @param file
	 * @param hasFile
	 */
	public FileWrapper(PeerConnection peer, File file, boolean hasFile){
		this.peer = peer;
		this.file = file;
		this.hasFile = hasFile;
	}

	/**
	 * Function to send a requested piece to a peer
	 * @param piece_index Index of the piece that was requested
	 * @return
	 */
	public void send_piece(int piece_index)
	{
		int start_index = piece_size * piece_index;
		int end_index = start_index + piece_size - 1;

		try
		{	
			byte[] file_byte_array = Files.readAllBytes(file.toPath());
			//if the last piece is smaller than piece size
			if((piece_index == num_pieces) && (end_index > file_byte_array.length))
			{
				end_index = file_byte_array.length - 1;
			}

			int piece_size = end_index - start_index + 1;
			
			//the piece message contains 4 bytes piece index followed by the piece data
			byte[] piece_payload = new byte[piece_size + 4];
			//first 4 bytes represent the index of piece
			byte[] piece_index_bytes = Protocol.intToBytes(piece_index);
			int i;
			for(i = 0; i < 4; i++)
			{
				piece_payload[i] = piece_index_bytes[i];
			}
			//now for the actual piece data
			for(i = 0; i < piece_size; i++)
			{
				piece_payload[i + 4] = file_byte_array[start_index + i];
			}

			//now we construct the Message and send it 
			Message piece_message = new Message(MessageType.piece, piece_payload);
			Protocol.putMessage(peer.out, piece_message);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	
}
