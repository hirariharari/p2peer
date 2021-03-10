/**
 * @author pkakaraparti
 */
package p2peer;

import java.io.*;
import java.sql.Timestamp;
import java.util.Arrays;

public class Logging {

   //generate current timestamp  
   public static Timestamp timestamp()
   {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    return timestamp;
   }

   //log message for outgoing TCP connection
   public void tcp_connect_to(String peer1, String peer2)
   {
        PeerLog p = new PeerLog(peer1);
        String message = "Peer " + peer1 + " makes a connection to Peer " + peer2; 
        p.write_close(message);
   }

   //log message for incoming TCP connection
   public void tcp_connect_from(String peer1, String peer2)
   {
        PeerLog p = new PeerLog(peer1);
        String message = "Peer " + peer1 + " is connected from Peer " + peer2; 
        p.write_close(message);
   }

   //log message for change of preferred neighbors
   public void preferred_neighbors(String peer1, String[] peer_list)
   {
       String list = Arrays.toString(peer_list);
       PeerLog p = new PeerLog(peer1);
       String message = "Peer " + peer1 + " has the preferred neighbors " + list; 
       p.write_close(message);
   }

   //log message for change of optimistically unchoked neighbor
   public void opt_unchoked_neighbor(String peer1, String peer2)
   {
        PeerLog p = new PeerLog(peer1);
        String message = "Peer " + peer1 + " has optimistically unchoked neighbor " + peer2; 
        p.write_close(message);

   }

   //log message for peer being unchoked by neighbor
   public void unchoked_by_neighbor(String peer1, String peer2)
   {
    PeerLog p = new PeerLog(peer1);
    String message = "Peer " + peer1 + " is unchoked by " + peer2; 
    p.write_close(message);
   }

    //log message for peer being choked by neighbor
   public void choked_by_neighbor(String peer1, String peer2)
   {
    PeerLog p = new PeerLog(peer1);
    String message = "Peer " + peer1 + " is choked by " + peer2; 
    p.write_close(message);
   }

   //log message for receiving 'have' message
   public void receive_have(String peer1, String peer2, int piece_index)
   {
    PeerLog p = new PeerLog(peer1);
    String message = "Peer " + peer1 + " received the 'have' message from " + peer2 + " for the piece " + piece_index; 
    p.write_close(message);
   }

   //log message for receiving 'interested' message
   public void receive_interested(String peer1, String peer2)
   {
    PeerLog p = new PeerLog(peer1);
    String message = "Peer " + peer1 + " received the 'interested' message from " + peer2; 
    p.write_close(message);
   }

   //log message for receiving 'not interested' message
   public void receive_not_interested(String peer1, String peer2)
   {
    PeerLog p = new PeerLog(peer1);
    String message = "Peer " + peer1 + " received the 'not interested' message from " + peer2; 
    p.write_close(message);
   }

   //log message for piece download by peer
   public void receive_have(String peer1, String peer2, int piece_index, int no_of_pieces)
   {
    PeerLog p = new PeerLog(peer1);
    String message = "Peer " + peer1 + " has downloaded the piece " + piece_index + " from " + peer2 + ". Now the number of pieces it has is " + no_of_pieces; 
    p.write_close(message);
   }

   //log message for download completion of total file
   public void download_complete(String peer1)
   {
    PeerLog p = new PeerLog(peer1);
    String message = "Peer " + peer1 + " has downloaded the entire file"; 
    p.write_close(message);
   }
}
