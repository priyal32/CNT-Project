package Peer;

import Messages.Bitfield;
import Messages.Handshake;
import Messages.Message;
import Messages.Type;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class peerClient implements Runnable{
    Socket requestSocket;           //socket connect to the server
    ObjectOutputStream out;         //stream write to the socket
    ObjectInputStream in;          //stream read from the socket

    Peer src;
    Peer dest;
    Log log;
    peerSelector peerSelector;
    public peerClient(Peer src, Peer dest, Log log, peerSelector peerSelector) throws IOException {
        this.src = src;
        this.dest = dest;
        this.log = log;
        this.peerSelector =peerSelector;
        requestSocket = new Socket(dest.getHost(), dest.getPort());
    }

    @Override
    public void run() {

        try {
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();

            in = new ObjectInputStream(requestSocket.getInputStream());

            // send handshake to the peer that it connected to

            Handshake handshake = new Handshake(src.getId());

            try{
                handshake.sendHandShake(out);
                log.connectTo(dest.getId());
                out.flush();
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }

            try {
                // read handshake from the peer it connected to
                handshake.readHandShake(in);
                out.flush();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // start the message handling class
            ConnectionHandler connectionHandler = new ConnectionHandler(requestSocket,src, dest,out,in, log,peerSelector);
            connectionHandler.start();
            dest.setConnectionHandler(connectionHandler);

            // if it has any file pieces then send the bitfield to the server
            if(src.getBitfield().totalPresentPieces > 0 || src.hasFile){
                byte[] bitfield = src.getBitfield().getBytes();
                connectionHandler.sendMessage(new Message(Type.BitField, bitfield));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}