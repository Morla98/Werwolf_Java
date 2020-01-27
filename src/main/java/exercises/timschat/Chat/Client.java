package exercises.timschat.Chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author Tim Berger
 */

class Client implements Runnable {
    private final String username;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    public Server server;


    private Client(String username) throws IOException {
        this.username = username;
        socket = new Socket("localhost", 1997);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream());
    }


    /* run class to run the exercises.client */
    private void sendMessage()  {
        Scanner scan = new Scanner(System.in);
        String message;
        while(true) {
            message = scan.nextLine();
            writer.println(username + ": " + message);
            writer.flush();
        }
    }

    @Override
    public void run() {
        String message;
        try {
            while (true) {
                while ((message = reader.readLine()) != null) {
                    System.out.println(message);
                }
            }
        } catch (Exception e){

        }
    }

    /* main method */
    public static void main(String args[]) throws IOException {
        Scanner scan = new Scanner(System.in);
        System.out.print("Gib deinen Namen ein: ");
        String name = scan.nextLine();
        Client c = new Client(name);
        c.writer.println(c.username + " hat den Chat betreten!");
        c.writer.flush();
        Thread read = new Thread(c);
        read.start();
        c.sendMessage();
    }

}
