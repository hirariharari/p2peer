/**
 * @author pkakaraparti
 */
package p2peer;

import java.io.*;
import java.util.HashMap;

public class ParseCommonConfig 
{   
    private HashMap<String, String> config_values = new HashMap<String, String>();
    public ParseCommonConfig()
    {
        try {
            File config_file =  new File("Common.cfg");
            BufferedReader br = new BufferedReader(new FileReader(config_file));
            String str;
            
            while((str = br.readLine()) != null)
            {
                String values[] = str.split(" ", 2);
                config_values.put(values[0], values[1]);
            }
            
            br.close();
        }
        catch (Exception err) 
        {
            err.printStackTrace();        
        }
    }
    
    public int get_number_of_preferred_neighbors()
    {
        return Integer.parseInt(config_values.get("NumberOfPreferredNeighbors"));
    }

    public int get_unchoking_interval()
    {
        return Integer.parseInt(config_values.get("Unchokinginterval"));
    }

    public int get_optimistic_unchoking_interval()
    {
        return Integer.parseInt(config_values.get("OptimisticUnchokingINterval"));
    }

    public String get_file_name()
    {
        return config_values.get("FileName");
    }

    public int get_file_size()
    {
        return Integer.parseInt(config_values.get("FileSize"));
    }

    public int get_piece_size()
    {
        return Integer.parseInt(config_values.get("PieceSize"));
    }
}
