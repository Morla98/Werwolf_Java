package logic;

import exercises.JohannsLogikBude.StateMachine.GameCycle;
import game.Game;

/**
 * @author Lukas Allermann
 */

@SuppressWarnings("Duplicates")
public class Timer {
    private GameCycle gameCycle;
    private Game game;
    private int seconds = 120;
    private int StepSize = 1000;
    public Timer(Game g, int se, int st){
        this.game = g;
        this.seconds = se;
        this.StepSize = st;
    }
    public Timer(Game g){
        this.game = g;
    }
    public Timer(Game g, int se){
        this.game = g;
        this.seconds = se;
    }
    public Timer(GameCycle gc, int se, int st) {
        this.gameCycle = gc;
        this.seconds = se;
        this.StepSize = st;
    }
    public void setTimer(int s){
        this.seconds = s;
    }
    public void start(int s){
        Thread t = new Thread(()->{
            game.getController().setTimer(s);
            game.getGameScreenController().setTimer(s);
            System.out.println("Starting Timer");
            for(int i = 0; i < s; i++){
                getGame().getController().incrementTimer();
                try {
                    Thread.sleep(StepSize);
                    System.out.println("Timer: " + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("TimerThread Error");
                }
            }
            System.out.println("Timer finished");
            hideTimer();
        });
        t.start();
    }
    public void start(){
        Thread t = new Thread(() -> {
            game.getController().setTimer(seconds);
            game.getGameScreenController().setTimer(seconds);
            System.out.println("Starting Timer");
            for(int i = 0; i < seconds; i++){
                getGame().getController().incrementTimer();
                try {
                    Thread.sleep(StepSize);
                    System.out.println("Timer: " + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("TimerThread Error");
                }
            }
            System.out.println("Timer finished");
            hideTimer();
        });
        t.start();
    }
    public void hideTimer(){
        game.getGameScreenController().hideTimer();
    }
    public Game getGame() {
        return this.game;
    }
    public void startWerewolfTimer(){
        game.getGameScreenController().EnableVoteButtons();
        Thread t = new Thread(()->{
            game.getController().setTimer(seconds);
            game.getGameScreenController().setTimer(seconds);
            System.out.println("Starting Timer");
            for(int i = 0; i < seconds; i++){
                getGame().getController().incrementTimer();
                try {
                    Thread.sleep(StepSize);
                    System.out.println("Timer: " + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("TimerThread Error");
                }
            }
            if(game.getWerewolveDecisionPlayer() != null) System.out.println("Timer finished\nWerewolf Desision: " + game.getWerewolveDecisionPlayer().getName());
            game.WerewolfVoteDecision(game.getWerewolveDecisionPlayer());
            game.setWerewolveDecisionPlayer(null);
            hideTimer();
        });
        t.start();
    }
    public void startTownTimer() {
        game.getGameScreenController().EnableVoteButtons();
        Thread t = new Thread(() -> {
            game.getController().setTimer(seconds);
            game.getGameScreenController().setTimer(seconds);
            System.out.println("Starting Timer");
            for (int i = 0; i < seconds; i++) {
                getGame().getController().incrementTimer();
                try {
                    Thread.sleep(StepSize);
                    System.out.println("Timer: " + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("TimerThread Error");
                }
            }
            System.out.println("Timer finished");
            game.TownVoteDecision(game.getTownDecisionPlayer());
            game.setTownDecisionPlayer(null);
            hideTimer();
        });
        t.start();
    }

    public void startStateMachine() {
            Thread t = new Thread(() -> {
                System.out.println("Starting Timer");
                for (int i = 0; i < seconds; i++) {
                    try {
                        Thread.sleep(StepSize);
                        System.out.println("Timer: " + i);
                        gameCycle.useRoleMove();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.out.println("TimerThread Error");
                    }
                }
                System.out.println("Timer finished");
            });
            t.start();
        }
}
