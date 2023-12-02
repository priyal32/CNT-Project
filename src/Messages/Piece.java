package Messages;
import Peer.Common;

import java.nio.ByteBuffer;

public class Piece extends Message {
    int index;
    byte[] filePiece;
    int peerID;
    int Present = 0;
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public byte[] getFilePiece() {
        return filePiece;
    }
    public void setPeerID(int peerID) {
        this.peerID = peerID;
    }
    public int isPresent() {
        return Present;
    }
    public void setPresent(int present) {
        Present = present;
    }


    public Piece(){
        super(Type.Piece);
        Common common = Common.readCommonFile("Common.cfg");
        filePiece = new byte[common.getPieceSize()];
        index = -1;
        Present = 0;
        peerID = -1;

    }
    public Piece(byte[] content, int index){
        super();
        this.filePiece = content;
        this.index = index;
    }
}
