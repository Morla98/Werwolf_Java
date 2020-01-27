package network;


import network.model.Message;
import network.model.Tuple;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Custom Class that serves as the main method for the server and Client to send an receive Data
 * @author Alexandro Steinert
 */

public class Datapacket implements Serializable{
    public final int id;
    /**
     * The Handshake to join a server, id 0
     */
    private Message handshake;
    /**
     * The msg, used for chat messages, id 1
     */
    private Message msg;
    private Data data;
    private final String receiver;
    /**
     * a list containing all clients
     */
    private ArrayList<Tuple> tulist;

    /**
     * constructor
     * @param id id
     * @param h handshake
     * @param m message
     * @param data data
     * @param rec receiver
     */
    public Datapacket(int id, Message h, Message m, Data data, String rec){
        this.id = id;
        this.handshake = h;
        this.msg = m;
        this.data = data;
        this.receiver = rec;
    }

    /**
     * Another Constructor
     * @param id id
     * @param tulist list of clients
     * @param rec receiver
     */
    public Datapacket(int id, ArrayList<Tuple> tulist, String rec) {
        this.id = id;
        this.tulist = tulist;
        this.receiver = rec;
    }


    /**
     * Handshake getter
     * @return
     */
    public Message getHandshake() {
        return handshake;
    }

    /**
     * Message getter
     * @return
     */
    public Message getMsg() {
        return msg;
    }

    public Data getData(){ return data; }

    /**
     * Receiver getter
     * @return
     */
    public String getReceiver(){
        return receiver;
    }

    /**
     * Tuple list getter
     * @return
     */
    public ArrayList<Tuple> getTulist(){ return tulist; }
}