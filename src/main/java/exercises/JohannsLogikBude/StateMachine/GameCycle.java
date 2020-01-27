package exercises.JohannsLogikBude.StateMachine;

import logic.Timer;
import network.server.WerewolfgameServer;

/**
 * @author Johann Hein
 */

public class GameCycle {
    private State state;
    public static final String testTag = "Cupidmove";

    private GameCycle() {
        this.state = State.CUPID_SEND;
    }
    public void useRoleMove() {
        this.state = state.simpleFunction();
    }

    public static String getTag() {
        return WerewolfgameServer.sentData.getTag();
    }

   public static void main(String[] args) {
        GameCycle g = new GameCycle();
        Timer timer = new Timer(g,10000000, 1000);
        timer.startStateMachine();
        //GameCycle game = new GameCycle();
        /*for(int i = 0; i < 10; i++) {
            game.useRoleMove();
        }*/
        //timer.schedule( new Task(), 0, 1000);
    }

   public static void testTimer() {
        System.out.println("test");
   }
}
