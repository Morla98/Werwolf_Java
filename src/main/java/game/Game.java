package game;

import controller.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.Timer;
import logic.WerewolfGame;
import models.Player;
import network.Data;
import network.Datapacket;
import network.client.Client;
import network.model.Message;
import network.model.Tuple;
import network.server.Server;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author Lukas Allermann
 */

public class Game extends Application {
    private String MayorJumpPoint;
    private Player WerewolveDecisionPlayer;
    private boolean host;
    private Stage primaryStage;
    private DayScreenController DayScreenController;
    private NightScreenController NightScreenController;
    private Controller ScreenController;
    private String Username;
    private Scene DayScene;
    private Scene NightScene;
    private Client client;
    private Player player;
    private WerewolfGame Logic;
    private Server server;
    private Thread clientThread;
    private Thread musicThread;
    private ArrayList<Tuple> Spielerliste;
    private boolean isPreparing = true;
    private Player TownDecisionPlayer;
    private String HunterJumpPoint;


    public void setPlayer(Player player){ this.player = player; }
    public Player getPlayer() { return this.player; }
    public Client getClient(){ return this.client; }
    public WerewolfGame getLogic() {
        return Logic;
    }
    public void setLogic(WerewolfGame logic) {
        Logic = logic;
    }
    public controller.DayScreenController getDayScreenController() {
        return DayScreenController;
    }
    public void setDayScreenController(controller.DayScreenController dayScreenController) {
        DayScreenController = dayScreenController;
    }
    public void callTestEvent(){
        DayScreenController.callTestEvent();
    }
    public controller.NightScreenController getNightScreenController() {
        return NightScreenController;
    }

    public ArrayList<Tuple> getSpielerliste() {
        return Spielerliste;
    }
    public void setSpielerliste(ArrayList<Tuple> spielerliste) {
        Spielerliste = spielerliste;
    }

