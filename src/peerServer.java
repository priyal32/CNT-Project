import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class peerServer implements Runnable {

    Peer peer;
    public peerServer(Peer peer){
        this.peer = peer;
    }

    @Override
    public void run() {
        try (ServerSocket listener = new ServerSocket(peer.getPort())) {
            System.out.println("The server is running on port " + peer.getPort() + " and host " + peer.getHost());
            while (true) {
                Socket connectionSocket = listener.accept();
                new Thread(new ConnectionHandler(connectionSocket, peer)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static class ConnectionHandler implements Runnable {
        private final Socket connectionSocket;
        private final Peer peer;
        private ObjectInputStream in;	//stream read from the socket
        private ObjectOutputStream out;    //stream write to the socket
        public ConnectionHandler(Socket connectionSocket, Peer peer) {
            this.connectionSocket = connectionSocket;
            this.peer = peer;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(connectionSocket.getOutputStream());
                in = new ObjectInputStream(connectionSocket.getInputStream());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try{
                while(true) {
                    // read message from client
                    // first read handshake
                    Handshake handshake = new Handshake(peer.getId());
                    handshake.readHandShake(in);
                    // send Handshake back to the client
                    try {
                        handshake.sendHandShake(out);
                        out.flush();

                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    byte[] bitfield = peer.getBitfield().getPayload();

                    Message message = new Message(Type.BitField, bitfield);
                    message.writeMessage(out);
                    out.flush();
                    System.out.println("Bitfield sent");

                    message.readMessage(in);
                    System.out.println("Bitfield received");

                }
           } catch (IOException e) {
                throw new RuntimeException(e);
            } finally{
            // Close connections
                try{

                    in.close();
                    out.close();
                    connectionSocket.close();
                }
                catch(IOException ioException){
                    System.out.println("Disconnect with Client ");
                }
            }
        }
    }

}
