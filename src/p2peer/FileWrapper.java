/**
 * TODO(any): Implement a file wrapper that puts the file in memory,
 * can package segments of a file for sending, and can piece together a complete
 * file from segments received.
 */
package p2peer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FileWrapper {
	/**
	 * 
	 * @param file		A file object defined by common.cfg.
	 * @param hasFile	Whether or not this process starts with this file.
	 */
	
	File file;
	
	FileWrapper(File file, boolean hasFile){
	}
}
