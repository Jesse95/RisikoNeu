//TODO Angriffsphase erweitern (Yannik)
//TODO verschieben nach angriff mit nur einer einheit
//TODO Javadoc
//TODO Laden eines Spiels
//TODO Speichern erweitern (Idee: Jeder Spieler bekommt beim ersten Onlinespiel eine eindeutige ID)
//TODO mit ostafrika kann man Nordafrika nicht angreifen?
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


public class RisikoClientGUI extends UnicastRemoteObject implements MapClickHandler, ButtonClickHandler, StartButtonClickHandler, ErstellenButtonClicked, KarteClickedHandler, GameEventListener, LoadHandler {

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
	private MenuBar menu;
	private Font schrift;
	private Font uberschrift;
	private Land land1 = null;
	private Land land2 = null;
	private int anzahlSetzbareEinheiten;
	private Spieler aktiverSpieler;
	private Spieler ownSpieler;
	private ServerRemote server;
	private ArrayList<Land> laenderListe;
	private JFrame frame;
	private String lokalSpielerString = "";
	private Spieler lokalSpieler = null;


	private RisikoClientGUI()throws RemoteException {
		start();
	}

	public static void main(String[] args)throws RemoteException{
		new RisikoClientGUI();
	}

	private void start() {
		//Schriften für alle Panel
		frame = new JFrame();
		uberschrift = new Font(Font.SERIF, Font.BOLD, 25);
		schrift = new Font(Font.SANS_SERIF, Font.PLAIN, 17);

		//Spielmenu Fenster erstellen
		frame.setTitle("Spiel starten");
		frame.setSize(330, 350);
		frame.setLocationRelativeTo(null);
		//		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		startPanel = new StartPanel(this);
		frame.add(startPanel);
		frame.setResizable(true);
		frame.setVisible(true);
	}

	private void spielErstellen() {

		//Spieler erstellen Fenster erstellen
		frame.setTitle("Spiel erstellen");
		frame.setSize(280, 200);
		frame.setLocationRelativeTo(null);
		//		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		erstellenPanel = new ErstellenPanel(this);
		frame.add(erstellenPanel);
		frame.setVisible(true);
		frame.repaint();
		frame.revalidate();
	}

