package network;

import models.Player;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The Data class containing all of the game data
 * @author Tim Berger
 */
public class Data implements Serializable {
    private Player[] playerArray;
    private ArrayList<Player> SomePlayerList;
    private String tag2;
    private String nextStep;
    private final String tag;
    private Player[] allPlayers;
    private ArrayList<Player> alivePlayers;
    private ArrayList<Player> deadPlayers;
    private Player[] couple;
    private int daycount;
    private Player placeholder;


    /**
     * Constructor used by Server to sync all Player lists and the phase
     * @param all updated Array of all Players
     * @param alive updated list of alive Players
     * @param dead updated list of dead Players
     *
     */
// alivePlayers - deadPlayers - dayCount - mayor - String (contains kill message(s))
    public Data(Player[] all, ArrayList<Player> alive, ArrayList<Player> dead, int daycount, Player mayor, Player[] couple) {
        tag = "UPDATE";
        this.allPlayers = all;
        this.alivePlayers = alive;
        this.deadPlayers = dead;
        this.daycount = daycount;
        this.placeholder = mayor;
        this.couple = couple;
    }
    public Data(String tag, ArrayList<Player> PlayerList){
        this.tag = tag;
        this.SomePlayerList = PlayerList;
    }
    public Data(String tag, String tag2){
        this.tag = tag;
        this.tag2 = tag2;
    }

    public Data(Player[] couple){
        tag = "Cupidmove";
        this.couple = couple;
    }

    public Data(Player p, String tag){
        this.tag = tag;
        this.placeholder = p;
    }
    public Data(String tag){
        this.tag = tag;
    }
    public Data(Player p, String tag, String nextStep){
        this.tag = tag;
        this.placeholder = p;
        this.nextStep = nextStep;
    }

    public Data(String tag, Player[] playerArray) {
        this.tag = tag;
        this.playerArray = playerArray;
    }

    public String getTag() {
        return tag;
    }
    public String getTag2() {
        return tag2;
    }

    public String getNextStep() {
        return nextStep;
    }

    public ArrayList<Player> getSomePlayerList() {
        return SomePlayerList;
    }

    /**
     * Getter for allPlayers
     * @return returns list of all players in the game
     */
    public Player[] getAllPlayers() {
        return allPlayers;
    }

    /**
     * Getter for list alivePlayers
     * @return returns a list of alive players
     */
    public ArrayList<Player> getAlivePlayers() {
        return alivePlayers;
    }

    /**
     * Getter for list of dead players
     * @return returns list of dead players
     */
    public ArrayList<Player> getDeadPlayers() {
        return deadPlayers;
    }

    /**
     * Getter for couple list
     * @return returns list of chosen couple;
     */
    public Player[] getCouple() {
        return couple;
    }


    public int getDaycount() {return daycount;}

    public Player getPlaceholder() {return placeholder; }

    public ArrayList<Player> getPlayers() {
        return this.SomePlayerList;
    }

    public Player[] getPlayerArray() {
        return this.playerArray;
    }
}
