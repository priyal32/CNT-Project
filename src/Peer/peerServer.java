package Peer;

import Messages.Handshake;
import Messages.Message;
import Messages.Type;

import java.net.*;
import java.io.*;
import java.util.*;

public class peerServer  {

    volatile ServerSocket listener;
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

    public synchronized void acceptClient() throws IOException {

        System.out.println("in here");


        connection = listener.accept();
        peerProcess.clientConnections.add(connection);
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
            System.out.println("Closing server");
            Thread.currentThread().interrupt();
        }

        Peer connectionFrom = peerProcess.getPeer(handshake.getPeerId());
        log.connectFrom(handshake.getPeerId());

        // start the message handling class
        ConnectionHandler connectionHandler = new ConnectionHandler(connection, peer, connectionFrom,out,in, log,peerSelector);
        connectionHandler.start();

        connectionFrom.setConnectionHandler(connectionHandler);

        // send bitfield if it has any file pieces
        if(peer.getBitfield().totalPresentPieces > 0 || peer.haveFile()){
            byte[] bitfield = peer.getBitfield().getBytes();
            connectionHandler.sendMessage(new Message(Type.BitField, bitfield));
        }
    }
}

