package Peer;

import java.io.*;

import java.util.HashMap;
import java.util.Map;

public class peerProcess {
    public static  HashMap<Integer, Peer> allPeers = new HashMap<Integer, Peer>();
    public static Peer getPeer(int id){
        return allPeers.get(id);
    }

    int peerID = -1;

    // takes in peer id of the peer to start with
    public static void main(String[] args) throws IOException {
        peerProcess p2p = new peerProcess();

        // get port of that peer id as the peer will listen to that port

        p2p.peerID = Integer.parseInt(args[0]);
        Peer currPeer = null;

        Peer init = new Peer();


        // have the peer process read the common attributes
        Common common = Common.readCommonFile("Common.cfg");

        peerServer server = null;
        for(Map.Entry<Integer, Peer> set : allPeers.entrySet()){
            if(set.getKey() == p2p.peerID){
                currPeer = set.getValue();
                // initialize server on the peer port of the peer id that was passed in
            }
        }
        assert currPeer != null;
        //System.out.println(currPeer.getId());

        // start server thread for the peer that's been inputted


        // establish connections to all previous peers (client -> server) i.e make client threads to those peers and start them
//        for(Peer b : currPeer.getBeforePeers()) {
//            System.out.println(b.getId());
//        }
        currPeer.getBitfield().printBytes(currPeer.getBitfield().getBytes());
        PeerStarter process = new PeerStarter(currPeer.getId(), currPeer.getHost(), currPeer.getPort(),currPeer.hasFile,currPeer.getBeforePeers());
        process.initialize(process);
    }

    // ------------------------------------HELPER FUNCTIONS--------------------------------------------------------------------------

}