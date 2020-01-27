package exercises.server;

import exercises.client.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**Thread of the exercises.server
 * @author Alex Steinert
 */
class RWThread implements Runnable {
    /**Out of the exercises.server
     *
     */
    private final ObjectOutputStream out;
    /**
     * In of the exercises.server
     */
    private ObjectInputStream in;
    /**
     * User socket
     */
    private final Socket user;
    /**
     * Callback exercises.server, final
     */
    private final Server server;
    /**
     * Username
     */
    private  String username;

    /**Constructor RWThread
     *
     * @param socket socket user
     * @param server exercises.server callback
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
    //TODO Add a way to sync all user lists
    public void run(){
        try{
            in = new ObjectInputStream(user.getInputStream());

            while(true){
                Message input = (Message)in.readObject();
                if(input == null) return;
                if(username == null){
                    username = input.getSender();
                    server.addlist(new Tuple(username, out));
                }
                if(input.getMessage().equals("QUIT")) return;
                server.getMsg(input);

            }

        }catch (Exception e){
            e.printStackTrace();
        }finally{
            try {
                server.removelist(out);
                server.removelist(new Tuple(username, out));
                server.getMsg(new Message("exercises/server", "user " + username + " left your channel", "ALL"));
                user.close();

            }catch (IOException e){
                e.printStackTrace();
            }
        }



    }



}
