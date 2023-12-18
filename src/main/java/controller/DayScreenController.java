package controller;

import game.Game;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import models.Player;
import network.Datapacket;
import network.model.Message;
import network.model.Tuple;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * @author Lukas Allermann */
@SuppressWarnings("Duplicates")
public class DayScreenController extends Controller{
    @FXML
    ImageView BackgroundDayScreen;
    @FXML
    TextArea RoleInformationField;
    @FXML
    ListView<String> GraveyardListView;
    @FXML
    TextArea ChatOutputDay;
    @FXML
    Label GraveyardLabel;
    @FXML
    TextField ChatInputDay;
    @FXML
    ListView<String> AllyList;
    @FXML
    Label TimeLeftLabel;
    @FXML
    Label TimeLabel;
    @FXML
    Label AllieLabel;
    @FXML
    Label DayCounter;
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
    Label IPLabel;
    @FXML
    Label LabelUsername3;
    @FXML
    Label ServerLabel;
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

    private ArrayList<Label> UsernameLabels;
    private ArrayList<Button> UsernameButtons;
    private int TimerMinuteCounter;
    private int TimerSecondsCounter;

    /**
     * The function is a help function so that the chat can be transferred to other scenes
     *
     * @return The string containing the old chat
     */
    public String getChatData(){
        return ChatOutputDay.getText();
    }
    public void EnableHost(){
        try {
            System.out.println("Host");
            ServerLabel.setVisible(true);
            IPLabel.setVisible(true);
            IPLabel.setText(Inet4Address.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void setGame(Game g){
        this.game = g;
    }
    @Override
    public void Exit(){
        game.getPrimaryStage().hide();
        System.out.println("Exit");
        System.exit(0);
    }

    @Override
    public void TestFunction(){
        game.callTownVotePhase(30);
    }
    public void callTestEvent(){
        EventWindowController.initTest();
    }
    /**
     * This function initializes the screen, creates the lists of labels and buttons, configures the chat
     */
    public void initScreen(){
        Image Background = new Image(getClass().getResource("/backgrounds/background_0.2.0.gif").toString());
        BackgroundDayScreen.setImage(Background);
        EventWindowController.setGame(game);
        ChatInputDay.setOnKeyPressed(ke -> {
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
        initButtons();
        ChatOutputDay.setWrapText(true);
        EventWindowController.setGame(game);
    }

    /**
     *
     * @param i is the place on which position name should be displayed
     * @param name is the name who is displayed
     */
    private void displayUsername(int i, String name){
        Platform.runLater(()->{
            UsernameLabels.get(i).setVisible(true);
            UsernameLabels.get(i).setText(name);
            System.out.println(name);
        });
    }

    /**
     * This help function can be controlled by other threads and causes the label of the phase to be set to the given string
     * @param Phasename is the String containing the new Phasename
     */
    @Override
    public void setPhase(String Phasename){
        Platform.runLater(() -> {
            PhaseLabel.setText(Phasename);
            PhaseLabel.setVisible(true);
        });
    }

    public void addText(String s){ ChatOutputDay.appendText(s + "\n"); }

    /**
     * This help function processes the timer label and sets it. The time is given in seconds and the timer can be set to a maximum of 09:59
     * @param seconds is the Time in seconds (2 Minutes = 120 seconds)
     */
    @Override
    public void setTimer(int seconds) {
        TimeLabel.setVisible(true);
        TimeLeftLabel.setVisible(true);
        if (seconds > 599) throw new IllegalStateException();
        if(seconds < 60){
            TimerMinuteCounter = 0;
            TimerSecondsCounter = seconds;
        }else if(seconds < 120){
            TimerMinuteCounter = 1;
            TimerSecondsCounter = (seconds - 60);
        }else{
            TimerMinuteCounter = 2;
            TimerSecondsCounter = 0;
        }
        displayTimer();
    }
    @Override
    public void setPrimaryStage(Stage ps){
        this.primaryStage = ps;
    }

    /**
     * This help function can display the time formatted from the stored variables
     */
    private void displayTimer(){
        Platform.runLater(() -> {
            if (TimerSecondsCounter == 0) {
                TimeLabel.setText("0" + TimerMinuteCounter + ":0" + TimerSecondsCounter);
            } else if (TimerSecondsCounter < 10) {
                TimeLabel.setText("0" + TimerMinuteCounter + ":0" + TimerSecondsCounter);
            } else {
                TimeLabel.setText("0" + TimerMinuteCounter + ":" + TimerSecondsCounter);
            }
            if(TimerMinuteCounter == 0 && TimerSecondsCounter == 0){
                hideTimer();
            }
        });
    }

    public void hideTimer() {
        TimeLeftLabel.setVisible(false);
        TimeLabel.setVisible(false);
    }

    /**
     * This help function reduces the time of the timer by one second
     */
    @SuppressWarnings("Duplicates")
    @Override
    public void incrementTimer(){
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
    // was once used to Simulate ChatOutput
    private void simulateChat(){
        for(int i = 0; i < 3; i++){ ChatOutputDay.appendText("Test: Hallo Welt!\n");}
    }

    /**
     * This help function gets the chat from the old scene and sets it in the new scene
     */
    private void updateChat(){
        if(game.getMe().getAlive()) ChatInputDay.setEditable(true);
        if(game.getNightScreenController() != null){
                Platform.runLater(()-> ChatOutputDay.setText(game.getNightScreenController().getChatData()));
        }
    }

    /**
     * Prints the Message in the Chat, formatted with the Layout: (USERNAME: MESSAGE \n)
     * @param msg is the Message Object;
     */
    @Override
    public void printMessage(Message msg){
        Platform.runLater(()->{
            if(msg.getReceiver().equals("ALL")) {
                ChatOutputDay.appendText(msg.getSender() + ": " + msg.getMessage() + "\n");
            }else if(game.getMe().getName().equals(msg.getReceiver())){ // Whisper
                ChatOutputDay.appendText(msg.getSender() + " whispers to you: " + msg.getMessage() + "\n");
            }
        });
    }

    public void callMayorElectionEvent(){
        EventWindowController.initMayorElection();
    }

    private void sendWhisper(String msg, String receiver){
        if(game.isPreparing()) {
            ChatOutputDay.appendText("You can not whisper in Preparing Phase!\n");
            return;
        }
        if(game.existPlayer(receiver)) {
            Message newMsg = new Message(game.getUsername(), msg, receiver);
            game.sendMessage(newMsg);
            ChatOutputDay.appendText("You whisper to " + receiver +": " + msg + "\n");
        }else{
            ChatOutputDay.appendText("This user does not exist\n");
        }
    }

    /**
     * This help function is called by pressing the Enter key in the chat menu, and processes the entered text to a message object and sends it to the exercises.exercises.AlexChat.exercises.client.
     */
    @Override
    public void sendMessage() {
        try{
            String msg = ChatInputDay.getText();
            String receiver = "";
            char msgArray[] = msg.toCharArray();
            if (msgArray != null) {
                if (msgArray[0] == '@') {// Whispernachricht
                    for (int i = 1; i < msgArray.length; i++) {
                        if (msgArray[i] == ' ') {
                            sendWhisper(msg.substring(i + 1, msg.length()), receiver);
                        } else {
                            receiver += msgArray[i];
                        }
                    }
                } else { // Normale Nachricht an alle
                    Message newMsg = new Message(game.getUsername(), msg, "ALL");
                    game.sendMessage(newMsg);
                }
                ChatInputDay.setText("");
            }
        } catch (Exception e){
            System.out.println("You are not allowed to write if you are dead");
        }

    }
    public void updateLists(){
        Player[] p = game.getPlayers();
        ArrayList<Player> deadPlayer = game.getDeadPlayers();
        ObservableList<String> graveyardList = FXCollections.observableArrayList();
        ArrayList<String> graveyardListnames = new ArrayList<>();
        for(int i = 0; i < p.length; i++){
            if(p[i] != null){
                if(p[i].getAlive()){
                    displayUsername(i, p[i].getName());
                }else{
                    displayUsername(i, "");
                }

            }
        }
        for(int i = 0; i < deadPlayer.size(); i++){
            graveyardListnames.add(deadPlayer.get(i).getName());
            if(game.getMe().getName().equals(deadPlayer.get(i).getName())){
                ChatInputDay.setOnAction(null);
                ChatInputDay.setEditable(false);
            }
        }
        graveyardList.setAll(graveyardListnames);
        Platform.runLater(()-> GraveyardListView.setItems(graveyardList));
    }
    public void PlayerVote1(){
        Player p[] = game.getPlayers();
        Message m = new Message(game.getMe().getName(), "I voted for " + p[0].getName() + "!", "ALL");
        game.setTownDecisionPlayer(p[0]);
        game.sendMessage(m);
    }
    public void PlayerVote2(){
        Player p[] = game.getPlayers();
        game.setTownDecisionPlayer(p[1]);
        game.sendMessage(new Message(game.getMe().getName(), "I voted for " + p[1].getName() + "!", "ALL"));
    }
    public void PlayerVote3(){
        Player p[] = game.getPlayers();
        game.setTownDecisionPlayer(p[2]);
        game.sendMessage(new Message(game.getMe().getName(), "I voted for " + p[2].getName() + "!", "ALL"));
    }
    public void PlayerVote4(){
        Player p[] = game.getPlayers();
        game.setTownDecisionPlayer(p[3]);
        game.sendMessage(new Message(game.getMe().getName(), "I voted for " + p[3].getName() + "!", "ALL"));
    }
    public void PlayerVote5(){
        Player p[] = game.getPlayers();
        game.setTownDecisionPlayer(p[4]);
        game.sendMessage(new Message(game.getMe().getName(), "I voted for " + p[4].getName() + "!", "ALL"));
    }
    public void PlayerVote6(){
        Player p[] = game.getPlayers();
        game.setTownDecisionPlayer(p[5]);
        game.sendMessage(new Message(game.getMe().getName(), "I voted for " + p[5].getName() + "!", "ALL"));
    }
    public void PlayerVote7(){
        Player p[] = game.getPlayers();
        game.setTownDecisionPlayer(p[6]);
        game.sendMessage(new Message(game.getMe().getName(), "I voted for " + p[6].getName() + "!", "ALL"));
    }
    public void PlayerVote8(){
        Player[] p = game.getPlayers();
        game.setTownDecisionPlayer(p[7]);
        game.sendMessage(new Message(game.getMe().getName(), "I voted for " + p[7].getName() + "!", "ALL"));
    }
    public void updatePrepareScreen(Datapacket d){
        // d.getData().getTuple();
        Platform.runLater(()->{
            ArrayList<Tuple> t = d.getTulist();
            setPhase("Preparing Phase");
            for(int i = 0; i < t.size(); i++){
                System.out.println(t.get(i).getName());
                System.out.println(i);
                displayUsername(i, t.get(i).getName());
            }
        });
    }

    /**
     * is called whenether you switch to the screen to update the newest infos
     */
    @Override
    public void updateScreen(boolean t){
        if(t) updateChat();
        updateLists();
        updateDayLabel();
        updateAllies();
        updateRoleInformation();
        /*
        TODO Update AllieList if WW, Update AliveList, Update etc.
         */
    }

    private void updateRoleInformation() {
        if(game.getMe().getAlive()){
            RoleInformationField.setText(game.getMe().getRole().getAboutRole());
        }else{ // after death the roleinformation should be gone
            RoleInformationField.setText("It seems you are dead!");
        }
    }

    private void updateDayLabel() {
        Platform.runLater(()->{
            DayCounter.setVisible(true);
            DayCounter.setText("Day " + game.getDayCounter());
        });
    }
    @Override
    public void EnableVoteButtons(){
        if(game.getMe().getAlive()){
            Platform.runLater(()-> {
                Player[] p = game.getPlayers();
                for(int i = 0; i < p.length; i++){
                    if(p[i] != null){
                        if(p[i].getAlive()){
                            UsernameButtons.get(i).setVisible(true);
                        }
                    }
                }
            });
        } else{
            DisableVoteButtons();
        }
    }
    @Override
    public void DisableVoteButtons(){
        Platform.runLater(()->{
            Player[] p = game.getPlayers();
            for(int i = 0; i < p.length; i++){
                UsernameButtons.get(i).setVisible(false);
            }
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
    /**
     * This help function turns on the view of the allies for the werewolves and lovers
     * @param t true if WW false if Lover
     */
    private void EnableAllies(boolean t){
        initAlly(t);
        AllieLabel.setVisible(true);
        AllyList.setVisible(true);
    }

    private void initAlly(boolean t) {
        Platform.runLater(()->{
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
                Platform.runLater(()->AllyList.setItems(p));
            }else{ // Lover case
                ObservableList<String> p = FXCollections.observableArrayList();
                ArrayList<String> LoverList = new ArrayList<>();
                Player Couples[] = game.getCouples();
                for(int i = 0; i < Couples.length; i++){
                    LoverList.add(Couples[i].getName());
                }
                p.setAll(LoverList);
                Platform.runLater(()->AllyList.setItems(p));
            }
        });
    }

    public void callMayorEvent() {
        EventWindowController.initMayorEvent();
    }

    public void callCupidEvent() {
        EventWindowController.initCupidEvent();
    }

    public void endEvent() {
        EventWindowController.hideAllEvents();
    }
}
