package controller;

import game.Game;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import models.Player;
import network.model.Message;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * @author Lukas Allermann */

@SuppressWarnings("Duplicates")
public class NightScreenController extends Controller{
    @FXML
    Label ServerLabel;
    @FXML
    Label IPLabel;
    @FXML
    TextArea RoleInformationField;
    @FXML
    TextArea GraveyardField;
    @FXML
    TextArea ChatOutputNight;
    @FXML
    TextField ChatInputNight;
    @FXML
    ListView<String> AllyList;
    @FXML
    Label AllieLabel;
    @FXML
    Label TimeLeftLabel;
    @FXML
    ListView<String> GraveyardListView;
    @FXML
    Label GraveyardLabel;
    @FXML
    Label TimeLabel;
    @FXML
    Label NightCounter;
    @FXML
    Button ButtonPlayer1;
    @FXML
    Button ButtonPlayer2;
    @FXML
    Button ButtonPlayer3;
    @FXML
    Button ButtonPlayer4;
    @FXML
    Button ButtonPlayer5;
    @FXML
    Button ButtonPlayer6;
    @FXML
    Button ButtonPlayer7;
    @FXML
    Button ButtonPlayer8;
    @FXML
    Button ExitButton;
    @FXML
    Label UsernameLabel;
    @FXML
    Button TestButton;
    @FXML
    Label LabelUsername1;
    @FXML
    Label LabelUsername2;
    @FXML
    Label LabelUsername3;
    @FXML
    Label LabelUsername4;
    @FXML
    Label LabelUsername5;
    @FXML
    Label LabelUsername6;
    @FXML
    Label LabelUsername7;
    @FXML
    Label LabelUsername8;
    @FXML
    Label PhaseLabel;
    @FXML
    Parent EventWindow;
    @FXML
    private
    EventController EventWindowController;
    private int TimerMinuteCounter;
    private int TimerSecondsCounter;
    private ArrayList<Label> UsernameLabels;
    private ArrayList<Button> UsernameButtons;


    @Override
    public void TestFunction(){
        game.callWerewolfPhase(30);
    }
    public void callTestEvent(){
        EventWindowController.initTest();
    }

