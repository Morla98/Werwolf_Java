package logic;

/**
 * @author Lukas Allermann
 */

@SuppressWarnings("Duplicates")
class TimerThread implements Runnable{
    private final Timer timer;
    private final int Seconds;
    private final int StepSize;

    TimerThread(Timer t, int se, int st){
        this.timer = t;
        this.Seconds = se;
        this.StepSize = st;
    }
    @Override
    public void run() {
        for(int i = 0; i < Seconds; i++){
            timer.getGame().getController().incrementTimer();
            try {
                Thread.sleep(StepSize);
                System.out.println("Timer: " + i);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("TimerThread Error");
            }
        }
        System.out.println("Timer finished");
        timer.hideTimer();
    }

}
