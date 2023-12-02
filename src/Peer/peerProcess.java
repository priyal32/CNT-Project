package Peer;

import Messages.Bitfield;

import java.io.*;

import java.net.Socket;
import java.util.*;

public class peerProcess {
    public static  HashMap<Integer, Peer> allPeers = new HashMap<Integer, Peer>();
    public static Peer getPeer(int id){
        return allPeers.get(id);
    }

    public static List<Thread> threads = new ArrayList<>();

    public static List<Socket> clientConnections = new ArrayList<>();
    public static Set<Peer> peersDone = new HashSet<Peer>();

    int peerID = -1;

    // takes in peer id of the peer to start with
    public static void main(String[] args) throws IOException {
        peerProcess p2p = new peerProcess();
        // get port of that peer id as the peer will listen to that port

        p2p.peerID = Integer.parseInt(args[0]);
        Peer currPeer = null;

        Peer init = new Peer();
        for(Map.Entry<Integer, Peer> set : allPeers.entrySet()){
            if(set.getKey() == p2p.peerID){
                currPeer = set.getValue();
                // initialize server on the peer port of the peer id that was passed in
            }
        }
        assert currPeer != null;
        PeerStarter process = new PeerStarter(currPeer.getId(), currPeer.getHost(), currPeer.getPort(),currPeer.hasFile,currPeer.getBeforePeers());
        process.initialize(process);
    }
}