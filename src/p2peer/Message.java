/**
 * @author cwphang@ufl.edu
 */
package src.p2peer;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class Message {
	public static enum MessageType {
		choke (0),
		unchoke (1),
		interested (2),
		notInterested (3),
		have (4),
		bitfield (5),
		request (6),
		piece (7)
		;
		
		private final byte[] typeValue = new byte [1];
		MessageType(int typeValue) {
			this.typeValue[0] = (byte) typeValue;
		}
		public byte[] getBytes() {
			return typeValue;
		}
		public static MessageType getMessageType(byte b) {
			return MessageType.values()[(int)b];
		}
	}
	
	public MessageType type;
	public ByteBuffer payload;
	
	public Message(Message.MessageType type) {
		this(type, new byte [0]);
	}
	
	public Message(Message.MessageType type, char[] payload)
			throws UnsupportedEncodingException {
		this(type, new String(payload).getBytes(Protocol.ENCODING));
	}
	
	public Message(Message.MessageType type, String payload)
		throws UnsupportedEncodingException {
		this(type, payload.getBytes(Protocol.ENCODING));
	}
	
	public Message(Message.MessageType type, byte[] payload) {
		this.type = type;
		this.payload = ByteBuffer.allocate(payload.length);
		this.payload.put(payload);
	}
	
	public Message(Message.MessageType type, ByteBuffer payload) {
		this.type = type;
		this.payload = payload;
	}
}
