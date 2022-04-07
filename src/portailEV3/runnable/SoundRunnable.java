package portailEV3.runnable;

import lejos.hardware.Sound;

public class SoundRunnable implements Runnable {
	
	private volatile boolean isRunning = false;
	
	@Override
    public void run() {
		Sound.setVolume(1);
		while(true) {
			if(this.isRunning) {
				try {
					playTune();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    }

    public void stopThread() {
        this.isRunning = false;
    }
    
    public void resumeThread() {
        this.isRunning = true;
    }
    
    public void playTune() throws InterruptedException
    {
    	int a = 600;
    	int b = 525;
    	int c = 450;

    	if(this.isRunning)
    		Sound.playTone(a, 200); 
    	if(this.isRunning)
    		Sound.playTone(a, 200);     
    	if(this.isRunning)
    		Sound.playTone(a, 200); 
    	if(this.isRunning)
    		Sound.playTone(a, 500); 
    	
    	if(this.isRunning)
    		Sound.playTone(c, 500);
    	if(this.isRunning)
    		Sound.playTone(b, 500);
    	if(this.isRunning)
    		Sound.playTone(a, 300);
    	if(this.isRunning)
    		Sound.playTone(b, 200);
    	if(this.isRunning)
    		Sound.playTone(a, 1000);
    }

}