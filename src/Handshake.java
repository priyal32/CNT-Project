import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Handshake {

    // Constants
    public final byte HEADER_LEN = 32;
    public final String HEADER = "P2PFILESHARINGPROJ";

    int id;
    public final byte ZEROES = 10;
    public final byte PEERID = 4;

    public Handshake(int id)
    {
        this.id = id;
    }

    // send handshake to dest;
    public void sendHandShake(OutputStream out) throws IOException
    {

        byte[] header = HEADER.getBytes(StandardCharsets.UTF_8);
        out.write(header);

        byte[] zeroes = new byte[10];
        out.write(zeroes);

        int srcID = id;
        byte[] peerID = new byte[Integer.BYTES];
        int length = peerID.length;
        for (int i = 0; i < length; i++)
        {
            peerID[length - i - 1] = (byte) (srcID & 0xFF);
            srcID >>= 8;
        }
        out.write(peerID);

    }

    public void readHandShake(InputStream in) throws IOException
    {

        // Receive the Handshake
        byte[] header = new byte[18];
        byte[] zeroBytes = new byte[10];
        byte[] peerID = new byte[Integer.BYTES];

        int headerlen = in.read(header);
        // convert byte[] to string
        String msg = new String(header, StandardCharsets.UTF_8);

        // should give 10 as there are 10-byte zero bits
        int zeroBytesRead = in.read(zeroBytes);

        int peerIDBytes = in.read(peerID);
        int peerIDVal = 0;
        for (byte b : peerID)
        {
            peerIDVal = (peerIDVal << 8) + (b & 0xFF);
        }

        System.out.println("Client/Server " + id + " received handshake with msg : " + msg + " " + zeroBytesRead + " from client " + peerIDVal);

    }
}