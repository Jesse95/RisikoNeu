//TODO Angriffsphase erweitern (Yannik)
//TODO verschieben nach angriff mit nur einer einheit
//TODO Javadoc
//TODO Laden eines Spiels
//TODO Speichern erweitern (Idee: Jeder Spieler bekommt beim ersten Onlinespiel eine eindeutige ID)
//TODO mit ostafrika kann man Nordafrika nicht angreifen?
//TODO Beim Karten eintauschen werden Karten nicht removed
//TODO Leerzeile zu Beginn in Konsole
//TODO Server cleanen, wenn Spiel abgebrochen, so dass Server nicht immer neu gestartet werden muss
//TODO zeigt 0 Einheiten zu Beginn an
//TODO wenn Spieleranzahl erreicht, darf Beitreten nicht mehr möglich sein
package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import client.BeitretenPanel.BeitretenButtonClicked;
import client.ButtonPanel.ButtonClickHandler;
import client.ErstellenPanel.ErstellenButtonClicked;
import client.LadenPanel.LadenButtonClicked;
import client.MapPanel.MapClickHandler;
import client.MissionPanel.KarteClickedHandler;
import client.StartPanel.StartHandler;
import local.domain.exceptions.KannLandNichtBenutzenException;
import local.domain.exceptions.KeinGegnerException;
import local.domain.exceptions.KeinNachbarlandException;
import local.domain.exceptions.LandBereitsBenutztException;
import local.domain.exceptions.NichtGenugEinheitenException;
import local.domain.exceptions.SpielerExistiertBereitsException;
import local.persistence.FilePersistenceManager;
import local.valueobjects.*;
import net.miginfocom.swing.MigLayout;


public class RisikoClientGUI extends UnicastRemoteObject implements MapClickHandler, ButtonClickHandler, ErstellenButtonClicked, KarteClickedHandler, GameEventListener, StartHandler, BeitretenButtonClicked, LadenButtonClicked {

	ServerRemote sp;
	int anzahlSpieler;
	private SpielerPanel spielerListPanel;
	public MissionPanel missionPanel;
	private MapPanel spielfeld;
	private InfoPanel infoPanel;
	private ButtonPanel buttonPanel;
	private StatistikPanel statistikPanel;
	private ConsolePanel consolePanel;
	private StartPanel startPanel;
	private ErstellenPanel erstellenPanel;
	private BeitretenPanel beitretenPanel;
	private LadenPanel ladenPanel;
	private MenuBar menu;
	private Font schrift;
	private Font uberschrift;
	private Land land1 = null;
	private Land land2 = null;
	private int anzahlSetzbareEinheiten;
	private Spieler aktiverSpieler;
	private Spieler ownSpieler;
	private JFrame frame;
	private AngriffRueckgabe angriffRueckgabe;
	private ArrayList<Spieler> spielerListe;
	private ArrayList<Land> laenderListe;
	private FilePersistenceManager pm = new FilePersistenceManager();;


	private RisikoClientGUI()throws RemoteException {
		erstesPanelStartmenu();
	}

	public static void main(String[] args)throws RemoteException{
		new RisikoClientGUI();
	}

	private void erstesPanelStartmenu() {
		//Schriften für alle Panel
		frame = new JFrame();
		uberschrift = new Font(Font.SERIF, Font.BOLD, 25);
		schrift = new Font(Font.SANS_SERIF, Font.PLAIN, 17);

		//Spielmenu Fenster erstellen
		frame.setTitle("Spiel starten");
		frame.setSize(335, 370);
		frame.setLocationRelativeTo(null);
		//frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		startPanel = new StartPanel(this);
		frame.add(startPanel);
		frame.setResizable(true);
		frame.setVisible(true);
	}

	private void zweitesPanelSpielErstellen() {
		//Spieler erstellen Fenster erstellen
		frame.setTitle("Spiel erstellen");
		frame.setSize(280, 200);
		frame.setLocationRelativeTo(null);
		//frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		erstellenPanel = new ErstellenPanel(this);
		frame.add(erstellenPanel);
		frame.setVisible(true);
		frame.repaint();
		frame.revalidate();
	}
	
