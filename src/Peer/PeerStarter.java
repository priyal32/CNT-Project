package Peer;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import static Peer.peerProcess.allPeers;

public class PeerStarter extends Peer implements Runnable{
    ServerSocket listener;
    Log log;
    Common common = Common.readCommonFile("Common.cfg");
    Peer peer;
    public PeerStarter(int id, String host, int port, Boolean hasFile, ArrayList<Peer> beforePeers) {
        super(id, host, port, hasFile, beforePeers);
        try {
            log = new Log(id);
            peer = allPeers.get(id);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void clientThread() {
                for(Peer p : this.getBeforePeers()){
                    peerClient pc = new peerClient(peer, p, log);
                    Thread pcThread = new Thread(pc);
                    pcThread.start();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

    }

    public void serverThread(){
            new Thread(() -> {
               while (!listener.isClosed()){
                    peerServer server = new peerServer(peer, listener, log);

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
        System.out.println("in");
        try {
            listener = new ServerSocket(this.getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        serverThread();
        clientThread();
    }
}
