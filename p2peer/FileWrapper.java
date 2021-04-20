/**
 * @author kai, pkakaraparti
 * Implement a file wrapper that puts the file in memory,
 * can package segments of a file for sending, and can piece together a complete
 * file from segments received.
 */
package p2peer;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import p2peer.Message.MessageType;

public class FileWrapper {
	/**
	 * 
	 * @param file		A file object defined by common.cfg.
	 * @param hasFile	Whether or not this process starts with this file.
	 * @param peer		The PeerConnection object for the peer that uses this FileWrapper 
	 * @param subFiles  An array of files, each representing a piece of the original  
	 */
	
	boolean hasFile;
	PeerConnection peer;

	 //some variables
	int fileSize = PeerConnection.commonCfg.get_file_size();
	int pieceSize = PeerConnection.commonCfg.get_piece_size();
	int pieceNum = fileSize/pieceSize + (fileSize%pieceSize==0?0:1);

	//TODO: check if paths are correct, find some way to get the ACTUAL file name 
	Path projectDir = Paths.get("").toAbsolutePath().getParent();
	String peer_name = "peer_" + PeerConnection.myPeerID;
    Path peerDir = Paths.get(projectDir + "/"+ peer_name).toAbsolutePath(); 

	File file = new File(peerDir + "temp_file_name");
	File[] subFiles = new File[pieceNum];
	
	public List<Integer> getDefectSubFiles() {
        return defectSubFiles;
    }

    private List<Integer> defectSubFiles = new ArrayList<Integer>();

	public FileWrapper() {}

	//if the peer starts with the File, initialize it
	public FileWrapper(PeerConnection peer, File file, boolean hasFile)
	{
		this.peer = peer;
		this.file = file;
		this.hasFile = hasFile;
		initialization();
	}

	//if the peer doesn't start with the file, we need to create one
	public FileWrapper(PeerConnection peer, boolean hasFile)
	{
		this.peer = peer;
		this.hasFile = hasFile;
		initialization();
	}

	/**
	 * If a peer has a complete file, call this method to initialize.
	 */
	public void initialization(){
		//if this peer has the entire file, divide it up into sub files
		if(hasFile)
		{
			try 
			{	
				//first, represent the file as a byte array
				byte[] file_byte_array = Files.readAllBytes(this.file.toPath());
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
						subFile[j - start_index] = file_byte_array[j];
					} 
					Files.write(subFiles[i].toPath(), subFile);
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
			for(int i = 0; i < subFiles.length; i++) {
				subFiles[i] = null;
				defectSubFiles.add(i); // Initialization
			}
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
	 * Check if the peer has all the subfiles. If it does, then combine them all into a
	 * single file and update peer's hasFile
	 */
	public void check_combine_subfiles()
	{	
		//check if any subfile is null
		boolean incomplete_file = false;
		for(int i = 0; i < subFiles.length; i++ )
			if(subFiles[i] == null)
				incomplete_file = true;
		//if the subfiles are all present with data
		//we take that to mean that the peer has all the pieces		
		if(!incomplete_file)
		{	
			try 
			{
				byte[] file_byte_array = Files.readAllBytes(file.toPath());
				int i,j;
				int start_index =0;
				int end_index = pieceSize -1;
				for(i=0; i< subFiles.length; i++)
				{
					byte[] subFile = Files.readAllBytes(subFiles[i].toPath());
					for(j=start_index; j<=end_index; j++)
					{
						file_byte_array[j] = subFile[j - start_index];
					}
					start_index += pieceSize;
					//last piece may not be full
					if(i == pieceNum - 2) //next i will be last piece
						end_index += file_byte_array.length - 1;
					else	
						end_index += pieceSize;
				}
				
				//write the file byte array to the file and update hasFile
				Files.write(file.toPath(), file_byte_array);
				hasFile = true;
				
			} 
			catch (Exception e) 
			{
				e.printStackTrace();			
			}
			
		}
				
	}


	/**
	 * Extract the piece data from the piece Message and handle it
	 * @param piece_message The piece Message received by the peer through in
	 */
	public void receive_piece_message(Message piece_message)
	{
		//if the received message is indeed a piece message
		if(piece_message.type == MessageType.piece)
		{
			//TODO: check if any problems caused by manipulating bytebuffer or is it better to copy it first?
			
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
				//check if the peer has all the subfiles at this point, to combine them 
				check_combine_subfiles();

			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}

			// Update defect subFiles.
			defectSubFiles.remove(defectSubFiles.indexOf(piece_index));
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
