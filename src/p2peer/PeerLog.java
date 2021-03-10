/**
 * @author pkakaraparti
 * 
 * The peer Log object to which log messages are written 
 */

package p2peer;

import java.io.File;
import java.io.FileWriter;

public class PeerLog {
    FileWriter fw;

    //opening the file (or creating if it doesn't exist) using peer id
    public PeerLog(String peer_id)
    {
        try
        {
            String file_name = "log_peer_" + peer_id + ".log";
            File file = new File(file_name);
            if (!file.exists())
                file.createNewFile();
            
            this.fw = new FileWriter(file,true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    //write the log message to log file and then close
    public void write_close(String str)
    {
        try
        {
            this.fw.write("[" + Logging.timestamp() + "] : " +  str + "\n");
            this.fw.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

        