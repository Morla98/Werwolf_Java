package network.server;

import models.Player;
import models.Role;
import models.roles.*;
import network.Data;
import network.Datapacket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * The game server running the game logic, only running on host
 * @author Lukas Allermann
 * @author Tim Berger
 * @author Johann Hein
 */
@SuppressWarnings("Duplicates")
public class WerewolfgameServer implements Runnable {
    public static Data sentData;

    /**
     * The Server itself, also running the chat
     */
    private final Server server;
    /**
     * Sleep timer
     */
    private final int SleepTimer = 5;
    /**
     * Player list, containing all players
     */
    private Player[] players;
    /**
     * List containing all living players
     */
    private ArrayList<Player> alivePlayers;
    /**
     * List containing all dead players
     */
    private final ArrayList<Player> deadPlayers;
    /**
     * Day count, keeping track of passed days
     */
    private int dayCount;
    /**
     * Mayor
     */
    private Player mayor;
    /**
     * list containing the couple
     */
    private Player[] couple;
    /**
     * Player that is chosen by the werewolfs to die
     */
    private Player willBeKilled;
    /**
     * list containing all werewolves
     */
    private ArrayList<Player> werewolves;
    /**
     * String keeping tracck of the current phase
     */
    private String phase;
    private Player[] voted;
    /**
     * boolean keeping track of the health pot
     */
    private boolean witchHealPot;
    /**
     * boolean keeping traack of the death pot
     */
    private boolean witchDeathPot;
    private int playerVotes[];
    private int playerVoted = 0; // Numbers of players who votet
    private int oldPlayerVoted;

    /**
     * Constructor
     * @param server server that is running
     */
    public WerewolfgameServer(Server server) {
        this.server = server;
        this.players = new Player[server.getTulist().size()];
        this.deadPlayers = new ArrayList<>();
        this.witchDeathPot = true;
        this.witchHealPot =  true;
        this.dayCount = 0;
        this.playerVotes = new int[players.length];
    }
    private void updateAlivePlayers(){
        ArrayList<Player> newAlivePlayers = new ArrayList<>();
        for(Player p : players){
            if(p.getAlive()){
                newAlivePlayers.add(p);
            }
        }
        this.alivePlayers = newAlivePlayers;
    }
    private void defineMe(Player me){
        for(int i = 0; i < players.length; i++){
            if(players[i].getName().equals(me.getName())){
                players[i] = me;
            }
        }
    }
    /**
     * Creates a new game
     * @throws IOException
     */
    public void createGame() throws IOException {
        generatePlayers();
        phase = "DAY";
        updateClients();
        voted = new Player[players.length];
        server.sendGameObject(new Datapacket(2, null, null, new Data("voteMayor", "Couple"), null));

        //cupid
        //server.sendGameObject(new Datapacket(2, null, null, new Data(this.couple), null));
    }

    private void updateDeath() throws IOException {
        server.sendGameObject(new Datapacket(2, null, null, new Data("deathUpdate", deadPlayers), null));
    }

