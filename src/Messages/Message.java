package Messages;

import Peer.Common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Message implements Serializable {
    Common commonfile = Common.readCommonFile("Common.cfg");
    int numOfFiles = commonfile.getFileSize();
    int pieceSize = commonfile.getPieceSize();
    int numPieces = (int)Math.ceil((double) numOfFiles / pieceSize);
    int pieceIndex = 0;

    public Message(){

    }
    public Message(Type type){
        this (type, null);
    }

    // Messages. Message has payload then length is payload + 1 else is 0
    public Message(Type type, byte[] payload) {
        if(payload == null){
            length = 0;
        }else{
            length = payload.length + 1;
        }
        this.type = type;
        this.payload = payload;
    }

    public Message(Type type, byte[] payload, int index) {
        if(payload == null){
            length = 0;
        }else{
            length = payload.length + 1;
        }
        this.type = type;
        this.payload = payload;
        this.pieceIndex = index;
    }

    public Type getType() {
        return type;
    }

    // return Instance of Messages.Message Messages.Type
    public Message getMessageInstance( Type type) {
        if (type == Type.Choke) {
            return new Choke();
        } else if (type == Type.Unchoke) {
            return new Unchoke();
        } else if (type == Type.Interested) {
            return new Interested();
        } else if (type == Type.NotInterested) {
            return new Choke();
        } else if (type == Type.Have) {
            return new Choke();
        } else if (type == Type.BitField) {
            return new Choke();
        } else if (type == Type.Request) {
            return new Choke();
        } else if (type == Type.Piece) {
            return new Choke();
        }
        return null;
    }

    public void readMessage(ObjectInputStream in) throws IOException{
        if(this.type == Type.BitField){
            if(payload != null && payload.length > 0){
                System.out.println(in.read(payload, 0, payload.length));

            }

        }

    }

    public void writeMessage(ObjectOutputStream out) throws IOException{
        // write length of message in 4 bytes = int
        out.writeInt(length);
        // write type in one byte
        out.writeByte(type.getValue());
        // write payload
        if(payload != null && payload.length > 0){
            out.write(payload,0,payload.length);
        }
    }
    int length;
    Type type;
    byte[] payload;
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }


}
