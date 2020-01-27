package models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Johann Hein
 */

public abstract class Role implements Serializable {

    protected String name;
    protected String aboutRole;
    protected boolean evil;

	protected Role() {}

	public Role(String name) {
		this.name = name;
	}

	public String getName () {
        return this.name;
    }
    public String getAboutRole() {
        return this.aboutRole;
    }
    public boolean getEvil() {
	    return this.evil;
    }

    protected Player searchPlayer(String name, ArrayList<Player> players) {
        for (Player p: players) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    public abstract boolean getHealPotion();
	public abstract boolean getDeathPotion();
    public abstract void setHealPotion(boolean healPotion);
    public abstract void setDeathPotion(boolean deathPotion);


}