package portailEV3;

import org.json.JSONException;

import ca.ualberta.awhittle.ev3btrc.MessageBluetooth;
import lejos.hardware.RemoteBTDevice;

import lejos.hardware.lcd.LCD;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;
import portailEV3.enumeration.EtatPortail;
import portailEV3.hardware.CapteurContact;
import portailEV3.hardware.CapteurPresence;
import portailEV3.hardware.Porte;
import portailEV3.hardware.Utilisateur;
import portailEV3.hardware.Vehicule;
import portailEV3.log.LogEV3;
import portailEV3.runnable.MoteurRunnable;
import portailEV3.runnable.PortailRunnable;
import portailEV3.runnable.SoundRunnable;


public class Brick{

	private static MessageBluetooth msgTelecommande;
	private static int msgVoiture;
	private static boolean app_alive, isConnected;
	
	private static CapteurContact capteurGaucheOuvert = new CapteurContact(SensorPort.S1);
	//private static CapteurContact capteurDroitOuvert = new CapteurContact(SensorPort.S2);
	private static CapteurContact capteurPortailFerme = new CapteurContact(SensorPort.S3);

	private static CapteurPresence capteurPresence = new CapteurPresence(SensorPort.S4);

	private static Porte porteGauche = new Porte(MotorPort.A);
	private static Porte porteDroite = new Porte(MotorPort.B);

	private static MoteurRunnable moteurDroit;
	private static MoteurRunnable moteurGauche;
	private static PortailRunnable portail;
	
	private static SoundRunnable sound;
	
	private static ServerTCP server = new ServerTCP();
	
	private static int nombre=0;
	private static int cptServer=0;
	
