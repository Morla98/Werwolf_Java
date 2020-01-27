package exercises.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * The Client can send messages to the exercises.server and receive them from the serrver
 * @author Alexandro Steinert
 */

public class Client {

    /**The Clients Username, also functions as an ID
     *
     */
    protected final String username;
    /**The Clients socket to the exercises.server
     *
     */
    private Socket usr;
    /**A static list containing all usernames
     *
     */
    private static final ArrayList<String> names = new ArrayList<>();
    /**The port
     *
     */
    protected final int port;
    /**True if connected
     *
     */
    protected boolean connected = true;

    /**A Constructor that needs name and port
     * @param name Client name
     * @param port port of Serversocket
     *
     */
    protected Client(String name, int port){
        this.port = port;
        this.username = name;
        addName(name);
    }

    /**Second Constructor, only takes the port and asks for a name
     *
     * @param port the serversocket port
     */

    /**Connects to the exercises.server and makes 2 thread, one for sending and one for receiving messages
     *
     * @throws IOException
     */
    private void connect() throws IOException {

        usr = new Socket("localhost",port); // TODO : localhost for now
        System.out.println("Type a Message (@username message for whisper and QUIT for Quit)");
        try {
            ObjectOutputStream oos = new  ObjectOutputStream( usr.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream( usr.getInputStream());

            while(connected) {
                Thread One = new Thread(new RWClient(usr,username, true, oos,this));
                Thread Two =new Thread(new RWClient(usr, username, false, ois,this));

                One.start();
                Two.start();
                One.join();
                Two.join();
            }
        }catch(Exception e){
            System.out.println("HI");
            e.printStackTrace();
        }
    }
    /**Adds a name to the name list of the local exercises.client name list
     *
     * @param n Name to be added
     */
    private void addName(String n){
        names.add(n);
    }
    public void setConnected(Boolean connected){
        this.connected = connected;
    }

    /**Removes a name from the local exercises.client name list
     *
     * @param n Name to be removed
     */
    public void removeName(String n){
        names.remove(n);
    }

    /**Checks the local name list if a name is available
     *
     * @param n The name to be checked
     * @return a boolean, true if available
     */
    public boolean isAvailable(String n){
        if(n == null) return false;
        for(String m : names){
            if(n.equals(m)) return false;
        }

        return true;
    }

}
