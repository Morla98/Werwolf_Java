package controller;

import game.Game;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import network.client.Client;
import network.model.Message;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * @author Lukas Allermann */

public class UsernameScreenController extends Controller {
    @FXML
    Button SubmitButton;
    @FXML
    TextField UsernameField;
    @FXML
    Label HostJoinLabel;
    @FXML
    TextField IPField;
    @FXML
    Label UsernameEmpty;

    public void EnableJoin(){
        HostJoinLabel.setText("Enter the IP address of the server:");
        IPField.setEditable(true);
        UsernameField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                SubmitUsername();
            }
        });
        IPField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                SubmitUsername();
            }
        });
    }
    public void initScreen(){

    }
    public void DisableVoteButtons(){
        throw new IllegalStateException();
    }
    public void EnableVoteButtons(){
        throw new IllegalStateException();
    }
    public void updateScreen(boolean t){

    }
    public void hideTimer(){throw new IllegalStateException();}

    @Override
    public void addText(String s) {
    }

    public void SetError(String s){
        Platform.runLater(()->{
            UsernameEmpty.setText(s);
            UsernameEmpty.setVisible(true);
        });
    }

    private boolean validUsername(String username) {
        if((username.equals("")) || (username.charAt(0) == ' ') || ((username.length() - 1)) == ' '){
            SetError("Username is Empty");
            return false;
        }
        if(username.length() < 3){
            SetError("Username is to short");
            return false;
        }
        for(int i = 0; i < username.length(); i++) {
            if(username.charAt(i) != ' ') return true;
        }
        return false;
    }

    public void EnableHost(){
        HostJoinLabel.setText("The IP address of your server is:");
        IPField.setEditable(false);
        try {
            IPField.setText(Inet4Address.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        UsernameField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                SubmitUsername();
            }
        });
    }
    @Override
    public void Exit(){
        game.getPrimaryStage().hide();
        System.out.println("Exit");
        System.exit(0);
    }
    @Override
    public void TestFunction(){
        // Stuff
    }

    @Override
    public void incrementTimer(){
        throw new IllegalStateException();
    }
    @FXML
    private void SubmitUsername(){
        String Username = UsernameField.getText();
        if(!(validUsername(Username))) {
            UsernameEmpty.setVisible(true);
            return;
        }
        String ServerIP = IPField.getText();
        game.setUsername(Username);
        game.setServerIP(ServerIP);
        createClient(Username, ServerIP);
    }

    private void createClient(String Username, String ServerIP) {
        Client c = new Client(Username, 2412, game, ServerIP);
        Thread clientThread = new Thread(c);
        game.setClient(c, clientThread);
        clientThread.start();
    }

    @Override
    public void sendMessage(){
    }
    @Override
    public void printMessage(Message m){
        System.out.println(m.getSender() + ":" + m.getMessage());
    }
    @Override
    public void setPhase(String phase){
        throw new IllegalStateException();
    }
    @Override
    public void setTimer(int i){
        throw new IllegalStateException();
    }
    @Override
    public void setGame(Game g){this.game = g;}
    @Override
    public void setPrimaryStage(Stage s){this.primaryStage = s;}
}
