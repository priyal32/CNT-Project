import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Common {
    private final int numOfPrefNeighbors;
    private final int unchokingInterval;
    private final int optimisticUnChokingInterval;
    private final String fileName;
    private final int fileSize;
    private final int pieceSize;

    public Common(int numOfPrefNeighbors, int unchokingInterval,
                  int optimisticUnChokingInterval, String fileName,
                  int fileSize, int pieceSize)
    {
        this.numOfPrefNeighbors = numOfPrefNeighbors;
        this.unchokingInterval = unchokingInterval;
        this.optimisticUnChokingInterval = optimisticUnChokingInterval;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.pieceSize = pieceSize;
    }

    public static Common readCommonFile(String fileName)
    {
        File commonFile = new File(fileName);
        ArrayList<String> fileContents = new ArrayList<>();

        try
        {
            Scanner sc = new Scanner(commonFile);
            while(sc.hasNextLine())
            {
                String line = sc.nextLine();
                // split to get the actual content of the cfg
                String[] item = line.split(" ");
                fileContents.add(item[1]);
            }
            sc.close();

            return new Common(Integer.parseInt(fileContents.get(0)), Integer.parseInt(fileContents.get(1)),
                    Integer.parseInt(fileContents.get(2)), fileContents.get(3), Integer.parseInt(fileContents.get(4)),
                    Integer.parseInt(fileContents.get(5)));
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }

    public int getNumOfPrefNeighbors()
    {
        return numOfPrefNeighbors;
    }

    public int getUnchokingInterval(){
        return unchokingInterval;
    }

    public int getOptimisticUnChokingInterval()
    {
        return optimisticUnChokingInterval;
    }

    public int getFileSize()
    {
        return fileSize;
    }

    public int getPieceSize()
    {
        return pieceSize;
    }

    public String getFileName()
    {
        return fileName;
    }

}
