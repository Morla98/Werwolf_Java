package models.roles;

import models.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Johann Hein
 */

public class Hunter extends Role implements Serializable {
    private boolean weaponLoaded;
    public Hunter () {
        this.name = "Hunter";
        this.weaponLoaded = true;
        this.aboutRole = "YOU ARE A HUNTER!\n\nOf course you should try to survive, but if you die you fire one last shot to kill a player of choice.\n\nSo if you can't safe yourself then at least try to safe all the innocent civilians!";
    }

    public Player hunterSkill(ArrayList<Player> players) {
        //chose from players-array a player and return it to kill
        System.out.println("Hunter:");
        Scanner scanner = new Scanner(System.in);
        Player p = null;
        System.out.println("Choose a player to kill!");
        while(p == null) {
            p = searchPlayer(scanner.nextLine(), players);
        }
        return p;
    }
    public boolean getWeaponLoaded() {
        return this.weaponLoaded;
    }
    public void setWeaponLoaded() {
        this.weaponLoaded = false;
    }

    public boolean getHealPotion() { return false; }

    public boolean getDeathPotion() { return false; }

    public void setHealPotion(boolean healPotion) {}

    public void setDeathPotion(boolean deathPotion) {}
}
