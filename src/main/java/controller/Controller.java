package controller;

import game.Game;
import javafx.stage.Stage;
import network.model.Message;

/**
 * Base class of the controller
 * @author Lukas Allermann
 * @version 1.0
 */
public abstract class Controller {
    Game game;
    Stage primaryStage;

    public abstract void setPrimaryStage(Stage primaryStage);
    public abstract void setGame(Game game);
    public abstract void Exit();
    public abstract void setTimer(int s);
    public abstract void incrementTimer();
    public abstract void sendMessage();
    public abstract void setPhase(String s);
    public abstract void printMessage(Message msg);
    public abstract void TestFunction();
    public abstract void updateScreen(boolean t);
    public abstract void initScreen();
    public abstract void EnableVoteButtons();
    public abstract void DisableVoteButtons();
    public abstract void hideTimer();

    public abstract void addText(String s);
}
