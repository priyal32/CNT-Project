package Peer;

import Messages.Bitfield;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import static Peer.peerProcess.allPeers;

public  class Peer {

     int id;

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    boolean up = false;

     String host;
     int port;

    public Boolean isInterested = false;
    public Boolean isUnChoked = false;
    private int downloadRate = 0;
    Boolean hasFile;
    ArrayList<Peer> beforePeers;

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    public void setConnectionHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    ConnectionHandler connectionHandler;


    public Bitfield getBitfield() {
        return bitfield;
    }

    public void setBitfield(Bitfield bitfield) {
        this.bitfield = bitfield;
    }

    Bitfield bitfield;
    public Peer() {
        readPeerFile("localPeerInfo.cfg");
    }
    public Peer(int id, String host, int port, Boolean hasFile, ArrayList<Peer> beforePeers) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.hasFile = hasFile;
        this.beforePeers = beforePeers;
        bitfield = new Bitfield();
    }

    public ArrayList<Peer> getBeforePeers(){
        return beforePeers;
    }


    public int getId() { return id; }

    public String getHost() { return host; }

    public int getPort() { return port; }

    public Boolean haveFile() { return hasFile; }

    public void setRate(int numPieces){
        Common common = Common.readCommonFile("Common.cfg");
        this.downloadRate  = (int) (numPieces/(double)common.getUnchokingInterval());
    }

    public Boolean getIsInterested(){
        return isInterested;
    }
    public void setRate(Boolean isInterested){
        this.isInterested = isInterested;
    }

    public int getRate(){
        return downloadRate;
    }

    public void readPeerFile(String filename) {
        ClassLoader classLoader = peerProcess.class.getClassLoader();

        // Load the file using the class loader
        InputStream file = classLoader.getResourceAsStream(filename);
        int id;
        String host;
        int port;
        boolean hasFile;
        ArrayList<Peer> beforePeers  = new ArrayList<Peer>();
        ArrayList<String []> students  = new ArrayList<String []>();
        ArrayList<Peer> peers  = new ArrayList<Peer>();

        Scanner sc = new Scanner(file);

        while (sc.hasNextLine()) {
            String i = sc.nextLine();
            String [] oneStudent= i.split(" ");
            students.add(oneStudent);
        }
        sc.close();
        for (int i = 0; i < students.size(); i++) {
            id = Integer.parseInt(students.get(i)[0]);
            host = students.get(i)[1];
            port = Integer.parseInt(students.get(i)[2]);
            if (Integer.parseInt(students.get(i)[3]) == 1) {
                hasFile = true;
            } else {
                hasFile = false;
            }

            Peer peer = new Peer(id, host, port, hasFile, new ArrayList<>(beforePeers));
            Bitfield bitfield1 = new Bitfield();
            bitfield1.initializeBitfield(peer.getId() ,peer.haveFile());
            peer.setBitfield(bitfield1);
            //peer.getBitfield().printPieces(peer.getBitfield().getPieces());
           //System.out.println(peer.getId());

           //  peer.getBitfield().printBytes(peer.getBitfield().getBytes());
           // System.out.println("next");
            beforePeers.add(peer);
            allPeers.put(peer.getId(), peer);
        }
    }
    public void run(){

    }

    public int getDownloadSpeed() {
        // TODO uh yeah
        return 0;
    }


}
