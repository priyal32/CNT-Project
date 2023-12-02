package Peer;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    public void ChokeMessage(int peerId1){
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
        writer.println(timeStamp + " Peer " + peerId + " is choked by " + peerId1);
        writer.flush();
    }


    public void optUnchokeMessage(int peerId1){
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
        writer.println(timeStamp + " Peer " + peerId + " has the optimistically-unchocked neighbor " + peerId1);
        writer.flush();
    }
    public void UnchokeMessage(int peerId1){
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
        writer.println(timeStamp + " Peer " + peerId + " is unchoked by " + peerId1);
        writer.flush();
    }

    public void HaveMessage(int peerId1, int index){
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
        writer.println(timeStamp + " Peer " + peerId + " received a 'have' message from " + peerId1  + " for the piece " + index);
        writer.flush();
    }

    public void DownloadedPiece(int peerId1, int index, int pieces){
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
        writer.println(timeStamp + " Peer " + peerId + " has downloaded the piece " + index  + " from " + peerId1 + ".\nNow the number of pieces it has is " + pieces);
        writer.flush();
    }

    public void fileDownloaded(){
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
        writer.println(timeStamp + " Peer " + peerId + " has downloaded the complete file.");
        writer.flush();
    }

    public void changeOfPreferredNeighbors(ArrayList<Peer> neighbors){
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
        writer.println(timeStamp + " Peer " + peerId + " has the preferred neighbors.");
        for(int i = 0; i < neighbors.size(); i++){
            writer.print(neighbors.get(i).id + " , ");
        }
        writer.println();
        writer.flush();
    }
}