	public void spielErstellen(String name, int anzahl) {
		//von Spiel erstellen zu Spiel wechseln
		try {
			try {
				spiel(name, anzahl);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (SpielerExistiertBereitsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void spiel(String name, int anzahlSpieler) throws SpielerExistiertBereitsException, RemoteException {
		this.anzahlSpieler = anzahlSpieler;
		this.lokalSpielerString = name;
		try{
			String servicename = "GameServer";
			Registry registry = LocateRegistry.getRegistry("127.0.0.1",4711);
			sp = (ServerRemote)registry.lookup(servicename);

			sp.addGameEventListener(this);

		}catch(RemoteException e){

		} catch (NotBoundException e) {
			e.printStackTrace();
		}

		//Spiel erzeugen
		try {
			frame.setLayout(new MigLayout("debug, wrap2", "[1050][]", "[][][]"));
			spielfeld = new MapPanel(this, schrift,1050, 550);
			spielerListPanel = new SpielerPanel(schrift, uberschrift);
			missionPanel = new MissionPanel(uberschrift, schrift,this);
			infoPanel = new InfoPanel(sp.getTurn() + "", schrift, uberschrift);
			buttonPanel = new ButtonPanel(this, uberschrift);
			statistikPanel = new StatistikPanel(sp.getSpielerList(), sp.getLaenderListe(), schrift, uberschrift);
			consolePanel = new ConsolePanel(schrift);

			//Spieler erstellen
			sp.erstelleSpieler(name, anzahlSpieler);
			frame.remove(erstellenPanel);
			//			for (int i = 1; i < anzahlSpieler; i++) {
			//				neuerSpieler();
			//			}
			aktiverSpieler = sp.getAktiverSpieler();

			for(Spieler s:sp.getSpielerList()) {
				if(s.getName().equals(name)){
					ownSpieler = s;
				}
			}

			frame.setTitle("Risiko - Spieler: " + ownSpieler.getName());
			frame.setSize(1250, 817);
			frame.setLocationRelativeTo(null);
			//			frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

			//Fenster mit Layout und Paneln füllen
			//			frame.setLayout(new MigLayout("debug, wrap2", "[1050][]", "[][][]"));
			//			spielfeld = new MapPanel(this, schrift,1050, 550);
			//			spielerListPanel = new SpielerPanel(schrift, uberschrift);
			//			missionPanel = new MissionPanel(uberschrift, schrift,this);
			//			infoPanel = new InfoPanel(sp.getTurn() + "", aktiverSpieler.getName(), schrift, uberschrift);
			//			buttonPanel = new ButtonPanel(this, uberschrift);
			//			statistikPanel = new StatistikPanel(sp.getSpielerList(), sp.getLaenderListe(), schrift, uberschrift);
			//			consolePanel = new ConsolePanel(schrift);

			//Menuleiste erstellen
			menu = new MenuBar();
			Menu datei = new Menu("Datei");
			Menu grafik = new Menu("Grafik");
			menu.add(datei);
			menu.add(grafik);
			MenuItem speichern = new MenuItem("Speichern");
			MenuItem schliessen = new MenuItem("Schließen");
			Menu aufloesung = new Menu("Aufloesung");
			MenuItem aufloesung1 = new MenuItem("1920x1080");
			MenuItem aufloesung2 = new MenuItem("1280x800");
			MenuItem aufloesung3 = new MenuItem("3.Auflösung");
			datei.add(speichern);
			datei.add(schliessen);
			grafik.add(aufloesung);
			aufloesung.add(aufloesung1);
			aufloesung.add(aufloesung2);
			aufloesung.add(aufloesung3);
			aufloesung1.addActionListener(ausfuehren -> aufloesungAendern(1920, 1080));
			aufloesung2.addActionListener(ausfuehren -> aufloesungAendern(1280, 800));
			speichern.addActionListener(save -> spielSpeichern());
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
			//Spiel beginnen
			System.out.println("Zu Beginn" + sp.getTurn());//!!!!!!!!!!!!!!!!!!!!!!!!TEST!!!!!!!!!
			sp.setTurn("STARTPHASE");
			anzahlSetzbareEinheiten = sp.checkAnfangsEinheiten();
			consolePanel.textSetzen(aktiverSpieler.getName() + " du kannst nun deine ersten Einheiten setzen. Es sind " + anzahlSetzbareEinheiten);
			System.out.println("nach setzen von SP" + sp.getTurn());//!!!!!!!!!!!!!!!!!!!!!!!!TEST!!!!!!!!!
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

	public void neuerSpieler() {
		JFrame frame = new JFrame("Spieler erstellen");
		frame.setSize(150, 300);
		frame.setLayout(new MigLayout("wrap 2", "[][100]", "[][]"));
		JLabel nameLab = new JLabel("Name:");
		JTextField nameText = new JTextField();
		JButton erstellenBtn = new JButton("Erstellen");

		erstellenBtn.addActionListener(erstellen -> {
			try {
				spielerErstellen(frame, nameText.getText());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		frame.add(nameLab, "right");
		frame.add(nameText, "left,growx");
		frame.add(erstellenBtn, "center,spanx 2");
		frame.setVisible(true);
	}

	private void spielerErstellen(JFrame frame, String name)throws RemoteException {
		//Spieler erstellen
		try {
			sp.erstelleSpieler(name, anzahlSpieler);
			frame.dispose();
		} catch (SpielerExistiertBereitsException sebe) {
			JOptionPane.showMessageDialog(null, sebe.getMessage(), "Name vergeben", JOptionPane.WARNING_MESSAGE);
		}

		//Wenn alle Spieler erstellt sind, dann Welt und Missionen erstellen und aufteilen

		//			ArrayList<Spieler> spielerListe = new ArrayList<>();
		//			for(int i = 1; i <= anzahlSpieler; i++){
		//				spielerListe.add(sp.getSpielerVonIndex(i));
		//			}
		//			try {
		//				sp.laenderErstellen();
		//				sp.laenderverbindungenUndKontinenteErstellen();
		//				sp.missionsListeErstellen();
		//			} catch (IOException e) {
		//				JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.WARNING_MESSAGE);
		//			}
		//			sp.missionenVerteilen();
		//			sp.laenderAufteilen();
		//			
		//			sp.farbenVerteilen();

	}



	public void spielLaden(){
		try{
			sp.spielLaden("Game2.txt");
			geladenesSpielErstellen();
		} catch(Exception e) {
			System.out.println("Kann nicht geladen werden");
		}
	}

	private void geladenesSpielErstellen()throws RemoteException {

		aktiverSpieler = sp.getAktiverSpieler();
		//Spieler erstellen
		frame.remove(startPanel);
		frame.setTitle("Risiko");
		frame.setSize(1250, 817);
		frame.setLocationRelativeTo(null);
		//			frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

		//Fenster mit Layout und Paneln füllen
		frame.setLayout(new MigLayout("debug, wrap2", "[1050][]", "[][][]"));
		spielfeld = new MapPanel(this, schrift,1050, 550);
		spielerListPanel = new SpielerPanel(schrift, uberschrift);
		missionPanel = new MissionPanel(uberschrift, schrift,this);
		infoPanel = new InfoPanel(sp.getTurn() + "",   schrift, uberschrift);
		buttonPanel = new ButtonPanel(this, uberschrift);
		statistikPanel = new StatistikPanel(sp.getSpielerList(), sp.getLaenderListe(), schrift, uberschrift);
		consolePanel = new ConsolePanel(schrift);


		//Menuleiste erstellen
		menu = new MenuBar();
		Menu datei = new Menu("Datei");
		Menu grafik = new Menu("Grafik");
		menu.add(datei);
		menu.add(grafik);
		MenuItem speichern = new MenuItem("Speichern");
		MenuItem schliessen = new MenuItem("Schließen");
		Menu aufloesung = new Menu("Aufloesung");
		MenuItem aufloesung1 = new MenuItem("1920x1080");
		MenuItem aufloesung2 = new MenuItem("1280x800");
		MenuItem aufloesung3 = new MenuItem("3.Auflösung");
		datei.add(speichern);
		datei.add(schliessen);
		grafik.add(aufloesung);
		aufloesung.add(aufloesung1);
		aufloesung.add(aufloesung2);
		aufloesung.add(aufloesung3);
		aufloesung1.addActionListener(ausfuehren -> aufloesungAendern(1920, 1080));
		aufloesung2.addActionListener(ausfuehren -> aufloesungAendern(1280, 800));
		speichern.addActionListener(save -> spielSpeichern());
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

		missionPanel.kartenAusgeben(aktiverSpieler);
		//Rahmen auf aktiven Spieler
		spielerListPanel.setAktiverSpieler(sp.getSpielerList().indexOf(aktiverSpieler));
		System.out.println(sp.getTurn());
		switch (sp.getTurn()) {
		case STARTPHASE:
			buttonPanel.phaseDisable();
			anzahlSetzbareEinheiten = sp.checkAnfangsEinheiten();
			buttonPanel.setEinheitenVerteilenLab(anzahlSetzbareEinheiten);
			consolePanel.textSetzen(aktiverSpieler.getName() + " kann nun seine ersten Einheiten setzen. Es sind " + anzahlSetzbareEinheiten);
			missionPanel.setMBeschreibung(sp.getMissionVonSpieler(aktiverSpieler).getBeschreibung());
			break;
		case ANGRIFF:
			missionPanel.klickDisablen();
			consolePanel.textSetzen(aktiverSpieler.getName() + " du kannst nun angreifen.");
			buttonPanel.angreifenAktiv("angreifendes Land", "verteidigendes Land");
			break;
		case VERTEILEN:
			missionPanel.kartenAusgeben(aktiverSpieler);
			missionPanel.klickEnablen();
			buttonPanel.phaseDisable();
			anzahlSetzbareEinheiten = sp.bekommtEinheiten(aktiverSpieler);
			consolePanel.textSetzen(
					aktiverSpieler.getName() + " du kannst " + anzahlSetzbareEinheiten + " Einheiten setzen.");
			buttonPanel.verteilenAktiv(anzahlSetzbareEinheiten);
			missionPanel.setMBeschreibung(sp.getMissionVonSpieler(aktiverSpieler).getBeschreibung());
			break;
		case VERSCHIEBEN:
			istSpielerRaus();
			spielfeld.wuerfelEntfernen();
			consolePanel.textSetzen(aktiverSpieler.getName() + " verschiebe nun deine Einheiten.");
			buttonPanel.verschiebenAktiv("erstes Land", "zweites Land");
			if(aktiverSpieler.getEinheitenkarten().size() < 5){
				sp.einheitenKarteZiehen(aktiverSpieler);			
			}
			missionPanel.kartenAusgeben(aktiverSpieler);
			break;
		}
		infoPanel.changePanel(sp.getTurn() + "");
		spielfeld.fahnenVerteilen(sp.getLaenderListe());
	}

	private void spielSpeichern() {
		try{
			sp.spielSpeichern("Game2.txt");
		}catch(IOException e){
			consolePanel.textSetzen("Spiel konnte nicht gespeichert werden" + e.getMessage());
		}
	}

	private void landWaehlen(String landcode)throws RemoteException {
		String landstring = sp.getLandVonFarbcode(landcode);
		Land land = sp.stringToLand(landstring);

		if (land != null) {
			spielfeld.labelsSetzen(land.getName(), land.getEinheiten(), land.getBesitzer().getName());
			//Phasen abhängige Aktion beim Klicken eines Landes
			switch (sp.getTurn()) {
			case STARTPHASE:
				verteilen(landstring, land);
				break;
			case ANGRIFF:

				angreifen(landstring, land, aktiverSpieler);
				break;
			case VERTEILEN:

				verteilen(landstring, land);
				break;
			case VERSCHIEBEN:
				verschieben(landstring, land);
				break;
			}
		}
		statistikPanel.statistikPanelAktualisieren(sp.getLaenderListe(), sp.getSpielerList());
	}


	private void verteilen(String landstring, Land land)throws RemoteException {
		if(sp.getTurn().toString() == "STARTPHASE"){
			try {
				sp.landWaehlen(landstring, ownSpieler);

				if (anzahlSetzbareEinheiten > 0) {
					missionPanel.klickDisablen();
					consolePanel.textSetzen("Du kannst nun keine Einheitenkarten mehr tauschen");
					sp.einheitenPositionieren(1, land);
					anzahlSetzbareEinheiten--;
					spielfeld.labelsSetzen("", land.getEinheiten(), "");
					//				spielfeld.fahneEinheit(land.getEinheitenLab());
					statistikPanel.statistikPanelAktualisieren(sp.getLaenderListe(), sp.getSpielerList());
					buttonPanel.setEinheitenVerteilenLab(anzahlSetzbareEinheiten);
				}
				if(anzahlSetzbareEinheiten == 0){
					consolePanel.textSetzen("Du hast alle Einheiten gesetzt.");
					sp.spielerBereit();
				}
			} catch (KannLandNichtBenutzenException lene) {
				consolePanel.textSetzen(lene.getMessage());
			}
		}else{
		//eine Einheit verteilen in Startphase oder Verteilen-Phase
		try {
			sp.landWaehlen(landstring, aktiverSpieler);

			if (anzahlSetzbareEinheiten > 0) {
				missionPanel.klickDisablen();
				consolePanel.textSetzen("Du kannst nun keine Einheitenkarten mehr tauschen");
				sp.einheitenPositionieren(1, land);
				anzahlSetzbareEinheiten--;
				spielfeld.labelsSetzen("", land.getEinheiten(), "");
				//				spielfeld.fahneEinheit(land.getEinheitenLab());
				statistikPanel.statistikPanelAktualisieren(sp.getLaenderListe(), sp.getSpielerList());
				buttonPanel.setEinheitenVerteilenLab(anzahlSetzbareEinheiten);
			}
			if(anzahlSetzbareEinheiten == 0){
				consolePanel.textSetzen("Du hast alle Einheiten gesetzt.");
				buttonPanel.phaseEnable();
			}
		} catch (KannLandNichtBenutzenException lene) {
			consolePanel.textSetzen(lene.getMessage());
		}
		}
	}

	private void angreifen(String landstring, Land land, Spieler spieler)throws RemoteException {
		if (land1 == null) {
			//Land wählen mit dem angegriffen werden soll
			try {
				sp.landWaehlen(landstring, spieler);
				sp.checkEinheiten(landstring, 1);
				land1 = land;
				buttonPanel.angreifenAktiv(land1.getName(), "verteidigendes land");
			} catch (KannLandNichtBenutzenException lene) {
				consolePanel.textSetzen(lene.getMessage());
			} catch (NichtGenugEinheitenException e) {
				consolePanel.textSetzen(e.getMessage());
			}
		} else {
			//Land wählen, welches angegriffen werden soll und angreifen
			try {
				sp.istNachbar(land1, land, spieler);
				sp.istGegner(landstring, spieler);
				land2 = land;
				buttonPanel.angreifenAktiv(land1.getName(), land2.getName());
				buttonPanel.angriffEnable();
			} catch (KeinNachbarlandException knle) {
				try {
					sp.landWaehlen(landstring, spieler);
					land1 = land;
				} catch (KannLandNichtBenutzenException lene) {
					consolePanel.textSetzen(knle.getMessage());
				}
			} catch (KeinGegnerException kge) {
				try {
					sp.landWaehlen(landstring, spieler);
				} catch (KannLandNichtBenutzenException lene) {
					consolePanel.textSetzen(lene.getMessage());
				}
			}
		}
	}

	private void verschieben(String landstring, Land land)throws RemoteException {
		if (land1 == null) {
			//Land wählen von dem aus verschoben werden soll
			try {
				sp.landWaehlen(landstring, aktiverSpieler);
				sp.benutzeLaender(land);
				sp.checkEinheiten(landstring, 1);
				land1 = land;
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
				sp.landWaehlen(landstring, aktiverSpieler);
				sp.istNachbar(land1, land, aktiverSpieler);
				land2 = land;
				buttonPanel.verschiebenAktiv(land1.getName(), land2.getName());
				buttonPanel.verschiebenEnabled();
			} catch (KannLandNichtBenutzenException klnbe) {
				consolePanel.textSetzen(klnbe.getMessage());
			} catch (KeinNachbarlandException kne) {
				try {
					sp.landWaehlen(landstring, aktiverSpieler);
					sp.checkEinheiten(landstring, 1);
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
		Land aLand = land1;
		Land vLand = land2;
		//Angriff durchführen
		AngriffRueckgabe angriffRueckgabe = sp.befreiungsAktion(new Angriff(aLand, vLand));
		//Würfel anzeigen lassen
		spielfeld.wuerfelAnzeigen(angriffRueckgabe.getWuerfelAngreifer(), angriffRueckgabe.getWuerfelVerteidiger());
		//Angriff auswerten und Ergebnis anzeigen
		if (angriffRueckgabe.isErobert() != true) {
			//Ausgabe falls nicht erobert ist
			if (angriffRueckgabe.hatGewonnen().equals("V")) {
				consolePanel.textSetzen(vLand.getBesitzer().getName() + " hat gewonnen.");
			} else if (angriffRueckgabe.hatGewonnen().equals("A")) {
				consolePanel.textSetzen(aLand.getBesitzer().getName() + " hat gewonnen.");
			} else {
				consolePanel.textSetzen("Ihr habt unentschieden gespielt, beide verlieren eine Einheit.");
			}
			//Einheiten auf Fahne setzen
			spielfeld.fahneEinheit(sp.getLaenderListe());
			land1 = null;
			land2 = null;
			buttonPanel.angreifenAktiv("angreifendes Land", "verteidigendes Land");
		} else {
			//bei Eroberung
			//			vLand.setFahne(aSpieler.getFarbe());
			spielfeld.fahnenVerteilen(sp.getLaenderListe());
			consolePanel.textSetzen(aLand.getBesitzer().getName() + " hat das Land erobert.");
			genugEinheiten = false;
			// Verschieben nach Eroberung
			if (aLand.getEinheiten() == 2) {
				//wenn nur zwei Einheiten auf ANgriffsland sind
				consolePanel.textSetzen("Eine Einheit wird auf " + vLand.getName() + " gesetzt.");
				sp.eroberungBesetzen(aLand, vLand, 1);
				genugEinheiten = true;
				spielfeld.fahneEinheit(sp.getLaenderListe());
				land1 = null;
				land2 = null;
				buttonPanel.angreifenAktiv("angreifendes Land", "verteidigendes Land");
			} else {
				//verschieben einstellungen in button panel öffnen
				buttonPanel.verschiebenNachAngreifenAktiv(aLand.getName(), vLand.getName());
			}
			schuss();
		}
	}


	private void istSpielerRaus()throws RemoteException{
		//Überprüfung ob ein Spieler verloren hat
		List<Spieler> spielerListe = sp.getSpielerList();
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

	public void karteEintauschen(ArrayList<String> tauschKarten) {
		//Karten eintauschen
		try {
			anzahlSetzbareEinheiten += sp.kartenEinloesen(aktiverSpieler, tauschKarten);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		missionPanel.kartenAusgeben(aktiverSpieler);
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
					landWaehlen(landcode);
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
		spielErstellen();
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
		//		spielerListPanel.setAktiverSpieler(sp.getSpielerList().indexOf(aktiverSpieler) + 1);

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
			sp.checkEinheiten(land1.getName(), einheiten);
			sp.einheitenPositionieren(-einheiten, land1);
			sp.einheitenPositionieren(einheiten, land2);
			spielfeld.labelsSetzen("", land1.getEinheiten(), "");
			spielfeld.labelsSetzen("", land2.getEinheiten(), "");
			spielfeld.fahneEinheit(sp.getLaenderListe());
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
			sp.checkEinheiten(land1.getName(), einheiten);
			sp.eroberungBesetzen(land1, land2, einheiten);
			spielfeld.fahneEinheit(sp.getLaenderListe());
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
		System.out.println("Beginn hGE " + sp.getTurn());//!!!!!!!!!!!!!!!!!!!!!!!!TEST!!!!!!!!!
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(event instanceof GameControlEvent){

			GameControlEvent gce = (GameControlEvent)event;
			aktiverSpieler = gce.getSpieler();

			missionPanel.kartenAusgeben(ownSpieler);
			infoPanel.changePanel(sp.getTurn() + "");
			//Rahmen auf aktiven Spieler
			spielerListPanel.setAktiverSpieler(sp.getSpielerList().indexOf(aktiverSpieler) + 1);
			if(aktiverSpieler.getName().equals(ownSpieler.getName())) {
				System.out.println("aktiver SPieler " + sp.getTurn());//!!!!!!!!!!!!!!!!!!!!!!!!TEST!!!!!!!!!
				switch (gce.getTurn()) {
				case STARTPHASE:
					System.out.println("gce " + gce.getTurn());
					buttonPanel.phaseDisable();
					anzahlSetzbareEinheiten = sp.checkAnfangsEinheiten();
					buttonPanel.setEinheitenVerteilenLab(anzahlSetzbareEinheiten);
					consolePanel.textSetzen(aktiverSpieler.getName() + " du kannst nun deine ersten Einheiten setzen. Es sind " + anzahlSetzbareEinheiten);
					missionPanel.setMBeschreibung(sp.getMissionVonSpieler(ownSpieler).getBeschreibung());
					break;
				case ANGRIFF:
					System.out.println("gce " + gce.getTurn());
					missionPanel.klickDisablen();
					consolePanel.textSetzen(aktiverSpieler.getName() + " du kannst nun angreifen.");
					buttonPanel.angreifenAktiv("angreifendes Land", "verteidigendes Land");
					break;
				case VERTEILEN:
					missionPanel.kartenAusgeben(aktiverSpieler);
					missionPanel.klickEnablen();
					buttonPanel.phaseDisable();
					anzahlSetzbareEinheiten = sp.bekommtEinheiten(aktiverSpieler);
					consolePanel.textSetzen(
							aktiverSpieler.getName() + " du kannst " + anzahlSetzbareEinheiten + " Einheiten setzen.");
					buttonPanel.verteilenAktiv(anzahlSetzbareEinheiten);
					missionPanel.setMBeschreibung(sp.getMissionVonSpieler(aktiverSpieler).getBeschreibung());
					break;
				case VERSCHIEBEN:
					istSpielerRaus();
					spielfeld.wuerfelEntfernen();
					consolePanel.textSetzen(aktiverSpieler.getName() + " verschiebe nun deine Einheiten.");
					buttonPanel.verschiebenAktiv("erstes Land", "zweites Land");
					if(aktiverSpieler.getEinheitenkarten().size() < 5){
						sp.einheitenKarteZiehen(aktiverSpieler);			
					}
					missionPanel.kartenAusgeben(aktiverSpieler);
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
					//					statistikPanel.statistikAktualisieren(sp.getLaenderListe(), sp.getSpielerList());
					missionPanel.setMBeschreibung(sp.getMissionVonSpieler(ownSpieler).getBeschreibung());
					statistikPanel.statistikAktualisieren(sp.getLaenderListe(), sp.getSpielerList());
					break;
				case ALLE_BEREIT:
					phaseButtonClicked();
					break;
				}
			} else {
				System.out.println("inaktiverSpieler " + sp.getTurn());//!!!!!!!!!!!!!!!!!!!!!!!!TEST!!!!!!!!!
				missionPanel.klickDisablen();
				missionPanel.kartenAusgeben(ownSpieler);
				missionPanel.setMBeschreibung(sp.getMissionVonSpieler(ownSpieler).getBeschreibung());
				switch (gce.getTurn()) {
				case STARTPHASE:
					System.out.println("I - gce " + gce.getTurn());
					//buttonPanel.phaseDisable();
					anzahlSetzbareEinheiten = sp.checkAnfangsEinheiten();
					buttonPanel.setEinheitenVerteilenLab(anzahlSetzbareEinheiten);
					consolePanel.textSetzen(aktiverSpieler.getName() + " du kannst nun deine ersten Einheiten setzen. Es sind " + anzahlSetzbareEinheiten);
					missionPanel.setMBeschreibung(sp.getMissionVonSpieler(ownSpieler).getBeschreibung());
					break;
				case ANGRIFF:
					System.out.println("I - gce " + gce.getTurn());
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
					for (Spieler s : sp.getSpielerList()) {
						spielerListPanel.setLabel(spielerNr, s.getName(), s.getFarbe());
						spielerNr++;
					}
					statistikPanel.statistikAktualisieren(sp.getLaenderListe(), sp.getSpielerList());
					break;
				}
			}	


			infoPanel.changePanel(sp.getTurn() + "");
		}else{
			GameActionEvent gae = (GameActionEvent)event;
			aktiverSpieler = gae.getSpieler();
			switch(gae.getType()){
			case VERTEILEN:

				spielfeld.fahneEinheit(sp.getLaenderListe());
				statistikPanel.statistikPanelAktualisieren(sp.getLaenderListe(), sp.getSpielerList());
				for(Spieler s : sp.getSpielerList()){
					System.out.println(s.getName());
				}
				break;
			}

		}

	}

	@Override
	public void karteEintauschen(List<String> tauschKarten) {
		// TODO Auto-generated method stub

	}


}	