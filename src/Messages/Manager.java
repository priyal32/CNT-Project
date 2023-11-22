package Messages;

import Peer.Common;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

public class Manager {
    public boolean[] isPieceOwned;
    HashMap<Integer, Integer> requestedPiece = new HashMap<Integer, Integer>();
    public int numFilePieces = (int) Math.ceil(Common.readCommonFile("Common.cfg").getFileSize() /
            Common.readCommonFile("Common.cfg").getPieceSize());
    public int numAvailable = 0;
    public String directory = null;
    public String fileName = Common.readCommonFile("Common.cfg").getFileName();
    public static int fileSize = Common.readCommonFile("Common.cfg").getFileSize();
    public static File file = null;

    public Manager(int id, boolean hasFile)
    {
        directory = "peer_" + id + "/";
        isPieceOwned = new boolean[numFilePieces];

        if(hasFile){
            Arrays.fill(isPieceOwned, true);
            numAvailable = numFilePieces;
        }

        File folder = new File(directory);
        if(!folder.exists()){
            folder.mkdirs();
        }

        file = new File(directory + fileName);
        if(!file.exists()){
            try{
                FileOutputStream fileOutput = new FileOutputStream(file);
                fileOutput.write(new byte[fileSize]);
                fileOutput.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Piece get(int index){
        try{
            FileInputStream fileInput = new FileInputStream(file);
            int pieceLocation = Common.readCommonFile("Common.cfg").getPieceSize() * index;
            fileInput.skip(pieceLocation);
            int pieceSize = Common.readCommonFile("Common.cfg").getPieceSize();
            if(fileSize - pieceLocation  < pieceSize){
                pieceSize = fileSize - pieceLocation;
            }
            byte[] fileContent = new byte[pieceSize];
            fileInput.read(fileContent);
            fileInput.close();
            return new Piece(fileContent,index);
        }
        catch (Exception e){
            return null;
        }


    }
}
