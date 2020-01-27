package exercises.AlexChat.controller;

import exercises.AlexChat.chat.Chat;
import exercises.client.Message;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.IOException;

/**
 * @author Lukas Allermann */

public class ChatController {
    @FXML
    TextArea Output;
    @FXML
    TextField Input;
    @FXML
    Button send;
    @FXML
    Button UsernameSave;
    @FXML
    TextField UsernameField;

    private Chat chat;

    public void printMessage(Message msg, boolean b) {
        Output.appendText(msg.getSender()+": " + msg.getMessage() + "\n");
    }
    public String getInputMessage(){
        return Input.getText();
    }
    public void setUsername(){
        Output.appendText("Username changed to " + UsernameField.getText() +"\n");
        UsernameField.setEditable(false);
        UsernameSave.setVisible(false);
        chat.setUsername(UsernameField.getText());
    }
    public void onInput(){
        Input.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER))
            {
                echoMessage();
            }
        });
    }
    @FXML
    private void echoMessage(){
        String username = chat.getUsername();
        String message = Input.getText();
        Input.setText("");
        Message msg = new Message(username, message, "ALL");
        // printMessage(msg, true);
        if(msg != null) try {
            chat.chatClient.getChatRWClientOutput().getOOS().writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

}
