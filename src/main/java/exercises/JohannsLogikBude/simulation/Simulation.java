package exercises.JohannsLogikBude.simulation;


import models.Player;
import models.Role;
import models.roles.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/** A Simulation of 'Werwolf' game
 * @author Johann Hein */
class Simulation {
    /** All players saved in an array */
    private final Player[] players;
    /** max players*/
    private final int numberOfPlayers;
    /** */
    private final ArrayList<Player> alivePlayers;
    /** */
    private final ArrayList<Player> deathList;
    /** number of passed days*/
    private int dayCount;
    /** current mayor */
    private Player mayor;
    /** 2 players being lovers  */
    private Player[] couple;
    /** current phase: DAY - NIGHT */
    private String phase;
    /** victim chosen during werewolf - phase */
    private Player chosenByWerewolves;
    /** true if hunter hasn't used his skill yet */
    private boolean hunterDone;

    /** Constructor - Simulation
     * @param numberOfPlayers max players
     */
    private Simulation(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        this.players = new Player[numberOfPlayers];
        this.couple = new Player[2];
        this.alivePlayers = new ArrayList<>();
        this.deathList = new ArrayList<>();
        this.phase = "NIGHT";
    }
    /** generates and sets random players for the simulation */
    private void setRandomPlayers() {
        String name;
        for (int i = 0; i < 8; i++) {
            name = "Player" + i;
            players[i] = new Player(name);

        }
    }
    //-------------------------------------------------------------------------
    /** All players added to the alivePlayers - list */
    private void arrayToList() {
        this.alivePlayers.addAll(Arrays.asList(players));
    }

    /** Checks if good wins
     * @return true if good wins
     */
    private boolean goodWins () {
        for(Player player: alivePlayers) {
            if(player.getRole().getEvil()) {
                return false;
            }
        }
        System.out.println("Good wins!");
        return true;
    }

    /** Checks if evil wins
     * @return true if evil wins
     */
    private boolean evilWins() {
        for(Player player: alivePlayers) {
            if(!player.getRole().getEvil()) {
                return false;
            }
        }
        System.out.println("Evil wins!");
        return true;
    }

    /** Checks if the couple wins
     * @return true if the couple wins
     */
    private boolean coupleWins() {
        if(couple[0] == null || couple[1] == null) {
            return false;
        }
        for(Player player: alivePlayers) {
            if(!player.getName().equals(couple[0].getName()) && !player.getName().equals(couple[1].getName())) {
                return false;
            }
        }
        System.out.println("Couple wins!");
        return true;
    }

    /** Calls all win-condition-methods
     * @return true if game can be finished
     */
    private boolean checkWinConditions() {
        if(coupleWins()) {
            return true;
        }
        if(goodWins()) {
            return true;
        }
        return evilWins();
    }
//----------------------------------------------------------------------------------------
    /** Prints a ArrayList
     * @param players list which should be printed
     */
    private void printList(ArrayList<Player> players) {
        for(Player p: players) {
            System.out.println(p.getName());
        }
    }

    /** Sets role of every player */
    private void setRandomRoles() {
        Random r = new Random();
        ArrayList<Role> rmRoles = new ArrayList<>();
        rmRoles.add(new Witch());
        rmRoles.add(new Hunter());
        rmRoles.add(new Seer());
        rmRoles.add(new Cupid());
        rmRoles.add(new Werewolf());
        rmRoles.add(new Werewolf());
        rmRoles.add(new Civilian());
        rmRoles.add(new Civilian());

        Role role;
        int n;
        for (Player player: players) {
            n = r.nextInt(rmRoles.size());
            role = rmRoles.get(n);
            rmRoles.remove(n);
            player.setRole(role);
        }
    }
    /** Switches phase: DAY - NIGHT */
    private void switchPhase() {
        if(phase.equals("NIGHT")) {
            phase = "DAY";
        }
        else phase = "NIGHT";
    }
    /** Checks if one of the lovers (couple) is already dead */
    private boolean checkCouple() {
        if(couple[0].getAlive() && couple[1].getAlive()) {
            return false;
        }
        else return couple[0].getAlive() || couple[1].getAlive();
    }
    /** Kills the other lover if one is dead */
    private void killCouple() {
        if(couple[0].getAlive()) {
            couple[0].setAlive();
            alivePlayers.remove(couple[0]);
            deathList.add(couple[0]);
            System.out.println(couple[0].getName() + " has been killed (couple-death)");
        }
        else {
            couple[1].setAlive();
            alivePlayers.remove(couple[1]);
            deathList.add(couple[1]);
            System.out.println(couple[1].getName() + " has been killed (couple-death)");
        }
    }

