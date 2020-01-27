package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Johann Hein
 */

public class Player implements Serializable {
	private final String name;
	private boolean alive;
	private Role role;
	private Player lover;
	private String DeathReason = "";

	public Player(String name) {
		this.name = name;
		this.alive = true;
	}

	public void setRole(Role r) {
		this.role = r;
	}

	public void setDeathReason(String deathReason) {
		DeathReason = deathReason;
	}

	public void setAlive() {
		this.alive = false;
	}

	public String getName() {
		return this.name;
	}

	public boolean getAlive() {
		return this.alive;
	}

	public Role getRole() {
		return this.role;
	}

	@Override
	public String toString(){
		return this.name;
	}
	public Player getLover() {
		return this.lover;
	}
	public void setLover(Player lover) {
		this.lover = lover;
	}
	public boolean isLover(){
		return (this.lover != null);
	}

	public Player vote(ArrayList<Player> players) {
	    //random-vote so far...
		Player p = null;
		//Scanner scanner = new Scanner(System.in);
		//System.out.println("Choose a player!");
		/*while(p == null) {
			p = searchPlayer(scanner.nextLine(), players);
		}*/
		Random r = new Random();
		int i = r.nextInt(players.size());
		p = players.get(i);
		return p;
	}

    public String getDeathReason() {
		return this.DeathReason;
    }
}