    public void EnableHost(){
        try {
            ServerLabel.setVisible(true);
            IPLabel.setVisible(true);
            IPLabel.setText(Inet4Address.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    private void displayUsername(int i, String name){
        UsernameLabels.get(i).setVisible(true);
        UsernameLabels.get(i).setText(name);
    }

    public void updateLists(){
        Platform.runLater(()->{
        Player[] p = game.getPlayers();
        ArrayList<Player> deadPlayer = game.getDeadPlayers();
        ObservableList<String> graveyardList = FXCollections.observableArrayList();
        ArrayList<String> graveyardListnames = new ArrayList<>();
        for(int i = 0; i < p.length; i++){
            if(p[i] != null){
                displayUsername(i, p[i].getName());
            }
        }
        for(int i = 0; i < deadPlayer.size(); i++){
            graveyardListnames.add(deadPlayer.get(i).getName());
        }
        graveyardList.setAll(graveyardListnames);
        GraveyardListView.setItems(graveyardList);
        updateAllies();
        });
    }

    /**
     * is called whenether you switch to the screen to update the newewst infos
     * @param t
     */
    @Override
    public void updateScreen(boolean t){
        Platform.runLater(()->{
        if(!game.getMe().getRole().getEvil()){
            ChatInputNight.setOnAction(null);
        }
        if(t) updateChat();
        updateLists();
        updateAllies();
        updateNightLabel();
        updateRoleInformation();
        });
    }

    private void updateAllies(){
        if(game.getMe().getRole().getEvil()){
            EnableAllies(true); // true because WW
        }
        if(game.getMe().isLover()){
            EnableAllies(false); // false because Lover
        }
    }

    public void PlayerVote1(){
        Player p[] = game.getPlayers();
        Message m = new Message(game.getUsername(), "I vote for " + p[0].getName(), "ALL");
        game.sendMessage(m);
        game.setWerewolveDecisionPlayer(p[0]);
    }
    public void PlayerVote2(){
        Player p[] = game.getPlayers();
        Message m = new Message(game.getUsername(), "I vote for " + p[1].getName(), "ALL");
        game.sendMessage(m);
        game.setWerewolveDecisionPlayer(p[1]);
    }
    public void PlayerVote3(){
        Player p[] = game.getPlayers();
        Message m = new Message(game.getUsername(), "I vote for " + p[2].getName(), "ALL");
        game.sendMessage(m);
        game.setWerewolveDecisionPlayer(p[2]);
    }
    public void PlayerVote4(){
        Player p[] = game.getPlayers();
        Message m = new Message(game.getUsername(), "I vote for " + p[3].getName(), "ALL");
        game.sendMessage(m);
        game.setWerewolveDecisionPlayer(p[3]);
    }
    public void PlayerVote5(){
        Player p[] = game.getPlayers();
        Message m = new Message(game.getUsername(), "I vote for " + p[4].getName(), "ALL");
        game.sendMessage(m);
        game.setWerewolveDecisionPlayer(p[4]);
    }
    public void PlayerVote6(){
        Player p[] = game.getPlayers();
        Message m = new Message(game.getUsername(), "I vote for " + p[5].getName(), "ALL");
        game.sendMessage(m);
        game.setWerewolveDecisionPlayer(p[5]);
    }
    public void PlayerVote7(){
        Player p[] = game.getPlayers();
        Message m = new Message(game.getUsername(), "I vote for " + p[6].getName(), "ALL");
        game.sendMessage(m);
        game.setWerewolveDecisionPlayer(p[6]);
    }
    public void PlayerVote8(){
        Player p[] = game.getPlayers();
        Message m = new Message(game.getUsername(), "I vote for " + p[7].getName(), "ALL");
        game.sendMessage(m);
        game.setWerewolveDecisionPlayer(p[7]);
    }

    @Override
    public void EnableVoteButtons(){
        if(game.getMe().getAlive()){
            Platform.runLater(()->{
                Player[] p = game.getPlayers();
                for(int i = 0; i < p.length; i++){
                    // System.out.println("EnableVote: " + i + ", p.length: " + p.length);
                    if(p[i] != null){
                        if(p[i].getAlive()){
                            // System.out.println("EnableVote: Player who get in if: " + p[i].getName() + ", in Nummer: " + i);
                            UsernameButtons.get(i).setVisible(true);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void DisableVoteButtons(){
        Platform.runLater(()-> {
                Player[] p = game.getPlayers();
                for (int i = 0; i < p.length; i++) {
                    UsernameButtons.get(i).setVisible(false);
                }
            });
    }

    /**
     * This function initializes the screen, creates the lists of labels and buttons, configures the chat
     */
    public void initScreen(){
        EventWindowController.setGame(game);
        ChatInputNight.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                sendMessage();
            }
        });
        UsernameLabel.setText(game.getUsername());
        UsernameLabels = new ArrayList<>();
        UsernameButtons = new ArrayList<>();
        UsernameLabels.add(LabelUsername1);UsernameLabels.add(LabelUsername2);UsernameLabels.add(LabelUsername3);
        UsernameLabels.add(LabelUsername4);UsernameLabels.add(LabelUsername5);UsernameLabels.add(LabelUsername6);
        UsernameLabels.add(LabelUsername7);UsernameLabels.add(LabelUsername8);
        UsernameButtons.add(ButtonPlayer1);UsernameButtons.add(ButtonPlayer2);UsernameButtons.add(ButtonPlayer3);
        UsernameButtons.add(ButtonPlayer4);UsernameButtons.add(ButtonPlayer5);UsernameButtons.add(ButtonPlayer6);
        UsernameButtons.add(ButtonPlayer7);UsernameButtons.add(ButtonPlayer8);
        initUsernames();
        initButtons();
        ChatOutputNight.setWrapText(true);
        // if(game.getLogic().getMe().getRole().getEvil() || ) EnableAllies(); // TODO implement later the Test if Player is Werwolf or Lover

    }

    @Override
    public void setPrimaryStage(Stage ps){
        this.primaryStage = ps;
    }

    /**
     * This help function can be controlled by other threads and causes the label of the phase to be set to the given string
     *
     * @param Phasename is the String containing the new Phasename
     */
    @Override
    public void setPhase(String Phasename) {
        Platform.runLater(() -> {
            PhaseLabel.setText(Phasename);
            PhaseLabel.setVisible(true);
        });
    }

    @Override
    public void setGame(Game g){
        this.game = g;
    }

    public String getChatData(){
        return ChatOutputNight.getText();
    }

    private void EnableAllies(boolean t){
        initAlly(t);
        AllieLabel.setVisible(true);
        AllyList.setVisible(true);
    }

    private void initAlly(boolean t) {
        if(t){ // Werewolf case
            ObservableList<String> p = FXCollections.observableArrayList();
            Player player[] = game.getPlayers();
            ArrayList<String> WerewolveListNames = new ArrayList<>();
            for(int i = 0; i < player.length; i++){
                if(player[i].getRole().getEvil()){
                    WerewolveListNames.add(player[i].getName());
                }
            }
            p.setAll(WerewolveListNames);
            Platform.runLater(()-> AllyList.setItems(p));
        }else{ // Lover case
            ObservableList<String> p = FXCollections.observableArrayList();
            ArrayList<String> LoverList = new ArrayList<>();
            Player Couples[] = game.getCouples();
            for(int i = 0; i < Couples.length; i++){
                LoverList.add(Couples[i].getName());
            }
            p.setAll(LoverList);
            AllyList.setItems(p);
        }
    }

    public void addText(String s){ ChatOutputNight.appendText(s + "\n"); }

    @Override
    public void Exit(){
        game.getPrimaryStage().hide();
        System.out.println("Exit");
        System.exit(0);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void setTimer(int seconds) {
        Platform.runLater(() -> {
            if (seconds > 120) throw new IllegalStateException();
            if (seconds < 60) {
                TimerMinuteCounter = 0;
                TimerSecondsCounter = seconds;
            } else if (seconds < 120) {
                TimerMinuteCounter = 1;
                TimerSecondsCounter = (seconds - 60);
            } else {
                TimerMinuteCounter = 2;
                TimerSecondsCounter = 0;
            }
            displayTimer();
        });
    }

    private void updateRoleInformation() {
        if(game.getMe().getAlive()){
            RoleInformationField.setText(game.getMe().getRole().getAboutRole());
        }else{ // after death the roleinformation should be gone
            RoleInformationField.setText("It seems you are dead!");
        }
    }

    @SuppressWarnings("Duplicates")
    private void displayTimer() {
        TimeLeftLabel.setVisible(true);
        TimeLabel.setVisible(true);
        Platform.runLater(() -> {
            if (TimerSecondsCounter == 0) {
                TimeLabel.setText("0" + TimerMinuteCounter + ":0" + TimerSecondsCounter);
            } else if (TimerSecondsCounter < 10) {
                TimeLabel.setText("0" + TimerMinuteCounter + ":0" + TimerSecondsCounter);
            } else {
                TimeLabel.setText("0" + TimerMinuteCounter + ":" + TimerSecondsCounter);
            }
        });
    }

    @SuppressWarnings("MoveFieldAssignmentToInitializer")
    @Override
    public void incrementTimer() {
        Platform.runLater(() -> {
            if (TimerMinuteCounter > 9 || TimerSecondsCounter > 59 || TimerMinuteCounter < 0 || TimerSecondsCounter < 0)
                throw new IllegalStateException();
            if (TimerMinuteCounter > 0) {
                if (TimerSecondsCounter == 0) {
                    TimerMinuteCounter--;
                    TimerSecondsCounter = 59;
                } else {
                    TimerSecondsCounter--;
                }
            } else {
                if (TimerSecondsCounter != 0) {
                    TimerSecondsCounter--;
                }
            }
            displayTimer();
        });
    }

    private void initButtons(){
        for(int i = 0; i < UsernameButtons.size(); i++){
            UsernameButtons.get(i).setVisible(false);
        }
    }

    private void initUsernames(){
        if(!game.isPreparing()) {
            Player p[] = game.getPlayers();
            for (int i = 0; i < p.length; i++) {
                UsernameLabels.get(i).setText(p[i].getName());
            }
        }
    }

    public void hideTimer() {
        TimeLeftLabel.setVisible(false);
        TimeLabel.setVisible(false);
    }
    private void updateNightLabel() {
        Platform.runLater(()->{
            NightCounter.setVisible(true);
            NightCounter.setText("Night " + game.getDayCounter());
        });
    }
    private void updateChat(){
            ChatOutputNight.setText(game.getDayScreenController().getChatData());
            // If player is dead or is good, lock the chat
            if(!(game.getMe().getAlive()) || (!game.getMe().getRole().getEvil())){
                ChatInputNight.setEditable(false);
            }
    }

    @Override
    public void printMessage(Message msg){
        if(game.getMe().getRole().getEvil()) {
            ChatOutputNight.appendText(msg.getSender() + ": " + msg.getMessage() + "\n");
        }
    }

    @Override
    public void sendMessage(){
        String msg = ChatInputNight.getText();
        ChatInputNight.setText("");
        Message newMsg = new Message(game.getUsername(), msg, "ALL");
        game.sendMessage(newMsg);
    }
    public void callHunterEvent() {
        EventWindowController.initHunterEvent();
    }

    public void callWitchHealEvent(Player p) {
        EventWindowController.initWitchEventHeal(p);
    }

    public void callWitchKillEvent() {
        EventWindowController.initWitchEventKill();
    }

    public void callSeerEvent() {
        EventWindowController.initSeerEvent();
    }

    public void endEvent() {
        EventWindowController.hideAllEvents();
    }
}
