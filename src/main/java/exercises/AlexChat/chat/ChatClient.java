package exercises.AlexChat.chat;

import exercises.client.Client;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author Lukas Allermann */

public class ChatClient extends Client implements Runnable {
    private Chat chat;

    public ChatRWClient getChatRWClientInput() {
        return chatRWClientInput;
    }

    public ChatRWClient getChatRWClientOutput() {
        return ChatRWClientOutput;
    }

    private ChatRWClient chatRWClientInput;
    private ChatRWClient ChatRWClientOutput;
    public void setChat(Chat chat) {
        this.chat = chat;
    }
    public Chat getChat(){
        return this.chat;
    }
    private Socket usr;
    public ChatClient(String name, int port){
        super(name, port);
    }
    public void init(){
        try {
            usr = new Socket("localhost",port); // TODO : localhost for now
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Type a Message (@username message for whisper and QUIT for Quit)");
    }
    // Need to Override connect because the print functions are in the RWClient Class
    public void run() {

        try {
            ObjectOutputStream oos = new  ObjectOutputStream( usr.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream( usr.getInputStream());

            while(connected) {
                ChatRWClient chatRWClient1 = new ChatRWClient(usr,username, true, oos,this);
                chatRWClient1.setChatClient(this);
                this.ChatRWClientOutput = chatRWClient1;
                ChatRWClient chatRWClient2 = new ChatRWClient(usr, username, false, ois,this);
                chatRWClient2.setChatClient(this);
                this.chatRWClientInput = chatRWClient2;
                Thread One = new Thread(chatRWClient1);
                Thread Two = new Thread(chatRWClient2);

                One.start();
                Two.start();
                One.join();
                Two.join();
                Platform.runLater(this);
            }
        }catch(Exception e){
            System.out.println("HI");
            e.printStackTrace();
        }


    }
}
