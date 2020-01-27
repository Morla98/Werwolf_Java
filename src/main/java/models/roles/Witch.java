package models.roles;

import models.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Johann Hein
 */

public class Witch extends Role implements Serializable {
    private boolean healPotion;
    private boolean deathPotion;

    public Witch () {
        this.name = "Witch";
        this.aboutRole = "YOU ARE A WITCH!\n\nYou are probably the most powerful magical being in Salem. Once a game you can recover a person that was killed by the Werewolfs from death.\n\nAlternatively you can throw a poison to kill somebody you think is evil.";
        this.healPotion = true;
        this.deathPotion = true;
    }

    public int checkPotions() {
        if(healPotion && deathPotion) {
            return 2;
        }
        else if (healPotion) {
            return 1;
        }
        else if (deathPotion) {
            return -1;
        }
        else {
            return 0;
        }
    }

    public boolean getHealPotion() {
        return healPotion;
    }

    public boolean getDeathPotion() {
        return deathPotion;
    }

    public void setHealPotion(boolean healPotion) {
        this.healPotion = healPotion;
    }

    public void setDeathPotion(boolean deathPotion) {
        this.deathPotion = deathPotion;
    }

    public boolean useHeal (Player p) {
        //decide if killed player should be revived
        Scanner scanner = new Scanner(System.in);
        System.out.println("Revive " + p.getName() + "?");
        String s = scanner.nextLine();
        while(!s.equals("yes") && !s.equals("no")) {
            System.out.println("Wrong input! Use 'yes' or 'no'!");
            s = scanner.nextLine();
        }
        if(s.equals("yes")) {
            System.out.println("Healing done!");
            healPotion = false;
            return true;
        }
        else {
            return false;
        }
    }

    public Player useDeath (ArrayList<Player> players) {
        //decide which player to kill
        System.out.println("Witch:");
        Player p = null;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose a player to kill! (use 'none' for no player)");
        String s = "";
        while(p == null) {
            s = scanner.nextLine();
            if(s.equals("none")) {
                return null;
            }
            p = searchPlayer(s, players);

        }
        deathPotion = false;
        return p;
    }
}
