package exercises.AlexChat.chat;

import exercises.client.Client;
import exercises.client.Message;
import exercises.client.RWClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author Lukas Allermann */

public class ChatRWClient extends RWClient {


    private ChatClient chatClient;
    public void setChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    public ChatRWClient(Socket usr, String name, boolean t, ObjectOutputStream oos, Client cl){
        super(usr, name, t, oos, cl);
    }
    public ChatRWClient(Socket usr, String name, boolean t, ObjectInputStream ois, Client cl){
        super(usr, name, t, ois, cl);
    }
    public ObjectInputStream getOIS(){
        return this.ois;
    }
    public ObjectOutputStream getOOS(){
        return this.oos;
    }
    @Override
    protected void displayMsg(Object o){
        Message msg = (Message) o;
        chatClient.getChat().getChatController().printMessage(msg, true);
    }
    @Override
    protected Message getMessage() {
        /*
        Scanner scan = new Scanner(System.in);
        String receiver = "ALL";
        String message = "Hi";
        String unprocessed = scan.nextLine().trim();
        if( unprocessed.startsWith("@") ) {
            receiver = unprocessed.substring(1, unprocessed.indexOf(" "));
            if(!exercises.client.isAvailable(receiver)){ System.out.println("false name"); return null;}
            message = unprocessed.substring(unprocessed.indexOf(" "));
        }else if (unprocessed.equals("QUIT")){
            //TODO IMPLEMENT it right so that no error message comes up
            connected = false;
            exercises.client.setConnected(false);
            exercises.client.removeName(username);
            return new Message(username, "QUIT", "ALL");
        }else{
            message = unprocessed;
        }
        */
        return null;
    }
    @Override
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


}
