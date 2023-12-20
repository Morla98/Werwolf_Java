package network.server;
import network.Datapacket;
import network.model.Message;
import network.model.Tuple;
import network.threads.RWThread;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * server, only invoke one on same IP and port
 * @author Alexandro Steiernt
 */

public class Server implements Runnable{


    private WerewolfgameServer gameServer;
    /**
     * saves a tuple of the Objectoutputstreams AND Username
     */
    private final ArrayList<Tuple> tulist = new ArrayList<>();
    /**
     * Port of the server
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
    public Server(int port){
        this.port = port;
    }

    /**
     * Starts up the server
     * @throws IOException
     */
    private void start() throws IOException {
        ServerSocket room = new ServerSocket(port);
        boolean gameStarted = false;
        while (true) {
            Socket clientrequest = room.accept();
            ObjectOutputStream out = new ObjectOutputStream(clientrequest.getOutputStream());
            Thread t =new Thread( new RWThread(clientrequest, this, out));
            t.start();
        }
    }
    /**Sends a message to clients
     * @param msg Message to be send
     * @throws IOException
     */
    public void sendMsg(Message msg) throws IOException {
        for(Tuple t : tulist) {
            if(msg.getReceiver().equals("ALL")){

                t.getOoi().flush();
                t.getOoi().writeObject(new Datapacket(1, null,msg, null, null));
            }
            if(msg.getReceiver().equals(t.getName())) {
                t.getOoi().reset();
                t.getOoi().flush();
                t.getOoi().writeObject(new Datapacket(1, null, msg, null, null));

            }
        }
    }

    /**
     * Sends game data to clients
     * @param data data to be send
     * @throws IOException
     */
    public void sendGameObject(Datapacket data) throws IOException {
        for(Tuple t : tulist) {
            if(data.getReceiver() == null){
                t.getOoi().flush();
                t.getOoi().reset();
                t.getOoi().writeObject(data);
            }
            else if(data.getReceiver().equals(t.getName())) {
                t.getOoi().flush();
                t.getOoi().reset();
                t.getOoi().writeObject(data);
            }

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

    /**
     * getter tuple lsit
     * @return
     */
    public ArrayList<Tuple> getTulist(){
        return tulist;
    }

    /**
     * getter, game logic server
     * @return
     */
    public WerewolfgameServer getGameServer(){ return gameServer; }

    /**
     * setter game logic server
     * @param game
     */
    public void setGameServer(WerewolfgameServer game) {this.gameServer = game; }

    /**
     * run
     */
    @Override
    public void run() {
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
