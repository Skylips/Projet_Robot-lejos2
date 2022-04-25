package portailEV3;

import lejos.hardware.Sound;

public class TestTonnalite {

	public static void main(String[] args) {
		int f = 349;
    	int a = 440;
    	int cH = 523;

    		Sound.playTone(600, 200); 
    		Sound.playTone(600, 200);    
    		Sound.playTone(600, 200); 
    		Sound.playTone(600, 500); 
    		
    		Sound.playTone(450, 500);
    		Sound.playTone(525, 500);    
    		Sound.playTone(600, 300); 
    		Sound.playTone(525, 200);     		
    		Sound.playTone(600, 800); 
    		
    		/*
        		Sound.playTone(a, 500); 
        		Sound.playTone(a, 500);  
        		Sound.playTone(a, 500); 
        		Sound.playTone(f, 350); 
        		Sound.playTone(cH, 150);

        		Sound.playTone(a, 500);
        		Sound.playTone(f, 350);
        		Sound.playTone(cH, 150);
        		Sound.playTone(a, 1000);
        	*/
	}

}