    /**
     * Processes the game data the server got from clients
     * @param data data that was sent by clients
     */
    public void dataProcess(Data data) throws IOException {
        if(data.getTag().equals("voteMayor")){
            playerVoted++;
            Player choosen = data.getPlaceholder();
            for(int i = 0; i < players.length; i++){ // erhöhe die Stimme des jeweiligen Players an der jeweiligen Stelle
                if(players[i].getName().equals(choosen.getName())){
                    playerVotes[i] += 1;
                }
            }
            if(playerVoted == playerAlive()) { // All Player Votes did arrived
                ArrayList<Player> votes = new ArrayList<>();
                int oldPlayerID = 0;
                int oldPlayerVotes = 0;
                for (int i = 0; i < players.length; i++) { // First Player gets choosen
                    if (playerVotes[i] >= 1) {
                        votes.add(players[i]);
                        oldPlayerVotes = playerVotes[i];
                        oldPlayerID = i;
                        break;
                    }
                }
                for (int i = oldPlayerID + 1; i < players.length; i++) { // Simple Sorting Algorithm which saves the most votet players in the votes array
                    if (playerVotes[i] > oldPlayerVotes) { // the player has more votes than the old one the old one is replaced,
                        votes.clear();
                        votes.add(players[i]);
                        oldPlayerVotes = playerVotes[i];
                    } else if (playerVotes[i] == oldPlayerVotes) { // if the Player has the Same amount of Votes he also get in the votes list
                        votes.add(players[i]);
                    }
                }
                if(votes.size() == 1) {
                    this.mayor = votes.get(0);
                    Data d = new Data(this.mayor, "Mayerelected");
                    server.sendGameObject(new Datapacket(2, null, null, d, null));
                    playerVoted = 0; // reset for the next vote
                    playerVotes = new int[players.length]; // reset for the next vote
                }else{
                    Random random = new Random();
                    int mayorID = random.nextInt(votes.size());
                    this.mayor = votes.get(mayorID);
                    Data d = new Data(this.mayor, "Mayerelected");
                    server.sendGameObject(new Datapacket(2, null, null, d, null));
                    playerVoted = 0; // reset for the next vote
                    playerVotes = new int[players.length]; // reset for the next vote
                }
            }
        }
        if(data.getTag().equals("NightBegin")){
            if(getPlayerWithRole("Seer").getAlive()){
                seerMove();
            } else {
                server.sendGameObject(new Datapacket(2, null, null, new Data("Werewolfmove"), null));
                voted = new Player[werewolves.size()];
            }
        }
        if(data.getTag().equals("Cupidmove")){
            this.couple = new Player[2];
            initCouple(data.getCouple());
            System.out.print("Couple are:\n" + couple[0].getName()+ " and " + couple[1].getName() + "\n");
            updateCouple();
            updateClients();
            //Cupid fertig, ist der Seer dran
            if(getPlayerWithRole("Seer").getAlive()) seerMove();
            //wenn Seer tot, dann direkt Werwolf dran
            else {
                server.sendGameObject(new Datapacket(2, null, null, new Data("Werewolfmove"), null));
                voted = new Player[werewolves.size()];
            }
        }

        if(data.getTag().equals("Seermove")) {
            //Seer fertig, Werwölfe sind dran;
            server.sendGameObject(new Datapacket(2, null, null, new Data("Werewolfmove"), null));
            voted = new Player[werewolves.size()];
        }

        if(data.getTag().equals("Werewolfmove")) {
            playerVoted++;
            System.out.println("Town Vote ist aufgerufen: " + "players.length: " + players.length + ", playerVoted steht bei " + playerVoted + ".");
            Player choosen = data.getPlaceholder();
            for(int i = 0; i < players.length; i++){ // erhöhe die Stimme des jeweiligen Players an der jeweiligen Stelle
                if(players[i].getName().equals(choosen.getName())){
                    playerVotes[i] += 1;
                }
            }
            if(playerVoted == werewolves.size()) { // All Player Votes did arrived
                ArrayList<Player> votes = new ArrayList<>();
                int oldPlayerID = 0;
                int oldPlayerVotes = 0;
                for (int i = 0; i < players.length; i++) { // First Player gets choosen
                    if (playerVotes[i] >= 1) {
                        votes.add(players[i]);
                        oldPlayerVotes = playerVotes[i];
                        oldPlayerID = i;
                        break;
                    }
                }
                for (int i = oldPlayerID + 1; i < players.length; i++) { // Simple Sorting Algorithm which saves the most votet players in the votes array
                    if (playerVotes[i] > oldPlayerVotes) { // the player has more votes than the old one the old one is replaced,
                        votes.clear();
                        votes.add(players[i]);
                        oldPlayerVotes = playerVotes[i];
                    } else if (playerVotes[i] == oldPlayerVotes) { // if the Player has the Same amount of Votes he also get in the votes list
                        votes.add(players[i]);
                    }
                }
                if (votes.size() == 1) {
                    willBeKilled = votes.get(0);
                    //Werwölfe fertig, Witch ist dran
                    playerVotes = new int[players.length];
                    playerVoted = 0;
                    if (getPlayerWithRole("Witch").getAlive() && (witchDeathPot || witchHealPot)) {
                        server.sendGameObject(new Datapacket(2, null, null, new Data(willBeKilled, "Witchmove"), null));
                    } else {
                        killPlayer(willBeKilled.getName(), "was mauled by the werewolves");
                        if (this.checkWin()) {
                            endGame();
                            return;
                        }
                        dayCount++;
                        updateClients();
                        updateDeath();
                        server.sendGameObject(new Datapacket(2, null, null, new Data("StartDay"), null));
                    }
                }else {
                    Random r = new Random();
                    int willbeKilledID = r.nextInt(votes.size());
                    willBeKilled = votes.get(willbeKilledID);
                    playerVotes = new int[players.length];
                    playerVoted = 0;
                    if (getPlayerWithRole("Witch").getAlive() && (witchDeathPot || witchHealPot)) {
                        server.sendGameObject(new Datapacket(2, null, null, new Data(willBeKilled, "Witchmove"), null));
                    } else {
                        killPlayer(willBeKilled.getName(), "was mauled by the werewolves");
                        willBeKilled = null;
                        if (this.checkWin()) {
                            endGame();
                            return;
                        }
                        dayCount++;
                        updateClients();
                        updateDeath();
                        server.sendGameObject(new Datapacket(2, null, null, new Data("StartDay"), null));
                        return;
                    }
                }
            }
        }

        if (data.getTag().equals("WitchHealed")) {
            System.out.println("Witch healed " + willBeKilled.getName());
            willBeKilled = null;
            witchHealPot = false;
            getPlayerWithRole("Witch").getRole().setHealPotion(false);
        }
        if (data.getTag().equals("WitchNOHeal")){
            if(couple != null) System.out.println("Couple should be: " + couple[0].getName() + " and " + couple[1].getName());
            killPlayer(willBeKilled.getName(), "was killed by the Werewolves");
            willBeKilled = null;
            System.out.println("NoHeal");
        }
        if (data.getTag().equals("NoKill")) {
            System.out.println("NoKillbyWitch");
            ++dayCount;
            voted = new Player[playerAlive()];
            updateClients();
            updateDeath();
            if(this.checkWin()) {
                endGame();
                return;
            }
            if(isMayorDead()){
                dayCount++;
                server.sendGameObject(new Datapacket(2, null, null, new Data("voteMayor", "StartDay"), null));
            }else{
                dayCount++;
                server.sendGameObject(new Datapacket(2, null, null, new Data("StartDay"),null));
            }

        }
        if (data.getTag().equals("Kill")) {
            System.out.println("Witch has killed " + data.getPlaceholder().getName());
            killPlayer(data.getPlaceholder().getName(), "was killed by Poison");
            getPlayerWithRole("Witch").getRole().setDeathPotion(false);
            witchDeathPot = false;
            ++dayCount;

            updateClients();
            updateDeath();
            if (this.checkWin()) {
                endGame();
                return;
            }
            voted = new Player[playerAlive()];
            if(isMayorDead()){
                System.out.println("Mayor died Neuwahl!");
                dayCount++;
                server.sendGameObject(new Datapacket(2, null, null, new Data("voteMayor", "StartDay"), null));
            }else{
                dayCount++;
                server.sendGameObject(new Datapacket(2, null, null, new Data("StartDay"),null));
            }

        }
        if(data.getTag().equals("Townvote2")){
            for(int i = 0; i < voted.length; i++) {
                if(voted[i] == null){
                    voted[i] = data.getPlaceholder();
                    System.out.println("Townvote ---> " + voted[i].getName());
                    break;
                }
            }
            if(voted[voted.length - 1] != null) {
                ArrayList<Player> player = new ArrayList<>();
                cutVotedArray();
                willBeKilled = null;
                if(evaluateVotes("Townvote").size() > 1) willBeKilled = evaluateVotes("Townvote").get(0);
                else {
                    player = evaluateVotes("Townvote");
                }// TODO willbeKilled NullPointerError
                if (willBeKilled != null) {
                    System.out.println("willbeKilled is: " + willBeKilled.getName());
                    System.out.println("Killedbyvillage wird aufgerufen");
                    server.sendGameObject(new Datapacket(2, null, null, new Data(willBeKilled, "Killedbyvillage"),null));
                    killPlayer(willBeKilled.getName(), "is getting lynched");
                    updateClients();
                    updateDeath();

                }else {
                    System.out.println("Mayordecide --------------------------------------------------");
                    server.sendGameObject(new Datapacket(2, null, null, new Data("Mayordecide", player),null));
                }
            }
        }
        if(data.getTag().equals("TownVote")){
            ++playerVoted;
            System.out.println("Town Vote ist aufgerufen: " + "players.length: " + players.length + ", playerVoted steht bei " + playerVoted + ".");
            Player choosen = data.getPlaceholder();
            for(int i = 0; i < players.length; i++){ // erhöhe die Stimme des jeweiligen Players an der jeweiligen Stelle
                if(players[i].getName().equals(choosen.getName())){
                    playerVotes[i] += 1;
                }
            }
            if(playerVoted == players.length) { // All Player Votes did arrived
                ArrayList<Player> votes = new ArrayList<>();
                int oldPlayerID = 0;
                int oldPlayerVotes = 0;
                for(int i = 0; i < players.length; i++){ // First Player gets choosen
                    if(playerVotes[i] >= 1){
                        votes.add(players[i]);
                        oldPlayerVotes = playerVotes[i];
                        oldPlayerID = i;
                        break;
                    }
                }
                for(int i = oldPlayerID+1; i < players.length; i++){ // Simple Sorting Algorithm which saves the most votet players in the votes array
                    if(playerVotes[i] > oldPlayerVotes){ // the player has more votes than the old one the old one is replaced,
                        votes.clear();
                        votes.add(players[i]);
                        oldPlayerVotes = playerVotes[i];
                    } else if(playerVotes[i] == oldPlayerVotes){ // if the Player has the Same amount of Votes he also get in the votes list
                        votes.add(players[i]);
                    }
                }
                if (votes.size() == 1) {
                    // Town Decision

                    killPlayer(votes.get(0).getName(), "is getting lynched");
                    updateAlivePlayers();
                    System.out.println("Town hat für " + votes.get(0).getName() + " gestimmt!");
                    playerVoted = 0; // reset for the next Town Vote
                    playerVotes = new int[players.length]; // reset for the next Town Vote
                    updateClients();
                    updateDeath();
                    if(checkWin()){
                        endGame();
                        return;
                    }
                    if(isMayorDead()){
                        if(getPlayerWithRole("Seer").getAlive()){
                            server.sendGameObject(new Datapacket(2, null, null, new Data("voteMayor", "SeerMove"), null));
                        } else {
                            voted = new Player[werewolves.size()];
                            server.sendGameObject(new Datapacket(2, null, null, new Data("voteMayor", "WerewolfMove"), null));
                        }
                    }else{
                        if(getPlayerWithRole("Seer").getAlive()){
                            server.sendGameObject(new Datapacket(2, null, null, new Data("SeerMove"), null));
                        } else {
                            voted = new Player[werewolves.size()];
                            server.sendGameObject(new Datapacket(2, null, null, new Data("Werewolfmove"), null));
                        }
                    }
                } else {
                    if(votes.size() == 0){
                        for(int i = 0; i < playerAlive(); i++){ // Fill the votes with all Players if no votes occured
                            votes.add(alivePlayers.get(i));
                        }
                    }
                    // MayorDecision
                    System.out.println("Town konnte sich nicht zwischen " + votes.size() + " Spielern entscheiden!");
                    playerVoted = 0;
                    server.sendGameObject(new Datapacket(2, null, null, new Data("Mayordecide", votes),null));
                }
            }
        }
        if (data.getTag().equals("Mayordecided")){
            server.sendGameObject(new Datapacket(2, null, null, new Data(data.getPlaceholder(), "Mayornews"), null));
            killPlayer(data.getPlaceholder().getName(), "was hanged");
            updateClients();
            updateDeath();
            if(checkWin()) return;
            if(getPlayerWithRole("Seer").getAlive()){
                seerMove();
            } else {
                server.sendGameObject(new Datapacket(2, null, null, new Data("Werewolfmove"), null));
                voted = new Player[werewolves.size()];
            }
        }
    }