	public static void main(String[] args) throws InterruptedException{
		
		sound = new SoundRunnable();
		new Thread(sound).start();
		
		moteurDroit = new MoteurRunnable(capteurGaucheOuvert, capteurPortailFerme, porteDroite, capteurPresence, sound, "droit");
		moteurGauche = new MoteurRunnable(capteurGaucheOuvert, capteurPortailFerme, porteGauche, capteurPresence, sound, "gauche");
		
		// INITIALISATION DES THREADS
		new Thread(moteurGauche).start();
		new Thread(moteurDroit).start();
		
		portail = new PortailRunnable(moteurDroit, moteurGauche);
		new Thread(portail).start();
		
		System.out.println("Debut de l'initialisation");
		
		// Initialisation du portail
		moteurDroit.setAction(1);
		moteurGauche.setAction(1);
		moteurDroit.resumeThread();
		moteurGauche.resumeThread();

		
		while (moteurDroit.getIsRunning() || moteurGauche.getIsRunning()) {
			Thread.sleep(1000);
		}
		
		if (portail.getEtatPortail() == EtatPortail.OUVERT) {
			moteurDroit.setAction(2);
			moteurGauche.setAction(2);
			moteurDroit.resumeThread();
			moteurGauche.resumeThread();
		}

		
		while (moteurDroit.getIsRunning() || moteurGauche.getIsRunning()) {
			Thread.sleep(1000);
		}
		
		if (!capteurPortailFerme.contact()) {
			LogEV3.addError("Erreur lors de l'initialisation");
			LCD.clear();
			LCD.drawString("Erreur lors de l'initialisation", 0, 5);
			Delay.msDelay(5000);
			LCD.clear();
			LCD.refresh();
		} else {
			
			System.out.println("Succes de l'initialisation");
			
			
			app_alive = true;
			
			int codeTelecommandePrecedent = 0;
			MessageBluetooth msgTelecommandePrecedent = null;
			msgVoiture = 0;
			
			while(app_alive){
				System.out.println("En attente de co : ");
				EcouteBT EBT = new EcouteBT();
				EBT.start();
				System.out.println("Connecté : ");
				isConnected = true;
				
				//Tant que la connexion est active
				while(isConnected) {
					msgTelecommande = EBT.msg;
					if (msgTelecommande != null && msgTelecommande != msgTelecommandePrecedent) {
						msgTelecommandePrecedent = msgTelecommande;
						
						switch(msgTelecommande.getCommande()){						
							case "commande":
								//Selon le message envoye depuis la tablette
								switch(msgTelecommande.getParams().get(0)){
								
									//Demande d'ouverture totale par l'application
									case "ouvTotale":
										System.out.println("TEST_BT_DEVICE :" +EBT.getBTadress());
										portail.majEtatPortail();
										ouvertureTotale();										
										break;
								
									//Demande d'ouverture partielle par l'application
									case "ouvPartielle":
										portail.majEtatPortail();
										ouverturePartielle();
										break;
										
									//Preparation de la gestion de la deconnexion, ne fonctionne pas encore 
									case "deconnexion":
										isConnected = false;
										System.out.println("Test deconnexion");
										break;
									default:
										System.out.println("test : "+(msgTelecommande.getParams()));
										break;
								}
								break;	
							
							//Connexion de l'application au portail	
							case "connexion":
								System.out.println(msgTelecommande.getParams().get(0) + " " + msgTelecommande.getParams().get(0));
								break;
								
							//Gestion de la création des users
							case "newuser":
								Utilisateur u = new Utilisateur(msgTelecommande.getParams().get(0), msgTelecommande.getParams().get(1));
								try {
									//Ecriture et lecture du fichiers "Utilisateurs"
									u.writeUtilisateur(u,"Utilisateurs");
									u.readUtilisateur(u,"Utilisateurs");
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								break;
								
							//Gestion de la création de nouveaux véhicules
							case "newvehicule":
								Vehicule v = new Vehicule(msgTelecommande.getParams().get(0), msgTelecommande.getParams().get(1));
								try {
									//Ecriture et lecture du fichiers "Vehicules"
									v.writeVehicule(v,"Vehicules");
									v.readVehicule(v,"Vehicules");
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								break;
								
							default:
								System.out.println("testDefault : ");
								System.out.println(msgTelecommande.getParams());
								break;
								
						}
						
					} else if(msgVoiture != 0) {
						System.out.println("TEST VOITURE");
						portail.majEtatPortail();
						ouvertureTotale();
						
					} 		
					
					//Appelle de la fonction ouverturePortailVoiture, pour lancer le serveur TCP							
					portail.majEtatPortail();
					ouverturePortailVoiture();														
					
				}
				System.out.println("INTERRUPTION!!!!!!!!!!!!!!!!!!!!!!!!");
				EBT.disconnect();
				EBT.interrupt();
			}
		}
	}

	//Actions effectuées lorsqu'on demande l'ouverture via l'application du véhicule
	public static void ouverturePortailVoiture() throws InterruptedException {
		
		//Condition pour que le lancement du serveur TCP ne s'effectue qu'une fois
		if(cptServer==0) {			
			server.start();
			server.connectionTCP(80);
			System.out.println("Attente Voiture");
			cptServer=1;			
		}
		
		//Condition : la voiture doit s'etre connecte au serveur TCP et ce dernier doit etre lance (variable cptServer)
		if(server.is_connect == true && cptServer==1) {
			System.out.println("Connection client success");
			System.out.println("La voiture entre");
			moteurDroit.setAction(3);
			moteurGauche.setAction(3);
			moteurDroit.resumeThread();
			moteurGauche.resumeThread();
			Thread.sleep(5500);
			System.out.println("La voiture est entree");
			moteurDroit.setAction(4);
			moteurGauche.setAction(4);
			moteurDroit.resumeThread();
			moteurGauche.resumeThread();
			cptServer=2;
		}
	}

	//Actions effectuées lorsqu'on demande l'ouverture totale via l'application 
	public static void ouvertureTotale() {
		switch (portail.getEtatPortail()) {
			case FERME:
				moteurDroit.setAction(1);
				moteurGauche.setAction(1);
				moteurDroit.resumeThread();
				moteurGauche.resumeThread();
				break;
			case ARRETE_EN_FERMETURE:
				moteurDroit.setAction(1);
				moteurGauche.setAction(1);
				moteurDroit.resumeThread();
				moteurGauche.resumeThread();
				break;
			case OUVERT:
				moteurDroit.setAction(2);
				moteurGauche.setAction(2);
				moteurDroit.resumeThread();
				moteurGauche.resumeThread();
				break;
			case ARRETE_EN_OUVERTURE:
				moteurDroit.setAction(2);
				moteurGauche.setAction(2);
				moteurDroit.resumeThread();
				moteurGauche.resumeThread();
				break;
			case OUVERT_PARTIELLEMENT:
				moteurGauche.setAction(2);
				moteurGauche.resumeThread();
				break;
			case EN_OUVERTURE_PARTIELLE:
				moteurDroit.stopThread();
				moteurGauche.stopThread();
				break;
			case EN_OUVERTURE_TOTALE:
				moteurDroit.stopThread();
				moteurGauche.stopThread();
				break;
			case EN_FERMETURE_TOTALE:
				moteurDroit.stopThread();
				moteurGauche.stopThread();
				break;
			case EN_FERMETURE_PARTIELLE:
				moteurGauche.stopThread();
				break;
			default:
				break;
		}
	}
	
	//Actions effectuées lorsqu'on demande l'ouverture partielle via l'application 
	public static void ouverturePartielle() {
		switch (portail.getEtatPortail()) {
			case FERME:
				moteurGauche.setAction(1);
				moteurGauche.resumeThread();
				break;
			case ARRETE_EN_FERMETURE:
				moteurDroit.setAction(2);
				moteurGauche.setAction(2);
				moteurDroit.resumeThread();
				moteurGauche.resumeThread();
				break;
			case OUVERT:
				moteurDroit.setAction(2);
				moteurGauche.setAction(2);
				moteurDroit.resumeThread();
				moteurGauche.resumeThread();
				break;
			case ARRETE_EN_OUVERTURE:
				moteurDroit.setAction(2);
				moteurGauche.setAction(2);
				moteurDroit.resumeThread();
				moteurGauche.resumeThread();
				break;
			case OUVERT_PARTIELLEMENT:
				moteurGauche.setAction(2);
				moteurGauche.resumeThread();
				break;
			case EN_OUVERTURE_TOTALE:
				moteurDroit.stopThread();
				moteurGauche.stopThread();
				break;
			case EN_OUVERTURE_PARTIELLE:
				moteurDroit.stopThread();
				moteurGauche.stopThread();
				break;
			case EN_FERMETURE_TOTALE:
				moteurDroit.stopThread();
				moteurGauche.stopThread();
				break;
			case EN_FERMETURE_PARTIELLE:
				moteurGauche.stopThread();
				break;
			default:
				break;
		}
	}
	
}