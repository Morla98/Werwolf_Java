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
    ImageView BackgroundStartScreen;
    @FXML
    Button HostButton;
    @FXML
    Button JoinButton;


    public void initScreen(){
        Image Background = new Image(getClass().getResource("/backgrounds/background_0.1.0.gif").toString());
        BackgroundStartScreen.setImage(Background);
    }

    // Button functions ------------------------------------------------------------------------------------------------
    public void startHost(){
        /*
        * Called when "Host Game" button is pressed.
        * */
        game.startHost();
    }
    public void startJoin(){
        /*
         * Called when "Join Game" button is pressed.
         * */
        game.startJoin(false);
    }

    public void ExitButtonPressed(){
        /*
         * Called when "Exit" button is pressed.
         * */
        Exit();
    }

    public void SettingButtonPressed(){
        game.Settings();
    }

    // Controller methods ----------------------------------------------------------------------------------------------
    @Override
    public void Exit(){
        /*
         * Closes the game window.
         */
        game.getPrimaryStage().hide();
        System.out.println("Exit");
        System.exit(0);
    }

    @Override
    public void updateScreen(boolean t) {

    }
    public void hideTimer(){throw new IllegalStateException();}

    @Override
    public void addText(String s) {}

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

    @Override
    public void setTimer(int i){
        throw new IllegalStateException();
    }
    @Override
    public void incrementTimer(){
        throw new IllegalStateException();
    }
    @Override
    public void sendMessage(){}

    @Override
    public void setPhase(String phase){}

    @Override
    public void setPrimaryStage(Stage s){this.primaryStage = s;}

    public void DisableVoteButtons(){
        throw new IllegalStateException();
    }

    public void EnableVoteButtons(){
        throw new IllegalStateException();
    }
}


