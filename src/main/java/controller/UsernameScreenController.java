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

import java.net.*;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Lukas Allermann */

public class UsernameScreenController extends Controller {
    @FXML
    Button SubmitButton;
    @FXML
    Button BackButton;
    @FXML
    TextField UsernameField;
    @FXML
    Label InfoLabel;
    @FXML
    TextField IPField;

    public void EnableJoin(){
        setInfo("Enter the IP of your host");
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

    public void setError(String s){
        Platform.runLater(()->{
            InfoLabel.setStyle("-fx-border-color: #241829 #241829 #241829 #241829;\n" +
                                "-fx-border-radius: 5 5 5 5; \n" +
                                "-fx-background-radius: 5 5 5 5;\n" +
                                "-fx-background-color: #78424f");
            InfoLabel.setText(s);
        });
    }

    public void setInfo(String s){
        Platform.runLater(()->{
            InfoLabel.setStyle("-fx-border-color: #241829 #241829 #241829 #241829;\n" +
                                "-fx-border-radius: 5 5 5 5; \n" +
                                "-fx-background-radius: 5 5 5 5;\n" +
                                "-fx-background-color: #8cb49c");
            InfoLabel.setText(s);
        });
    }

    private boolean validUsername(String username) {
        if((username.equals("")) || (username.charAt(0) == ' ') || ((username.length() - 1)) == ' '){
            setError("Username is Empty");
            return false;
        }
        if(username.length() < 3){
            setError("Username is to short");
            return false;
        }
        for(int i = 0; i < username.length(); i++) {
            if(username.charAt(i) != ' ') return true;
        }
        return false;
    }

    public void EnableHost(){
        setInfo("Local IP detected");
        IPField.setEditable(false);
        try {
            String myAddress = "";
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            while (nets.hasMoreElements()){
                List<InterfaceAddress> netInterface = nets.nextElement().getInterfaceAddresses();
                for (InterfaceAddress interfaceAddress: netInterface) {
                    String currAddress = interfaceAddress.getAddress().getHostAddress();
                    System.out.println("Interface address: " + currAddress);
                    if (currAddress.contains("192.168.")){
                        myAddress = currAddress;
                    }
                    game.setServerIP(currAddress);
                }
            }
            IPField.setText(myAddress);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        UsernameField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                SubmitUsername();
            }
        });
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
    private void backToStart(){
        game.start(this.primaryStage);
    }

    @FXML
    private void SubmitUsername(){
        String Username = UsernameField.getText();
        if(!(validUsername(Username))) {
            InfoLabel.setVisible(true);
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
    public void setPrimaryStage(Stage s){
        this.primaryStage = s;
    }
}
