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
    }
}
