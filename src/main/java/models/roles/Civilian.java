package models.roles;

import models.Role;

import java.io.Serializable;

/**
 * @author Johann Hein
 */

public class Civilian extends Role implements Serializable {

    public Civilian () {
        this.name = "Civilian";
        this.aboutRole = "YOU ARE CIVILIAN!\n\nAs a Civilian you are constantly at risk of being killed by dark creatures.\n\nPay attention who could be the traitor amongst you and vote to kill one of them every day to rescue your own life!";
    }

    public boolean getHealPotion() { return false; }

    @Override
    public boolean getDeathPotion() { return false; }

    public void setHealPotion(boolean healPotion) {}

    public void setDeathPotion(boolean deathPotion) {}

}
