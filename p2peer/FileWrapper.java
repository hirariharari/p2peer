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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileWrapper {
	/**
	 * 
	 * @param file		A file object defined by common.cfg.
	 * @param hasFile	Whether or not this process starts with this file.
	 */
	
	File file;
	Map fileMap = new HashMap<String, File[]>();

	public FileWrapper() {}

	public FileWrapper(File file, boolean hasFile){
	}

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
}
