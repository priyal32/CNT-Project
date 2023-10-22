import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ReadCommon {

    int prefferedNeighbors;
    int unchokingIntervals;
    int optimisticUnchokingInterval;
    String fileName;
    int fileSize;
    int pieceSize;

    public ReadCommon( int prefferedNeighbors, int unchokingIntervals, int optimisticUnchokingInterval, String fileName, int fileSize, int pieceSize) {
        this.prefferedNeighbors = prefferedNeighbors;
        this.unchokingIntervals = unchokingIntervals;
        this.optimisticUnchokingInterval = optimisticUnchokingInterval;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.pieceSize = pieceSize;
    }

    public int getPrefferedNeighbors() { return prefferedNeighbors; }

    public int getUnchokingIntervals() { return unchokingIntervals; }

    public int getOptimisticUnchokingInterval() { return optimisticUnchokingInterval; }

    public String getFileName() { return fileName; }

    public int getFileSize() { return fileSize; }

    public int getPieceSize() { return pieceSize; }

    public void readCommonFile(String filename) {
        File file = new File("Common.cfg");
        int prefferedNeighbors;
        int unchokingIntervals;
        int optimisticUnchokingInterval;
        String fileName;
        int fileSize;
        int pieceSize;
        ArrayList<String> network  = new ArrayList<String>();

        try {

            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String i = sc.nextLine();
                String [] content = i.split(" ");
                network.add(content[1]);
            }
            sc.close();
            
            for(int i = 0; i < network.size(); i++) {
                System.out.println(network.get(i));
            }

            prefferedNeighbors = network.get(0);
            unchokingIntervals = network.get(1);
            optimisticUnchokingInterval = network.get(2);
            fileName = network.get(3);
            fileSize = network.get(4);
            pieceSize = network.get(5);
            
            ReadCommon common = ReadCommon(prefferedNeighbors, unchokingIntervals, optimisticUnchokingInterval, fileName, fileSize, pieceSize);

        } 
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("hello");
    }

}