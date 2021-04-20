/**
 * @author pkakaraparti
 */
package p2peer;

import java.io.*;
import java.util.HashMap;
import java.util.ArrayList;

public class ParsePeerInfoConfig 
{   
    private HashMap<String, String[]> config_values = new HashMap<String, String[]>();
    private ArrayList<String> host_ids = new ArrayList<String>();
    
    public ParsePeerInfoConfig()
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
                host_ids.add(values[0]);
            }
            
            br.close();
        }
        catch (Exception err) 
        {
            err.printStackTrace();        
        }
    }
    
    public String[] get_host_ids()
    {
        // return config_values.keySet().toArray(String[]::new);
    	return (String[]) host_ids.toArray(new String[host_ids.size()]);
    }
    public String[] get_host_values(int host_id)
    {
        return config_values.get(Integer.toString(host_id));
    }
}
