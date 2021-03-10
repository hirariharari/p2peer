package src.p2peer;

import java.sql.Timestamp;
import java.util.Arrays;

public class Logging {

   /**
    * Generate instant timestamp 
    * @return The timestamp for this instant
    */
   public static Timestamp timestamp()
   {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    return timestamp;
   }

   /**
     * Log message for outgoing TCP connection
    * @param peer1 Peer which makes the TCP connection 
    * @param peer2 Peer which is being connected to 
    */
   public void tcp_connect_to(int peer1, int peer2)
   {
        PeerLog p = new PeerLog(peer1);
        String message = "Peer " + peer1 + " makes a connection to Peer " + peer2; 
        p.write_close(message);
   }

   /**
    * Log message for incoming TCP connection
    * @param peer1 Peer which is being connected to
    * @param peer2 Peer which makes the TCP connection 
    */
   public void tcp_connect_from(int peer1, int peer2)
   {
        PeerLog p = new PeerLog(peer1);
        String message = "Peer " + peer1 + " is connected from Peer " + peer2; 
        p.write_close(message);
   }

   /**
    * Log message for change of preferred neighbors
    * @param peer1 Peer which changes preferred neighbors 
    * @param peer_list List of preferred neighbors 
    */
   public void preferred_neighbors(int peer1, int[] peer_list)
   {
       String list = Arrays.toString(peer_list);
       PeerLog p = new PeerLog(peer1);
       String message = "Peer " + peer1 + " has the preferred neighbors " + list; 
       p.write_close(message);
   }

   /**
    * Log message for change of optimistically unchoked neighbor
    * @param peer1 Peer which unchokes neighbor
    * @param peer2 Peer which is being optimistically unchoked
    */
   public void opt_unchoked_neighbor(int peer1, int peer2)
   {
        PeerLog p = new PeerLog(peer1);
        String message = "Peer " + peer1 + " has optimistically unchoked neighbor " + peer2; 
        p.write_close(message);

   }

   /**
    * Log message for peer being unchoked by neighbor
    * @param peer1 Peer which is being optimistically unchoked
    * @param peer2 Peer which unchokes neighbor
    */
   public void unchoked_by_neighbor(int peer1, int peer2)
   {
    PeerLog p = new PeerLog(peer1);
    String message = "Peer " + peer1 + " is unchoked by " + peer2; 
    p.write_close(message);
   }

   /**
    * Log message for peer being choked by neighbor
    * @param peer1 Peer which is choked by neighbor
    * @param peer2 Peer which is doing the choking 
    */
   public void choked_by_neighbor(int peer1, int peer2)
   {
    PeerLog p = new PeerLog(peer1);
    String message = "Peer " + peer1 + " is choked by " + peer2; 
    p.write_close(message);
   }

   /**
    * Log message for receiving 'have' message
    * @param peer1 Peer which receives the 'have' message
    * @param peer2 Peer which sends the 'have' message
    * @param piece_index Piece index for the piece which peer2 has
    */
   public void receive_have(int peer1, int peer2, int piece_index)
   {
    PeerLog p = new PeerLog(peer1);
    String message = "Peer " + peer1 + " received the 'have' message from " + peer2 + " for the piece " + piece_index; 
    p.write_close(message);
   }

   /**
    * Log message for receiving 'interested' message
    * @param peer1 Peer which receives the 'interested' message
    * @param peer2 Peer which sends the 'interested' message
    */
   public void receive_interested(int peer1, int peer2)
   {
    PeerLog p = new PeerLog(peer1);
    String message = "Peer " + peer1 + " received the 'interested' message from " + peer2; 
    p.write_close(message);
   }

   /**
    * Log message for receiving 'not interested' message
    * @param peer1 Peer which receives the 'not interested' message
    * @param peer2 Peer which sends the 'not interested' message
    */
   public void receive_not_interested(int peer1, int peer2)
   {
    PeerLog p = new PeerLog(peer1);
    String message = "Peer " + peer1 + " received the 'not interested' message from " + peer2; 
    p.write_close(message);
   }

   /**
    * Log message for piece download by peer
    * @param peer1 Peer which downloads the piece
    * @param peer2 Peer which uploads the piece
    * @param no_of_pieces Number of pieces peer1 now has
    */
   public void receive_have(int peer1, int peer2, int piece_index, int no_of_pieces)
   {
    PeerLog p = new PeerLog(peer1);
    String message = "Peer " + peer1 + " has downloaded the piece " + piece_index + " from " + peer2 + ". Now the number of pieces it has is " + no_of_pieces; 
    p.write_close(message);
   }

   /**
    * Log message for download completion of total file
    * @param peer1 Peer which has finished downloading the entire file
    */
   public void download_complete(int peer1)
   {
    PeerLog p = new PeerLog(peer1);
    String message = "Peer " + peer1 + " has downloaded the entire file"; 
    p.write_close(message);
   }
}
