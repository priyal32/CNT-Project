import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Peer {

    private final int id;
    private final String host;
    private final int port;
    private final Boolean hasFile;
    ArrayList<Peer> beforePeers;

    public Bitfield getBitfield()
    {
        return bitfield;
    }

    public void setBitfield(Bitfield bitfield)
    {
        this.bitfield = bitfield;
    }

    Bitfield bitfield;
    public Peer(int id, String host, int port, Boolean hasFile, ArrayList<Peer> beforePeers)
    {
        this.id = id;
        this.host = host;
        this.port = port;
        this.hasFile = hasFile;
        this.beforePeers = beforePeers;
        bitfield = new Bitfield();
    }

    public ArrayList<Peer> getBeforePeers()
    {
        return beforePeers;
    }


    public int getId()
    {
        return id;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    public Boolean haveFile()
    {
        return hasFile;
    }


}
