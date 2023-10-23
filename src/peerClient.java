import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class peerClient implements Runnable{
    Socket requestSocket;           //socket connect to the server
    ObjectOutputStream out;         //stream write to the socket
    ObjectInputStream in;          //stream read from the socket
    String message;                //message send to the server
    String MESSAGE;                //capitalized message read from the server

    Peer src;
    Peer dest;
    public peerClient(Peer src, Peer dest){
        this.src = src;
        this.dest = dest;
    }

    @Override
    public void run() {

        try {


            requestSocket = new Socket(dest.getHost(), dest.getPort());
            System.out.println("Client " + src.getId() +  " connected to client id " + dest.getId() + " on " + dest.getHost() + " in port " + dest.getPort());
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();


            // do some message handling? Handshake? HOW ? :/ I hate this

                System.out.println("Sending handshake to " + dest.getHost() + " in "  + dest.getPort());
                //String msg = "Client to Server Handshake";
                Handshake handshake = new Handshake(src.getId());
                try{
                    //stream write the message
                    handshake.sendHandShake(out);
                    out.flush();
                }
                catch(IOException ioException){
                    ioException.printStackTrace();
                }

            try {
                while (true) {
                    try {
                        in = new ObjectInputStream(requestSocket.getInputStream());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    // Receive the Handshake from the Server
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
                    for (byte b : peerID) {
                        peerIDVal = (peerIDVal << 8) + (b & 0xFF);
                    }

                    System.out.println("Client " + src.getId() + " received handshake with msg : " + msg + " " + zeroBytesRead + " from client " + peerIDVal);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
