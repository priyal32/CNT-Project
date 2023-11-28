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
        System.out.println("Numpieces: " + numPieces);
        pieces = new Piece[numPieces];

        // initialize each piece
        initializePieces(numPieces);
    }

    public void initializePieces(int numPieces){
        for(int i = 0; i < numPieces; i++){
            pieces[i] = new Piece();
        }
    }

    public void setPiece(int index){

        pieces[index].setPresent(1);
        totalPresentPieces++;
    }

    public boolean checkPiecesFilled(){
        for(Piece piece : pieces){
            if(piece.isPresent() == 0){
                return false;
            }
        }
        return true;
    }
    public int getNumPieces() {
        return numPieces;
    }

    public void setNumPieces(int numPieces) {
        this.numPieces = numPieces;
    }

    public Piece[] getPieces() {
        return pieces;
    }

    public void setPieces(Piece[] pieces) {
        this.pieces = pieces;
    }

    public void printPieces(Piece[] pieces){
        for(Piece p : pieces){
            System.out.print(p.Present + " ");
        }
        System.out.println();
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


    public byte[] getBytes(){
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
//    public byte[] getBytes()
//    {
//        int s = this.numPieces / 8;
//        if (numPieces % 8 != 0)
//            s = s + 1;
//        byte[] iP = new byte[s];
//        int tempInt = 0;
//        int count = 0;
//        int Cnt;
//        for (Cnt = 1; Cnt <= this.numPieces; Cnt++)
//        {
//            int tempP = this.pieces[Cnt-1].isPresent();
//            tempInt = tempInt << 1;
//            if (tempP == 1)
//            {
//                tempInt = tempInt + 1;
//            } else
//                tempInt = tempInt + 0;
//
//            if (Cnt % 8 == 0 && Cnt!=0) {
//                iP[count] = (byte) tempInt;
//                count++;
//                tempInt = 0;
//            }
//
//        }
//        if ((Cnt-1) % 8 != 0)
//        {
//            int tempShift = ((numPieces) - (numPieces / 8) * 8);
//            tempInt = tempInt << (8 - tempShift);
//            iP[count] = (byte) tempInt;
//        }
//        return iP;
//    }

    public void printBytes(byte[] arr){
        for (byte currentByte : arr) {
            // Iterate through each bit in the byte (8 bits per byte)
            for (int bitPosition = 0; bitPosition < 8; bitPosition++) {
                // Extract the bit at the current position
                int bit = (currentByte >> bitPosition) & 1;
                System.out.print(bit); // Print the bit
            }
            System.out.print(" ");
        }
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