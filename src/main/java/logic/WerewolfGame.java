package logic;

import game.Game;
import javafx.application.Platform;
import models.Player;
import network.Data;
import network.Datapacket;

import java.util.ArrayList;

/**
 * @author Lukas Allermann
 * @author Tim Berger
 */

@SuppressWarnings("ALL")
public class WerewolfGame{
    private Game game;
    private String Username;
    //attributes from game (simulation)
    /** all existing players - 8 players yet: NOT LESS NOR MORE */
    private Player[] players;
    /** List of Players who are alive (attribute player.alive == true)*/
    private ArrayList<Player> alivePlayers;
    /** alivePlayers | deadPlayers */
    private ArrayList<Player> deadPlayers = new ArrayList<>();
    private  ArrayList<Player> DyingPlayers = new ArrayList<>(); // List that on Switch to DayScreen the Messages come from
    /** current day */
    private int dayCount;
    /** current mayor */
    private Player mayor;
    /** couples - chosen by Cupid at the beginning of the game */
    private Player[] couple;

    private String phase;
    // list has to be made out of players - not implemented yet */
    private ArrayList<Player> werewolvesList;
    private ArrayList<Player> MayorDecisionPossibilities;
    private Player me;
    public Player getMe() { return me; }

    public WerewolfGame(Game g, String usr){
        this.game = g;
        this.Username = usr;
        this.werewolvesList = new ArrayList<>();
        this.alivePlayers = new ArrayList<>();
        this.dayCount = 0;
    }
    private void defineMe(Player me){
        for(Player p : players){
            if(p.getName().equals(me.getName())){
                p = me;
            }
        }
    }
    private void updateMe(){
            for(int i = 0; i < players.length; i++){
                if(players[i].getName().equals(game.getUsername())){
                    this.me = players[i];
                }
            }
    }

    public void dataProcessing(Data data) {
        if(data.getTag().equals("voteMayor")){
            Platform.runLater(()-> presentDeaths() );
            if(game.isNight()) game.switchToDay();
            game.setMayorJumpPoint(data.getTag2());
            try {
                if (this.checkWin()) {
                    game.endGame(checkWinConditions());
                    return;
                } else {
                    if (me.getAlive()) game.callMayorElectionEvent();
                }
            } catch (Exception e){
                if (me.getAlive()) game.callMayorElectionEvent();
            }
        }
        if (data.getTag().equals("Mayerelected")){
            Player newmayor = data.getPlaceholder();
            for(int i = 0; i < players.length; i++){
                if(players[i].getName().equals(newmayor.getName())){
                    this.mayor = players[i];
                }
            }
            String nextStep = game.getMayorJumpPoint();
            System.out.println("After MayorElection: nextStep is: " + nextStep);
            game.addText("Server: The election has ended, your mayor is " + mayor.getName()+ "!");
            if(nextStep.equals("Couple")) {
                if (couple == null) {
                    game.updateStartScreen();
                    game.getGameScreenController().setPhase("Cupid Move");
                    if (me.getRole().getName().equals("Cupid")) {
                        this.couple = new Player[2];
                        game.callCupidEvent();
                    }
                }
            } else if(nextStep.equals("StartDay")){
                if(this.checkWin()) {
                    game.endGame(checkWinConditions());
                    return;
                }
                StartDay();
            } else if(nextStep.equals("SeerMove")){
                if(this.checkWin()) {
                    game.endGame(checkWinConditions());
                    return;
                }
                SeerMove();
            } else if(nextStep.equals("WerewolfMove")){
                WerewolfMove();
            }
        }
        if(data.getTag().equals("StartDay")){
            StartDay();

        }
        if(data.getTag().equals("UPDATE")) {
            if (me == null) game.getGameScreenController().addText("----------------\nDay " + dayCount + " has started\n----------------");
            this.dayCount = data.getDaycount();
            this.players = data.getAllPlayers();
            this.alivePlayers = data.getAlivePlayers();
            this.mayor = data.getPlaceholder();
            this.dayCount = data.getDaycount();
            //updateDeaths(data);
            updateMe();
            /*
            if (me.getLover() == null) {
                if (couple != null) {
                    if (me.getName().equals(couple[0].getName())) {
                        me.setLover(couple[1]);
                        defineMe(me);
                        game.getDayScreenController().addText("Server: You have fallen in love with " + couple[1].getName());
                    } else if (me.getName().equals(couple[1].getName())) {
                        me.setLover(couple[0]);
                        defineMe(me);
                        game.getDayScreenController().addText("Server: You have fallen in love with " + couple[0].getName());
                    }
                }
            }
            */
            for (Player p : players) if (p.getRole().getName().equals("Werewolf")) this.werewolvesList.add(p);
            game.updateScreen(false);
        }


        if(data.getTag().equals("Seermove")){
            SeerMove();
        }
        if(data.getTag().equals("CoupleUpdate")){
            this.couple = new Player[2];
            Player[] couplearray = data.getPlayerArray();
            Player choosen1 = null, choosen2 = null;
            for(int i = 0; i < players.length; i++){
                if(players[i].getName().equals(couplearray[0].getName())){
                    choosen1 = players[i];
                } else if(players[i].getName().equals(couplearray[1].getName())){
                    choosen2 = players[i];
                }
            }
            this.couple[0] = choosen1;
            this.couple[1] = choosen2;
            if(me.getName().equals(choosen1.getName())) {
                me.setLover(choosen2);
                game.getGameScreenController().addText("Server: You have fallen in love with " + choosen2.getName());
            }
            if(me.getName().equals(choosen2.getName())){
                me.setLover(choosen1);
                game.getGameScreenController().addText("Server: You have fallen in love with " + choosen1.getName());
            }
            game.updateScreen(false);
        }
        if(data.getTag().equals("GameEnding")){
            String reason = data.getTag2();
            game.endGame(reason);
        }
        if(data.getTag().equals("Werewolfmove")) {
            WerewolfMove();
        }
        if(data.getTag().equals("Witchmove")) WitchHealEvent(data.getPlaceholder());
        if(data.getTag().equals("Killedbyvillage")) {
            game.addText("The village has decided: " + data.getPlaceholder().getName() + " will be hanged!");
        }
        if(data.getTag().equals("deathUpdate")){
            updateDeaths(data);
        }
        if (data.getTag().equals("Mayordecide")){
            this.MayorDecisionPossibilities = data.getSomePlayerList();
            if (me.getName().equals(mayor.getName())) game.callMayorEvent();
            else game.addText("The village couldn't decide, so your mayor have to!");
        }
        if (data.getTag().equals("Mayornews")) game.addText("Your mayor decided to kill " + data.getPlaceholder().getName() + "!");


    }

