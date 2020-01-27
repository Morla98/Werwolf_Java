package exercises.server;


import exercises.client.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * exercises.server, only invoke one on same IP and port
 * @author Alexandro Steinert
 */

class Server {

    /**
     * Saves the Obectoutputstreams in a list
     */
    private final ArrayList< ObjectOutputStream> sender= new ArrayList<>();
    /**
     * saves a tuple of the Objectoutputstreams AND Username
     */
    private final ArrayList<Tuple> tulist = new ArrayList<>();
    /**
     * Port of the exercises.server
     */
    private final int port;

    /**Server Constructor without parameters, sets port on 1500
     *
     */
    public Server(){
        this.port = 1500;
    }

    /**Server Constructor with port input
     *
     * @param port between 0 and 5562(?)
     */
    private Server(int port){
        this.port = port;
    }

    /**
     * Starts up the exercises.server
     * @throws IOException
     */
    private void start() throws IOException {
        ServerSocket room = new ServerSocket(port);
        while (true) {
            Socket clientrequest = room.accept();
            ObjectOutputStream out = new ObjectOutputStream(clientrequest.getOutputStream());
            sender.add(out);
            Thread t = new Thread( new RWThread(clientrequest, this, out));
            t.start();

        }

    }


    /**Sends a message to every user in the Sender array
     *
     * @param msg Message to be send
     * @throws IOException
     */
    public void getMsg(Message msg) throws IOException {
        for(ObjectOutputStream w : sender) {
            w.writeObject(msg);
        }
    }

    /**
     * Adds a tuple to the tuple list
     * @param t tupple to be added
     */
    public void addlist(Tuple t){
        tulist.add(t);
    }

    /**Removes a tuple from the tuple list
     *
     * @param t tuple to be removed
     */
    public void removelist(Tuple t){
        tulist.remove(t);
    }

    /**Test main to start up a exercises.server with port 2001
     *
     * @param args
     */
    public static void main( String args[]){
        Server s = new Server(2001);
        try{
            s.start();

        }catch(Exception e){
            System.out.println("FUCK");
            e.printStackTrace();
        }
    }

    /**removes a ObjectOutputStream from the List
     *
     * @param out stream to be removed
     */
    public void removelist(ObjectOutputStream out){
        sender.remove(out);
    }


}