    private void bugFixTownVote() {
    }

    private int playerAlive() {
        int aliveplayer = 0;
        for(int i = 0; i < players.length; i++){
            if(players[i].getAlive()){
                aliveplayer++;
            }
        }
        return aliveplayer;
    }

    private void initCouple(Player[] couple) {
        Player choosen1 = couple[0];
        Player choosen2 = couple[1];
        for(int i = 0; i < players.length; i++){
            if(players[i].getName().equals(choosen1.getName())){
                players[i].setLover(choosen2);
                this.couple[0] = players[i];
            }else if(players[i].getName().equals(choosen2.getName())){
                players[i].setLover(choosen1);
                this.couple[1] = players[i];
            }
        }
    }

    private void updateCouple() throws IOException {
        Data data = new Data("CoupleUpdate", this.couple);
        Datapacket d = new Datapacket(2, null, null, data, null);
        server.sendGameObject(d);
    }

    /**
     * run
     */
    @Override
    public void run() {

    }



    /**
     * generates the players and gves everyone a ra1ndom role
     */

    private void generatePlayers() {
        players = new Player[server.getTulist().size()];
        for(int i = 0; i < server.getTulist().size(); i++){
            Player p = new Player(server.getTulist().get(i).getName());
            players[i] = p;
        }

        Random r = new Random();
        ArrayList<Role> rmRoles = new ArrayList<>();
        rmRoles.add(new Witch());
        rmRoles.add(new Seer());
        rmRoles.add(new Cupid());
        rmRoles.add(new Werewolf());
        rmRoles.add(new Werewolf());
        rmRoles.add(new Civilian());
        rmRoles.add(new Civilian());
        // rmRoles.add(new Hunter()); // not implemented DO NOT ADD
        Role role;
        int n;
        for (Player player: players) {
            n = r.nextInt(rmRoles.size());
            role = rmRoles.get(n);
            rmRoles.remove(n);
            player.setRole(role);
        }
        werewolves = new ArrayList<>();
        for(int i = 0; i < players.length; i++){
            if(players[i].getRole().getName().equals("Werewolf")) werewolves.add(players[i]);
        }
        arrayToList();
    }