    /** Choses a random Player who isn't a werewolf
     * @return player chosen randomly
     */
    private Player killRandomPlayer() {
        Random r = new Random();
        Player p = alivePlayers.get(r.nextInt(alivePlayers.size()));
        while(p.getRole() instanceof Werewolf) {
             p = alivePlayers.get(r.nextInt(alivePlayers.size()));
        }
       return p;
    }
    /** Searches for a player with a certain role
     * @param role which player should be searched
     * @return player with given role */
    private Player searchPlayer(String role) {
        for (Player player : players) {
            if (player.getRole().getName().equals(role)) {
                return player;
            }
        }
        System.out.println("No player found!");
        return null;
    }

    /** Prints a Player-array
     * @param p player-array
     */
    private void printPlayers(Player[] p) {
        System.out.println("Players: ");
        for (Player aP : p) {
            System.out.print(aP.getName());
            System.out.println("  Role: " + aP.getRole().getName());
        }
    }

    /** Choose 2 players to be a couple
     * @param playerCupid the cupid in the game
     */
    private void cupidMove (Player playerCupid) {
        System.out.println(playerCupid.getName() + "'s turn:");
        Cupid r = (Cupid) playerCupid.getRole();
        this.couple = r.cupidSkill(alivePlayers);
        System.out.println(couple[0].getName() + " and " + couple[1].getName() + " are a couple!");
    }

    /** Choose 1 player to see if he is good or evil
     * @param playerSeer the seer in the game
     */
    private void seerMove(Player playerSeer) {
        Seer s = (Seer) playerSeer.getRole();
        System.out.println(playerSeer.getName() + "'s turn:");
        Player check = s.seerSkill(alivePlayers);
        if (check.getRole().getEvil()) {
            System.out.println(check.getName() + " is evil!");
        } else {
            System.out.println(check.getName() + " is not evil!");
        }
    }

    /** Decide if the victom of the werewolfes should be revived. Also choose a player to kill
     * @param playerWitch the witch of the game
     * @return true if killed player is a hunter
     */
    private boolean witchMove(Player playerWitch) {
        Witch w = (Witch) playerWitch.getRole();
        if (w.checkPotions() > 0) {
            if (w.useHeal(chosenByWerewolves)) {
                chosenByWerewolves = null;
            }
        }
        if (w.checkPotions() == -1 || w.checkPotions() == 2) {
            Player deadPlayer = w.useDeath(alivePlayers);
            if(deadPlayer != null) {
                deadPlayer.setAlive();
                alivePlayers.remove(deadPlayer);
                deathList.add(deadPlayer);
                System.out.println(deadPlayer.getName() + " has been killed by the witch");
                return deadPlayer.getRole() instanceof Hunter;
            }
        }
        return false;
    }

    /** Choose a player to kill
     * @param playerHunter the hunter of the game
     */
    private void hunterMove (Player playerHunter) {
        Hunter h = (Hunter) playerHunter.getRole();
        Player killedBHunter = h.hunterSkill(alivePlayers);
        killedBHunter.setAlive();
        alivePlayers.remove(killedBHunter);
        deathList.add(killedBHunter);
        System.out.println(killedBHunter.getName() + " has been killed by Hunter!");
        this.hunterDone = true;
    }

    /** See what position a player has
     * @param p any existing player
     * @return integure as index, returns -1 if the player is not found
     */
    private int getIndex(Player p) {
        for(int i = 0; i < this.players.length; i++) {
            if (this.players[i].getName().equals(p.getName())) {
                return i;
            }
        }
        return -1;
    }

    /** Searches for the greatest value of an int-array
     * @param votes any int[]
     * @return greatest value
     */
    private int getGreatestValue (int[] votes)  {
        int a = votes[0];
        for(int i = 1; i < votes.length; i++) {
            if(votes[i] > a) {
                a = votes[i];
            }
        }
        return a;
    }

    /** Adds all existing werewolves to a list
     * @return ArrayList containing players with role 'Werewolf'
     */
    private ArrayList<Player> getWerewolves () {
        ArrayList<Player> werewolves = new ArrayList<>();
        for(Player player: this.players) {
            if(player.getRole() instanceof Werewolf) {
                werewolves.add(player);
            }
        }
        return werewolves;
    }

    /** Collect and convert votes from Players to integures
     * @param players all players who are supposed to vote
     * @return an array containing the votes
     */
    private int[] receiveVotes(ArrayList<Player> players) {
        int[] votes = new int[numberOfPlayers];
        for(Player player: players) {
            votes[getIndex(player.vote(alivePlayers))]++;
        }
        return votes;
    }

