package portailEV3.hardware;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public class CapteurPresence {

	EV3UltrasonicSensor CapteurPresence;

	public CapteurPresence(Port port) {
		this.CapteurPresence = new EV3UltrasonicSensor(port);
		System.out.println("Capteur de presence initialise");
	}

	public boolean presence() {

		float[] sample = new float[CapteurPresence.sampleSize()];
		CapteurPresence.fetchSample(sample, 0);

		float etat = sample[0];
		
		if (etat < 0.2)
			return true;
		else
			return false;

	}
}