    private void cutVotedArray(){
        int counter = 0;
        for(int i = 0; i < voted.length; i++){
            if(!(voted[i].getName().equals("."))) counter++;
        }
        int place = 0;
        Player[] shorted = new Player[counter];
        for(int i = 0; i < voted.length; i++){
            if(!(voted[i].getName().equals("."))) {
                shorted[place] = voted[i];
                place++;
            }
        }
        voted = shorted;
    }
    /**
     * Werewolf list getter
     * @return
     */
    public ArrayList<Player> getWerewolves () { return werewolves; }

    /**
     * arrayToList
     */
    private void arrayToList() {
        alivePlayers = new ArrayList<>();
        this.alivePlayers.addAll(Arrays.asList(players));
    }

    /**
     * checks win conditions of goods
     * @return
     */
    // Win conditions---------------------------------------------------
    private boolean goodWins () {
        for(Player player: alivePlayers) {
            if(player.getRole().getEvil()) {
                return false;
            }
        }
        System.out.println("Good wins!");
        return true;
    }

    /**
     * Checks win conditions of evil
     * @return
     */
    private String evilWins() {
        for(Player player: alivePlayers) {
            if(!player.getRole().getEvil()) {
                return "";
            }
        }
        System.out.println("Evil wins!");
        return "EvilWins";
    }

