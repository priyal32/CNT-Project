package Messages;

import Peer.Common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Message implements Serializable {
    Common commonfile = Common.readCommonFile("Common.cfg");
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
    public Type getType() {
        return type;
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
        out.flush();
    }
    int length;
    Type type;
    byte[] payload;
    public void setType(Type type) {
        this.type = type;
    }

}
