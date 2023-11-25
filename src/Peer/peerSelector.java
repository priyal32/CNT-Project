package Peer;

import Messages.Manager;
import Messages.Message;
import Messages.Type;

import java.awt.*;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.util.*;

public class peerSelector extends Thread{
    Peer src;
    HashMap<Integer, Peer> peers;
    ArrayList<Peer> interestedPeers = new ArrayList<Peer>();
    ArrayList<Integer> prefPeers = new ArrayList<Integer>();

    ArrayList<Peer> kNPrefNeighborsPeers = new ArrayList<Peer>();
    Peer optUnchokedPeer;
    Log log;
    Common common = Common.readCommonFile("Common.cfg");

    public peerSelector(Peer src, HashMap<Integer, Peer> peers, Log log)
    {
        this.src = src;
        this.peers = peers;
        this.log = log;
    }

    static class sortInterestedPeers implements Comparator<Peer>
    {
        public int compare(Peer a, Peer b)
        {
            Random r = new Random();
            if (a.getDownloadSpeed() == b.getDownloadSpeed())
            {
                return r.nextInt(2);
            }
            return a.getDownloadSpeed() - b.getDownloadSpeed();
        }
    }

    public void kPreferredNeighbors()
    {
        long time = common.getUnchokingInterval();

        new Thread(() -> {
            try
            {
                while (true){
                    synchronized (this)
                    {
                        if(!interestedPeers.isEmpty())
                        {
                            kNPrefNeighborsPeers = new ArrayList<Peer>();
                            if(!src.hasFile)
                            {
                                interestedPeers.sort(new sortInterestedPeers());
                            }
                            Iterator<Peer> itr = interestedPeers.iterator();
                            int i = 0;
                            while(i < common.getNumOfPrefNeighbors() && itr.hasNext())
                            {
                                Peer temp = itr.next();
                                temp.getConnectionHandler().resetPieces();
                                kNPrefNeighborsPeers.add(temp);
                                if(!temp.isUnChoked){
                                    System.out.println("in unchoke not optimistic");
                                    log.UnchokeMessage(temp.getId());
                                    unchoke(temp); // not optimistic
                                }
                                i++;
                            }

                            System.out.println("reached end  of while");
                            prefPeers = new ArrayList<Integer>();
                            for (Peer kNPrefNeighborsPeer : kNPrefNeighborsPeers) {
                                prefPeers.add(kNPrefNeighborsPeer.getId());
                            }

                            choke();
                        }
                    }
                    Thread.sleep(time * 1000);
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    private void unchokeOptimistic()
    {
        Common common = Common.readCommonFile("Common.cfg");
        long time = common.getUnchokingInterval();

        new Thread(() -> {

            try
            {
                while(true){
                    synchronized (this)
                    {

                        Random random =  new Random();
                        System.out.println("Interested: " + interestedPeers.size());
                        if(!interestedPeers.isEmpty())
                        {
                            ArrayList<Peer> chokedPeers = new ArrayList<>();

                            for(Peer p : interestedPeers){
                                if(!p.isUnChoked){
                                    chokedPeers.add(p);
                                }
                            }
                            if(!chokedPeers.isEmpty()){
                                int index_of_choked = random.nextInt(chokedPeers.size());
                                Peer peer;
                                peer = interestedPeers.get(index_of_choked);
//                                while(!peer.isUnChoked){
//                                    index_of_choked = random.nextInt(interestedPeers.size());
//                                    peer = interestedPeers.get(index_of_choked);
//                                }
                                optUnchokedPeer = peer;
                                System.out.println("in unchoke optimistic");

                                if(!peer.isUnChoked) {
                                    unchoke(peer);
                                    log.optUnchokeMessage(peer.getId());

                                }
                            }

                        }
                    }
                    Thread.sleep(time * 1000);
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    private void unchoke(Peer peer)
    {
        peer.isUnChoked = true;
        Message unchokeMsg = new Message(Type.Unchoke);
        System.out.println("unchoke message length: " + unchokeMsg.getLength());
        peer.getConnectionHandler().sendMessage(unchokeMsg);
    }

    public void sendHave(int index){
        byte[] payload = ByteBuffer.allocate(4).putInt(index).array();
        Message message = new Message(Type.Have, payload);

        for(Peer p : peerProcess.allPeers.values()){
            if(p.getConnectionHandler() != null){
                p.getConnectionHandler().sendMessage(message);
            }
        }
    }
    private void choke()
    {
        for (Map.Entry<Integer, Peer> entry : peers.entrySet()) {
            Peer peer = entry.getValue();
            if (!kNPrefNeighborsPeers.contains(peer) && peer != optUnchokedPeer && peer.getConnectionHandler() != null) {
                peer.isUnChoked = false;
                Message chokeMsg = new Message(Type.Choke);
                peer.getConnectionHandler().sendMessage(chokeMsg);
            }
        }
    }

    public void run()
    {
        kPreferredNeighbors();
        unchokeOptimistic();
        System.out.println("ran all");
    }

}