    /**
     * checks win conditions of couple
     * @return
     */
    private boolean coupleWins() {
        Player couple1 = couple[0];
        Player couple2 = couple[1];
        if(alivePlayers.size() == 2){
            if(alivePlayers.get(0).getName().equals(couple1.getName()) && alivePlayers.get(1).getName().equals(couple2.getName())){
                return true;
            }else
                return alivePlayers.get(1).getName().equals(couple1.getName()) && alivePlayers.get(0).getName().equals(couple2.getName());
        }
        return false;
    }

    /**
     * Checks all win conditions
     * @return
     */
    public String checkWinConditions() {
        if(coupleWins()) {
            return "CoupleWins";
        }
        if(goodWins()) {
            return "GoodWins";
        }
        return evilWins();
    }
    //--------------------------------------------------------------------

    private boolean checkWin(){
        String s = checkWinConditions();
        if(s.equals("")){
            return false;
        } else if(s.equals("GoodWins")){
            return true;
        } else if(s.equals("CoupleWins")){
            return true;
        } else return s.equals("EvilWins");
    }
    /*
        This Function is called if the game is Ending
     */
    private void endGame() throws IOException {
        String s = checkWinConditions();
        Data endGameData = new Data("GameEnding", s);
        Datapacket endGameDatapacket = new Datapacket(2, null, null, endGameData, null);
        server.sendGameObject(endGameDatapacket);
    }
    /**
     * Chooses a rndom player
     * @param votedPlayers
     * @return
     */
    private Player chooseRandomly(ArrayList<Player> votedPlayers) {
        Random r = new Random();
        int i = r.nextInt(votedPlayers.size());
        return votedPlayers.get(i);
    }

