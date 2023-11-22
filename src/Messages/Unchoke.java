package Messages;
import Peer.Peer;
public class Unchoke extends Message {
    public Unchoke() {
        super(Type.Unchoke);
    }

    public int requestFilePiece(Peer src, Peer dest){
        int size = (int) Math.ceil(numPieces/(double)8);
        byte[] interested = new byte[size];
        boolean[] isInteresting = new boolean[numPieces];

        byte[] srcBitfield = src.getBitfield().getBytes();
        byte[] destBitfield = dest.getBitfield().getBytes();

        for(int i = 0; i < srcBitfield.length; i++){
            interested[i] = (byte) (srcBitfield[i] ^ destBitfield[i]);
        }

        for(int i = 0; i < interested.length; i++){
            for(int j = 0; j < 8; j++){
                isInteresting[i * 8 + j] = ((interested[i] >> (7 - j) & 1) == 1);
            }
        }
           for(int i = 0; i < numPieces; i++){
            if(isInteresting[i]){
                return i;
            }
        }
        return  -1;


    }
}
