package Peer;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Map;

import static Peer.peerProcess.allPeers;

public class PeerStarter extends Peer implements Runnable{
    ServerSocket listener;
    Log log;
    Peer peer;
    peerSelector peerSelector;

    public PeerStarter(int id, String host, int port, Boolean hasFile, ArrayList<Peer> beforePeers) {
        super(id, host, port, hasFile, beforePeers);
        try {
            log = new Log(id);
            peer = allPeers.get(id);
            peerSelector = new peerSelector(peer, allPeers,log);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // establish connection to all the peers that started before this peer
    public synchronized void clientThread() throws IOException {
                for(Peer p : peer.getBeforePeers()) {
                    peerClient pc = new peerClient(peer, p, log, peerSelector);
                    Thread pcThread = new Thread(pc);
                    pcThread.start();
                }

    }

    // terminate program by seeing if every peer's (thats in common.cfg) bitfield is complete
    // if every peer is done downloading close sockets
    // TODO: handle this better. Don't think you can just close sockets, gives error
    // TODO: look at PeerSelector class - may be the issue
    public void terminateProgram(){
        new Thread(() -> {
            while(true){
                boolean allPeersDoneDownloading = true;
                for(Map.Entry<Integer, Peer> set : allPeers.entrySet()){
                    if(!set.getValue().getBitfield().checkPiecesFilled()){
                        allPeersDoneDownloading = false;
                        System.out.println("Peer " + set.getValue().getId() + " is not done");
                        break;
                    }
                }
                if(allPeersDoneDownloading){
                    System.out.println("doneeeeee");
                    try {
                        listener.close();
                        break;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    // checks every 10 seconds
                    Thread.sleep(10000 );
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }).start();
    }

    // start a listening/server thread on this peer
    public synchronized void serverThread(){
            new Thread(() -> {
               while (!listener.isClosed()){
                    peerServer server = new peerServer(peer, listener, log, peerSelector);

                   try {
                       server.acceptClient();
                   } catch (IOException e) {
                       throw new RuntimeException(e);
                   }
               }
            }).start();
    }

    public void initialize(PeerStarter process){
        Thread t = new Thread(process);
        t.start();

    }


    @Override
    public void run() {
        try {
            listener = new ServerSocket(peer.getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // start client thread (will also start the message handling thread)
        try {
            clientThread();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // start server thread (will also start the message handling thread)
        serverThread();


        // start the choking/unchoking of the peers
        peerSelector.start();
        terminateProgram();
    }
}
