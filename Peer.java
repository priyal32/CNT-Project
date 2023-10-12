import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Peer {

    int id;
    String host;
    int port;
    Boolean hasFile;

    public Peer(int id, String host, int port, Boolean hasFile) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.hasFile = hasFile;
    }

    public int getId() { return id; }

    public String getHost() { return host; }

    public int getPort() { return port; }

    public Boolean haveFile() { return hasFile; }

    public ArrayList readFile(String filename) {
        File file = new File(filename);
        int id;
        String host;
        int port;
        Boolean hasFile;
        ArrayList<String []> students  = new ArrayList<String []>();
        ArrayList<Peer> peers  = new ArrayList<Peer>();

        try {

            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String i = sc.nextLine();
                String [] oneStudent= i.split(" ");
                students.add(oneStudent);
            }
            sc.close();
            for (int i = 0; i < students.size(); i++) {
                id = Integer.parseInt(students.get(i)[0]);
                host = students.get(i)[1];
                port = Integer.parseInt(students.get(i)[2]);
                if (Integer.parseInt(students.get(i)[3]) == 1) {
                    hasFile = true;
                } else {
                    hasFile = false;
                }
                Peer peer = Peer(id, host, port, hasFile);
                peers.add(peer);
            }
        } 
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
