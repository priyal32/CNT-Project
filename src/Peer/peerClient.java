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
    String message;                //message send to the server
    String MESSAGE;                //capitalized message read from the server

    Peer src;
    Peer dest;
    Log log;
    peerSelector peerSelector;
    public peerClient(Peer src, Peer dest, Log log, peerSelector peerSelector){
        this.src = src;
        this.dest = dest;
        this.log = log;
        this.peerSelector =peerSelector;
    }

    @Override
    public void run() {

        try {

            requestSocket = new Socket(dest.getHost(), dest.getPort());
            // System.out.println("Client " + src.getId() +  " connected to client id " + dest.getId() + " on " + dest.getHost() + " in port " + dest.getPort());
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            out.flush();


            // send handshake to server

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
                // read handshake from the server
                    handshake.readHandShake(in);
                    out.flush();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // start the message handling class
            ConnectionHandler connectionHandler = new ConnectionHandler(requestSocket,src, dest,out,in, log,peerSelector);
            connectionHandler.start();
            dest.setConnectionHandler(connectionHandler);

            // if it has file then send the bitfield to the server
            if(src.haveFile()){
                byte[] bitfield = src.getBitfield().getBytes();
                connectionHandler.sendMessage(new Message(Type.BitField, bitfield));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}