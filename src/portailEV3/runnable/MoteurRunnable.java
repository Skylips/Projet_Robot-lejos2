package portailEV3.runnable;

import portailEV3.enumeration.EtatPorte;
import portailEV3.hardware.CapteurContact;
import portailEV3.hardware.CapteurPresence;
import portailEV3.hardware.Porte;
import portailEV3.log.LogEV3;

public class MoteurRunnable implements Runnable {

	private CapteurContact capteurOuvert;
	private CapteurContact capteurFerme;
	private CapteurPresence capteurPresence;
	private Porte porte;
	private SoundRunnable sound;
	private String cote;

	private boolean isRunning = true;
	private volatile int action; // 1 = ouvrir, 2 = fermer, 3 = ouverture voiture

	public MoteurRunnable(CapteurContact capteurOuvert, CapteurContact capteurFerme, Porte porteP, CapteurPresence cp, SoundRunnable sound, String cote) {
		this.capteurOuvert = capteurOuvert;
		this.capteurFerme = capteurFerme;
		this.porte = porteP;
		this.capteurPresence = cp;
		this.sound = sound;
		this.cote = cote;
	}

	@Override
	public void run() {
		while (true) {
			if (this.isRunning) {
				System.out.println("ACTION : "+action);
				
				//Selon la valeur envoyée depuis Brick.java, l'action du portail est choisie
				switch (this.action) {
				case 1:
					try {
						ouvrir();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					break;
					
				case 2:
					try {
						fermer();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					break;
					
				case 3:
					try {
						ouvrirVoiture();
					}catch (InterruptedException e) {
						e.printStackTrace();
					}
					break;
					
				case 4:
					try {
						fermerVoiture();
					}catch (InterruptedException e) {
						e.printStackTrace();
					}
					break;
					
				default:
					break;
				}

				this.action = 0;
				this.stopThread();
			}
		}

	}

	//Action d'ouvrir la porte 
	public void ouvrir() throws InterruptedException {
		this.porte.setEtat(EtatPorte.EN_OUVERTURE);
		this.sound.resumeThread();
		
		float lastPosition = -1000;
		
		System.out.println("OUVRIR");
		
		while (!this.capteurOuvert.contact() && this.isRunning && !this.capteurPresence.presence()) {
			
			//Si la porte a dépasse un angle defini, ou alors si la porte a encore bouge
			if (this.porte.getPosition() < 360 || this.porte.getPosition() != lastPosition) {
				this.porte.ouvrir();
				lastPosition = this.porte.getPosition();
			} else {
				Thread.sleep(1000);
				if (this.porte.getPosition() == lastPosition) {
					LogEV3.addError("Disfonctionnement capteur ouverture" + this.cote);
					System.exit(1);
					break;
				}
			}			
		}

		//Si le capteur de contact est active, alors la porte est ouverte
		if (this.capteurOuvert.contact()) {
			this.porte.setEtat(EtatPorte.OUVERTE);
		} else {
			this.porte.setEtat(EtatPorte.ARRETEE_EN_OUVERTURE);
		}
		this.sound.stopThread();
		this.porte.stop(true);
	}

	//Action de fermer la porte
	public void fermer() throws InterruptedException {
		this.porte.setEtat(EtatPorte.EN_FERMETURE);
		this.sound.resumeThread();
		
		float lastPosition = 1000;
		
		while (!this.capteurFerme.contact() && this.isRunning && !this.capteurPresence.presence()) {
			
			//Si la porte a depasse un angle defini, ou alors si la porte a encore bouge
			if (this.porte.getPosition() < 360 || this.porte.getPosition() != lastPosition) {
				this.porte.fermer();
				lastPosition = this.porte.getPosition();
			} else {
				Thread.sleep(1000);
				if (this.porte.getPosition() == lastPosition) {
					LogEV3.addError("Disfonctionnement capteur fermeture");
					System.exit(1);
					break;
				}
			}
		}

		//Si le capteur de contact est active, alors la porte est fermee
		if (this.capteurFerme.contact()) {
			this.porte.setEtat(EtatPorte.FERMEE);
		} else {
			this.porte.setEtat(EtatPorte.ARRETEE_EN_FERMETURE);
		}
		
		this.sound.stopThread();
		this.porte.stop(true);
	}

	//Action d'ouvrir la porte, quand demandé par le vehicule
	public void ouvrirVoiture() throws InterruptedException{
		this.porte.setEtat(EtatPorte.EN_OUVERTURE);
		this.sound.resumeThread();
		
		float lastPosition = -1000;
		
		System.out.println();
		
		//Le portail doit etre ferme et le vehicule doit etre detecte (capteur présence = vehicule devant)
		while (!this.capteurOuvert.contact() && this.isRunning && this.capteurPresence.presence()) {

			//Si la porte a dépasse un angle drfini, ou alors si la porte a encore bouge
			if (this.porte.getPosition() < 360 || this.porte.getPosition() != lastPosition) {
				this.porte.ouvrir();
				lastPosition = this.porte.getPosition();
			} else {
				Thread.sleep(1000);
				if (this.porte.getPosition() == lastPosition) {
					LogEV3.addError("Disfonctionnement capteur ouverture" + this.cote);
					System.exit(1);
					break;
				}
			}			
		}

		//Si le capteur de contact est active, alors la porte est ouverte
		if (this.capteurOuvert.contact()) {
			this.porte.setEtat(EtatPorte.OUVERTE);
		} else {
			this.porte.setEtat(EtatPorte.ARRETEE_EN_OUVERTURE);
		}
		this.sound.stopThread();
		this.porte.stop(true);
	}
	
	public void fermerVoiture() throws InterruptedException {
		this.porte.setEtat(EtatPorte.EN_FERMETURE);
		this.sound.resumeThread();
		
		float lastPosition = 1000;
		
		while (!this.capteurFerme.contact() && this.isRunning && this.capteurPresence.presence()) {
			if (this.porte.getPosition() < 360 || this.porte.getPosition() != lastPosition) {
				this.porte.fermer();
				lastPosition = this.porte.getPosition();
			} else {
				Thread.sleep(1000);
				if (this.porte.getPosition() == lastPosition) {
					LogEV3.addError("Disfonctionnement capteur fermeture");
					System.exit(1);
					break;
				}
			}
		}
		
		//Si le capteur de contact est active, alors la porte est fermee
		if (this.capteurFerme.contact()) {
			this.porte.setEtat(EtatPorte.FERMEE);
		} else {
			this.porte.setEtat(EtatPorte.ARRETEE_EN_FERMETURE);
		}
		
		this.sound.stopThread();
		this.porte.stop(true);
	}
		
	public void setAction(int action) {
		System.out.println("action");
		this.action = action;
	}

	public void stopThread() {
		this.isRunning = false;
	}

	public void resumeThread() {
		System.out.println("thread");
		this.isRunning = true;
	}

	public EtatPorte getEtatPorte() {
		return this.porte.getEtat();
	}
	
	public boolean getIsRunning() {
		return this.isRunning;
	}

}