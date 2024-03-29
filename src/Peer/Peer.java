package Peer;

import Messages.Bitfield;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import static Peer.peerProcess.allPeers;
import static Peer.peerProcess.peersDone;

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
     int numPieces =0;

    public Boolean isInterested = false;
    public Boolean isUnChoked = false;
    private double downloadRate = 0;
    Boolean hasFile;
    ArrayList<Peer> beforePeers;
    Common common = Common.readCommonFile("Common.cfg");

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

        this.downloadRate  = (numPieces/(double)common.getUnchokingInterval());
        this.numPieces = numPieces;
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
            beforePeers.add(peer);
            allPeers.put(peer.getId(), peer);
            peersDone.add(peer);
        }
    }
    public double getDownloadSpeed() {
        return downloadRate;
    }


}
