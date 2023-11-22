package Peer;

import Messages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ConnectionHandler extends Thread {
    private final Socket connectionSocket;
    private final Peer src;
    private final Peer dest;
    private ObjectInputStream in;	//stream read from the socket
    private ObjectOutputStream out;    //stream write to the socket

    Log log;

    public ConnectionHandler(Socket connectionSocket, Peer peer, Peer dest, ObjectOutputStream out, ObjectInputStream in, Log log) {
        this.connectionSocket = connectionSocket;
        this.src = peer;
        this.dest = dest;
        this.in = in;
        this.out = out;
        this.log = log;
    }


    public void sendMessage(Message message){
        try{
            message.writeMessage(out);
//            out.writeObject(message);
            out.flush();
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

    public void getMessage(){
        new Thread(()->{
            while (true){
                try {
                    int available = in.available();
                    if(available <= 0)
                        continue;
                    int length = in.readInt();
                    if(length < 0)
                        continue;
                    System.out.println(length);
                    Type type = Type.valueOf(in.readByte());

                    // TODO: Implement different protocols. Only have bitfield
                    if(type == Type.Unchoke){
                    }else if(type == Type.Choke){

                    }else if (type == Type.Interested) {
                        log.interestedMessage(dest.getId());

                    }else if (type == Type.NotInterested) {
                        log.notInterestedMessage(dest.getId());

                    }else if (type == Type.Piece) {

                    }else if (type == Type.BitField) {

                       // System.out.println(src.getId() + " received bitfield from " + dest.getId());
                        byte[] srcBitfield = src.getBitfield().getBytes();
                        src.getBitfield().printBytes(srcBitfield);
                        byte[] destBitfield = dest.getBitfield().getBytes();
                        dest.getBitfield().printBytes(destBitfield);
                        if(compareBitfields(srcBitfield, destBitfield)){
                            Message msg = new Message(Type.Interested,null);
                            System.out.println("in interested if loop");
                            sendMessage(msg);
                        }else{
                            Message msg = new Message(Type.NotInterested,null);
                            System.out.println("in not interested if loop");
                            sendMessage(msg);
                        }
//
//                        if(compareBitfields(destBitfield, srcBitfield)){
//                            Message msg = new Message(Type.Interested);
//                            sendMessage(msg);
//                            log.interestedMessage(src.getId());
//                        }else{
//                            log.notInterestedMessage(src.getId());
//                        }

//                        Message msg = new Message(Type.BitField, bitfield);
//                        sendMessage(msg);
                    }else if (type == Type.Request) {

                    }else if (type == Type.Have) {

                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
    }


    public void sendRequest(Type type){
        if(type == Type.Unchoke){
            Unchoke unchoke = new Unchoke();
            unchoke.requestFilePiece(src, dest);
        }
    }
    @Override
    public void run() {
        getMessage();
    }
}
