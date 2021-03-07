import java.io.*;

public class Test {
    public static void main(String[] args)
    {
        ParseCommonConfig inst = new ParseCommonConfig();
        System.out.println(inst.get_number_of_preferred_neighbors());

        ParsePeerInfoConfig psg = new ParsePeerInfoConfig();
        System.out.println(psg.get_host_values(1001));
        String[] host_list = psg.get_host_ids();
        for(String host : host_list)
        {
            System.out.println(host + " " + psg.get_host_values(Integer.parseInt(host)));

        }

        //PeerLog p = new PeerLog("6666");
        //p.write_close("Hello World peer 6666 calling \n" + "[" + Logging.timestamp() + "]\n");
        String[] peer_list = {"6000","3000","6666","89786"};
        Logging log = new Logging();
        log.tcp_connect_to("1000", "2000");
        log.tcp_connect_to("1000", "3000");
        log.tcp_connect_from("1000", "6666");
        log.preferred_neighbors("1000", peer_list);


    }
}
