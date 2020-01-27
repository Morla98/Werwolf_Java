package controller;

import game.Game;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import network.model.Message;

/**
 * @author Lukas Allermann */

public class StartScreenController extends Controller{
    @FXML
    ImageView BackgroundView;
    @FXML
    Button HostButton;
    @FXML
    Button JoinButton;
    @FXML
    Button StartButton;
    public void StartButtonPressed(){
        DecideHostOrJoin();
    }
    public void startHost(){
        game.startHost();
    }
    public void startJoin(){
        game.startJoin(false);
    }
    private void DecideHostOrJoin() {
        StartButton.setVisible(false);
        HostButton.setVisible(true);
        JoinButton.setVisible(true);
    }
    public void DisableVoteButtons(){
        throw new IllegalStateException();
    }
    public void EnableVoteButtons(){
        throw new IllegalStateException();
    }

    @Override
    public void updateScreen(boolean t) {

    }
    public void hideTimer(){throw new IllegalStateException();}

    @Override
    public void addText(String s) {

    }

    public void initScreen(){
        Image Background = new Image(getClass().getResource("/backgrounds/LogoVersion1.png").toString());
        BackgroundView.setImage(Background);
    }
    @Override
    public void Exit(){
        game.getPrimaryStage().hide();
        System.out.println("Exit");
        System.exit(0);
    }
    @Override
    public void printMessage(Message m){
        System.out.println(m.getSender() + ":" + m.getMessage());
    }
    @Override
    public void setGame(Game g){this.game = g;}
    @Override
    public void TestFunction(){
        // Stuff
    }
    public void SettingButtonPressed(){
        game.Settings();
    }
    public void ExitButtonPressed(){
        Exit();
    }
    @Override
    public void setTimer(int i){
        throw new IllegalStateException();
    }
    @Override
    public void incrementTimer(){
        throw new IllegalStateException();
    }
    @Override
    public void sendMessage(){

    }
    @Override
    public void setPhase(String phase){
    }
    @Override
    public void setPrimaryStage(Stage s){this.primaryStage = s;}
}
