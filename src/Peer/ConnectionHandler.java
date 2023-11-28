package Peer;

import Messages.*;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.*;

import static Peer.peerProcess.allPeers;

public class ConnectionHandler extends Thread {
    private final Socket connectionSocket;
    private final Peer src;
    private final Peer dest;
    private ObjectInputStream in;	//stream read from the socket
    private ObjectOutputStream out;    //stream write to the socket
    private peerSelector peerSelector;

    private int hasPieces = 0;
    Log log;

    public ConnectionHandler(Socket connectionSocket, Peer peer, Peer dest, ObjectOutputStream out, ObjectInputStream in,
                             Log log, peerSelector peerSelector) {
        this.connectionSocket = connectionSocket;
        this.src = peer;
        this.dest = dest;
        this.in = in;
        this.out = out;
        this.log = log;
        this.peerSelector = peerSelector;
    }


    public synchronized void sendMessage(Message message){
        try{
            message.writeMessage(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean compareBitfields(byte[] src, byte[] dest){
        if(src.length != dest.length){
            System.out.println("bitfields not equal in length!");
            return false;
        }

        for (int i = 0; i < src.length; i++) {
            // Iterate through each bit in the byte (8 bits per byte)
            for (int bitPosition = 0; bitPosition < 8; bitPosition++) {
                // Extract the bit at the current position
                int srcBit = (src[i] >> bitPosition) & 1;
                // System.out.println("srcbit: " + srcBit);
                int destBit = (dest[i] >> bitPosition) & 1;
                // System.out.println("destbit: " + destBit);
                if(destBit == 1 && srcBit == 0){
                    return true;
                }
            }
            // System.out.print(" ");
        }
        return false;
    }


    public int readIntFromStream() throws IOException {
        byte[] bytes = new byte[4];
        int b = in.read(bytes);
        if(b == 4){
            return ((bytes[0] & 0xFF) << 24 | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0XFF));
        }
        return -1;

    }

    public byte[] concat(byte[] one, byte[] two){
        int lenOne = one.length;
        int lenTwo = two.length;
        byte[] res =Arrays.copyOf(one, lenOne + lenTwo);
        System.arraycopy(two, 0, res, lenOne, lenTwo);
        return res;

    }


    // get messages from other peers and send messages based on them
    public synchronized void getMessage(){
        new Thread(()->{
            boolean isUnchoked = false;

            while (true){
                try {
                    int available = in.available();
                    if(available < 0)
                        continue;
                    int length = readIntFromStream();
                    if(length < 0)
                        continue;
                    System.out.println(length);
                    Type type = Type.valueOf(in.readByte());
                    System.out.println(type.name());

                    // if gotten unchoken message then the peer can request a piece if its interested (send piece message or send not interested message)
                    if(type == Type.Unchoke){
                        System.out.println("In unchoke switch statement");

                        isUnchoked = true;
                        log.UnchokeMessage(dest.getId());
                        sendRequest(Type.Unchoke);
                    }
                    // if choked then do nothing
                    else if(type == Type.Choke){
                        System.out.println("In choke switch statement");

                        log.ChokeMessage(dest.getId());
                        isUnchoked = false;
                    }
                    // if gotten interested message then add that peer to this peer's interested peers
                    else if (type == Type.Interested) {
                        System.out.println("In INTERESTED switch statement");

                        if(!peerSelector.interestedPeers.contains(dest))
                            peerSelector.interestedPeers.add(dest);
                        System.out.println("Interested Peers: " + peerSelector.interestedPeers.size());
                        log.interestedMessage(dest.getId());
                    }
                    // if gotten not interested message then remove that from this peers interested peers (if it was there)
                    else if (type == Type.NotInterested) {
                        System.out.println("In not interested statement");
                        peerSelector.interestedPeers.remove(dest);
                        log.notInterestedMessage(dest.getId());
                    }
                    // if gotten piece message then we need to put it in this peer's file
                    //  update bitfield
                    //  send have messages to all other connected peers so that they know this peers has this piece now

                    else if (type == Type.Piece) {
                        int contentLen = length - 1;
                        System.out.println("In Piece switch statement");

                        byte[] data = new byte[contentLen];
                        int bytesRead = 0;

                        while (bytesRead < contentLen) {
                            int chunckSize = in.read(data, bytesRead, contentLen - bytesRead);
                            if(chunckSize == -1){
                                System.err.println("Error: cannot read piece properly");
                            }

                            bytesRead += chunckSize;
                        }
                        int index = Manager.readIntFromStream(data);
                        if(src.getBitfield().pieces[index].isPresent() == 1){
                            continue;
                        }

                        Manager.store(data);

                        hasPieces++;
                        log.DownloadedPiece(dest.getId(), index, Manager.numAvailable);
                        peerSelector.sendHave(index);

                        if(src.getBitfield().checkPiecesFilled()){
                            log.fileDownloaded();
                        }

                        if(isUnchoked)
                            sendRequest(Type.Unchoke);

                        // if gotten bitfield message (only during beginning) then send "interested" or "not interested" message based on whether the other peer has interesting pieces


                    }else if (type == Type.BitField) {
                        System.out.println("In bitfield switch statement");
                        int contentLen = length - 1;

                        byte[] data = new byte[contentLen];
                        int bytesRead = 0;
                        while (bytesRead < contentLen) {
                            int chunckSize = in.read(data, bytesRead, contentLen - bytesRead);
                            if(chunckSize == -1){
                                System.err.println("Error: cannot read piece properly");
                            }

                            bytesRead += chunckSize;
                        }
                        //  log.writer.println(data.length);
                        byte[] srcBitfield = src.getBitfield().getBytes();
                        Bitfield destBitfield = Bitfield.decode(data);
                        dest.setBitfield(destBitfield);
//                        log.writer.println(dest.getId() + " " + "bitfield received");
//                        for(int i = 0; i < dest.getBitfield().numPieces; i++){
//                            if(dest.getBitfield().getPieces()[i].isPresent() == 1){
//                                System.out.println("Piece " +i + " is there");
//                            }else {
//                                log.writer.println("Piece " + i + " is not there");
//                            }
//                        }

                        if(compareBitfields(srcBitfield, dest.getBitfield().getBytes())){
                            Message msg = new Message(Type.Interested,null);
                            System.out.println("in interested if loop");
                            sendMessage(msg);
                        }else{
                            Message msg = new Message(Type.NotInterested,null);
                            System.out.println("in not interested if loop");
                            sendMessage(msg);
                        }

                    }

                    // if gotten request message  then send the piece  the other peer is requesting to that peer
                    else if (type == Type.Request) {
                        if(dest.isUnChoked){
                            System.out.println("In request switch statement");

                            int index = in.readInt();
                            int pieceIndex = Objects.requireNonNull(Manager.get(index)).getIndex();
                            System.out.println("piece index requested: " + pieceIndex);
                            byte[] pIndex = ByteBuffer.allocate(4).putInt(pieceIndex).array();

                            byte[] content = Objects.requireNonNull(Manager.get(index)).getFilePiece();
                            System.out.println("piece size " + content.length);
                            byte[] payload = concat(pIndex, content);

                            Message sendPiece = new Message(Type.Piece, payload);
                            sendMessage(sendPiece);
                        }

                    }

                    // if gotten have message then we need to update its bitfield and check again if it now has interesting pieces
                    else if (type == Type.Have) {
                        System.out.println("in have");
                        int index = in.readInt();
                        dest.getBitfield().setPiece(index);

                        //byte[] pIndex = ByteBuffer.allocate(4).putInt(index).array();
                        log.HaveMessage(dest.getId(), index);
                        if(src.getBitfield().pieces[index].isPresent() == 0){
                            System.out.println("SHOULD BE INTERESTED");
                            Message message = new Message(Type.Interested, null);
                            sendMessage(message);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
    }

    public void sendRequest(Type type){
        int pieces = dest.numPieces + 1;
        dest.setRate(pieces);

        System.out.println("In send request function statement");

        if(type == Type.Unchoke){
            int index = Manager.requestFilePiece(src, dest);
            System.out.println("Requesting piece of index " + index);

            if(index == -1){
                sendMessage(new Message(Type.NotInterested));
            }else{
                byte[] payload = ByteBuffer.allocate(4).putInt(index).array();
                Message msg = new Message(Type.Request, payload);
                sendMessage(msg);

            }
        }
    }
    @Override
    public void run() {
        new Manager(src);
        getMessage();
        dest.setRate(0);
    }

    public void resetPieces() {
        hasPieces = 0;
        // TODO yeah do it
    }
}
