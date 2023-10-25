import java.nio.ByteBuffer;

public class Piece extends Message
{
    int index;
    byte[] filePiece;
    int peerID;
    int Present = 0;

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public byte[] getFilePiece()
    {
        return filePiece;
    }

    public void setFilePiece(byte[] filePiece)
    {
        this.filePiece = filePiece;
    }

    public int getPeerID()
    {
        return peerID;
    }

    public void setPeerID(int peerID)
    {
        this.peerID = peerID;
    }

    public int isPresent()
    {
        return Present;
    }

    public void setPresent(int present)
    {
        Present = present;
    }


    public Piece()
    {
        super(Type.Piece);
        Common common = Common.readCommonFile("Common.cfg");
        filePiece = new byte[common.getPieceSize()];
        index = -1;
        Present = 0;
        peerID = -1;

    }

    public Piece decodePiece(byte[] payload)
    {
        if(payload.length < 4)
        {
            return null;
        }
        // extract first 4 bytes as that is message length
        byte[] byteIndex = new byte[4];
        Piece piece = new Piece();

        int value = 0;
        ByteBuffer buffer = ByteBuffer.wrap(payload, 0, 4);
        value = buffer.getInt();
        piece.index = value;
        piece.filePiece = new byte[payload.length - 4];

        // copy remaining pieces into the actual filepiece
        System.arraycopy(payload, 4, piece.filePiece, 0, payload.length-4);
        return piece;
    }
}
