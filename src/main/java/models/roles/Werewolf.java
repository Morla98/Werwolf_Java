package models.roles;

import models.Role;

import java.io.Serializable;

/**
 * @author Johann Hein
 */

public class Werewolf extends Role implements Serializable {

    public Werewolf() {
        this.name = "Werewolf";
        this.aboutRole = "YOU ARE A WEREWOLF!\n\nEveryone fears you, because you and your comrade are only satisfied when all of them are dead.\n\nBut be carefull! If they uncover your identity they will hang you instantly!";
        this.evil = true;
    }

    @Override
    public boolean getHealPotion() { return false; }

    @Override
    public boolean getDeathPotion() { return false; }

    public void setHealPotion(boolean healPotion) {}

    public void setDeathPotion(boolean deathPotion) {}


}
