package network.threads;

import network.Datapacket;
import network.client.Client;
import network.model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**The Client read write thread
 * @author Alexandro Steinert
 */
public class RWClient implements Runnable {
    /**The socket of the specifiedd user
     *
     */
    private final Socket usr;
    /**
     * The username
     */
    private final String username;
    /**
     * The callback to the client
     */
    private final Client client;
    /**
     * ObjectOutputstream of the client
     */
    private transient ObjectOutputStream oos;
    /**
     * ObjectInputStream of the Client
     */
    private transient ObjectInputStream ois;
    /**
     * Boolean that indicates read or write, true if write falls if read, do not change
     */
    private final boolean write;
    /**
     * Boolean connected
     */
    private final boolean connected = true;

    /**RWClient constructor for write
     *
     * @param usr socket of the user
     * @param name name of the user
     * @param t ONLY SET TRUE
     * @param oos ObjectOutputStream of the User
     * @param cl Callback Client
     */
    public RWClient(Socket usr, String name, boolean t, ObjectOutputStream oos, Client cl){
        this.usr = usr;
        this.username = name;
        this.write = t;
        this.client = cl;
        this.oos = oos;
    }

    /**RwClient Constructor for read
     *
     * @param usr socket of the user
     * @param name name of the user
     * @param t ONLY SET FALSE
     * @param ois ObjectInputStream of the USer
     * @param cl Callback Client
     */
    public RWClient(Socket usr, String name, boolean t, ObjectInputStream ois, Client cl){
        this.usr = usr;
        this.username = name;
        this.write = t;
        this.client = cl;
        this.ois = ois;
    }

    /**run override
     * @override
     */
    public void run(){
        if(write){
            try {
                oos.writeObject(new Datapacket(0, new Message(username,"handshake","Server"), null, null, null));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        while(connected) if (!write) {
            Object o = null;
            try {
                o = ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            if(o == null) return;
            Datapacket packet = (Datapacket) o;
            if(packet.id == 1) displayMsg(o);
            if(packet.id == 2) client.getGame().getLogic().dataProcessing(packet.getData());
            if(packet.id == 99) client.getGame().connectionFailed();
            if(packet.id == 98) client.getGame().connectionSuccess(packet);
            if(packet.id == 97) client.getGame().updatePrepareScreen(packet);
            if(packet.id == 88) ;
        }
    }

    /**Displays message on screen
     *
     * @param o received Object, needs to be a Message
     */
    private void displayMsg(Object o) {
        Message msg = ((Datapacket)o).getMsg();
        client.printMessage(msg);
    }


    /**Uses the Message gotten by getMessage and sends over the ObjectOutputStream to the Server
     *
     * @param msg ObjectOutputStream to Server
     */
    public void sendmsg(Message msg){
        try {
            oos.writeObject(new Datapacket(1, null, msg,null, null));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends data to the server
     * @param packet
     */
    public void sendData(Datapacket packet){
        try {
            oos.flush();
            oos.reset();
            oos.writeObject(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
