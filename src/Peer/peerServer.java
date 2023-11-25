package Peer;

import Messages.Handshake;
import Messages.Message;
import Messages.Type;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class peerServer  {

    ServerSocket listener;
    Socket connection;
    ObjectInputStream in;
    ObjectOutputStream out;
    Peer peer;
    Log log;

    peerSelector peerSelector;
    public peerServer(Peer peer, ServerSocket listener, Log log, peerSelector peerSelector){
        this.peer = peer;
        this.listener = listener;
        this.log = log;
        this.peerSelector = peerSelector;
    }


    public void startServer() throws IOException {
        listener = new ServerSocket(peer.getPort());
        //System.out.println("The server is running on port " + peer.getPort() + " and host " + peer.getHost());


    }

    public void acceptClient() throws IOException {
        connection = listener.accept();
        out = new ObjectOutputStream(connection.getOutputStream());
        out.flush();
        in = new ObjectInputStream(connection.getInputStream());

        Handshake handshake = new Handshake(peer.getId());

        // read handshake from client
        handshake.readHandShake(in);

        if(!Objects.equals(handshake.getPeerHeader(), handshake.HEADER)){
            System.out.println("Error performing handshake");
        }

        // send Handshake back to the client
        try {
            handshake.sendHandShake(out);
            out.flush();

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        Peer connectionFrom = peerProcess.getPeer(handshake.getPeerId());
        log.connectFrom(handshake.getPeerId());

        // start the message handling class
        ConnectionHandler connectionHandler = new ConnectionHandler(connection, peer, connectionFrom,out,in, log,peerSelector);
        connectionHandler.start();

        connectionFrom.setConnectionHandler(connectionHandler);

        // send bitfield if it has the file
        if(peer.haveFile()){
            byte[] bitfield = peer.getBitfield().getBytes();
            connectionHandler.sendMessage(new Message(Type.BitField, bitfield));
        }

        System.out.println("Sent bitfield message from " + this.peer.getId() + " to " + handshake.getPeerId());


    }

}

