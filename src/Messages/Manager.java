package Messages;

import Peer.Common;
import Peer.Peer;

import java.io.*;
import java.util.*;

public class Manager {
    static HashSet<Integer> reqPiece = new HashSet<>();
    static Common common = Common.readCommonFile("Common.cfg");

    public static int numFilePieces = (int) Math.ceil(common.getFileSize() / (double) common.getPieceSize());
    public static int numAvailable = 0;
    public static String directory = null;
    public static String fileName = common.getFileName();
    public static int fileSize = common.getFileSize();
    public static File file = null;
    static int numPieces = (int) Math.ceil(common.getFileSize() / (double) common.getPieceSize());

    static Peer src;

    public Manager(Peer src) {
        directory = "peer_" + src.getId() + "/";
        Manager.src = src;

        if (src.haveFile()) {
            numAvailable = numFilePieces;
        }

        File folder = new File(directory);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        file = new File(directory + fileName);
        if (!file.exists()) {
            try {
                FileOutputStream fileOutput = new FileOutputStream(file);
                fileOutput.write(new byte[fileSize]);
                fileOutput.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Piece get(int index) {
        System.out.println("in get piece function");

        try {
            FileInputStream fileInput = new FileInputStream(file);
            int pieceLocation = common.getPieceSize() * index;
            fileInput.skip(pieceLocation);
            int pieceSize = common.getPieceSize();
            if (fileSize - pieceLocation < pieceSize) {
                pieceSize = fileSize - pieceLocation;
            }
            byte[] fileContent = new byte[pieceSize];
            int bytesRead = fileInput.read(fileContent);
            fileInput.close();
            return new Piece(fileContent, index);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public static int readIntFromStream(byte[] bytes) throws IOException {

        return ((bytes[0] & 0xFF) << 24 | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0XFF));


    }

    public static void store(byte[] payload) throws Exception {

        System.out.println("in store function");
        int index = readIntFromStream(payload);

        byte[] content = new byte[payload.length - 4];
        System.arraycopy(payload, 4, content, 0, content.length);

        int loc = common.getPieceSize() * index;
        RandomAccessFile fos = null;
        try {
            fos = new RandomAccessFile(file, "rw");
            fos.seek(loc);
            fos.write(content);
            fos.close();
            numAvailable++;

            src.getBitfield().setPiece(index);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static int requestFilePiece(Peer src, Peer dest) {

        if (src.getBitfield().checkPiecesFilled()) {
            return -1;
        }
        List<Integer> availablePieces = new ArrayList<>();
        for(int i = 0; i < numPieces; i++){
            if (src.getBitfield().getPieces()[i].isPresent() == 0 && dest.getBitfield().getPieces()[i].isPresent() == 1) {
                availablePieces.add(i);
            }
        }
        if(availablePieces.isEmpty()){
            return -1;
        }
        Random random = new Random();
        int Index = random.nextInt(availablePieces.size());
        int pieceToRequest = availablePieces.get(Index);
        reqPiece.add(pieceToRequest);
        return pieceToRequest;
    }
}