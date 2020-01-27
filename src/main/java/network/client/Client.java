package network.client;

import game.Game;
import network.Datapacket;
import network.model.Message;
import network.threads.RWClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * The Client can send messages to the server and receive them from the serrver
 * @author Alexandro Steinert
 */

public class Client implements Runnable{

    private Thread One;
    private Thread Two;
    private final String serverIP;
    /**The Clients Username, also functions as an ID
     *
     */
    private final String username;
    /**The Clients socket to the server
     *
     */
    private Socket usr;

    /**The port
     *
     */
    private final int port;
    /**True if connected
     *
     */
    private boolean connected = true;

    private final Game game;
    private RWClient RWClientOutput;
    private RWClient RWClientInput;

    /**A Constructor that needs name and port
     * @param name Client name
     * @param port port of Serversocket
     *
     */
    public Client(String name, int port, Game g, String ServerIP){
        this.port = port;
        this.username = name;
        this.game = g;
        this.serverIP = ServerIP;
    }

    public void sendMessage(Message newMsg) {
        this.RWClientOutput.sendmsg(newMsg);
    }

    public void sendGameObject(Datapacket packet) { this.RWClientOutput.sendData(packet); }

    /**Connects to the server and makes 2 thread, one for sending and one for receiving messages
     *
     * @throws IOException
     */
    private void connect() throws IOException {
        usr = new Socket(serverIP,port);
        try {
            ObjectOutputStream oos = new  ObjectOutputStream( usr.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream( usr.getInputStream());

            while(connected) {
                this.RWClientOutput = new RWClient(usr,username, true, oos,this);
                this.RWClientInput = new RWClient(usr, username, false, ois, this);
                this.One = new Thread(this.RWClientOutput);
                this.Two =new Thread(this.RWClientInput);

                this.One.start();
                this.Two.start();
                One.join();
                Two.join();
            }
        }catch(Exception e){
            System.out.println("HI");
            e.printStackTrace();
        }


    }
    public void setConnected(Boolean connected){
        this.connected = connected;
    }

    public void printMessage(Message msg) {
        game.printMessage(msg);
    }

    @Override
    public void run() {
        try {
            connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Game getGame() { return game; }

}
