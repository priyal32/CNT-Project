package Messages;

import Peer.Common;
import Peer.Peer;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class Manager {
    static HashSet<Integer> reqPiece = new HashSet<>();
    static Common common = Common.readCommonFile("Common.cfg");

    public static int numFilePieces = (int) Math.ceil(common.getFileSize() / (double) common.getPieceSize());
    public static int numAvailable = 0;
    public static String directory = null;
    public static String fileName = common.getFileName();
    public static int fileSize = common.getFileSize();
    public static File file = null;

    static Peer src;

    public Manager(Peer src, boolean hasFile) {
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

            if (bytesRead != pieceSize) {
                System.err.println("Error reading expected no. of bytes");
            }
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


    //    public static boolean[] convertByteToBoolean(byte val){
//        boolean[] booleans = new boolean[8];
//        for(int i = 0; i < 8; i++){
//            booleans[7 -i] = (val & 1) == 1 ? true : false;
//            val = (byte) (val >> 1);
//        }
//        return booleans;
//    }
    public static int requestFilePiece(Peer src, Peer dest) {
        int numPieces = (int) Math.ceil(common.getFileSize() / (double) common.getPieceSize());

        if (src.getBitfield().checkPiecesFilled()) {
            return -1;
        }
        Random random = new Random();
        int pieceToRequest = random.nextInt(numPieces);
        while (reqPiece.contains(pieceToRequest) || src.getBitfield().getPieces()[pieceToRequest].isPresent() == 1) {
            pieceToRequest = random.nextInt(numPieces);
        }
        reqPiece.add(pieceToRequest);
        return pieceToRequest;
    }
}

    //        byte[] interested = new byte[size];
//        boolean[] isInteresting = new boolean[numPieces];
//        int maxLength = numPieces;
//        int size = (int) Math.ceil(numPieces / (double) 8);
//        if (size > 1)
//            maxLength = numPieces % 8;
//
//
//        byte[] srcBitfield = src.getBitfield().getBytes();
//        byte[] destBitfield = dest.getBitfield().getBytes();

//
//        int s = 0;
//        int e = 0;
//        int  j = 0;
//        for(int i = 0; i < srcBitfield.length; i++) {
//            interested[i] = (byte) ((srcBitfield[i] ^ destBitfield[i]) & destBitfield[i]);
//            if (i == size - 1) {
//                s = 8 - maxLength;
//                e = maxLength;
//            } else {
//                s = 0;
//                e = 8;
//            }
//            boolean[] booleans = Manager.convertByteToBoolean(interested[i]);
//            System.arraycopy(booleans, s, isInteresting, j, e);
//            if (j + 8 < numPieces) {
//                j = j + 8;
//            } else {
//                j = numPieces - maxLength;
//            }
//
//            for (int m = 0; m < numPieces; m++) {
//                if (isInteresting[m] && !requestedPiece.containsKey(m)) {
//                    requestedPiece.put(m, m);
//                    return m;
//                }
//                m++;
//            }
//        }
//        //return  -1;
//
//    }


