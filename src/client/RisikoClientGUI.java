//TODO Angriffsphase erweitern (Yannik)
//TODO verschieben nach angriff mit nur einer einheit
//TODO Javadoc
//TODO Laden eines Spiels
//TODO Speichern erweitern (Idee: Jeder Spieler bekommt beim ersten Onlinespiel eine eindeutige ID)
//TODO mit ostafrika kann man Nordafrika nicht angreifen?
//TODO bei falscher Namenseinagbe kackt er rein
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
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import client.BeitretenPanel.BeitretenButtonClicked;
import client.ButtonPanel.ButtonClickHandler;
import client.ErstellenPanel.ErstellenButtonClicked;
import client.MapPanel.MapClickHandler;
import client.MissionPanel.KarteClickedHandler;
import client.StartPanel.LoadHandler;
import client.StartPanel.StartButtonClickHandler;
import local.domain.exceptions.KannLandNichtBenutzenException;
import local.domain.exceptions.KeinGegnerException;
import local.domain.exceptions.KeinNachbarlandException;
import local.domain.exceptions.LandBereitsBenutztException;
import local.domain.exceptions.NichtGenugEinheitenException;
import local.domain.exceptions.SpielerExistiertBereitsException;
import local.persistence.FilePersistenceManager;
import local.valueobjects.Angriff;
import local.valueobjects.AngriffRueckgabe;
import local.valueobjects.GameActionEvent;
import local.valueobjects.GameControlEvent;
import local.valueobjects.GameEvent;
import local.valueobjects.GameEventListener;
import local.valueobjects.Land;
import local.valueobjects.ServerRemote;
import local.valueobjects.Spieler;
import net.miginfocom.swing.MigLayout;


