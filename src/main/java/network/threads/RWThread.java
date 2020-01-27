package network.threads;

import models.Player;
import network.Datapacket;
import network.model.Message;
import network.model.Tuple;
import network.server.Server;
import network.server.WerewolfgameServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**Thread of the server
 * @author Alexandro Steinert
 */
public class RWThread implements Runnable {
    /**Out of the server
     *
     */
    private final transient ObjectOutputStream out;
    /**
     * In of the server
     */
    private transient ObjectInputStream in;
    /**
     * User socket
     */
    private final Socket user;
    /**
     * Callback server, final
     */
    private final Server server;
    /**
     * Username
     */
    private  String username;

    /**Constructor RWThread
     *
     * @param socket socket user
     * @param server server callback
     * @param out outputstream
     */
    public RWThread(Socket socket, Server server, ObjectOutputStream out){
        this.user = socket;
        this.out = out;
        this.server = server;
    }

    /**Run Override
     * @override
     */

    public void run(){
        try{
            in = new ObjectInputStream(user.getInputStream());
            while(true) {
                Object input = in.readObject();
                if(input == null) return;
                processdata(input);

            }

        }catch (Exception e){
            e.printStackTrace();
        }finally{
            try {
                for (Player p: server.getGameServer().getAlivePlayers()) {
                    if(p.getName().equals(username)) server.getGameServer().killPlayer(username, "has committed suicide");
                }
                server.getGameServer().checkWinConditions();
                server.removelist(new Tuple(username, out));
                server.sendMsg(new Message("Server","user " + username + " left your channel!", "ALL"));

                user.close();

            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private boolean nameUsed(Message handshake) throws IOException {
        for(int i = 0; i < server.getTulist().size(); i++){
            if(handshake.getSender().equals(server.getTulist().get(i).getName())){
                out.writeObject(new Datapacket(99, null, null, null, null));
                return true;
            }
        }
        return false;
    }


    private void processdata(Object in) throws IOException, InterruptedException {
        Datapacket d = (Datapacket) in;
        if(d.id == 0){
            Message g = d.getHandshake();
            if(nameUsed(g)) return;
            username  = g.getSender();
            server.addlist(new Tuple(g.getSender(), this.out));
            Datapacket host = new Datapacket(98, server.getTulist(), username);
            Datapacket join = new Datapacket(97, server.getTulist(), null);
            server.sendGameObject(host);
            server.sendGameObject(join);
            server.sendMsg(new Message("Server", username + " ist dem Spiel beigetreten!", "ALL"));
            if((server.getGameServer() == null) && (server.getTulist().size() == 7)){
                server.setGameServer(new WerewolfgameServer(server));
                Thread.sleep(1000);
                server.getGameServer().createGame();
            }
        }
        if(d.id == 1){
            Message f = d.getMsg();
            try {
                server.sendMsg(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(d.id == 2){
            server.getGameServer().dataProcess(d.getData());
        }
        if(d.id == 4){
            for(Player p: server.getGameServer().getWerewolves()) {
                server.sendMsg(new Message(d.getMsg().getSender(), d.getMsg().getMessage(), p.getName()));
            }
        }

        if(d.id == 6){}

        if(d.id == 88) {
            WerewolfgameServer.sentData = d.getData();
        }

    }




}
