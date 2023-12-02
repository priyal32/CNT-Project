package Messages;

import Peer.Common;

public class Bitfield extends Message{
    public int numPieces;
    public Piece[] pieces;

    public int totalPresentPieces = 0;
    Common commonfile = Common.readCommonFile("Common.cfg");

    public Bitfield()
    {
        super(Type.BitField);
        int numOfFiles = commonfile.getFileSize();
        int pieceSize = commonfile.getPieceSize();
        numPieces = (int)Math.ceil((double) numOfFiles / pieceSize);
        //System.out.println("Numpieces: " + numPieces);
        pieces = new Piece[numPieces];

        // initialize each piece
        initializePieces(numPieces);
    }

    public void initializePieces(int numPieces){
        for(int i = 0; i < numPieces; i++){
            pieces[i] = new Piece();
        }
    }

    public synchronized void setPiece(int index){

        pieces[index].setPresent(1);
        totalPresentPieces++;
    }

    public synchronized boolean checkPiecesFilled(){
        for(Piece piece : pieces){
            if(piece.isPresent() == 0){
                return false;
            }
        }
        return true;
    }
    public Piece[] getPieces() {
        return pieces;
    }
    public static Bitfield decode(byte[] b){
        Bitfield bitfield = new Bitfield();
        for(int i = 0; i < bitfield.numPieces; i++){
            int index = i/8;
            int bitIndex = 7 - (i % 8);

            if(index < b.length){
                int bit = (b[index] >>  bitIndex) & 1;
                bitfield.pieces[i].setPresent(bit);

            }
        }
        return bitfield;
    }


    public synchronized byte[] getBytes(){
        int size = this.numPieces/8;
        if(numPieces % 8 != 0){
            size = size + 1;
        }
        byte[] bytes = new byte[size];
        int temp = 0;
        int count = 0;
        int n;

        for(n = 1; n <= this.numPieces; n++){
            int p = this.pieces[n - 1].isPresent();
            temp = temp << 1;
            if(p == 1){
                temp = temp + 1;
            }else{
                temp = temp + 0;
            }

            if(n!= 0 && n % 8 == 0){
                bytes[count] = (byte)temp;
                count++;
                temp = 0;
            }
        }

        if((n-1) % 8 != 0){
            int shift = (numPieces - (numPieces /8 ) * 8);
            temp = temp << (8 - shift);
            bytes[count] = (byte)temp;
        }

        return bytes;

    }

    public void initializeBitfield(int OwnPeerId, boolean hasFile) {

        if (!hasFile) {

            // If no file
            for (int i = 0; i < this.numPieces; i++) {
                this.pieces[i].setPresent(0);
                this.pieces[i].setPeerID(OwnPeerId);
            }

        } else {

            // If file
            for (int i = 0; i < this.numPieces; i++) {
                this.pieces[i].setPresent(1);
                this.pieces[i].setPeerID(OwnPeerId);
            }

        }
    }
}