package Peer;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Log {

    public PrintWriter writer;
    int peerId;
    public Log(int peerId) throws FileNotFoundException {
        this.peerId = peerId;
        writer = new PrintWriter("log_peer_ " + peerId + ".log");
    }

    public void connectTo(int peerId1){
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());

        writer.println(timeStamp + " Peer " + peerId + " makes a connection to Peer " + peerId1);
        writer.flush();
    }

    public void connectFrom(int peerId1){
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());

        writer.println(timeStamp + " Peer " + peerId + " is connected from Peer " + peerId1);
        writer.flush();
    }

    public void interestedMessage(int peerId1){
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());

        writer.println(timeStamp + " Peer " + peerId + " received a 'interested' message from " + peerId1);
        writer.flush();
    }

    public void notInterestedMessage(int peerId1){
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
        writer.println(timeStamp + " Peer " + peerId + " received a 'not interested' message from " + peerId1);
        writer.flush();
    }








}