    public void setNightScreenController(controller.NightScreenController nightScreenController) {
        NightScreenController = nightScreenController;
    }
    public Controller getScreenController() {
        return ScreenController;
    }
    private void setScreenController(Controller screenController) {
        ScreenController = screenController;
    }
    private Scene getDayScene() {
        return DayScene;
    }
    private String ServerIP;
    public void setDayScene(Scene dayScene) {
        DayScene = dayScene;
    }
    private Scene getNightScene() {
        return NightScene;
    }
    public void setNightScene(Scene nightScene) {
        NightScene = nightScene;
    }
    public String getUsername() {
        return Username;
    }
    public void setUsername(String username) {
        Username = username;
    }
    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    /**
     * This function initializes the start window and sets some things at the primary stage.
     *
     * @param primaryStage ist das Fenster
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        try {
            musicThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    playWavFile("/sounds/bg_sound.wav");
                }
            });
            musicThread.start();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/StartScreen.fxml"));
            Parent root = loader.load();
            ScreenController = loader.getController();
            ScreenController.initScreen();
            initController();
            Scene scene = new Scene(root,815,615);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setTitle("Werwolf the Game");
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    private void playWavFile(String fileName) {
        InputStream inputStream = getClass().getResourceAsStream(fileName);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(
                inputStream);
        AudioInputStream audioStream = null;
        AudioFormat audioFormat = null;

        try {
            audioStream = AudioSystem.getAudioInputStream(bufferedInputStream);
            audioFormat = audioStream.getFormat();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        DataLine.Info info = new DataLine.Info(SourceDataLine.class,
                audioFormat);
        SourceDataLine sourceLine;
        try {
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            return;
        }

        sourceLine.start();

        int nBytesRead = 0;
        byte[] abData = new byte[128000];
        while (nBytesRead != -1) {
            try {
                nBytesRead = audioStream.read(abData, 0, abData.length);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            if (nBytesRead >= 0) {
                sourceLine.write(abData, 0, nBytesRead);
            }
        }

        sourceLine.drain();
        sourceLine.close();

        try {
            audioStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    /**
     * This function is called after confirming the username. Here the game object and the exercises.client are generated and
     * placed on a separate thread. Additionally, the day and night scenes are initialized as well as their controllers.
     * Then the exercises.server/logic should control the game
     */
    private void startGame(Datapacket d){
        this.Logic = new WerewolfGame(this, this.Username);
        /*
            Here the game object and the exercises.client object are created and links are started, as well as the scene is changed. The game is then started.
            At the moment, only the current scene is changed to the day scene.
         */
        FXMLLoader loader = new FXMLLoader();
        try {
            loader.setLocation(getClass().getResource("/view/DayScreen.fxml"));
            Parent root = loader.load();
            this.DayScreenController = loader.getController();
            ScreenController = DayScreenController;
            ScreenController.hideTimer();
            initController();
            DayScreenController.initScreen();
            if(this.host) {DayScreenController.EnableHost();}
            this.DayScene = new Scene(root);
            primaryStage.setScene(this.DayScene);
            updatePrepareScreen(d);
            FXMLLoader loader2 = new FXMLLoader();
            loader2.setLocation(getClass().getResource("/view/NightScreen.fxml"));
            Parent root2 = loader2.load();
            NightScreenController = loader2.getController();
            ScreenController = NightScreenController;
            initController();
            ScreenController = DayScreenController;
            NightScreenController.initScreen();
            this.NightScene = new Scene(root2);
            this.NightScreenController = NightScreenController;
            if(this.host) {NightScreenController.EnableHost();}
            // ((DayScreenController) ScreenController).EnableAllies(); Shows Allies for WerewolfSites, implementation later
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        Thread LogicThread = new Thread(Logic);
        LogicThread.start();
        */
        ScreenController.setPhase("Preparation Phase");
    }

    private void initController(){
        ScreenController.setGame(this);
        ScreenController.setPrimaryStage(this.primaryStage);
    }

    public void connectionSuccess(Datapacket d){
        Platform.runLater(()-> startGame(d));
    }
    public void connectionFailed(){
        this.client = null;
        this.ServerIP = null;
        this.Username = null;
        this.startJoin(true);
    }
    // This function may remain unimplemented because I can't think of any benefit yet
    public void Settings() {
    }
    public void updateScreen(boolean t){
        try {
            Thread.sleep(100);
            this.isPreparing = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ScreenController.updateScreen(t);
    }

    /**
     * Autogenerated. the call to launch builds the window and ends with the call to start()
     * @param args is the args you can open the main with.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This function should be called when the player decides to host a game
     */
    public void startHost(){
        this.host = true;
        startUsernameHost();
    }

    /**
     * This function should be called when the player decides to join a game
     */
    public void startJoin(boolean b){
        Platform.runLater(()-> startUsernameJoin(b));
    }

    /**
     * This function first covers Join and Host.
     * Currently she opens a new scene where you can enter your username.
     * This will be changed later
     */
    private void startUsernameJoin(boolean b) {
        /*
            Here the game object and the exercises.client object are created and links are started, as well as the scene is changed. The game is then started.
            At the moment, only the current scene is changed to the day scene.
         */
        FXMLLoader loader = new FXMLLoader();
        try {
            loader.setLocation(getClass().getResource("/view/UsernameScreen.fxml"));
            Parent root = loader.load();
            ScreenController = loader.getController();
            initController();
            primaryStage.setScene(new Scene(root));
            UsernameScreenController s = loader.getController();
            s.EnableJoin();
            if(b) s.setError("Username is already taken");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * This function first covers Join and Host.
     * Currently, she opens a new scene where you can enter your username.
     * This will be changed later
     */
    @SuppressWarnings("Duplicates")
    private void startUsernameHost() {
        /*
            Here the game object and the exercises.client object are created and links are started, as well as the scene is changed. The game is then started.
            At the moment, only the current scene is changed to the day scene.
         */
        this.Logic = new WerewolfGame(this, this.Username);
        FXMLLoader loader = new FXMLLoader();
        try {
            loader.setLocation(getClass().getResource("/view/UsernameScreen.fxml"));
            Parent root = loader.load();
            ScreenController = loader.getController();
            initController();
            primaryStage.setScene(new Scene(root));
            ((UsernameScreenController)loader.getController()).EnableHost();
            this.server = new Server(2412);
            Thread serverThread = new Thread(this.server);
            serverThread.start();
            // ((DayScreenController) ScreenController).EnableAllies(); Shows Allies for WerewolfSites, implementation later
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function forwards the message coming from the controller to the exercises.client object
     * @param newMsg is the message
     */
    public void sendMessage(Message newMsg) {
        client.sendMessage(newMsg);
    }
    public Player[] getPlayers(){ return Logic.getPlayers();}
    public Player getMe(){
        return Logic.getMe();
    }

    public void printMessage(Message newMsg) {
        ScreenController.printMessage(newMsg);
    }

    public void setServerIP(String serverIP) {
        this.ServerIP = serverIP;
    }
    public void callMayorElectionEvent(){
        DayScreenController.callMayorElectionEvent();}
    public void callHunterEvent() {
        NightScreenController.callHunterEvent();
    }

    public void callMayorEvent() {
        DayScreenController.callMayorEvent();
    }

    public void callWitchHealEvent(Player toheal) {
        NightScreenController.callWitchHealEvent(toheal);
    }

    public void callWitchKillEvent() {
        NightScreenController.callWitchKillEvent();
    }

    public void callCupidEvent() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(DayScreenController != null) {
            DayScreenController.callCupidEvent();
        }else{
            System.out.println("DayScreenController is NULL");
        }
    }

    public void callSeerEvent() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(NightScreenController != null) NightScreenController.callSeerEvent();
    }

    public void endEvent() {
        DayScreenController.endEvent();
        NightScreenController.endEvent();
    }
    public void callTownVotePhase(int length){
        Timer t = new Timer(this, length);
        DayScreenController.setPhase("Town Phase");
        DayScreenController.EnableVoteButtons();
        t.startTownTimer();
    }
    public void callWerewolfPhase(int length){
        Platform.runLater( () -> {
            Timer t = new Timer(this, length);
            if(getMe().getRole().getEvil()) {
                NightScreenController.setPhase("WerewolfPhase");
                NightScreenController.EnableVoteButtons();
                t.startWerewolfTimer();
            }else{
                NightScreenController.setPhase("WerewolfPhase");
                t.start();
            }
        });
    }

    public void switchToNight() {
        setScreenController(NightScreenController);
        Platform.runLater(()->{
        System.out.println("Switched to NightScene");
        NightScreenController.updateScreen(true);
        primaryStage.setScene(NightScene);
        NightScreenController.addText("----------------\nNight " + getDayCounter() + " has started\n----------------");
        });
    }

    public void updatePlayerList(){
        DayScreenController.updateLists();
        NightScreenController.updateLists();
    }
    public void switchToDay() {
        Platform.runLater(()-> {
            setScreenController(DayScreenController);
            System.out.println("Switched to DayScene");
            DayScreenController.updateScreen(true);
            primaryStage.setScene(DayScene);
            DayScreenController.addText("----------------");
            DayScreenController.addText("Day "+ getDayCounter() + " has started");
            DayScreenController.addText("----------------");
        });
    }

    public boolean existPlayer(String p){
        Player players[] = getPlayers();
        for(int i = 0; i < players.length; i++){
             if(p.equals(players[i].getName())){
                 return true;
             }
        }
        return false;
    }
    public void hideTimer(){
        DayScreenController.hideTimer();
        NightScreenController.hideTimer();
    }
    public String getDayCounter() {
        return Integer.toString(Logic.getDayCount());
    }
    public String getNightCounter() {
        return Integer.toString(Logic.getDayCount());
    }
    public void switchScene(){
        Platform.runLater(()-> {
            if (getPrimaryStage().getScene().equals(getDayScene())) {
                System.out.println("Switched to NightScene");
                setScreenController(getNightScreenController());
                getNightScreenController().updateScreen(true);
                getPrimaryStage().setScene(getNightScene());
            } else {
                System.out.println("Switched to DayScene");
                primaryStage.setScene(getDayScene());
                getDayScreenController().updateScreen(true);
                setScreenController(getDayScreenController());
            }
        });
    }
    public void updatePrepareScreen(Datapacket d){
        if(DayScreenController != null){
            DayScreenController.updatePrepareScreen(d);
        }

    }
    public void setClient(Client c, Thread clientThread) {
        this.client = c;
        this.clientThread = clientThread;
    }
    public ArrayList<Player> getAlivePlayers() {
        return Logic.getAlivePlayers();
    }
    public ArrayList<Player> getMayorDecisionPossibilities() {
        return Logic.getMayorDecisionPossibilities();
    }

    // TODO Weiterverarbeiten
    public void CupidDecision(Player choosen1, Player choosen2) {
        Logic.setCouple(choosen1, choosen2);
        Player[] couple = new Player[2];
        couple[0] = choosen1;
        couple[1] = choosen2;
        client.sendGameObject(new Datapacket(2, null, null, new Data(couple), null));
        System.out.print("Couple are:\n" + couple[0].getName()+ " and " + couple[1].getName() + "\n");
    }
    // TODO Weiterverarbeiten
    public void MayorDecision(Player choosen) {
        addText("You decided to kill " + choosen.getName());
        String mjp = this.MayorJumpPoint;
        this.MayorJumpPoint = "";
        client.sendGameObject(new Datapacket(2, null, null, new Data(choosen, "Mayordecided", mjp), null));
    }
    // TODO Weiterverarbeiten
    public void WitchHealDecisionYes() {
        Logic.WitchHealDecision(true);
    }
    public void WitchHealDecisionNo() { Logic.WitchHealDecision(false); }
    public void WitchKillDecisionYes(Player choosen) { Logic.WitchKillDecision(choosen); }
    public void WitchKillDecisionNo() { Logic.WitchKillDecision(null); }

    public String getMayorJumpPoint() {
        return MayorJumpPoint;
    }

    public void SeerDecision(Player choosen) {
        client.sendGameObject(new Datapacket(2, null, null, new Data(choosen, "Seermove"), null));
    }
    // TODO Weiterverarbeiten
    public void HunterDecisionYes(Player choosen, String hunterJumpPoint) {

    }
    // TODO Weiterverarbeiten(Nur falls du ein Nein für deine Logik brauchst ansonsten ruhig ignorieren, das Fenster schließst sich automatisch)
    public void HunterDecisionNo(String hunterJumpPoint) {

    }
    public ArrayList<Player> getWerewolvesList() {
        return Logic.getWerewolvesList();
    }
    public Player[] getCouples() {
        return Logic.getCouple();
    }

    public ArrayList<Player> getDeadPlayers() {
        return Logic.getDeadPlayers();
    }

    public boolean isPreparing() {
        return isPreparing;
    }
    // TODO Weiterverarbeiten
    public void TownVoteDecision(Player choosen){
        DayScreenController.DisableVoteButtons();
        if(choosen == null) choosen = new Player(".");
        client.sendGameObject(new Datapacket(2, null, null, new Data(choosen, "TownVote"), null));
    }

    public void WerewolfVoteDecision(Player choosen){
        NightScreenController.DisableVoteButtons();
        if(choosen == null) choosen = new Player(".");
        client.sendGameObject(new Datapacket(2, null, null, new Data(choosen,"Werewolfmove"), null));
    }

    public Player getWerewolveDecisionPlayer() {
        return this.WerewolveDecisionPlayer;
    }
    public void setWerewolveDecisionPlayer(Player p) {
        this.WerewolveDecisionPlayer = p;
    }
    public Player getTownDecisionPlayer() {
        return this.TownDecisionPlayer;
    }
    public void setTownDecisionPlayer(Player p) {
        this.TownDecisionPlayer = p;
    }

    public void updateStartScreen() {
        updateScreen(false);
    }

    public void MayorElectionDecision(Player choosen) {
        client.sendGameObject(new Datapacket(2, null, null, new Data(choosen, "voteMayor"), null));
    }

    public void addText(String t) {
        ScreenController.addText(t);
    }

    public boolean isNight() {
        return ScreenController == NightScreenController;
    }

    public void endGame(String reason) {
        Platform.runLater(()->{
        DayScreenController.setPhase(reason);
        NightScreenController.setPhase(reason);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/EndScreen.fxml"));
        try {
            Parent root = loader.load();
            EndScreenController c = loader.getController();
            Scene s = new Scene(root);

                c.setGame(this);
                c.initScreen(reason);
                primaryStage.setScene(s);

        } catch (IOException e) {
            e.printStackTrace();
        }
        });
    }

    public void setMayorJumpPoint(String nextStep) {
        this.MayorJumpPoint = nextStep;
    }

    public String getHunterJumpPoint() {
        return this.HunterJumpPoint;
    }
}