    /**
     * returns a random victim
     * @return victim
     */
    private Player chooseRandomVictim() {
        Random r = new Random();
        Player p = null;
        while(p == null) {
            int n = r.nextInt(alivePlayers.size());
            if (!(alivePlayers.get(n).getRole() instanceof Werewolf)) {
                p = alivePlayers.get(n);
            }
        }
        return p;
    }

    /**
     * returns the greatest value of a integer list
     * @param votes integer list
     * @return greatest value of the list
     */
    private int getGreatestValue (int[] votes)  {
        int a = votes[0];
        for(int i = 1; i < votes.length; i++) {
            if(votes[i] > a) a = votes[i];
        }
        return a;
    }

    /**
     * get the index corresponding to the Player p
     * @param p player, whose index is needed
     * @return index
     */
    private int getIndex(Player p) {
        for(int i = 0; i < this.alivePlayers.size(); i++) {
            if (this.alivePlayers.get(i).getName().equals(p.getName())) return i;
        }
        return -1;
    }

    /**
     * Evaluates the player votes
     * @return player that got voted
     */
    private ArrayList<Player> evaluateVotes(String tag) {
        ArrayList<Player> player = new ArrayList<>();
        int[] votes = new int[playerAlive()];
        for (Player p: voted){
            for(int i = 0; i < playerAlive(); i++){
                if(p.getName().equals(alivePlayers.get(i).getName())) {
                    votes[i]++;
                }
            }
        }
        int a = getGreatestValue(votes);
        if(a == 0) {
            Random r = new Random();
            int n = r.nextInt(playerAlive());
            System.out.println("Randomized!");
            player.add(alivePlayers.get(n));
            return player;
        }
        //check if there is more than one player with highest votes
        ArrayList<Player> votedPlayers = new ArrayList<>();
        for (int i = 0; i < votes.length; i++) {
            if(votes[i] == a) votedPlayers.add(this.players[i]);
        }
        System.out.print(votedPlayers.size() + " " + votedPlayers.get(0));
        //if there is only one player voted most list will contain only one player -> return that player
        if(votedPlayers.size() == 1){
            player.add(votedPlayers.get(0));
            return player;
        }

        if(tag.equals("Werewolfmove") || tag.equals("Mayorelection")) {
            player.add(chooseRandomly(votedPlayers));
            return player;
        }
        else if (tag.equals("Townvote")){
            return votedPlayers;
        }
        return null;
    }


    /**
     * One game cycle
     * @param seer the seer
     * @param witch the witch
     * @throws InterruptedException
     * @throws IOException

    private void gameCycle (Player seer, Player witch) throws InterruptedException, IOException {
        //Seer
        if(seer.getAlive()) {
            seerMove(seer);
        }
        //Werewolf
       // this.chosenByWerewolves = werewolfMove();
        //Witch
        if(witchHealPot) {
            if(witchMoveHeal(witch, this.chosenByWerewolves)) {
               this.chosenByWerewolves = null;
            }
        }
        if(witchDeathPot) {
            //kill(witchMoveDeath(witch));
        }
        //update
        updateClients();
        //Vote mayor
        //this.mayor = voting();
        //
        updateClients();
        //Vote toBeKilled
        //kill(voting());
        //update
        updateClients();
    }
     */

    /**Updates the clients data
     *
     * @throws IOException
     */
    private void updateClients() throws IOException {
        if(this.couple != null) { // Check so that this doesn't trigger at beginning of the Game
            if (checkWin()) {
                System.out.println("checkWin is true in updateClients");
                endGame();
                return;
            }
        }
        updateAlivePlayers();
        server.sendGameObject(new Datapacket(2, null, null, new Data(players, alivePlayers, deadPlayers, dayCount, mayor, this.couple), null));
    }

    private boolean isMayorDead() {
        for(int i = 0; i < deadPlayers.size(); i++){
            if(deadPlayers.get(i).getName().equals(mayor.getName())){
                return true;
            }
        }
        return false;
    }