public class RisikoClientGUI extends UnicastRemoteObject implements MapClickHandler, ButtonClickHandler, StartButtonClickHandler, ErstellenButtonClicked, KarteClickedHandler, GameEventListener, LoadHandler, BeitretenButtonClicked {

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
		frame.setSize(330, 350);
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
		frame.setTitle("Spiel erstellen");
		frame.setSize(280, 200);
		frame.setLocationRelativeTo(null);
		//frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		beitretenPanel = new BeitretenPanel(this);
		frame.add(beitretenPanel);
		frame.setVisible(true);
		frame.repaint();
		frame.revalidate();
	}
	
	public void hauptspielStarten(String name, int anzahlSpieler) throws RemoteException {
		this.anzahlSpieler = anzahlSpieler;
		
		//Verbindung mit Server aufbauen
			String servicename = "GameServer";
			Registry registry = LocateRegistry.getRegistry("127.0.0.1",4711);
				try {
					sp = (ServerRemote)registry.lookup(servicename);
				} catch (NotBoundException e1) {
					e1.printStackTrace();
				}
			sp.addGameEventListener(this);
			sp.serverBenachrichtigung("Spieler registriert: " + name);

		
		try {
			//Frame erzeugen
			frame.setLayout(new MigLayout("debug, wrap2", "[1050][]", "[][][]"));
			spielfeld = new MapPanel(this, schrift,1050, 550);
			spielerListPanel = new SpielerPanel(schrift, uberschrift);
			missionPanel = new MissionPanel(uberschrift, schrift,this);
			infoPanel = new InfoPanel(sp.getTurn() + "", schrift, uberschrift);
			buttonPanel = new ButtonPanel(this, uberschrift);
			statistikPanel = new StatistikPanel(sp.getSpielerList(), sp.getLaenderListe(), schrift, uberschrift);
			consolePanel = new ConsolePanel(schrift);
			frame.setSize(1250, 817);
			frame.setLocationRelativeTo(null);
			
			//Spieler erstellen und Spielwelt erzeugen
			if(anzahlSpieler > 0){
				sp.erstelleErstenSpieler(name, anzahlSpieler);
				frame.remove(erstellenPanel);
			}else{
				sp.erstelleWeiterenSpielerUndSpielaufbau(name);
				frame.remove(beitretenPanel);
			}
			aktiverSpieler = sp.getAktiverSpieler();
			
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
					//TODO: Speichern anschauen
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

	public void spielLaden(){
		try{
			sp.spielLaden("Game2.txt");
			geladenesSpielErstellen();
		} catch(Exception e) {
			System.out.println("Kann nicht geladen werden");
		}
	}

	//TODO KOMPLETT ÜBERARBEITEN UND AN NEUE DINGE ANPASSEN
	private void geladenesSpielErstellen()throws RemoteException {
		this.anzahlSpieler = spielerListe.size();
		try{
			String servicename = "GameServer";
			Registry registry = LocateRegistry.getRegistry("127.0.0.1",4711);
			sp = (ServerRemote)registry.lookup(servicename);

			sp.addGameEventListener(this);

		}catch(RemoteException e){

		} catch (NotBoundException e) {
			e.printStackTrace();
		}

		frame.setLayout(new MigLayout("debug, wrap2", "[1050][]", "[][][]"));
		spielfeld = new MapPanel(this, schrift,1050, 550);
		spielerListPanel = new SpielerPanel(schrift, uberschrift);
		missionPanel = new MissionPanel(uberschrift, schrift,this);
		infoPanel = new InfoPanel(sp.getTurn() + "", schrift, uberschrift);
		buttonPanel = new ButtonPanel(this, uberschrift);
		statistikPanel = new StatistikPanel(spielerListe, sp.getLaenderListe(), schrift, uberschrift);
		consolePanel = new ConsolePanel(schrift);

		//Spieler erstellen
		sp.geladenesSpielStarten(anzahlSpieler);
		frame.remove(erstellenPanel);

		aktiverSpieler = sp.getAktiverSpieler();
		//TODO OwnSpieler muss gespeichert wertden und hier genutzt werden
		for(Spieler s:spielerListe) {
			if(s.getName().equals(sp.getAktiverSpieler())){
				ownSpieler = s;
			}
		}

		frame.setTitle("Risiko - Spieler: " + ownSpieler.getName());
		frame.setSize(1250, 817);
		frame.setLocationRelativeTo(null);
		//			frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

		//Menuleiste erstellen
		menu = new MenuBar();
		Menu datei = new Menu("Datei");
		Menu grafik = new Menu("Grafik");
		menu.add(datei);
		menu.add(grafik);
		MenuItem speichern = new MenuItem("Speichern");
		MenuItem schliessen = new MenuItem("Schließen");
//		Menu aufloesung = new Menu("Aufloesung");
//		MenuItem aufloesung1 = new MenuItem("1920x1080");
//		MenuItem aufloesung2 = new MenuItem("1280x800");
//		MenuItem aufloesung3 = new MenuItem("3.Auflösung");
		datei.add(speichern);
		datei.add(schliessen);
//		grafik.add(aufloesung);
//		aufloesung.add(aufloesung1);
//		aufloesung.add(aufloesung2);
//		aufloesung.add(aufloesung3);
//		aufloesung1.addActionListener(ausfuehren -> aufloesungAendern(1920, 1080));
//		aufloesung2.addActionListener(ausfuehren -> aufloesungAendern(1280, 800));
		speichern.addActionListener(save -> {
			try {
				spielSpeichern();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		schliessen.addActionListener(close -> System.exit(0));
		menu.setFont(schrift);
		frame.setMenuBar(menu);

		//Layout anpassen
		frame.add(spielfeld, "left,spany 3,grow");
		frame.add(infoPanel, "left,growx");
		frame.add(spielerListPanel, "growx");
		frame.add(statistikPanel, "left,top,growx,spany 2");
		//			frame.add(missionPanel, "left,top,split3,wmin 300, wmax 300");
		frame.add(missionPanel, "left,top,split3");
		frame.add(consolePanel, "left, top");
		//			frame.add(buttonPanel, "right,growy, wmin 180, wmax 180");
		frame.add(buttonPanel, "right,growy");
		frame.setResizable(false);
		frame.setVisible(true);
		frame.pack();
		infoPanel.changePanel(sp.getTurn() + "");
	}
	// ebenfalls überarbeiten
	private void spielSpeichern() throws RemoteException {
		String name = JOptionPane.showInputDialog(frame, "Spiel speichern.");
		if(name.length() > 0){
			FilePersistenceManager pm = new FilePersistenceManager();
			try {
				pm.schreibkanalOeffnen(name + ".txt");
			} catch (IOException e) {
				consolePanel.textSetzen("Spiel konnte nicht gespeichert werden" + e.getMessage());
			}
			pm.spielSpeichern(sp.getLaenderListe(), spielerListe, sp.getTurn() + "", sp.getAktiverSpielerNummer(), sp.getMissionsListe());
			pm.close();
		}else{
			consolePanel.textSetzen("Du musst einen Namen eingeben");
		}
	}

	private void landAnklicken(String landcode)throws RemoteException {
		Land land = sp.stringToLand(sp.getLandVonFarbcode(landcode));

		if (land != null) {
			spielfeld.labelsSetzen(land.getName(), land.getEinheiten(), land.getBesitzer().getName());
			
			//Phasen abhängige Aktion beim Klicken eines Landes
			switch (sp.getTurn()) {
			case STARTPHASE:
				verteilenBeiLandklick(land);
				break;
			case ANGRIFF:
				angreifenBeiLandklick(land);
				break;
			case VERTEILEN:
				verteilenBeiLandklick(land);
				break;
			case VERSCHIEBEN:
				verschiebenBeiLandklick(land);
				break;
			}
		}
		statistikPanel.statistikPanelAktualisieren(sp.getLaenderListe(), spielerListe);
	}


	private void verteilenBeiLandklick(Land land)throws RemoteException {
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

	private void angreifenBeiLandklick(Land land)throws RemoteException {
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

	private void verschiebenBeiLandklick(Land land)throws RemoteException {
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
	
	private void angriff(boolean genugEinheiten, Spieler aSpieler) throws KeinNachbarlandException, RemoteException {

		//Angriff durchführen
		angriffRueckgabe = sp.befreiungsAktion(new Angriff(land1, land2));
		//Würfel anzeigen lassen
		spielfeld.wuerfelAnzeigen(angriffRueckgabe.getWuerfelAngreifer(), angriffRueckgabe.getWuerfelVerteidiger());
		//Angriff auswerten und Ergebnis anzeigen
		if (angriffRueckgabe.isErobert() != true) {
			//Ausgabe falls nicht erobert ist
			if (angriffRueckgabe.hatGewonnen().equals("V")) {
				consolePanel.textSetzen(land2.getBesitzer().getName() + " hat gewonnen.");
			} else if (angriffRueckgabe.hatGewonnen().equals("A")) {
				consolePanel.textSetzen(land1.getBesitzer().getName() + " hat gewonnen.");
			} else {
				consolePanel.textSetzen("Ihr habt unentschieden gespielt, beide verlieren eine Einheit.");
			}

			//Einheiten auf Fahne setzen
			spielfeld.fahneEinheit(laenderListe);
			land1 = null;
			land2 = null;
			buttonPanel.angreifenAktiv("angreifendes Land", "verteidigendes Land");
		} else {
			//bei Eroberung
			//			vLand.setFahne(aSpieler.getFarbe());
			spielfeld.fahnenVerteilen(laenderListe);
			consolePanel.textSetzen(land1.getBesitzer().getName() + " hat das Land erobert.");
			genugEinheiten = false;
			// Verschieben nach Eroberung
			if (land1.getEinheiten() == 2) {
				//wenn nur zwei Einheiten auf ANgriffsland sind
				consolePanel.textSetzen("Eine Einheit wird auf " + land2.getName() + " gesetzt.");
				sp.eroberungBesetzen(land1, land2, 1);
				genugEinheiten = true;
				spielfeld.fahneEinheit(laenderListe);
				land1 = null;
				land2 = null;
				buttonPanel.angreifenAktiv("angreifendes Land", "verteidigendes Land");
			} else {
				//verschieben einstellungen in button panel öffnen
				buttonPanel.verschiebenNachAngreifenAktiv(land1.getName(), land2.getName());
			}
			schuss();
		}
	}
	
	private void istSpielerRaus()throws RemoteException{
		//Überprüfung ob ein Spieler verloren hat
		List<Spieler> spielerListe = this.spielerListe;
		for(Spieler s : spielerListe){
			String name = s.getName();
			if(sp.spielerRaus(s)){
				System.out.println("Der Spieler " + name + " hat verloren und ist raus");
				istSpielerRaus();
				break;
			}
		}
	}

	//alle Panels entfernen und Gewonnen Screen zeigen
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
		JLabel firework = new JLabel(new ImageIcon("./firework.gif"));
		frame.add(gewinner, "center");
		frame.add(firework, "center");
		frame.setBackground(Color.BLACK);
		frame.repaint();
		frame.revalidate();
	}

	public void karteEintauschen(ArrayList<String> tauschKarten) throws RemoteException {
		//Karten eintauschen
		try {
			anzahlSetzbareEinheiten += sp.kartenEinloesen(aktiverSpieler, tauschKarten);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		missionPanel.kartenAusgeben(aktiverSpieler, spielerListe);
		buttonPanel.setEinheitenVerteilenLab(anzahlSetzbareEinheiten);
	}

	public void tauschFehlgeschlagen() {
		consolePanel.textSetzen("Die Karten konnten nicht eingetauscht werden.");
	}

	public void processMouseClick(Color color) {
		try {
			if(sp.getAktiverSpieler().equals(ownSpieler) || sp.getTurn().toString() == "STARTPHASE"){
				//Farbcode auslesen
				String landcode = color.getRed() + "" + color.getGreen() + "" + color.getBlue();
				try {
					landAnklicken(landcode);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				consolePanel.textSetzen("Du bist nicht dran!");
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void startButtonClicked() {
		//von Anfangsmenü zu Spieler erstellen wechseln
		frame.remove(startPanel);
		zweitesPanelSpielErstellen();
	}

	public void spielBeitreten() {
		frame.remove(startPanel);
		zweitesPanelSpielBeitreten();
		
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
		//		aktiverSpieler = sp.getAktiverSpieler();
		//		missionPanel.kartenAusgeben(aktiverSpieler);
		//		//Rahmen auf aktiven Spieler
		//		spielerListPanel.setAktiverSpieler(spielerListe.indexOf(aktiverSpieler) + 1);

		//		switch (sp.getTurn()) {
		//		case STARTPHASE:
		//			buttonPanel.phaseDisable();
		//			anzahlSetzbareEinheiten = sp.checkAnfangsEinheiten();
		//			buttonPanel.setEinheitenVerteilenLab(anzahlSetzbareEinheiten);
		//			consolePanel.textSetzen(aktiverSpieler.getName() + " du kannst nun deine ersten Einheiten setzen. Es sind " + anzahlSetzbareEinheiten);
		//			missionPanel.setMBeschreibung(sp.getMissionVonAktivemSpieler().getBeschreibung());
		//			break;
		//		case ANGRIFF:
		//			
		//			missionPanel.klickDisablen();
		//			consolePanel.textSetzen(aktiverSpieler.getName() + " du kannst nun angreifen.");
		//			buttonPanel.angreifenAktiv("angreifendes Land", "verteidigendes Land");
		//			break;
		//		case VERTEILEN:
		//			missionPanel.kartenAusgeben(aktiverSpieler);
		//			missionPanel.klickEnablen();
		//			buttonPanel.phaseDisable();
		//			anzahlSetzbareEinheiten = sp.bekommtEinheiten(aktiverSpieler);
		//			consolePanel.textSetzen(
		//			aktiverSpieler.getName() + " du kannst " + anzahlSetzbareEinheiten + " Einheiten setzen.");
		//			buttonPanel.verteilenAktiv(anzahlSetzbareEinheiten);
		//			missionPanel.setMBeschreibung(sp.getMissionVonAktivemSpieler().getBeschreibung());
		//			break;
		//		case VERSCHIEBEN:
		//			istSpielerRaus();
		//			spielfeld.wuerfelEntfernen();
		//			consolePanel.textSetzen(aktiverSpieler.getName() + " verschiebe nun deine Einheiten.");
		//			buttonPanel.verschiebenAktiv("erstes Land", "zweites Land");
		//			if(aktiverSpieler.getEinheitenkarten().size() < 5){
		//				sp.einheitenKarteZiehen(aktiverSpieler);			
		//			}
		//			missionPanel.kartenAusgeben(aktiverSpieler);
		//			break;
		//		}
		//		infoPanel.changePanel(sp.getTurn() + "");
	}

	public void angriffClicked() {
		//Angreifen Button klicken
		try {
			angriff(true, aktiverSpieler);
		} catch (KeinNachbarlandException | RemoteException e) {
			e.printStackTrace();
		}
	}

	public void verschiebenClicked(int einheiten) {
		//verschieben Button klicken
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
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void verschiebenNAClicked(int einheiten) {
		//nach angreifen verteilen klicken
		try {
			sp.checkEinheiten(land1, einheiten);
			sp.eroberungBesetzen(land1, land2, einheiten);
			land1 = null;
			land2 = null;
			buttonPanel.angreifenAktiv("erstes Land", "zweites Land");
		} catch (NichtGenugEinheitenException ngee) {
			consolePanel.textSetzen(ngee.getMessage());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void schuss(){
		//Schuss Geräusch für Angriff
		try{
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("hit.wav"));
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

		//TODO:hier könnten Probleme auftreten?
		spielerListe =  sp.getSpielerList();
		laenderListe = sp.getLaenderListe();
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(event instanceof GameControlEvent){

			GameControlEvent gce = (GameControlEvent)event;
			aktiverSpieler = gce.getSpieler();
			System.out.println("TEST: Karte wird ausgegeben!");
			missionPanel.kartenAusgeben(ownSpieler, spielerListe);
			infoPanel.changePanel(sp.getTurn() + "");
			//Rahmen auf aktiven Spieler
			spielerListPanel.setAktiverSpieler(spielerListe.indexOf(aktiverSpieler) + 1);
			if(aktiverSpieler.getName().equals(ownSpieler.getName())) {
				System.out.println("aktiver SPieler " + sp.getTurn());//!!!!!!!!!!!!!!!!!!!!!!!!TEST!!!!!!!!!
				System.out.println("gce - " + gce.getTurn());
				switch (gce.getTurn()) {
				case STARTPHASE:
					buttonPanel.phaseDisable();
					anzahlSetzbareEinheiten = sp.checkAnfangsEinheiten();
					buttonPanel.setEinheitenVerteilenLab(anzahlSetzbareEinheiten);
					consolePanel.textSetzen(aktiverSpieler.getName() + " du kannst nun deine ersten Einheiten setzen. Es sind " + anzahlSetzbareEinheiten);
					missionPanel.setMBeschreibung(sp.getMissionVonSpieler(ownSpieler).getBeschreibung());
					break;
				case ANGRIFF:
					buttonPanel.phaseEnable();
					missionPanel.klickDisablen();
					consolePanel.textSetzen(aktiverSpieler.getName() + " du kannst nun angreifen.");
					buttonPanel.angreifenAktiv("angreifendes Land", "verteidigendes Land");
					break;
				case VERTEILEN:
					missionPanel.kartenAusgeben(aktiverSpieler, spielerListe);
					missionPanel.klickEnablen();
					buttonPanel.phaseDisable();
					anzahlSetzbareEinheiten = sp.bekommtEinheiten(aktiverSpieler);
					consolePanel.textSetzen(
					aktiverSpieler.getName() + " du kannst " + anzahlSetzbareEinheiten + " Einheiten setzen.");
					buttonPanel.verteilenAktiv(anzahlSetzbareEinheiten);
					missionPanel.setMBeschreibung(sp.getMissionVonSpieler(aktiverSpieler).getBeschreibung());
					break;
				case VERSCHIEBEN:
					buttonPanel.phaseEnable();
					istSpielerRaus();
					spielfeld.wuerfelEntfernen();
					consolePanel.textSetzen(aktiverSpieler.getName() + " verschiebe nun deine Einheiten.");
					buttonPanel.verschiebenAktiv("erstes Land", "zweites Land");
					if(aktiverSpieler.getEinheitenkarten().size() < 5){
						System.out.println("Karte gezogen");
						sp.einheitenKarteZiehen(aktiverSpieler);			
					}
					break;
				case STARTEN:
					ArrayList<Land> laenderListe = new ArrayList<>();
					for(int i = 0; i < 42; i++){
						laenderListe.add(sp.getLandVonIndex(i));
					}

					spielfeld.fahnenVerteilen(laenderListe);

					int spielerNr = 1;
					for (Spieler s : sp.getSpielerList()) {
						spielerListPanel.setLabel(spielerNr, s.getName(), s.getFarbe());
						spielerNr++;
					}
					//					statistikPanel.statistikAktualisieren(laenderListe, spielerListe);
					missionPanel.setMBeschreibung(sp.getMissionVonSpieler(ownSpieler).getBeschreibung());
					statistikPanel.statistikAktualisieren(laenderListe, spielerListe);
					break;
				case ALLE_BEREIT:
					sp.setTurn("VERTEILEN");
					missionPanel.kartenAusgeben(aktiverSpieler, spielerListe);
					missionPanel.klickEnablen();
					buttonPanel.phaseDisable();
					anzahlSetzbareEinheiten = sp.bekommtEinheiten(aktiverSpieler);
					consolePanel.textSetzen(aktiverSpieler.getName() + " du kannst " + anzahlSetzbareEinheiten + " Einheiten setzen.");
					buttonPanel.verteilenAktiv(anzahlSetzbareEinheiten);
					break;
				default:
					System.out.println("Fehler bei: Aktiv - " + gce.getTurn());
					break;
				}
			} else {
				System.out.println("inaktiverSpieler " + sp.getTurn());//!!!!!!!!!!!!!!!!!!!!!!!!TEST!!!!!!!!!
				missionPanel.klickDisablen();
				missionPanel.kartenAusgeben(ownSpieler, spielerListe);
				missionPanel.setMBeschreibung(sp.getMissionVonSpieler(ownSpieler).getBeschreibung());
				switch (gce.getTurn()) {
				case STARTPHASE:
					System.out.println("I - gce " + gce.getTurn());
					//buttonPanel.phaseDisable();
					anzahlSetzbareEinheiten = sp.checkAnfangsEinheiten();
					buttonPanel.setEinheitenVerteilenLab(anzahlSetzbareEinheiten);
					consolePanel.textSetzen(aktiverSpieler.getName() + " du kannst nun deine ersten Einheiten setzen. Es sind " + anzahlSetzbareEinheiten);
					break;
				case ANGRIFF:
					buttonPanel.removeAll();
					consolePanel.textSetzen(aktiverSpieler.getName() + " kann nun angreifen.");					
					break;
				case VERTEILEN:
					buttonPanel.removeAll();
					consolePanel.textSetzen(aktiverSpieler.getName() + " kann nun Einheiten setzen.");
					break;
				case VERSCHIEBEN:
					buttonPanel.removeAll();
					istSpielerRaus();
					spielfeld.wuerfelEntfernen();
					consolePanel.textSetzen(aktiverSpieler.getName() + " darf nun Einheiten verschieben.");
					break;
				case STARTEN:
					ArrayList<Land> laenderListe = new ArrayList<>();
					for(int i = 0; i < 42; i++){
						laenderListe.add(sp.getLandVonIndex(i));
					}

					spielfeld.fahnenVerteilen(laenderListe);

					int spielerNr = 1;
					for (Spieler s : spielerListe) {
						spielerListPanel.setLabel(spielerNr, s.getName(), s.getFarbe());
						spielerNr++;
					}
					statistikPanel.statistikAktualisieren(laenderListe, sp.getSpielerList());
					break;
				case ALLE_BEREIT:
					sp.setTurn("VERTEILEN");
					missionPanel.kartenAusgeben(ownSpieler, spielerListe);
					missionPanel.klickDisablen();
					buttonPanel.removeAll();
					anzahlSetzbareEinheiten = sp.checkAnfangsEinheiten();
					buttonPanel.setEinheitenVerteilenLab(anzahlSetzbareEinheiten);
					consolePanel.textSetzen(aktiverSpieler.getName() + " kann nun seine Einheiten setzen.");
					break;
				default:
					System.out.println("Fehler bei: Inaktiv - " + gce.getTurn());
					break;
				}
			}	


			infoPanel.changePanel(sp.getTurn() + "");
			missionPanel.kartenAusgeben(ownSpieler, spielerListe);
		}else{
			GameActionEvent gae = (GameActionEvent)event;
			aktiverSpieler = gae.getSpieler();
			switch(gae.getType()){
			case VERTEILEN:

				spielfeld.fahneEinheit(laenderListe);
				statistikPanel.statistikPanelAktualisieren(laenderListe, spielerListe);
				for(Spieler s : spielerListe){
					System.out.println(s.getName());
				}
				break;
			case EROBERT:
				spielfeld.fahnenVerteilen(laenderListe);
				spielfeld.fahneEinheit(laenderListe);
				statistikPanel.statistikPanelAktualisieren(laenderListe, spielerListe);
				consolePanel.textSetzen(gae.getText());
				break;
			case ANGRIFF:
				spielfeld.fahneEinheit(laenderListe);
				statistikPanel.statistikPanelAktualisieren(laenderListe, spielerListe);
				consolePanel.textSetzen(gae.getText());
//				spielfeld.wuerfelAnzeigen(angriffRueckgabe.getWuerfelAngreifer(), angriffRueckgabe.getWuerfelVerteidiger());
				
			}

		}

	}

	@Override
	public void karteEintauschen(List<String> tauschKarten) {
		// TODO Auto-generated method stub

	}
	
	public void spielLaden2() throws RemoteException, IOException{
		FilePersistenceManager pm = new FilePersistenceManager();
		pm.lesekanalOeffnen("Game5.txt");
		sp.setTurn(pm.spielstandLaden());
		pm.spielstandLaden();
		ArrayList<String> spielerListe = new ArrayList<>();
		String spieler;
		do{
			spieler = pm.spielstandLaden();
			if(spieler.length() != 0){
				sp.spielerErstellen(spieler);
			}
		}while(spieler.length() != 0);
		String land;
		do{
			 land = pm.spielstandLaden();
			if(land.length() != 0){
				ArrayList<String> liste = new ArrayList<>();
				liste.add(land);
				liste.add(pm.spielstandLaden());
				liste.add(pm.spielstandLaden());
				liste.add(pm.spielstandLaden());
				liste.add(pm.spielstandLaden());
				liste.add(pm.spielstandLaden());
				
				sp.landErstellen(liste);
					
			}	
		}while(land.length() != 0);
		sp.setAktiverSpielerNummer(Integer.parseInt(pm.spielstandLaden()));
		
		pm.close();
	}
}	