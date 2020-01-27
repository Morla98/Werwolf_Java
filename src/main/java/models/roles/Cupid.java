package models.roles;

import models.*;

import java.io.Serializable;
import java.util.*;

/**
 * @author Johann Hein
 */

public class Cupid extends Role implements Serializable {

    public Cupid() {
        this.name = "Cupid";
        this.aboutRole = "YOU ARE THE CUPID!\n\nCardinally you act like a normal civilian. Try to survive and kill your enemies. \n\nBut the first night you can choose a couple of players. Their lives are cobbled and if one of them dies the other one dies too!";
    }

    public Player[] cupidSkill(ArrayList<Player> players) {
        Player[] couple = new Player[2];
        String p1 = "";
        String p2 = "";
        //choose 2 players from players-array and return chosen as array(2)
        Scanner scanner = new Scanner(System.in);
        System.out.println("Cupid:");
        System.out.println("Choose first player...");
        while(true) {
            p1 = scanner.nextLine();
            couple[0] = searchPlayer(p1, players);
            if(couple[0] != null) {
                break;
            }
            else {
                System.out.println("Player not found! Choose another player...");
            }
        }
        System.out.println("Choose second player...");
        while(true) {
            p2 = scanner.nextLine();
            couple[1] = searchPlayer(p2, players);
            if(couple[1] != null && !couple[0].getName().equals(couple[1].getName())) {
                break;
            }
            else {
                System.out.println("Player not found or already chosen! Choose another player...");
            }
        }

        return couple;
    }

    public boolean getHealPotion() { return false; }
    public boolean getDeathPotion() { return false; }

    @Override
    public void setHealPotion(boolean healPotion) {}

    @Override
    public void setDeathPotion(boolean deathPotion) {}
}
