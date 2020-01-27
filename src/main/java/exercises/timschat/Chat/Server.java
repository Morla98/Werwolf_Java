package exercises.timschat.Chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author Tim Berger
 */

public class Server {
    private ServerSocket server;
    private Socket client;
    public ArrayList<PrintWriter> clientlist;


    private Server() {
        try {
            server = new ServerSocket(1997);
            System.out.println("Server gestartet!");
            clientlist = new ArrayList<>();

        } catch (IOException e) {
            System.out.print("Server konnte nicht gestartet werden!");
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void start() throws IOException {
        // Verbindung zum Server wird hergestellt und gehalten
        while(true){
            client = server.accept();

            PrintWriter pr = new PrintWriter(client.getOutputStream());
            clientlist.add(pr);

            Thread rwthread = new Thread(new RWThread(this, client));
            rwthread.start();
        }
    }

    public void printMsg(String s) {
        System.out.println(s);
    }


    public static void main(String args[]) throws IOException {
        Server s = new Server();
        s.start();
    }
}

