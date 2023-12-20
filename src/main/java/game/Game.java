package game;

import controller.Controller;
import controller.EndScreenController;
import controller.GameScreenController;
import controller.UsernameScreenController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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

import static java.lang.System.exit;

/**
 * @author Lukas Allermann
 */

public class Game extends Application {
    private String MayorJumpPoint;
    private Player WerewolveDecisionPlayer;
    private boolean host;
    private Stage primaryStage;
    private GameScreenController gameScreenController;
    private Controller controller;
    private String Username;
    private Scene scene;
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
    private String serverIP;


    public void setPlayer(Player player){ this.player = player; }
    public Player getPlayer() { return this.player; }
    public Client getClient(){ return this.client; }
    public WerewolfGame getLogic() {
        return Logic;
    }
    public void setLogic(WerewolfGame logic) {
        Logic = logic;
    }
    public GameScreenController getGameScreenController() {
        return gameScreenController;
    }
    public void setGameScreenController(GameScreenController gameScreenController) {
        this.gameScreenController = gameScreenController;
    }
    public void callTestEvent(){
        gameScreenController.callTestEvent();
    }

    public ArrayList<Tuple> getSpielerliste() {
        return Spielerliste;
    }
    public void setSpielerliste(ArrayList<Tuple> spielerliste) {
        Spielerliste = spielerliste;
    }
    public Controller getController() {
        return controller;
    }
    private void setController(Controller controller) {
        this.controller = controller;
    }
    private Scene getScene() {
        return scene;
    }
    public void setScene(Scene scene) {
        this.scene = scene;
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
        // maximizeScreen();
        try {
            musicThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        playWavFile("/sounds/bg_sound.wav");
                    }
                }
            });
            musicThread.start();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/StartScreen.fxml"));
            Parent root = loader.load();
            controller = loader.getController();
            controller.initScreen();
            initController();
            Scene scene = new Scene(root,1080,720);
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    exit(0);
                }
            });
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);

            primaryStage.setTitle("Werwolf the Game");
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void maximizeScreen(){
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        if (primaryStage.isFullScreen()) {
            primaryStage.setWidth(1080);
            primaryStage.setHeight(720);
            primaryStage.setFullScreen(false);
            System.out.println("Minimize");
        } else {
            primaryStage.setX(bounds.getMinX());
            primaryStage.setY(bounds.getMinY());
            primaryStage.setWidth(bounds.getWidth());
            primaryStage.setHeight(bounds.getHeight());
            primaryStage.setFullScreen(true);
            System.out.println("Maximize");
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
            loader.setLocation(getClass().getResource("/view/GameScreen-draft.fxml"));
            Parent root = loader.load();
            this.gameScreenController = loader.getController();
            controller = gameScreenController;
            controller.hideTimer();
            initController();
            gameScreenController.initScreen();
            if(this.host) {
                gameScreenController.EnableHost();}
            this.scene = new Scene(root);
            primaryStage.setScene(this.scene);
            updatePrepareScreen(d);
            // ((DayScreenController) ScreenController).EnableAllies(); Shows Allies for WerewolfSites, implementation later
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        Thread LogicThread = new Thread(Logic);
        LogicThread.start();
        */
        controller.setPhase("Preparation Phase");
    }

    private void initController(){
        controller.setGame(this);
        controller.setPrimaryStage(this.primaryStage);
    }

    public void connectionSuccess(Datapacket d){
        Platform.runLater(()-> startGame(d));
    }
    public void connectionFailed(){
        this.client = null;
        this.serverIP = null;
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
        controller.updateScreen(t);
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
            controller = loader.getController();
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
            controller = loader.getController();
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
        controller.printMessage(newMsg);
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public String getServerIP(){
        return this.serverIP;
    }

    public void callMayorElectionEvent(){
        gameScreenController.callMayorElectionEvent();
    }

    public void callHunterEvent() {
        gameScreenController.callHunterEvent();
    }

    public void callMayorEvent() {
        gameScreenController.callMayorEvent();
    }

    public void callWitchHealEvent(Player toheal) {
        gameScreenController.callWitchHealEvent(toheal);
    }

    public void callWitchKillEvent() {
        gameScreenController.callWitchKillEvent();
    }

    public void callCupidEvent() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(gameScreenController != null) {
            gameScreenController.callCupidEvent();
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
        if(gameScreenController != null) gameScreenController.callSeerEvent();
    }

    public void endEvent() {
        gameScreenController.endEvent();
    }

    public void callTownVotePhase(int length){
        Timer t = new Timer(this, length);
        gameScreenController.setPhase("Town Phase");
        gameScreenController.EnableVoteButtons();
        t.startTownTimer();
    }
    public void callWerewolfPhase(int length){
        Platform.runLater( () -> {
            Timer t = new Timer(this, length);
            if(getMe().getRole().getEvil()) {
                gameScreenController.setPhase("WerewolfPhase");
                gameScreenController.EnableVoteButtons();
                t.startWerewolfTimer();
            }else{
                gameScreenController.setPhase("WerewolfPhase");
                t.start();
            }
        });
    }

    public void switchToNight() {
        // setController(NightScreenController);
        Platform.runLater(()->{
        System.out.println("Switched to NightScene");
        gameScreenController.updateScreen(true);
        primaryStage.setScene(NightScene);
        gameScreenController.addText("----------------\nNight " + getDayCounter() + " has started\n----------------");
        });
    }

    public void updatePlayerList(){
        gameScreenController.updateLists();
    }
    public void switchToDay() {
        Platform.runLater(()-> {
            setController(gameScreenController);
            System.out.println("Switched to DayScene");
            gameScreenController.updateScreen(true);
            primaryStage.setScene(scene);
            gameScreenController.addText("----------------");
            gameScreenController.addText("Day "+ getDayCounter() + " has started");
            gameScreenController.addText("----------------");
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
        gameScreenController.hideTimer();
    }
    public String getDayCounter() {
        return Integer.toString(Logic.getDayCount());
    }
    public String getNightCounter() {
        return Integer.toString(Logic.getDayCount());
    }

    public void updatePrepareScreen(Datapacket d){
        if(gameScreenController != null){
            gameScreenController.updatePrepareScreen(d);
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
        gameScreenController.DisableVoteButtons();
        if(choosen == null) choosen = new Player(".");
        client.sendGameObject(new Datapacket(2, null, null, new Data(choosen, "TownVote"), null));
    }

    public void WerewolfVoteDecision(Player choosen){
        gameScreenController.DisableVoteButtons();
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
        controller.addText(t);
    }

    public boolean isNight() {
       //  return controller == NightScreenController; // todo
        return false;
    }

    public void endGame(String reason) {
        Platform.runLater(()->{
        gameScreenController.setPhase(reason);
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