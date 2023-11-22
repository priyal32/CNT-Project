package Messages;

import Peer.Common;

public class Bitfield extends Message{
    public int numPieces;
    public Piece[] pieces;
    public Bitfield()
    {
        super(Type.BitField);
        Common commonfile = Common.readCommonFile("Common.cfg");
        int numOfFiles = commonfile.getFileSize();
        int pieceSize = commonfile.getPieceSize();
        numPieces = (int)Math.ceil((double) numOfFiles / pieceSize);
        pieces = new Piece[numPieces];

        // initialize each piece
        initializePieces(numPieces);
    }

    public void initializePieces(int numPieces){
        for(int i = 0; i < numPieces; i++){
            this.pieces[i] = new Piece();
        }
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
    public byte[] encode()
    {
        return this.getBytes();
    }

    public byte[] getBytes(){
        int s = (int) Math.ceil((double) this.numPieces / 8);

        byte[] arr = new byte[s];

        int b = 0;
        int i = 0;

        for(i = 0; i <  numPieces; i+=8){
            for(int j = 7; j >= 0; j--){
                if(i + j >= pieces.length){
                    arr[b] &= (byte) ~(1 << j);
                    continue;
                }
                if(pieces[i + j].isPresent() == 1){
                    arr[b] |= (byte) (1 << j);
                }else{
                    arr[b] &= (byte) ~(1 << j);
                }
            }
            b++;
        }
        /*int tempInt = 0;
        int count = 0;
        int i;
        for (i = 1; i <= this.numPieces; i++)
        {
            int tempP = this.pieces[i-1].Present;
            tempInt = (int) (tempInt << 1);
            if (tempP == 1)
            {
                tempInt = tempInt + 1;
            }

            if (i % 8 == 0) {
                arr[count] = (byte) tempInt;
                count++;
                tempInt = 0;
            }

        }
        if ((i-1) % 8 != 0)
        {
            int tempShift = ((numPieces) - (numPieces / 8) * 8);
            tempInt = tempInt << (8 - tempShift);
            arr[count] = (byte) tempInt;
        }*/
        return arr;
    }

    public void printBytes(byte[] arr){
        for (byte currentByte : arr) {
            // Iterate through each bit in the byte (8 bits per byte)
            for (int bitPosition = 0; bitPosition < 8; bitPosition++) {
                // Extract the bit at the current position
                int bit = (currentByte >> bitPosition) & 1;
              //  System.out.print(bit); // Print the bit
            }
            //System.out.print(" ");
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