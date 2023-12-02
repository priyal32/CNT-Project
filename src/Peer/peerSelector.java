package Peer;

import Messages.Message;
import Messages.Type;

import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.util.*;

// Right now its the methods are in a while true loop but it should be checking
// TODO: whether the socket is closed or not since I think thats where the error is coming when socket.close() is used in PeerStarter
public class peerSelector extends Thread{
    Peer src;
    HashMap<Integer, Peer> peers;
    final ArrayList<Peer> interestedPeers = new ArrayList<Peer>();
    ArrayList<Integer> prefPeers = new ArrayList<Integer>();
    ArrayList<Peer> kNPrefNeighborsPeers = new ArrayList<Peer>();
    Peer optUnchokedPeer;
    Log log;
    Common common = Common.readCommonFile("Common.cfg");

    ServerSocket listener;

    public peerSelector(Peer src, HashMap<Integer, Peer> peers, Log log, ServerSocket listener)
    {
        this.src = src;
        this.peers = peers;
        this.log = log;
        this.listener = listener;

    }

    static class sortInterestedPeers implements Comparator<Peer>
    {
        public int compare(Peer a, Peer b)
        {
            Random r = new Random();
            if (a.getDownloadSpeed() == b.getDownloadSpeed())
            {
                return Integer.compare(r.nextInt(), r.nextInt());
            }
            return Integer.compare((int)a.getDownloadSpeed(), (int) b.getDownloadSpeed());
        }
    }

    public synchronized void kPreferredNeighbors()
    {


        long time = common.getUnchokingInterval();

        Thread kPref = new Thread(() -> {
            try
            {
                while (!PeerStarter.shouldStopThreads && !Thread.currentThread().isInterrupted()){
                    synchronized (interestedPeers)
                    {
                        if(!interestedPeers.isEmpty())
                        {
                            kNPrefNeighborsPeers = new ArrayList<Peer>();
                            HashSet<Integer> indexes = new HashSet<>();

                            // if the src peer has complete file then choose peers randomly
                            if(src.hasFile){
                                Random random = new Random();
                                int temp = Math.min(common.getNumOfPrefNeighbors(), interestedPeers.size());
                                while(kNPrefNeighborsPeers.size() < temp){
                                    int randomIndice = random.nextInt(interestedPeers.size());

                                    if(!kNPrefNeighborsPeers.contains(interestedPeers.get(randomIndice))){
                                        kNPrefNeighborsPeers.add(interestedPeers.get(randomIndice));
                                        if(!interestedPeers.get(randomIndice).isUnChoked)
                                            unchoke(interestedPeers.get(randomIndice));
                                    }

                                }
                            }else{
                                // sort them by downloading rate
                                interestedPeers.sort(new sortInterestedPeers());
                                Iterator<Peer> itr = interestedPeers.iterator();
                                int i = 0;
                                while(i < common.getNumOfPrefNeighbors() && itr.hasNext())
                                {
                                    Peer temp = itr.next();
                                    temp.setRate(0);
                                    kNPrefNeighborsPeers.add(temp);
                                    if(!temp.isUnChoked){
                                        unchoke(temp); // not optimistic
                                    }
                                    i++;
                                }
                            }

                            prefPeers = new ArrayList<Integer>();
                            for (Peer kNPrefNeighborsPeer : kNPrefNeighborsPeers) {
                                prefPeers.add(kNPrefNeighborsPeer.getId());
                            }
                            log.changeOfPreferredNeighbors(kNPrefNeighborsPeers);
                            choke();
                        }
                    }
                    Thread.sleep(time * 1000);
                }

            }
            catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        });
        kPref.start();
        kPref.setName("Kpref Neighbors thread");
        peerProcess.threads.add(kPref);
    }

    private synchronized void unchokeOptimistic()
    {
        long time = common.getOptimisticUnChokingInterval();

        Thread unchokeOpt = new Thread(() -> {

            try
            {
                while(!PeerStarter.shouldStopThreads && !Thread.currentThread().isInterrupted()){
                    synchronized (interestedPeers)
                    {

                        Random random =  new Random();
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
                                optUnchokedPeer = peer;
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
                Thread.currentThread().interrupt();
            }
        });
        unchokeOpt.start();
        unchokeOpt.setName("Unchoke Optimistic thread");
        peerProcess.threads.add(unchokeOpt);
    }

    private synchronized void unchoke(Peer peer)
    {
        peer.isUnChoked = true;
        Message unchokeMsg = new Message(Type.Unchoke);
        if(peer.getConnectionHandler() != null && !PeerStarter.shouldStopThreads)
            peer.getConnectionHandler().sendMessage(unchokeMsg);
    }

    public synchronized void sendHave(int index) {


        for(Peer p : peerProcess.allPeers.values()){
            if(p.getConnectionHandler() != null){
                byte[] payload = ByteBuffer.allocate(4).putInt(index).array();
                Message message = new Message(Type.Have, payload);
                if(p.getConnectionHandler() != null)
                    p.getConnectionHandler().sendMessage(message);
            }
        }
    }
    private synchronized void choke()
    {

        for (Peer peer:interestedPeers) {
            if (!kNPrefNeighborsPeers.contains(peer) && peer != optUnchokedPeer && peer.getConnectionHandler() != null) {
                if(peer.isUnChoked){
                    peer.isUnChoked = false;
                    Message chokeMsg = new Message(Type.Choke);
                    if(peer.getConnectionHandler() != null && !PeerStarter.shouldStopThreads)
                        peer.getConnectionHandler().sendMessage(chokeMsg);
                }

            }
        }
    }

    public void run()
    {
        kPreferredNeighbors();
        unchokeOptimistic();
    }

}