	private void zweitesPanelSpielBeitreten(){
		frame.setTitle("Spiel beitreten");
		frame.setSize(320, 250);
		frame.setLocationRelativeTo(null);
		//frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		beitretenPanel = new BeitretenPanel(this);
		frame.add(beitretenPanel);
		frame.setVisible(true);
		frame.repaint();
		frame.revalidate();
	}
	
	public void zweitesPanelSpielLaden(){
		frame.remove(startPanel);
		frame.setTitle("Spiel laden");
		frame.setSize(320, 250);
		frame.setLocationRelativeTo(null);
		ladenPanel = new LadenPanel(this);
		frame.add(ladenPanel);
		frame.setVisible(true);
		ladenPanel.aktuelleSpeicherstandAuswahlAnzeigen(pm.speicherstaendeLaden());
		frame.repaint();
		frame.revalidate();
	}

	public void spielLaden(String dat) throws RemoteException, IOException {
		frame.remove(ladenPanel);
		//Verbindung mit Server aufbauen
			String servicename = "GameServer";
			Registry registry = LocateRegistry.getRegistry("127.0.0.1",4711);
				try {
					sp = (ServerRemote)registry.lookup(servicename);
				} catch (NotBoundException e1) {
					e1.printStackTrace();
				}
			sp.addGameEventListener(this);

		
		try {
			//Frame erzeugen
			frame.setLayout(new MigLayout("wrap2", "[1050][]", "[][][]"));
			spielfeld = new MapPanel(this, schrift,1050, 550);
			spielerListPanel = new SpielerPanel(schrift, uberschrift);
			missionPanel = new MissionPanel(uberschrift, schrift,this);
			infoPanel = new InfoPanel(sp.getTurn() + "", schrift, uberschrift);
			buttonPanel = new ButtonPanel(this, uberschrift);
			statistikPanel = new StatistikPanel(schrift, uberschrift);
			consolePanel = new ConsolePanel(schrift);
			frame.setSize(1250, 817);
			frame.setLocationRelativeTo(null);

			Spielstand spielstand = sp.spielLaden(dat);
			//Hier muss noch festgestellt werden wer OwnSpieler ist
			ownSpieler = spielstand.getSpielerListe().get(0);
			
			sp.spielaufbauMitSpielstand(spielstand);
			
			frame.setTitle("Risiko - Spieler: " + ownSpieler.getName());

			//Menuleiste erstellen
			menu = new MenuBar();
			Menu datei = new Menu("Datei");
			menu.add(datei);
			MenuItem speichern = new MenuItem("Speichern");
			MenuItem schliessen = new MenuItem("Schließen");
			datei.add(speichern);
			datei.add(schliessen);
			speichern.addActionListener(save -> {
				try {
					spielSpeichern();
				} catch (RemoteException e) {}
			});
			schliessen.addActionListener(close -> System.exit(0));
			menu.setFont(schrift);
			frame.setMenuBar(menu);

			//Layout anpassen
			frame.add(spielfeld, "left,spany 3,grow");
			frame.add(infoPanel, "left,growx");
			frame.add(spielerListPanel, "growx");
			frame.add(statistikPanel, "left,top,growx,spany 2");
			frame.add(missionPanel, "left,top,split3");
			frame.add(consolePanel, "left, top");
			frame.add(buttonPanel, "right,growy");
			frame.setResizable(false);
			frame.setVisible(true);
			frame.pack();
			
			sp.setTurn(spielstand.getAktuellePhase());
			
			//anzahl setzbare Einheiten?
			consolePanel.textSetzen("Spiel wurde geladen. Spieler ist dran und befindet sich in der Phase");
			infoPanel.changePanel(sp.getTurn() + "");
			
		} catch (SpielerExistiertBereitsException sebe) {
			JOptionPane.showMessageDialog(null, sebe.getMessage(), "Name vergeben", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	private void spielSpeichern() throws RemoteException {
		String name = JOptionPane.showInputDialog(frame, "Spiel speichern.");
		if(name.length() > 0){
			try {
				pm.schreibkanalOeffnen("./Speicher/" + name + ".txt");
			} catch (IOException e) {
				consolePanel.textSetzen("Spiel konnte nicht gespeichert werden. " + e.getMessage());
			}
			pm.spielSpeichern(sp.getLaenderListe(), spielerListe, sp.getTurn() + "", sp.getAktiverSpielerNummer(), sp.getMissionsListe());
			pm.close();
		}else{
			consolePanel.textSetzen("Du musst einen Namen eingeben.");
		}
	}

	public void hauptspielStarten(String name, int anzahlSpieler) throws RemoteException {
		this.anzahlSpieler = anzahlSpieler;
		
		//Verbindung mit Server aufbauen
				try {
					String servicename = "GameServer";
					Registry registry = LocateRegistry.getRegistry("127.0.0.1",4711);
					sp = (ServerRemote)registry.lookup(servicename);
					sp.addGameEventListener(this);
					sp.serverBenachrichtigung("Spieler registriert: " + name);
				} catch (NotBoundException nbe) {
				}
		
		try {
			sp.spielerErstellen(name);
			//Frame erzeugen
			frame.setLayout(new MigLayout("wrap2", "[1050][]", "[][][]"));
			spielfeld = new MapPanel(this, schrift,1050, 550);
			spielerListPanel = new SpielerPanel(schrift, uberschrift);
			missionPanel = new MissionPanel(uberschrift, schrift,this);
			infoPanel = new InfoPanel(sp.getTurn() + "", schrift, uberschrift);
			buttonPanel = new ButtonPanel(this, uberschrift);
			statistikPanel = new StatistikPanel(schrift, uberschrift);
			consolePanel = new ConsolePanel(schrift);
			frame.setSize(1250, 817);
			frame.setLocationRelativeTo(null);
			aktiverSpieler = sp.getAktiverSpieler();
			
			//Spieler erstellen und Spielwelt erzeugen
			if(anzahlSpieler > 0){
				sp.spieleranzahlSetzen(anzahlSpieler);
				frame.remove(erstellenPanel);
			}else{
				sp.spielaufbauWennSpieleranzahlErreicht();
				frame.remove(beitretenPanel);
			}
			
			//Spieler dem Thread zuweisen
			for(Spieler s:sp.getSpielerList()) {
				if(s.getName().equals(name)){
					ownSpieler = s;
					frame.setTitle("Risiko - Spieler: " + s.getName());
				}
			}
			
			//Menuleiste erstellen
			menu = new MenuBar();
			Menu datei = new Menu("Datei");
			menu.add(datei);
			MenuItem speichern = new MenuItem("Speichern");
			MenuItem schliessen = new MenuItem("Schließen");
			datei.add(speichern);
			datei.add(schliessen);
			speichern.addActionListener(save -> {
				try {
					spielSpeichern();
				} catch (RemoteException e) {}
			});
			schliessen.addActionListener(close -> System.exit(0));
			menu.setFont(schrift);
			frame.setMenuBar(menu);

			//Layout anpassen
			frame.add(spielfeld, "left,spany 3,grow");
			frame.add(infoPanel, "left,growx");
			frame.add(spielerListPanel, "growx");
			frame.add(statistikPanel, "left,top,growx,spany 2");
			frame.add(missionPanel, "left,top,split3");
			frame.add(consolePanel, "left, top");
			frame.add(buttonPanel, "right,growy");
			frame.setResizable(false);
			frame.setVisible(true);
			frame.pack();
			
			//Spiel beginnen
			sp.setTurn("STARTPHASE");
			anzahlSetzbareEinheiten = sp.checkAnfangsEinheiten();
			//Funktion mit allgemeinener Aktulaisierung spielfeldAktualisieren(String message)
			consolePanel.textSetzen("Du kannst nun die ersten Einheiten setzen.");
			infoPanel.changePanel(sp.getTurn() + "");
			
		} catch (SpielerExistiertBereitsException sebe) {
			JOptionPane.showMessageDialog(null, sebe.getMessage(), "Name vergeben", JOptionPane.WARNING_MESSAGE);
		}
	}

	public void aufloesungAendern(int breite, int hoehe) {
		
		frame.setSize(breite, hoehe);
		spielfeld.neuMalen(1000, 600);
		frame.repaint();
		frame.revalidate();

		frame.setLocationRelativeTo(null);
	}

	private void landAnklicken(String landcode)throws RemoteException {
		Land land = sp.stringToLand(sp.getLandVonFarbcode(landcode));

		if (land != null) {
			spielfeld.labelsSetzen(land.getName(), land.getEinheiten(), land.getBesitzer().getName());
			
			//Phasen abhängige Aktion beim Klicken eines Landes
			switch (sp.getTurn()) {
			case STARTPHASE:
				verteilenButtonPanelAnzeige(land);
				break;
			case ANGRIFF:
				angreifenButtonPanelAnzeige(land);
				break;
			case VERTEILEN:
				verteilenButtonPanelAnzeige(land);
				break;
			case VERSCHIEBEN:
				verschiebenButtonPanelAnzeige(land);
				break;
			}
		}
		statistikPanel.statistikPanelAktualisieren(sp.getLaenderListe(), spielerListe);
	}


	private void verteilenButtonPanelAnzeige(Land land)throws RemoteException {
		try {
			sp.landWaehlen(land, ownSpieler);
			
			if (anzahlSetzbareEinheiten > 0) {
				missionPanel.klickDisablen();
				sp.einheitenPositionieren(1, land);
				anzahlSetzbareEinheiten--;
				spielfeld.labelsSetzen("", land.getEinheiten(), "");
				statistikPanel.statistikPanelAktualisieren(sp.getLaenderListe(), spielerListe);
				buttonPanel.setEinheitenVerteilenLab(anzahlSetzbareEinheiten);
					
				if(anzahlSetzbareEinheiten == 0){
					consolePanel.textSetzen("Du hast alle Einheiten gesetzt.");
					if(sp.getTurn().toString() == "STARTPHASE"){
						sp.spielerBereit();
					} else {
						buttonPanel.phaseEnable();
					}					
				}
			}
		} catch (KannLandNichtBenutzenException lene) {
			consolePanel.textSetzen(lene.getMessage());
		}
	}

	private void angreifenButtonPanelAnzeige(Land land)throws RemoteException {
		if (land1 == null) {
			//Land wählen mit dem angegriffen werden soll
			try {
				sp.landWaehlen(land, aktiverSpieler);
				sp.checkEinheiten(land, 1);
				land1 = land;
				buttonPanel.angreifenAktiv(land1.getName(), "verteidigendes land");
				consolePanel.textSetzen("Wähle nun ein Land was du angreifen möchtest.");
			} catch (KannLandNichtBenutzenException lene) {
				consolePanel.textSetzen(lene.getMessage());
			} catch (NichtGenugEinheitenException e) {
				consolePanel.textSetzen(e.getMessage());
			}
		} else {
			//Land wählen, welches angegriffen werden soll und angreifen
			try {
				sp.istNachbar(land1, land);
				sp.istGegner(land, aktiverSpieler);
				land2 = land;
				buttonPanel.angreifenAktiv(land1.getName(), land2.getName());
				buttonPanel.angriffEnable();
			} catch (KeinNachbarlandException | KeinGegnerException e) {
				try {
					sp.landWaehlen(land,aktiverSpieler);
					land1 = land;
				} catch (KannLandNichtBenutzenException lene) {
					consolePanel.textSetzen(e.getMessage());
				}
			}
		}
	}

	private void verschiebenButtonPanelAnzeige(Land land)throws RemoteException {
		if (land1 == null) {
			//Land wählen von dem aus verschoben werden soll
			try {
				sp.landWaehlen(land, aktiverSpieler);
				sp.benutzeLaender(land);
				sp.checkEinheiten(land, 1);
				land1 = land;
				buttonPanel.verschiebenAktiv(land1.getName(), "zweites Land");
			} catch (KannLandNichtBenutzenException lene) {
				consolePanel.textSetzen(lene.getMessage());
			} catch (NichtGenugEinheitenException ngee) {
				consolePanel.textSetzen(ngee.getMessage());
			} catch (LandBereitsBenutztException lbbe) {
				consolePanel.textSetzen(lbbe.getMessage());
			}
		} else {
			//Land wählen auf das verschoben werden soll und verschieben
			try {
				sp.landWaehlen(land, aktiverSpieler);
				sp.istNachbar(land1, land);
				land2 = land;
				buttonPanel.verschiebenAktiv(land1.getName(), land2.getName());
				buttonPanel.verschiebenEnabled();
			} catch (KannLandNichtBenutzenException klnbe) {
				consolePanel.textSetzen(klnbe.getMessage());
			} catch (KeinNachbarlandException kne) {
				try {
					sp.landWaehlen(land, aktiverSpieler);
					sp.checkEinheiten(land, 1);
					land1 = land;
				} catch (KannLandNichtBenutzenException lene) {
					consolePanel.textSetzen(lene.getMessage());
				} catch (NichtGenugEinheitenException ngee) {
					consolePanel.textSetzen(ngee.getMessage());
				}
			}
		}
	}
	
	private void istSpielerRaus()throws RemoteException{
		//Überprüfung ob ein Spieler verloren hat
		for(Spieler s : spielerListe){
			if(sp.spielerRaus(s)){
				consolePanel.textSetzen("Der Spieler " + s.getName() + " hat verloren und ist raus");
				istSpielerRaus();
				break;
			}
		}
	}

	//TODO: Überarbeiten
	public void gewonnen(){
		frame.remove(spielfeld);
		frame.remove(spielerListPanel);
		frame.remove(missionPanel);
		frame.remove(infoPanel);
		frame.remove(statistikPanel);
		frame.remove(consolePanel);
		frame.remove(buttonPanel);
		frame.setLayout(new MigLayout("wrap1","[]","[][]"));
		frame.setForeground(Color.black);

		JLabel gewinner = new JLabel("Spieler" + " hat gewonnen.");
		gewinner.setFont(uberschrift);
		gewinner.setForeground(Color.white);
		JLabel firework = new JLabel(new ImageIcon("./Bilder/firework.gif"));
		frame.add(gewinner, "center");
		frame.add(firework, "center");
		frame.setBackground(Color.BLACK);
		frame.repaint();
		frame.revalidate();
	}
	
	public void karteEintauschen(Boolean erfolgreich, ArrayList<String> tauschKarten) {
		if(erfolgreich) {
			try {
				anzahlSetzbareEinheiten += sp.kartenEinloesen(aktiverSpieler, tauschKarten);
			} catch (RemoteException e) {}
			
			missionPanel.kartenAusgeben(aktiverSpieler, spielerListe);
			buttonPanel.setEinheitenVerteilenLab(anzahlSetzbareEinheiten);
			consolePanel.textSetzen("Du hast die Karten eingetauscht und kannst nun " + anzahlSetzbareEinheiten + " setzen.");
		} else {
			consolePanel.textSetzen("Die Karten konnten nicht eingetauscht werden.");
		}
	}

	public void mausklickAktion(Color color) {
		try {
			if(aktiverSpieler.equals(ownSpieler) || sp.getTurn().toString().equals("STARTPHASE")){
				//Farbcode auslesen
				String landcode = color.getRed() + "" + color.getGreen() + "" + color.getBlue();
				try {
					landAnklicken(landcode);
				} catch (RemoteException e) {}
			}else{
				consolePanel.textSetzen("Du bist nicht dran!");
			}
		} catch (RemoteException e) {}
	}

	public void SpielerRegistrierungOeffnen(boolean ersterSpieler) {
		frame.remove(startPanel);
		if(ersterSpieler) {
			zweitesPanelSpielErstellen();
		} else {
			zweitesPanelSpielBeitreten();
		}
	}

	public void phaseButtonClicked() throws RemoteException{
		//Wenn Mission erfüllt, dann gewonnen aufrufen
		try {
			if(sp.getSpielerMission(aktiverSpieler).istAbgeschlossen()){
				gewonnen();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		sp.nextTurn();
	}

	public void angriffButtonClicked() {
			try {
				//Angriff durchführen
				angriffRueckgabe = sp.befreiungsAktion(new Angriff(land1, land2));
				//Würfel anzeigen lassen
				spielfeld.wuerfelAnzeigen(angriffRueckgabe.getWuerfelAngreifer(), angriffRueckgabe.getWuerfelVerteidiger());
				//Angriff auswerten und Ergebnis anzeigen
				
				if (angriffRueckgabe.isErobert() != true) {
					if (angriffRueckgabe.hatGewonnen().equals("V")) {
						consolePanel.textSetzen(land2.getBesitzer().getName() + " hat gewonnen.");
					} else if (angriffRueckgabe.hatGewonnen().equals("A")) {
						consolePanel.textSetzen(land1.getBesitzer().getName() + " hat gewonnen.");
					} else {
						consolePanel.textSetzen("Ihr habt unentschieden gespielt, beide verlieren eine Einheit.");
					}
					spielfeld.fahneEinheit(laenderListe);
					land1 = null;
					land2 = null;
					buttonPanel.angreifenAktiv("angreifendes Land", "verteidigendes Land");
				} else {
					spielfeld.fahnenVerteilen(laenderListe);
					consolePanel.textSetzen(land1.getBesitzer().getName() + " hat das Land erobert.");
					spielfeld.fahneEinheit(laenderListe);
					if (land1.getEinheiten() == 2) {
						//wenn nur zwei Einheiten auf Angriffsland sind
						consolePanel.textSetzen("Eine Einheit wird auf " + land2.getName() + " gesetzt.");
						sp.eroberungBesetzen(land1, land2, 1);
						land1 = null;
						land2 = null;
						buttonPanel.angreifenAktiv("angreifendes Land", "verteidigendes Land");
					} else {
						//verschieben einstellungen in button panel öffnen
						buttonPanel.verschiebenNachAngreifenAktiv(land1.getName(), land2.getName());
					}
					schussSound();
				}
			} catch (RemoteException e) {
			} catch (KeinNachbarlandException e) {
				consolePanel.textSetzen(e.getMessage());
			}
	}

	public void verschiebenButtonClicked(int einheiten) {
		try {
			sp.checkEinheiten(land1, einheiten);
			sp.einheitenPositionieren(-einheiten, land1);
			sp.einheitenPositionieren(einheiten, land2);
			spielfeld.labelsSetzen("", land1.getEinheiten(), "");
			spielfeld.labelsSetzen("", land2.getEinheiten(), "");
			spielfeld.fahneEinheit(laenderListe);
			land1 = null;
			land2 = null;
			buttonPanel.verschiebenAktiv("erstes Land", "zweites Land");
			buttonPanel.verschiebenDisabled();
		} catch (NichtGenugEinheitenException ngee) {
			consolePanel.textSetzen(ngee.getMessage());
		} catch (RemoteException e) {}
	}

	public void verschiebenNachAngriffButtonClicked(int einheiten) {
		try {
			sp.checkEinheiten(land1, einheiten);
			sp.eroberungBesetzen(land1, land2, einheiten);
			land1 = null;
			land2 = null;
			buttonPanel.angreifenAktiv("erstes Land", "zweites Land");
		} catch (NichtGenugEinheitenException ngee) {
			consolePanel.textSetzen(ngee.getMessage());
		} catch (RemoteException e) {}
	}

	public void schussSound(){
		try{
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("Sounds/hit.wav"));
			AudioFormat af = audioInputStream.getFormat();
			int size = (int)(af.getFrameSize() * audioInputStream.getFrameLength());
			byte[] audio = new byte[size];
			DataLine.Info info = new DataLine.Info(Clip.class, af, size);
			audioInputStream.read(audio, 0, size);
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(af, audio, 0, size);
			clip.start();
		}catch(Exception e){ 
			e.printStackTrace(); 
		}
	}


	public void handleGameEvent(GameEvent event) throws RemoteException {
		spielerListe =  sp.getSpielerList();
		laenderListe = sp.getLaenderListe();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {}
		
		if(event instanceof GameControlEvent){

			GameControlEvent gce = (GameControlEvent)event;
			aktiverSpieler = gce.getSpieler();
			missionPanel.kartenAusgeben(ownSpieler, spielerListe);
//			infoPanel.changePanel(sp.getTurn() + "");
			//Rahmen auf aktiven Spieler
			spielerListPanel.setAktiverSpielerBorder(spielerListe.indexOf(aktiverSpieler));
			
			boolean istAktiverSpieler;
			if(aktiverSpieler.getName().equals(ownSpieler.getName())) {
				istAktiverSpieler = true;
			} else {
				istAktiverSpieler = false;
			}
				
				switch (gce.getTurn()) {
				case STARTPHASE:
					buttonPanel.phaseDisable();
					anzahlSetzbareEinheiten = sp.checkAnfangsEinheiten();
					buttonPanel.verteilenAktiv(anzahlSetzbareEinheiten);
					consolePanel.textSetzen(ownSpieler.getName() + " du kannst nun deine ersten Einheiten setzen. Es sind " + anzahlSetzbareEinheiten);
					break;
				case ANGRIFF:
					missionPanel.klickDisablen();
					if(istAktiverSpieler) {
						buttonPanel.phaseEnable();
						consolePanel.textSetzen(ownSpieler.getName() + " du kannst nun angreifen.");
						buttonPanel.angreifenAktiv("angreifendes Land", "verteidigendes Land");
					} else {
						buttonPanel.removeAll();
						consolePanel.textSetzen(aktiverSpieler.getName() + " kann nun angreifen.");		
					}
					break;
				case VERTEILEN:
					missionPanel.klickEnablen();
					if(istAktiverSpieler) {
						buttonPanel.phaseDisable();
						anzahlSetzbareEinheiten = sp.bekommtEinheiten(ownSpieler);
						consolePanel.textSetzen(aktiverSpieler.getName() + " du kannst " + anzahlSetzbareEinheiten + " Einheiten setzen.");
						buttonPanel.verteilenAktiv(anzahlSetzbareEinheiten);
					} else {
						buttonPanel.removeAll();
						consolePanel.textSetzen(aktiverSpieler.getName() + " kann nun Einheiten setzen.");
					}
					break;
				case VERSCHIEBEN:
					istSpielerRaus();
					spielfeld.wuerfelEntfernen();
					if(istAktiverSpieler) {
						buttonPanel.phaseEnable();
						consolePanel.textSetzen(ownSpieler.getName() + " verschiebe nun deine Einheiten.");
						buttonPanel.verschiebenAktiv("erstes Land", "zweites Land");
						//TODO: nur wenn Land erobert
						sp.einheitenKarteZiehen(ownSpieler);		
					} else {
						buttonPanel.removeAll();
						consolePanel.textSetzen(aktiverSpieler.getName() + " darf nun Einheiten verschieben.");
					}
					break;
				case STARTEN:
					spielfeld.fahnenVerteilen(laenderListe);
					for (Spieler s : sp.getSpielerList()) {
						spielerListPanel.setLabel(s);
					}
					missionPanel.setMBeschreibung(sp.getMissionVonSpieler(ownSpieler).getBeschreibung());
					statistikPanel.statistikAktualisieren(laenderListe, spielerListe);
					break;
				case AKTUALISIEREN:
					consolePanel.textSetzen("Das Spiel wurde vom Admin geaendert");
					statistikPanel.statistikPanelAktualisieren(laenderListe, spielerListe);
					spielfeld.fahnenVerteilen(laenderListe);
					infoPanel.setInfo(sp.getTurn()+"");
					aktiverSpieler = gce.getSpieler();
					if(aktiverSpieler.equals(ownSpieler)){
						consolePanel.textSetzen("Du bist am Zug");
					}
					break;
				}
			infoPanel.changePanel(sp.getTurn() + "");
			missionPanel.kartenAusgeben(ownSpieler, spielerListe);
		}else{
			GameActionEvent gae = (GameActionEvent)event;
			aktiverSpieler = gae.getSpieler();
			spielfeld.fahneEinheit(laenderListe);
			
			switch(gae.getType()){
			case VERTEILEN:
				statistikPanel.statistikPanelAktualisieren(laenderListe, spielerListe);
				break;
			case EROBERT:
				spielfeld.fahnenVerteilen(laenderListe);
				statistikPanel.statistikPanelAktualisieren(laenderListe, spielerListe);
				consolePanel.textSetzen(gae.getText());
				break;
			case ANGRIFF:
				statistikPanel.statistikPanelAktualisieren(laenderListe, spielerListe);
				consolePanel.textSetzen(gae.getText());
			}
		}
	}
}	