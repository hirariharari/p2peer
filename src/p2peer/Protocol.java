/**
 * @author cwphang@ufl.edu
 * The message protocol used by the project.
 * This will contain the message headers and enums needed for a message
 * as well as the methods for encoding and decoding headers and messages
 * via UTF-8 encoded stream.
 *   
 * Methods are available as static methods.
 * (this class cannot be instantiated)
 *   
 * This protocol is intended to work with byte streams and should be used with
 * BufferedInputStream and BufferedOutputStream.
 * @see BufferedInputStream
 * @see BufferedOutputStream
 */
package src.p2peer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;



public class Protocol {
	private Protocol() {}
	static final String ENCODING = "UTF-8";
	static final String HANDSHAKE_HEADER = "P2PFILESHARINGPROJ";
	static final byte[] HEADER_BYTES;
	static final byte[] ZERO_BYTES;
	static {
		try {
			HEADER_BYTES = HANDSHAKE_HEADER.getBytes(ENCODING);
			ZERO_BYTES = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		} catch (UnsupportedEncodingException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	/**
	 * Return a 32 byte array header using this message's peerID.
	 * 
	 * @param peerID	The peer ID to use with this message
	 * @return 			A 32 byte array with the handshake message.
	 */
	public static byte[] encodeHandshake(int peerID) {
		byte[] handshake = new byte[32];

		// Generate a 4 byte array with the peerID, with leading 0s.
		byte[] peerIDBytes = intToBytes(peerID);

		// Copy handshake to return object
		System.arraycopy(HEADER_BYTES, 0, handshake, 0, 18);
		System.arraycopy(ZERO_BYTES, 0, handshake, 18, 10);
		System.arraycopy(peerIDBytes, 0, handshake, 28, 4);

		return handshake;
	}

	/**
	 * Generate a string representation of a positive integer value with leading 0's
	 * as necessary.
	 * 
	 * @param message 	The integer to convert to string representation.
	 * @param length  	How many characters long the string should be.
	 * @return 			A string representation for the integer.
	 */
	static String intToString(int message, int length) {
		String str = "%0" + String.valueOf(length) + "d";
		return str.formatted(message);
	}

	/**
	 * Generate a byte representation of an integer value.
	 * 
	 * @param message 	The integer to convert.
	 * @return 			A byte array representation of that integer.
	 */
	static byte[] intToBytes(int message) {
		byte[] bytes = new byte[4];
		ByteBuffer buf = ByteBuffer.allocate(4).putInt(message);
		System.arraycopy(buf, 0, bytes, 0, 4);
		return bytes;
	}
	
	/**
	 * Convert a byte representation of an integer value into an integer. 
	 * 
	 * @param bytes	The byte representation of the integer to return.
	 * @return		An integer.
	 */
	static int bytesToInt(byte[] bytes) {
		ByteBuffer buf = ByteBuffer.allocate(4);
		System.arraycopy(bytes, 0, buf.array(), 0, 4);
		return buf.getInt();
	}
	
	/**
	 * Wrap a message payload with a header.
	 * 
	 * @param message 	The message to encode.
	 * @return			A byte array representation of the message.
	 */
	static byte[] encodeMessage(Message message) {
		byte[] messageBytes = new byte[5+message.payload.array().length];
		
		byte[] lengthBytes = intToBytes(message.payload.array().length);
		byte[] messageType = message.type.getBytes();
		
		System.arraycopy(lengthBytes, 0, messageBytes, 0, 4);
		System.arraycopy(messageType, 0, messageBytes, 4, 1);
		System.arraycopy(message.payload, 0, messageBytes, 5, 
				message.payload.array().length);
		
		return messageBytes;
		
	}
	
	/**
	 * Returns a peer ID obtained from a handshake.
	 * @param in	The buffered input stream to read from.
	 * @return		The peerID sent from this handshake.
	 * @throws IOException
	 */
	public static int getHandshake(BufferedInputStream in) throws IOException {
		ByteBuffer buf = ByteBuffer.allocate(32);
		in.read(buf.array());
		
		// TODO: Make it so this cares that the rest of the message is correct.
		
		ByteBuffer peerID = ByteBuffer.allocate(4);
		System.arraycopy(buf.array(), 28, peerID.array(), 0, 4);
		return peerID.getInt();
	}
	
	public static void putHandshake(BufferedOutputStream out, int peerID) throws IOException {
		byte[] encodedHandshake = encodeHandshake(peerID);
		out.write(encodedHandshake);
		out.flush();
		
	}
	
	/**
	 * Returns the message type obtained from a peer, plus the payload if
	 * applicable.
	 * 
	 * @param in	The buffered input stream to read from.
	 * @return		A message.
	 * @throws IOException
	 */
	public static Message getMessage(BufferedInputStream in) throws IOException {
		ByteBuffer msgHeader = ByteBuffer.allocate(5);
		in.read(msgHeader.array());
		
		int length = msgHeader.getInt();
		Message.MessageType type = Message.MessageType.getMessageType(msgHeader.get());
		
		if (length <= 0) {
			return new Message(type);
		}
		else {
			ByteBuffer msgBody = ByteBuffer.allocate(length);
			return new Message(type,msgBody);
		}
	}
	
	public static void putMessage(BufferedOutputStream out, Message msg) throws IOException {
		byte[] encodedMessage = encodeMessage(msg);
		out.write(encodedMessage);
		out.flush();
	}
}
