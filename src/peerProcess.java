import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class peerProcess {

    int peerID = -1;

    // takes in peer id of the peer to start with
    public static void main(String[] args) throws IOException {
        peerProcess p2p = new peerProcess();

        p2p.peerID = Integer.parseInt(args[0]);
        Peer currPeer = null;

        // get port of that peer id as the peer will listen to that port
        ArrayList<Peer> peerInfo = readPeerFile("localPeerInfo.cfg");
        peerServer server = null;
        for(Peer peer : peerInfo){
            if(peer.getId() == p2p.peerID){
                currPeer = peer;
                server = new peerServer(peer);
            }
        }

        // start server thread for the peer that's been inputted
        Thread peerServerThread = new Thread(server);
        peerServerThread.start();

        // establish connections to all previous peers i.e make client threads to those peers
        assert currPeer != null;
        for(Peer b : currPeer.getBeforePeers()) {
            peerClient pc = new peerClient(currPeer,b);
            Thread pcThread = new Thread(pc);
            pcThread.start();
        }
    }

    public static ArrayList<Peer> readPeerFile(String filename) {
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
            beforePeers.add(peer);
            peers.add(peer);
        }
        return peers;
    }
}
