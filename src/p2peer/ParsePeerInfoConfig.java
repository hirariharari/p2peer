package p2peer;

import java.io.*;
import java.sql.Array;
import java.util.HashMap;
import java.util.Arrays;

public class ParsePeerInfoConfig 
{   
    private HashMap<String, String[]> config_values = new HashMap<String, String[]>();
    ParsePeerInfoConfig()
    {
        try {
            File config_file =  new File("PeerInfo.cfg");
            BufferedReader br = new BufferedReader(new FileReader(config_file));
            String str, values[], host_values[];
            
            while((str = br.readLine()) != null)
            {
                values = str.split(" ", 2);
                host_values =  values[1].split(" ");
                config_values.put(values[0], host_values);
            }
        }
        catch (Exception err) 
        {
            err.printStackTrace();        
        }
    }
    
    public String[] get_host_ids()
    {
        return config_values.keySet().toArray(String[]::new);
    }
    public String get_host_values(int host_id)
    {
        return Arrays.toString(config_values.get(Integer.toString(host_id)));
    }
   
}