    /**
     * Move of the mayor
     * @param mayor mayor
     * @param votedPlayers voted players
     * @return
     * @throws InterruptedException
     */
    private Player mayorMove(Player mayor, ArrayList<Player> votedPlayers) throws InterruptedException {
        boolean sleepNoMore = false;
        Player p = null;
        //send package to 'mayor' with ArrayList<Player> 'votedPlayers'

        for (int i = 0; i < SleepTimer * 10; i++) {
            //var 'sleepNoMore' will be changed by Client
            if(sleepNoMore) {
                break;
            }
            Thread.sleep(100);
        }
        //can be manipulated by mayor sending a package
        return p;
    }

    private void hunterMove(Player hunter) {
        Hunter huntersRole = (Hunter)hunter.getRole();
        if(!hunter.getAlive() && huntersRole.getWeaponLoaded()) {
            //send package to hunter so he can return Data containing a Player
            //killPlayer('Player chosen by hunter');
        }
    }
    /**
     * Seer move
     * @throws IOException
     */
    private void seerMove() throws IOException {
        server.sendGameObject(new Datapacket(2, null, null, new Data( "Seermove"), null));
    }

    /**
     * Witch move heal
     * @param witch witch
     * @param toBeHealed person to be healed
     * @return
     * @throws InterruptedException
     */
    private boolean witchMoveHeal(Player witch, Player toBeHealed) throws InterruptedException {
        boolean sleepNoMore = false;
        boolean heal = false;

        //package to 'witch': function call
        //wait for answer
        for (int i = 0; i < SleepTimer * 10; i++) {
            //var 'sleepNoMore' will be changed by Client
            if(sleepNoMore) {
                break;
            }
            Thread.sleep(100);
        }
        //boolean 'heal' can be manipulated by witch by sending package
        return heal;
    }

    /**
     * Witch move death
     * @param witch witch
     * @return
     * @throws InterruptedException
     */
    private Player witchMoveDeath(Player witch) throws InterruptedException {
        boolean sleepNoMore = false;
        Player victim = null;

        //package to 'witch': function call

        //wait for answer
        for (int i = 0; i < SleepTimer * 10; i++) {
            //var 'sleepNoMore' will be changed by Client
            if(sleepNoMore) {
                break;
            }
            Thread.sleep(100);
        }
        //Player 'victim' can be manipulated by witch by sending a package
        return victim;
    }

    /**
     * returns the player with given role
     * @param role the role
     * @return player
     */
    private Player getPlayerWithRole(String role){
        for(int i = 0; i < players.length; i++){
            Player p = players[i];
            if(p.getRole().getName().equals(role)) return p;
        }
        return null;
    }

    private boolean isLover(String Username){
        if(this.couple[0].getName().equals(Username)) return true;
        return this.couple[1].getName().equals(Username);
    }
    private boolean isPartnerAlive(Player me){
        Player partner = null;
        if(this.couple[0].getName().equals(me.getName())) partner = this.couple[1];
        if(this.couple[1].getName().equals(me.getName())) partner = this.couple[0];
        for(int i = 0; i < players.length; i++){
            if(players[i].getName().equals(partner.getName())){
                return !players[i].getAlive();
            }
        }
        return true;
    }
    public Player[] getVoted(){ return voted; }
    public ArrayList<Player> getAlivePlayers() {return alivePlayers; }
    public void killPlayer(String username, String DeathReason) throws IOException {
        for (int i = 0; i < players.length; i++){
            Player p = players[i];
            if (p.getName().equals(username)) {
                p.setAlive();
                p.setDeathReason(DeathReason);
                defineMe(p);
                for(int j = 0; j < alivePlayers.size(); j++){
                    if(alivePlayers.get(j).equals(p.getName())){
                        alivePlayers.remove(j);
                        break;
                    }
                }
                deadPlayers.add(p);
                if (isLover(p.getName())){
                    if(!isPartnerAlive(p)){
                        killCouple(p);
                    }
                }
                // werewolves.remove(p);
                System.out.print(p.getName() + " dies!\n");

            }
        }
        updateAlivePlayers();
        updateDeath();
    }

    private void killCouple(Player me) throws IOException {
        Player partner = null;
        if(this.couple[0].getName().equals(me.getName())) partner = this.couple[1];
        if(this.couple[1].getName().equals(me.getName())) partner = this.couple[0];
        killPlayer(partner.getName(), "died from broken Heart");
    }
}