    private boolean checkWin() {
        String s = "";
        try {
            s = checkWinConditions();
        } catch (Exception e){
            throw new IllegalStateException();
        }
        if(s.equals("")){
            return false;
        } else if(s.equals("GoodWins")){
            return true;
        } else if(s.equals("CoupleWins")){
            return true;
        } else return s.equals("EvilWins");
    }

    private String checkWinConditions() {
        try {
            if (coupleWins()) {
                return "CoupleWins";
            }
        } catch (Exception e){
            throw new IllegalStateException();
        }
        if(goodWins()) {
            return "GoodWins";
        }
        return evilWins();
    }

    private String evilWins() {
        for(Player player: alivePlayers) {
            if(!player.getRole().getEvil()) {
                return "";
            }
        }
        System.out.println("Evil wins!");
        return "EvilWins";
    }

    private boolean goodWins() {
        for(Player player: alivePlayers) {
            if(player.getRole().getEvil()) {
                return false;
            }
        }
        System.out.println("Good wins!");
        return true;
    }

    private boolean coupleWins() {
        Player couple1, couple2;
        try {
            couple1 = couple[0];
            couple2 = couple[1];
        } catch (NullPointerException e){
            throw new IllegalStateException();
        }
        if(alivePlayers.size() == 2){
            if(alivePlayers.get(0).getName().equals(couple1.getName()) && alivePlayers.get(1).getName().equals(couple2.getName())){
                return true;
            }else if(alivePlayers.get(1).getName().equals(couple1.getName()) && alivePlayers.get(0).getName().equals(couple2.getName())){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    private void StartDay() {
        System.out.println("Goes in StartDay");
        if (game.isNight()) {
            game.switchToDay();
        }
        presentDeaths();
        game.callTownVotePhase(45);
    }

    private void presentDeaths() {
        if(DyingPlayers.size() >= 1){
            for(int i = 0; i < DyingPlayers.size(); i++){
                game.addText("Server: " + DyingPlayers.get(i).getName() + " " + DyingPlayers.get(i).getDeathReason() + ". He was " + DyingPlayers.get(i).getRole().getName() + ".");
            }
            DyingPlayers.clear();
        }
    }

    private void WerewolfMove() {
        if(!game.isNight()) game.switchToNight();
        game.getGameScreenController().DisableVoteButtons(); // Better more than to less
        game.updatePlayerList();
        game.callWerewolfPhase(35);
    }


    private boolean updateDeathHelp(ArrayList<Player> PWD, Player p){
        for(Player q: PWD){
            if(q.getName().equals(p.getName())) return false;
        }
        return true;
    }
    private void updateDeaths(Data data) {
        ArrayList<Player> newDeaths = data.getSomePlayerList();
        ArrayList<Player> oldDeadPlayers = this.deadPlayers;
        ArrayList<Player> newdeadPlayers = new ArrayList<>();
        for(int i = 0; i < players.length; i++){
            for(int j = 0; j < newDeaths.size(); j++){
                if(players[i].getName().equals(newDeaths.get(j).getName())){
                    players[i].setAlive();
                    players[i].setDeathReason(newDeaths.get(j).getDeathReason());
                    newdeadPlayers.add(players[i]);
                    if(me.getName().equals(players[i])){
                        me.setAlive();
                    }
                    if(updateDeathHelp(oldDeadPlayers, players[i])){
                        DyingPlayers.add(players[i]);
                    }
                }
            }
            this.deadPlayers = newdeadPlayers;
        }
    }

    private void SeerMove(){
        game.getGameScreenController().DisableVoteButtons();
        game.switchToNight();
        game.updatePlayerList();
        game.getGameScreenController().setPhase("Seer Move");
        if(me.getRole().getName().equals("Seer") && me.getAlive()) {
            game.callSeerEvent();
        }
    }
    /*
     How to call Events:
            Obv. before you call any Event, the Lists AlivePlayer, and the Player[] have to be initialized
            game.callHunterEvent();
            game.callMayorEvent();  before you load, fill the MayorDecisionPossibilities
            game.callWitchHealEvent(Player dyingPlayer); Send The Player with the function who is dying so the name can be displayed
            game.callWitchKillEvent();
            game.callCupidEvent();
            game.callSeerEvent();
            */

    /**
     * The existing implementation is for test purposes only and can therefore be changed as required
     */

    public ArrayList<Player> getDeadPlayers() {
        return deadPlayers;
    }


    private void startGame(){


        while(true){
            game.getController().setTimer(120); // In This Testing purpose this is done by Hand
            // game.getScreenController().setTimer(120);
            game.getController().setPhase("Preparing Phase"); // look above
            try {
                TestPhase();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Player> getWerewolvesList() {
        return werewolvesList;
    }

    public ArrayList<Player> getAlivePlayers() {
        return alivePlayers;
    }


    public Player[] getCouple() {
        return couple;
    }
    public void WitchHealDecision(boolean heal){
        if(heal) {
            game.getClient().sendGameObject(new Datapacket(2, null, null, new Data(new Player("."), "WitchHealed"), null));
        }
        else game.getClient().sendGameObject(new Datapacket(2, null, null, new Data(new Player("."), "WitchNOHeal"), null));
        if (me.getRole().getDeathPotion()) game.callWitchKillEvent();
        else game.getClient().sendGameObject(new Datapacket(2, null, null, new Data(new Player("."), "NoKill"), null));

    }

    public void WitchHealEvent(Player p){
        game.getGameScreenController().setPhase("Witch Move");
        if(me.getRole().getName().equals("Witch")){
           if(me.getRole().getHealPotion()) game.callWitchHealEvent(p);
           else if(me.getRole().getDeathPotion())  game.callWitchKillEvent();
        }
    }

    public void WitchKillDecision(Player p){
        if(p == null) game.getClient().sendGameObject(new Datapacket(2, null, null, new Data(new Player("."), "NoKill"), null));
        else game.getClient().sendGameObject(new Datapacket(2, null, null, new Data(p, "Kill"), null));

    }

    /**
     * The existing implementation is for test purposes only and can therefore be changed as required
     */
    public void EvaluationPhase(){

        game.getController().incrementTimer();
    }

    /**
     * The existing implementation is for test purposes only and can therefore be changed as required
     */
    public void DiscussionPhase() {

        // call "game.getScreencontroller().prepareScreen(); which sets the Timer, Names the Labels etc.

    }

    public ArrayList<Player> getMayorDecisionPossibilities() {
        return MayorDecisionPossibilities;
    }

    private void TestPhase() throws InterruptedException {
        game.getController().EnableVoteButtons();
        for(int i = 0; i < 120; i++){
            Thread.sleep(1000); // Maybe change to 995-998 etc., thinking of processing time
            game.getController().incrementTimer();
            /*  As this Loop goes on, the Players will be able to chat
                In The VotinPhase the Players get Buttons to manipulate the Voting Array which can be
                be evaluated after the Loop.*/
            // Testing
            if(i == 3) game.getController().setPhase("Another Preparing Phase");
        }
        game.getController().DisableVoteButtons();


    }


    public Player[] getPlayers() {
        return players;

    }

    public void setCouple(Player choose1, Player choose2) {
        couple = new Player[2];
        couple[0] = choose1;
        couple[1] = choose2;
    }

    public int getDayCount(){ return dayCount; }

}
