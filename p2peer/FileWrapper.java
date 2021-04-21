/**
 * @author kai, pkakaraparti, cwphang
 * Implement a file wrapper that puts the file in memory,
 * can package segments of a file for sending, and can piece together a complete
 * file from segments received.
 */
package p2peer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Semaphore;

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
	//PeerConnection peer;

	 //some variables
	int fileSize = PeerConnection.commonCfg.get_file_size();
	int pieceSize = PeerConnection.commonCfg.get_piece_size();
	int pieceNum = fileSize/pieceSize + (fileSize%pieceSize==0?0:1);

	//TODO: check if paths are correct, find some way to get the ACTUAL file name 
	Path projectDir = Paths.get("").toAbsolutePath().getParent();
	String peer_name = "peer_" + PeerConnection.myPeerID;
    Path peerDir = Paths.get(projectDir + "/"+ peer_name).toAbsolutePath(); 

	File file = new File(peerDir + PeerConnection.commonCfg.get_file_name());
	ByteBuffer[] subFiles = new ByteBuffer[pieceNum];
	
	Semaphore [] subFileSemaphore = new Semaphore[pieceNum];
	
	public List<Integer> getDefectSubFiles() {
        return defectSubFiles;
    }

    private List<Integer> defectSubFiles = new ArrayList<Integer>();

	//if the peer starts with the File, initialize it
	public FileWrapper(File file, boolean hasFile)
	{
		this.file = file;
		this.hasFile = hasFile;
		initialization();
	}


	/**
	 * If a peer has a complete file, call this method to initialize.
	 */
	public void initialization(){
		// Allocate semaphores
		for(int i=0; i<pieceNum; i++) {
			subFileSemaphore[i] = new Semaphore(1);
		}
		
		//if this peer has the entire file, divide it up into sub files
		if(hasFile)
		{
			try 
			{	
				//first, represent the file as a byte array
				ByteBuffer file_byte_array = ByteBuffer.wrap(Files.readAllBytes(file.toPath()));
				
				//now populate the subfiles
				for (int i = 0; i < pieceNum; i++) {
					subFiles[i] = ByteBuffer.allocate(pieceSize);
					subFiles[i].put(0, file_byte_array.array(), i*pieceNum, pieceNum);
				}
			} 
			catch (Exception e) 
			{
				// There was a problem reading from the file.
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
	public ByteBuffer getSubFile(int pieceIndex)
	{
		if(pieceIndex > pieceNum)
			return null;
		else
		{
			return subFiles[pieceIndex].asReadOnlyBuffer();
		}
	}

	/**
	 * This method is called when a peer receives a subFile.
	 * @param file
	 * @param pieceIndex
	 * @param pieceNum
	 */
	public void receiveSubFile(ByteBuffer subFile, int pieceIndex) 
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
				BufferedOutputStream file_out = new BufferedOutputStream(Files.newOutputStream(file.toPath()));
				for(int i = 0; i < pieceNum; i++) {
					file_out.write(subFiles[i].array());
				}
				file_out.flush();
				file_out.close();
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
			
			// Lock the subfile to avoid concurrent writes.
			try {
				subFileSemaphore[piece_index].acquire();
			}
			catch(Exception e) {e.printStackTrace();}
			
			//now for the actual piece data
			//allocate to the size of piece_data to be the size of payload's remaining data
			subFiles[piece_index] = ByteBuffer.allocate(piece_message.payload.remaining());
			//copy data from bytebuffer into piece_data
			piece_message.payload.get(
					subFiles[piece_index].array(),
					0,
					subFiles[piece_index].array().length);

			// Update defect subFiles.
			defectSubFiles.remove(defectSubFiles.indexOf(piece_index));
			
			// Release file lock.
			subFileSemaphore[piece_index].release();
		}
	
	}

	/**
	 * Construct a piece message and send it to the other peer
	 * @param pieceIndex
	 */
	public void send_piece_message(int pieceIndex, BufferedOutputStream out)
	{	
		ByteBuffer piece_payload = ByteBuffer.allocate(pieceSize + 4);
		try
		{
			//the piece message contains 4 bytes piece index followed by the piece data
			//4 bytes representing piece index
			piece_payload.putInt(pieceIndex);
			
			//the payload of the piece message
			piece_payload.put(subFiles[pieceIndex].array());

			//now to construct the piece message and send it
			Message piece_message = new Message(MessageType.piece, piece_payload);
			Protocol.putMessage(out, piece_message);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
