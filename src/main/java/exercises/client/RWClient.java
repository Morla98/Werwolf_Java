package exercises.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**The Client read write thread
 *@author Alexandro Steinert
 */
public class RWClient implements Runnable {
    /**The socket of the specifiedd user
     *
     */
    private Socket usr;
    /**
     * The username
     */
    private String username;
    /**
     * The callback to the exercises.client
     */
    private Client client;
    /**
     * ObjectOutputstream of the exercises.client
     */
    protected ObjectOutputStream oos;
    /**
     * ObjectInputStream of the Client
     */
    protected ObjectInputStream ois;
    /**
     * Boolean that indicates read or write, true if write falls if read, do not change
     */
    protected boolean t;
    /**
     * Boolean connected
     */
    protected boolean connected = true;

    protected RWClient(){super();} // Default Constructor

    /**RWClient constructor for write
     *
     * @param usr socket of the user
     * @param name name of the user
     * @param t ONLY SET TRUE
     * @param oos ObjectOutputStream of the User
     * @param cl Callback Client
     */
    protected RWClient(Socket usr, String name, boolean t, ObjectOutputStream oos, Client cl){
        this.usr = usr;
        this.username = name;
        this.t = t;
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
    protected RWClient(Socket usr, String name, boolean t, ObjectInputStream ois, Client cl){
        this.usr = usr;
        this.username = name;
        this.t = t;
        this.client = cl;
        this.ois = ois;

    }

    /**run override
     * @override
     */
    public void run(){
        while(connected) if (!t) {
            Object o = null;
            try {
                o = ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            if(o == null) return;
            displayMsg(o);
        } else {
            Message msg  = getMessage();
            if(msg != null) try {
                oos.writeObject(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**Displays message on screen
     *
     * @param o received Object, needs to be a Message
     */
    //TODO Overload for GUI use
    protected void displayMsg(Object o) {
        Message msg  = (Message) o;
        /*
        if(exercises.client.isAvailable(msg.getReceiver())) exercises.client.addName(msg.getReceiver());
        if(exercises.client.isAvailable(msg.getSender())) exercises.client.addName(msg.getSender());
        */
        if(msg.getReceiver().equals(username) ){
            System.out.println("Whisper from " + msg.getSender() + ": " + msg.getMessage());
        }else if(msg.getReceiver().equals("ALL")){
            System.out.println(msg.getSender() + ": " + msg.getMessage());
        }

    }

    /**Returns the Message the user wants to send
     *
     * @return the MEssage the user wants to send
     */
    //TODO Overload for GUI use
    protected Message getMessage() {
        Scanner scan = new Scanner(System.in);
        String receiver = "ALL";
        String message = "Hi";
        String unprocessed = scan.nextLine().trim();
        if( unprocessed.startsWith("@") ) {
            receiver = unprocessed.substring(1, unprocessed.indexOf(" "));
            if(!client.isAvailable(receiver)){ System.out.println("false name"); return null;}
            message = unprocessed.substring(unprocessed.indexOf(" "));
        }else if (unprocessed.equals("QUIT")){
            //TODO IMPLEMENT it right so that no error message comes up
            connected = false;
            client.setConnected(false);
            client.removeName(username);
            return new Message(username, "QUIT", "ALL");
        }else{
            message = unprocessed;
        }
        return new Message(username,message,receiver);
    }

    /**Uses the Message gotten by getMessage and sends over the ObjectOutputStream to the Server
     *
     * @param oos ObjectOutputStream to Server
     */
    public void sendmsg(ObjectOutputStream oos){
        Message mes = getMessage();
        try {
            oos.writeObject(mes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**Gets a message from The Objectinputstream, that is to be displayed for the user
     *
     * @param ois ObjectINputStream
     */
    public void getmsg(ObjectInputStream ois){
        Object o = null;
        try {
            o = ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(o == null) return;
        displayMsg(o);
    }


}
