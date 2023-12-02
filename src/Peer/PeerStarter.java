package Peer;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

import static Peer.peerProcess.allPeers;
import static Peer.peerProcess.peersDone;

public class PeerStarter extends Peer implements Runnable{
    volatile ServerSocket listener;
    Thread t;
    Thread serverThread;
    public static volatile boolean shouldStopThreads = false;
    Log log;
    Peer peer;
    peerSelector peerSelector;

    public PeerStarter(int id, String host, int port, Boolean hasFile, ArrayList<Peer> beforePeers) {
        super(id, host, port, hasFile, beforePeers);
        try {
            log = new Log(id);
            peer = allPeers.get(id);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // establish connection to all the peers that started before this peer
    public synchronized void clientThread() throws IOException {
                for(Peer p : peer.getBeforePeers()) {
                    peerClient pc = new peerClient(peer, p, log, peerSelector);
                    Thread pcThread = new Thread(pc);
                    pcThread.setName("client thread");
                    peerProcess.threads.add(pcThread);
                    pcThread.start();
                }

    }

    // terminate program by seeing if every peer's (thats in common.cfg) bitfield is complete
    // if every peer is done downloading close sockets
    // TODO: handle this better. Don't think you can just close sockets, gives error
    // TODO: look at PeerSelector class - may be the issue
    public synchronized void terminateProgram(){
        new Thread(() -> {
            while(true){
                boolean allPeersDoneDownloading = false;
                Peer peerDone = null;
                for(Peer p : peersDone){
                    if(p.getBitfield().checkPiecesFilled()){
                        peerDone = p;
                        break;
                    }
                }
                if(peerDone != null)
                    peersDone.remove(peerDone);
                if(peersDone.isEmpty()){
                    allPeersDoneDownloading = true;
                }
                if(allPeersDoneDownloading){
                    System.out.println("All peers are done");
                    shouldStopThreads = true;
                    for(Socket s : peerProcess.clientConnections){
                        try {
                            s.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    for(Thread th : peerProcess.threads){
                        System.out.println(th.getId());
                        System.out.println(th.getName());
                        th.interrupt();
                    }
                    try {
                        System.out.println("closing server socket");
                        listener.close();
                        System.exit(0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }).start();
    }

    // start a listening/server thread on this peer
    public synchronized void serverThread(){

        serverThread = new Thread(() -> {
               while (!listener.isClosed()){
                   peerServer server = new peerServer(peer, listener, log, peerSelector);
                   try {
                       if(!listener.isClosed())
                            server.acceptClient();
                   } catch (IOException e) {
                       System.out.println("Closing server");
                       Thread.currentThread().interrupt();
                       System.exit(0);
                   }
               }
        });
        serverThread.start();
        serverThread.setName("server thread");
        peerProcess.threads.add(serverThread);
    }

    public void initialize(PeerStarter process){
        t = new Thread(process);
        t.setName("peerStarter thread");
        t.start();


    }

    @Override
    public void run() {
        try {
            listener = new ServerSocket(peer.getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        peerSelector = new peerSelector(peer, allPeers,log, listener);
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
