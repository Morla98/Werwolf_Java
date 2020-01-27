package exercises.timschat.Chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author Tim Berger
 */

class RWThread implements Runnable{
    private final Server server;
    private final Socket client;
    private BufferedReader reader;


    public RWThread(Server server, Socket client) throws IOException {
        this.server = server;
        this.client = client;
        reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                server.printMsg(message);
                sendMessage(message);
            }
        } catch (Exception e) {

        }
    }

    private void sendMessage(String message) {
        for(int i = 0; i < server.clientlist.size(); i++){
            server.clientlist.get(i).println(message);
            server.clientlist.get(i).flush();
        }
    }

}
