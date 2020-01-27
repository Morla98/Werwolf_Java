package models.roles;

import models.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Johann Hein
 */

public class Seer extends Role implements Serializable {

    public Seer() {
        this.name = "Seer";
        this.aboutRole = "YOU ARE A SEER!\n\nAs a Seer you are good-natured creature that is acting in sense of the innocent village inhabitants.\n\nEvery night you can see behind the facade of one player of your choise. The next day it's your task to brighten up your fellows about who is evil and who is not. You should use your ability wisely!";
    }

    public Player seerSkill (ArrayList<Player> players) {
        //choose from players-array a player and return to get information about him
        System.out.println("Seer:");
        Player p = null;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose a player!");
        while(p == null) {
            p = searchPlayer(scanner.nextLine(), players);
        }
        return p;
    }

    public boolean getHealPotion() { return false; }

    public boolean getDeathPotion() { return false; }

    public void setHealPotion(boolean healPotion) {}

    public void setDeathPotion(boolean deathPotion) {}
}
