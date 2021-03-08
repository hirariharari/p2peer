
public static enum MessageType
{
    CHOKE((byte)0, "choke"),
    UNCHOKE((byte)1, "unchoke"),
    INTERESTED((byte)2, "interested"),
    NOT_INTERESTED((byte)3, "not interested"),
    HAVE((byte)4, "have"),
    BITFIELD((byte)5, "bitfield"),
    REQUEST((byte)6, "request"),
    PIECE((byte)7, "piece")
};