    /** Disclaims mayor's function
     * @param votedPlayers players to choose from
     * @return chosen player
     */
    private Player mayorAction (ArrayList<Player> votedPlayers) {
        //choose by list
        Random r = new Random();
        int i = r.nextInt(votedPlayers.size());
        return votedPlayers.get(i);
    }

    /** Starts voting and evaluate the vote
     * @param players players who are supposed t o vote
     * @return chosen play from whole voting process
     */
    private Player startVoting(ArrayList<Player> players) {
        int[] votes = receiveVotes(players);
        int a = getGreatestValue(votes);
        ArrayList<Player> votedPlayers = new ArrayList<>();
        for (int i = 0; i < votes.length; i++) {
            if(votes[i] == a) {
                votedPlayers.add(this.players[i]);
            }
        }
        if(votedPlayers.size() == 1) {
            return votedPlayers.get(0);
        }
        else if (this.mayor != null){
            return mayorAction(votedPlayers);
        }
        else {
            Random r = new Random();
            int n = r.nextInt(votedPlayers.size());
            return votedPlayers.get(n);
        }
    }
    /** Disclamins the game cycle */
    private void startSimulation() {
        Player playerCupid = searchPlayer("Cupid");
        Player playerWitch = searchPlayer("Witch");
        Player playerHunter = searchPlayer("Hunter");
        Player playerSeer = searchPlayer("Seer");
        ArrayList<Player> werewolves = getWerewolves();
        boolean hunterDead = false;
        Player toBeKilled = null;

        while (true) {
            if (checkWinConditions()) {
                break;
            }
            chosenByWerewolves = null;

            System.out.println("Day: " + dayCount);
            //NIGHT
            System.out.println("\n" + phase);
            //CUPID
            if (dayCount == 0) {
                assert playerCupid != null;
                cupidMove(playerCupid);
            }
            //SEER
            assert playerSeer != null;
            if (playerSeer.getAlive()) {
                seerMove(playerSeer);
            }

            //WEREWOLVES
            //chosenByWerewolves = startVoting(werewolves);
            chosenByWerewolves = killRandomPlayer();
            System.out.println(chosenByWerewolves.getName() + " was chosen by the werewolves!");

            //WITCH
            assert playerWitch != null;
            if (playerWitch.getAlive()) {
                hunterDead = witchMove(playerWitch);
            }
            if (checkWinConditions()) {
                break;
            }

            if (chosenByWerewolves != null) {
                chosenByWerewolves.setAlive();
                alivePlayers.remove(chosenByWerewolves);
                deathList.add(chosenByWerewolves);
                if(chosenByWerewolves.getRole() instanceof Hunter) {
                    hunterDead = true;
                }
                System.out.println(chosenByWerewolves.getName() + " has been killed!");
            }
            if (checkWinConditions()) {
                break;
            }
            if(checkCouple()) {
                killCouple();
            }
            if (checkWinConditions()) {
                break;
            }
            // --- DAY ---
            switchPhase();
            if(hunterDead && !hunterDone) {
                assert playerHunter != null;
                hunterMove(playerHunter);
            }
            if (checkWinConditions()) {
                break;
            }
            if(checkCouple()) {
                killCouple();
            }
            if (checkWinConditions()) {
                break;
            }
            System.out.println("Alive players:");
            printList(alivePlayers);
            System.out.println("Dead players:");
            printList(deathList);
            if (checkWinConditions()) {
                break;
            }
            if(checkCouple()) {
                killCouple();
            }
            if(checkWinConditions()) {
                break;
            }

            if (mayor == null) {
                this.mayor = startVoting(alivePlayers);
            }
            //village votes
            toBeKilled = startVoting(alivePlayers);
            toBeKilled.setAlive();
            alivePlayers.remove(toBeKilled);
            System.out.println(toBeKilled.getName() + " has been killed by voting!");
            if (checkWinConditions()) {
                break;
            }
            if(checkCouple()) {
                killCouple();
            }
            if(checkWinConditions()) {
                break;
            }
            if (!playerHunter.getAlive() && !hunterDone) {
                hunterMove(playerHunter);
            }
            if(checkCouple()) {
                killCouple();
            }
            if(checkWinConditions()) {
                break;
            }
            dayCount++;
            switchPhase();

        }
        System.out.println("\n\nSimulation done!");
    }

    public static void main (String[] args){
        Simulation sim = new Simulation(8);
        sim.setRandomPlayers();
        sim.setRandomRoles();
        sim.arrayToList();
        sim.printPlayers(sim.players);
        sim.startSimulation();
        System.out.println("Simulation-test: DONE!");
    }
}