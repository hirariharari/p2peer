/**
 * @author kai, pkakaraparti
 * Implement a file wrapper that puts the file in memory,
 * can package segments of a file for sending, and can piece together a complete
 * file from segments received.
 */
package p2peer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.Arrays;
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
	 * @param subFiles  An array of files, each representing a piece of the original  
	 */
	
	 //some variables
	int fileSize = PeerConnection.commonCfg.get_file_size();
	int pieceSize = PeerConnection.commonCfg.get_piece_size();
	int pieceNum = fileSize/pieceSize + (fileSize%pieceSize==0?0:1);

	File file;
	File[] subFiles = new File[pieceNum];
	boolean hasFile;
	PeerConnection peer;

	public FileWrapper() {}

	public FileWrapper(PeerConnection peer, File file, boolean hasFile)
	{
		this.peer = peer;
		this.file = file;
		this.hasFile = hasFile;
		initialization(this.file);
	}

	/**
	 * If a peer has a complete file, call this method to initialize.
	 * @param file
	 */
	public void initialization(File file){
		//if this peer has the entire file, divide it up into sub files
		if(hasFile)
		{
			try 
			{	
				//first, represent the file as a byte array
				byte[] file_byte_array = Files.readAllBytes(file.toPath());
				//now populate the subfiles
				int i,j;
				int start_index = 0;
				int end_index = pieceSize -1;
				for(i = 0; i < pieceNum; i++)
				{	
					//represent the subFile as a byte array
					byte[] subFile = Files.readAllBytes(subFiles[i].toPath());
					for(j = start_index; j <= end_index; j++)
					{
						subFile[j] = file_byte_array[j];
					} 
					start_index += pieceSize;
					//last piece may not be full
					if(i == pieceNum - 2) //next i will be last piece
						end_index += file_byte_array.length - 1;
					else	
						end_index += pieceSize;
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		//if this peer starts with no file, make the subfiles array into null
		else
		{	
			for(int i = 0; i < subFiles.length; i++)
				subFiles[i] = null;
		}
	}

	/**
	 * This method is called when a peer sends a subFile to others.
	 * Return the subFile to be sent.
	 * @param pieceIndex
	 * @return
	 */
	public File getSubFile(int pieceIndex)
	{
		if(pieceIndex > pieceNum)
			return null;
		else
		{
			File subFile = subFiles[pieceIndex];
			return subFile;
		}
	}

	/**
	 * This method is called when a peer receives a subFile.
	 * @param file
	 * @param pieceIndex
	 * @param pieceNum
	 */
	public void receiveSubFile(File subFile, int pieceIndex) 
	{
		subFiles[pieceIndex] = subFile;
	}

	/**
	 * Extract the piece data from the piece Message and handle it
	 * @param piece_message The piece Message receieved by the peer through in
	 */
	public void receive_piece_message(Message piece_message)
	{
		//if the recieved message is indeed a piece message
		if(piece_message.type == MessageType.piece)
		{
			//TODO: check if any problems caused by manipulaitng bytebuffer or is it better to copy it first?
			
			//assign the piece index (bytebuffer is moved)
			int piece_index = piece_message.payload.getInt();
			//now for the actual piece data
			//allocate to the size of piece_data to be the size of payload's remaining data
			byte[] piece_data = new byte[piece_message.payload.remaining()];
			//copy data from bytebuffer into piece_data
			piece_message.payload.get(piece_data, 0, piece_data.length);

			//now to write piece_data byte array into its respective subFile
			try 
			{
				Files.write(subFiles[piece_index].toPath(), piece_data);

			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}

			
		}
	
	}

	/**
	 * Construct a piece message and send it to the other peer
	 * @param pieceIndex
	 */
	public void send_piece_message(int pieceIndex)
	{	
		//get the appropriate subfile corresponding to pieceIndex
		File file = getSubFile(pieceIndex);
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
