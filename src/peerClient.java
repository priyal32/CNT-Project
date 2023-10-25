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
    public peerClient(Peer src, Peer dest)
    {
        this.src = src;
        this.dest = dest;
    }

    @Override
    public void run()
    {

        try
        {


            requestSocket = new Socket(dest.getHost(), dest.getPort());
            System.out.println("Client " + src.getId() +  " connected to client id " + dest.getId() + " on " + dest.getHost() + " in port " + dest.getPort());
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            out.flush();


            // do some message handling? Handshake? HOW ? :/ I hate this

                System.out.println("Sending handshake to " + dest.getHost() + " in "  + dest.getPort());
                //String msg = "Client to Server Handshake";
                Handshake handshake = new Handshake(src.getId());
                byte[] bitfield = src.getBitfield().getBytes();

                Message message = new Message(Type.BitField, bitfield);
                try
                {
                    //stream write the message
                    handshake.sendHandShake(out);
                    out.flush();
                }
                catch(IOException ioException)
                {
                    ioException.printStackTrace();
                }

            try
            {
                while (true)
                {
                    handshake.readHandShake(in);
                    message.writeMessage(out);
                    out.flush();
                    System.out.println("sent bitfield from peer");
                    message.readMessage(in);
                    System.out.println("read bitfield from peer");

                }

            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }
}
