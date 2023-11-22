package Peer;

import Messages.Message;
import Messages.Type;

import java.awt.*;
import java.net.ServerSocket;
import java.util.*;

public class peerSelector extends Thread{
    Peer src;
    HashMap<Integer, Peer> peers;
    ArrayList<Peer> interestedPeers = new ArrayList<Peer>();
    ArrayList<Peer> kNPrefNeighborsPeers = new ArrayList<Peer>();
    Peer optUnchokedPeer;

    public peerSelector(Peer src, HashMap<Integer, Peer> peers)
    {
        this.src = src;
        this.peers = peers;
    }

    class sortInterestedPeers implements Comparator<Peer>
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
        Common common = Common.readCommonFile("Common.cfg");
        long time = common.getUnchokingInterval();

        new Thread(() -> {
            try
            {
                synchronized (interestedPeers)
                {
                    if(interestedPeers.size() != 0)
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
                                unchoke(temp); // not optimistic
                            }
                            i++;
                        }

                        ArrayList<Integer> prefPeers = new ArrayList<Integer>();
                        for(int j = 0; j < kNPrefNeighborsPeers.size(); j++){
                            prefPeers.add(kNPrefNeighborsPeers.get(j).getId());
                        }
                        // TODO LOG IT
                        choke();
                    }
                }
                Thread.sleep(time * 1000);
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
                synchronized (interestedPeers)
                {
                    Random random =  new Random();
                    System.out.println("Interested: " + interestedPeers.size());
                    if(interestedPeers.size() != 0)
                    {
                        int index_of_choked = random.nextInt(0,interestedPeers.size());
                        Peer peer;
                        peer = interestedPeers.get(index_of_choked);
                        while(!peer.isUnChoked){
                            index_of_choked = random.nextInt(interestedPeers.size());
                            peer = interestedPeers.get(index_of_choked);
                        }
                        optUnchokedPeer = peer;
                        unchoke(peer);
                        //TODO: LOGGER
                    }
                }
                Thread.sleep(time * 1000);
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
        peer.getConnectionHandler().sendMessage(unchokeMsg);
        //TODO LOGGER
    }

    private void choke()
    {
        Iterator<Map.Entry<Integer,Peer>> itr = peers.entrySet().iterator();
        while(itr.hasNext())
        {
            Map.Entry<Integer, Peer> entry = itr.next();
            Peer peer = entry.getValue();
            if(!kNPrefNeighborsPeers.contains(peer) && peer != optUnchokedPeer && peer.getConnectionHandler() != null)
            {
                peer.isUnChoked = false;
                Message chokeMsg = new Message(Type.Choke);
                peer.getConnectionHandler().sendMessage(chokeMsg);
                // TODO logger
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
