package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import client.BeitretenPanel.BeitretenButtonClicked;
import client.ButtonPanel.ButtonClickHandler;
import client.ErstellenPanel.ErstellenButtonClicked;
import client.LadenPanel.LadenButtonClicked;
import client.MapPanel.MapClickHandler;
import client.MissionPanel.KarteClickedHandler;
import client.StartPanel.StartHandler;
import exceptions.KannEinheitenNichtVerschiebenException;
import exceptions.KannLandNichtBenutzenException;
import exceptions.KeinGegnerException;
import exceptions.KeinNachbarlandException;
import exceptions.LandBereitsBenutztException;
import exceptions.NichtGenugEinheitenException;
import exceptions.SpielBereitsErstelltException;
import exceptions.SpielerExistiertBereitsException;
import exceptions.SpielerGibtEsNichtException;
import exceptions.SpieleranzahlErreichtException;
import net.miginfocom.swing.MigLayout;
import valueobjects.Angriff;
import valueobjects.AngriffRueckgabe;
import valueobjects.GameActionEvent;
import valueobjects.GameControlEvent;
import valueobjects.GameEvent;
import valueobjects.GameEventListener;
import valueobjects.Land;
import valueobjects.ServerRemote;
import valueobjects.Spieler;
import valueobjects.Spielstand;

public class RisikoClientGUI extends UnicastRemoteObject implements MapClickHandler, ButtonClickHandler, ErstellenButtonClicked, KarteClickedHandler, GameEventListener, StartHandler, BeitretenButtonClicked, LadenButtonClicked {

	private ServerRemote sp;
	private SpielerPanel spielerListPanel;
	private MissionPanel missionPanel;
	private MapPanel spielfeld;
	private InfoPanel infoPanel;
	private ButtonPanel buttonPanel;
	private StatistikPanel statistikPanel;
	private ConsolePanel consolePanel;
	private StartPanel startPanel;
	private ErstellenPanel erstellenPanel;
	private BeitretenPanel beitretenPanel;
	private GewonnenPanel gewonnenPanel;
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
	private Boolean erobert = false;
	private Boolean gewonnen = false;
	private boolean imSpiel = false;
	private Registry registry;
	
	/**
	 * Konstruktor
	 * Jframe wird erstellt und das erste Panel (erstesPanelStartmenu) wird aufgerufen
	 * @throws RemoteException
	 */
	private RisikoClientGUI()throws RemoteException {
		frame = new JFrame();
		erstesPanelStartmenu();
		
	}
	
	/**
	 * main Methode erstellt ein Objekt vom Konstruktor
	 * @param args
	 * @throws RemoteException
	 */
	public static void main(String[] args)throws RemoteException{
		new RisikoClientGUI();
	}
	
	/**
	 * Erstes Panel (Startmenü)
	 * Setzt die CloseOperation und fügt das StartPanel in frame ein.
	 * Buttons: Spiel erstellen, Spiel laden, Spiel beitreten, Beenden
	 */
	private void erstesPanelStartmenu() {
		imSpiel = false;
		//Schriften für alle Panel
		
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter(){

			public void windowClosing(WindowEvent we){
				if(imSpiel && !gewonnen){
	
						try {
							if(!sp.getTurn().toString().equals("STARTPHASE")){
								spielSpeichernNachEndeFrage();
							}else{
								sp.spielBeenden(ownSpieler);
								

							}
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					
				}else if(gewonnen){
						try {
							sp.spielBeenden(ownSpieler);
						} catch (RemoteException e) {
							e.printStackTrace();
						}

				} else {
					System.exit(0);
				}
			}
		});

		//Läd und setzt Icon
		Image im = null;
		try {
			im = ImageIO.read(new File("./Bilder/land.png"));
		} catch (IOException ex) {
		}
		frame.setIconImage(im);

		uberschrift = new Font(Font.SERIF, Font.BOLD, 25);
		schrift = new Font(Font.SANS_SERIF, Font.PLAIN, 17);

		//Spielmenu Fenster erstellen
		frame.setTitle("Spiel starten");
		frame.setSize(335, 370);
		frame.setLocationRelativeTo(null);
		startPanel = new StartPanel(this);
		frame.add(startPanel);
		frame.setResizable(true);
		frame.setVisible(true);
	}

	
	/**
	 * Spiel erstellen Panel:
	 * Das vorherige Panel wird removed und erstellenPanel wird im Frame eingebunden.
	 * 
	 */
	private void zweitesPanelSpielErstellen() {
		//Spieler erstellen Fenster erstellen
		frame.setTitle("Spiel erstellen");
		frame.setSize(300, 200);
		frame.setLocationRelativeTo(null);
		erstellenPanel = new ErstellenPanel(this);
		frame.add(erstellenPanel);
		frame.setVisible(true);
		frame.repaint();
		frame.revalidate();
	}

	/**
	 * Spiel beitreten Panel:
	 * Das vorherige Panel wird removed und beitretenPanel wird im Frame eingebunden.
	 * 
	 */
	private void zweitesPanelSpielBeitreten(){
		frame.setTitle("Spiel beitreten");
		frame.setSize(320, 120);
		frame.setLocationRelativeTo(null);
		beitretenPanel = new BeitretenPanel(this);
		frame.add(beitretenPanel);
		frame.setVisible(true);
		frame.repaint();
		frame.revalidate();
	}

	/**
	 * Spiel laden Panel:
	 * Das vorherige Panel wird removed und ladenPanel wird im Frame eingebunden.
	 * 
	 */
	public void zweitesPanelSpielLaden(){
		frame.remove(startPanel);
		frame.setTitle("Spiel laden");
		frame.setSize(320, 250);
		frame.setLocationRelativeTo(null);
		ladenPanel = new LadenPanel(this);
		frame.add(ladenPanel);
		frame.setVisible(true);
		frame.repaint();
		frame.revalidate();
	}

	/**
	 * Ein JOptionPane wird angezeigt, indem man einen Namen für den Spielstand eingeben kann.
	 * Die länge vom angegebenen Namen wird auf eine Länge > 0 geprüft und spielSpeichern wird auf dem Server aufgerufen.
	 * Dem Namen wird ein .txt hinzugefügt, damit der Server die entsprechende Datei anlegen kann.
	 * @throws RemoteException
	 */
	private void spielSpeichern() throws RemoteException {
		String name = "";

		name = JOptionPane.showInputDialog(frame, "Spiel speichern. Gebe einen Namen ein.");
		if(name.length() > 0){
			try {
				sp.spielSpeichern("../RisikoCommon/Speicher/" + name + ".txt");
			} catch (IOException e) {
				consolePanel.textSetzen("Spiel konnte nicht gespeichert werden. " + e.getMessage());
			}
		}else{
			consolePanel.textSetzen("Du musst einen Namen eingeben.");
		}
	}

	/**
	 * Wenn das Fenster geschlossen wird (X), wird ein JOptionPane angezeigt.
	 * Je nach Auswahl werden die Methoden 
	 * Ja/spielSpeichern(),spielBeenden()
	 * Nein/sp.spielBeenden()
	 * aufgerufen. Bei Abbruch schließt sich das JOptionPane.
	 * @throws RemoteException
	 */
	private void spielSpeichernNachEndeFrage() throws RemoteException {
		//Rückgabe = 0 ist JA, Rückgabe = 1 ist NEIN, Rückgabe 2 ist CANCEL
		int antwort = JOptionPane.showConfirmDialog(frame, "Spiel speichern bevor es geschlossen wird?");
		if( antwort == 0) {
			spielSpeichern();
			sp.spielBeenden(ownSpieler);
		} else if( antwort == 1) {
			sp.spielBeenden(ownSpieler);
		}
	}

	/**
	 * erstellt oder läd ein Spiel, erzeugt das Frame mit Spielstand oder komplett neu
	 */
	public void hauptspielStarten(String name, int anzahlSpieler, String dateiPfad) throws RemoteException, SpielBereitsErstelltException {
		boolean geladenesSpiel = false;
		if(dateiPfad != null) {
			geladenesSpiel = true;
		}
		serverVerbindungHerstellen();

		if(anzahlSpieler > 1){
			if(sp.getSpielerList().size() > 0){
				throw new SpielBereitsErstelltException();
				
			}else{
				spielerRegistrieren(name);
			}
		}else{
			spielerRegistrieren(name);
		}

		Spielstand spielstand = null;
		if(geladenesSpiel) {

			try {
				spielstand = sp.spielLaden(dateiPfad);
				name = spielstand.getSpielerListe().get(0).getName();
				if(ownSpieler == aktiverSpieler) {
					anzahlSetzbareEinheiten = spielstand.getSetzbareEinheitenVerteilen();
				}
			} catch (IOException | SpielerExistiertBereitsException e) {
				e.printStackTrace();
			}
		}
		if(geladenesSpiel) {
			sp.spielaufbauMitSpielstand(spielstand);
			frame.remove(ladenPanel);
		}
		try {
			//Spieler muss erstellt werden, bevor frame gebaut wird, da sonst bei falscher Namenseingabe spackt
			sp.spielerErstellen(name,anzahlSpieler);


			//Frame erzeugen
			frame.setLayout(new MigLayout("wrap2", "[1050][]", "[][][]"));
			spielfeld = new MapPanel(this, schrift,1050, 550);
			spielerListPanel = new SpielerPanel(schrift, uberschrift);
			missionPanel = new MissionPanel(uberschrift, schrift,this);
			infoPanel = new InfoPanel("Warten", schrift, uberschrift);
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
				if(!geladenesSpiel) {
					sp.spielaufbauWennSpieleranzahlErreicht();
					frame.remove(beitretenPanel);
				}
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
			imSpiel = true;
		} catch (SpielerExistiertBereitsException sebe) {
			JOptionPane.showMessageDialog(null, sebe.getMessage(), "Name vergeben", JOptionPane.WARNING_MESSAGE);
		} catch (SpielerGibtEsNichtException sgene) {
			JOptionPane.showMessageDialog(null, sgene.getMessage(), "Gibts nicht", JOptionPane.WARNING_MESSAGE);
		} catch (SpieleranzahlErreichtException see) {
			JOptionPane.showMessageDialog(null, see.getMessage(), "Belegt", JOptionPane.WARNING_MESSAGE);

		}
	}

	/**
	 * Stellt Verbindung zwischen GameClient und GameServer her und übermittelt
	 * anschließend den registrierten Spieler an den Server
	 * @param name
	 * @throws RemoteException
	 */
	private void serverVerbindungHerstellen() throws RemoteException {
		try {
			String servicename = "GameServer";
			Registry registry = LocateRegistry.getRegistry("127.0.0.1",4711);
			sp = (ServerRemote)registry.lookup(servicename);
			
		} catch (NotBoundException nbe) {
		}

	}
	
	/**
	 * registriert Spieler mit GameEventListener
	 * @param name
	 * @throws RemoteException
	 */
	private void spielerRegistrieren(String name) throws RemoteException {
		sp.addGameEventListener(this);
		sp.serverBenachrichtigung("Spieler registriert: " + name);
	}

	/** Zeigt nach dem Anklicken eines Landes je nach Phase das entsprechende
	 * ButtonPanel an und aktualisiert anschließend die Statistik
	 * @param landcode
	 * @throws RemoteException
	 */
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


	/** Wenn setzbare Einheiten vorhanden sind  werden die Einheiten gesetzt.
	 * Wenn nicht, gibt die Konsole aus, dass alle gesetzt wurden. Wenn in der Startphase
	 * keine setzbaren Einheiten übrig sind, ist der Spieler bereit und es wird in
	 * die nächste Phase übergegangen.
	 * 
	 * @param land
	 * @throws RemoteException
	 */
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

	/** Wenn noch kein Land ausgewählt wurde wird das ausgewählte Land überprüft,
	 * ob es genug setzbare Einheiten besitzt und zum AngreiferLand.
	 * Wenn bereits ein Land ausgewählt wurde wird überprüft ob es Nachbarländer und
	 * die Besitzer Gegner sind und anschließend zum angegriffenen Land.
	 * @param land
	 * @throws RemoteException
	 */
	private void angreifenButtonPanelAnzeige(Land land)throws RemoteException {
		if (land1 == null) {
			//Land wählen mit dem angegriffen werden soll
			try {
				sp.landWaehlen(land, aktiverSpieler);
				sp.checkObMehrAlsZweiEinheiten(land);
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
					buttonPanel.angreifenAktiv(land1.getName(), "verteidigendes land");
				} catch (KannLandNichtBenutzenException lene) {
					consolePanel.textSetzen(e.getMessage());
				}
			}
		}
	}

	/** Wenn noch kein Land ausgewählt wurde, wird von dem ausgewählten Land aus verschoben.
	 * Wenn bereits eins ausgewählt wurde, wird auf das ausgewählte Land verschoben.s
	 * @param land
	 * @throws RemoteException
	 */
	private void verschiebenButtonPanelAnzeige(Land land)throws RemoteException {
		if (land1 == null) {
			//Land wählen von dem aus verschoben werden soll
			try {
				sp.landWaehlen(land, aktiverSpieler);
				sp.benutzeLaender(land);
				sp.checkObMehrAlsZweiEinheiten(land);
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
					sp.checkObMehrAlsZweiEinheiten(land);
					land1 = land;
				} catch (KannLandNichtBenutzenException lene) {
					consolePanel.textSetzen(lene.getMessage());
				} catch (NichtGenugEinheitenException ngee) {
					consolePanel.textSetzen(ngee.getMessage());
				}
			}
		}
	}

	/** Überprüfung ob ein Spieler verloren hat
	 * @throws RemoteException
	 */
	private void istSpielerRaus()throws RemoteException{
		
		for(Spieler s : spielerListe){
			if(sp.spielerRaus(s)){
				consolePanel.textSetzen("Der Spieler " + s.getName() + " hat verloren und ist raus");
				istSpielerRaus();
				break;
			}
		}
	}

	/**
	 * Der Spieler der gewonnen hat wird im gewonnenPannel
	 * angezeigt.
	 */
	public void gewonnenPanelAnzeigen(){
		frame.remove(spielfeld);
		frame.remove(spielerListPanel);
		frame.remove(missionPanel);
		frame.remove(infoPanel);
		frame.remove(statistikPanel);
		frame.remove(consolePanel);
		frame.remove(buttonPanel);
		frame.remove(menu);
		frame.setTitle(aktiverSpieler.getName() + " hat gewonnen");
		frame.setSize(250, 300);
		frame.setLocationRelativeTo(null);
		gewonnenPanel = new GewonnenPanel(aktiverSpieler, uberschrift);
		frame.add(gewonnenPanel, "center");
		frame.setVisible(true);
		frame.repaint();
		frame.revalidate();
	}

	public void karteEintauschen(Boolean erfolgreich, ArrayList<String> tauschKarten) {
		if(erfolgreich) {
			try {
				anzahlSetzbareEinheiten += sp.kartenEinloesen(aktiverSpieler, tauschKarten);
				missionPanel.klickDisablen();
				buttonPanel.setEinheitenVerteilenLab(anzahlSetzbareEinheiten);
				consolePanel.textSetzen("Du hast die Karten eingetauscht und kannst nun " + anzahlSetzbareEinheiten + " setzen.");
				missionPanel.kartenAusgeben(aktiverSpieler,sp.getSpielerList());
			} catch (RemoteException e) {}
		} else {
			consolePanel.textSetzen("Die Karten konnten nicht eingetauscht werden.");
		}
	}

	/**
	 * wertet Farbcode bei jedem Mausklick aus
	 */
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

	/**
	 * startet Spiel Erstellen oder Beitreten Panel
	 */
	public void SpielerRegistrierungOeffnen(boolean ersterSpieler) {
		frame.remove(startPanel);
		if(ersterSpieler) {
			zweitesPanelSpielErstellen();
		} else {
			zweitesPanelSpielBeitreten();
		}
	}

	/**
	 * setzt den nächsten Zug beim Klicken des Nächste Phase Buttons
	 */
	public void phaseButtonClicked() throws RemoteException{
		//Wenn Mission erfüllt, dann gewonnen aufrufen
		try {
			if(sp.getSpielerMission(aktiverSpieler).istAbgeschlossen()){
				sp.zeigeGewinner(aktiverSpieler);
			} else {
				sp.nextTurn();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Button Angriff führt einen Angriff aus, zeigt die Würfel an und wertet die Anzeige aus je nach Wurd Ergebnis
	 */
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
					land2.setEinheiten(0);
					buttonPanel.verschiebenNachAngreifenAktiv(land1.getName(), land2.getName());
					spielfeld.mapEnabled(false);
				}
				schussSound();
			}
		} catch (RemoteException e) {
		} catch (KeinNachbarlandException e) {
			consolePanel.textSetzen(e.getMessage());
		}
	}

	/**
	 * Verschieben Button ermöglicht das Klicken auf erst ein und dann das zweite Land und posotioniert die Einheitzen je nach Eingabe
	 */
	public void verschiebenButtonClicked(int einheiten) {
		try {
			sp.checkEinheitenAnzahlVerteilbar(land1, einheiten);
			sp.einheitenPositionieren(-einheiten, land1);
			sp.einheitenPositionieren(einheiten, land2);
			spielfeld.labelsSetzen("", land1.getEinheiten(), "");
			spielfeld.labelsSetzen("", land2.getEinheiten(), "");
			spielfeld.fahneEinheit(laenderListe);
			land1 = null;
			land2 = null;
			buttonPanel.verschiebenAktiv("erstes Land", "zweites Land");
			buttonPanel.verschiebenDisabled();
		} catch (KannEinheitenNichtVerschiebenException | RemoteException kenve) {
			consolePanel.textSetzen(kenve.getMessage());
		}
	}

	/**
	 * zeigt die Verschieben Aktion an, nachdem erobert wurde
	 */
	public void verschiebenNachAngriffButtonClicked(int einheiten) {
		try {
			sp.checkEinheitenAnzahlVerteilbar(land1, einheiten);
			sp.eroberungBesetzen(land1, land2, einheiten);
			land1 = null;
			land2 = null;
			buttonPanel.angreifenAktiv("erstes Land", "zweites Land");
			spielfeld.mapEnabled(true);
		} catch (KannEinheitenNichtVerschiebenException kenve) {
			consolePanel.textSetzen(kenve.getMessage());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * gibt einen Schuss Sound aus
	 */
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
		if(event.getSpieler() != null){
		spielerListe =  sp.getSpielerList();
		laenderListe = sp.getLaenderListe();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {}
		
		if(event instanceof GameControlEvent){

			GameControlEvent gce = (GameControlEvent)event;
			aktiverSpieler = gce.getSpieler();
			
			boolean istAktiverSpieler;
			if(aktiverSpieler.getName().equals(ownSpieler.getName())) {
				istAktiverSpieler = true;
			} else {
				istAktiverSpieler = false;
			}
				
				switch (gce.getTurn()) {
				case STARTPHASE:
					spielfeld.mapLaden();
					spielfeld.fahnenVerteilen(laenderListe);
					for (Spieler s : sp.getSpielerList()) {
						spielerListPanel.setLabel(s);
					}
					if(!sp.isSpielGeladen()) {
						anzahlSetzbareEinheiten = sp.checkAnfangsEinheiten();
						consolePanel.textSetzen("Du kannst nun die ersten Einheiten setzen.");
					} else {
						for(int s = 0; s < sp.getSpielerList().size();s++) {
							if(sp.getSpielerList().get(s).equals(aktiverSpieler)) {
								spielerListPanel.setAktiverSpielerBorder(s);
							}
					missionPanel.kartenAusgeben(ownSpieler, spielerListe);

						}
						sp.beiGeladenemSpielNaechstenListener();
					}
					buttonPanel.startphase(anzahlSetzbareEinheiten);
					missionPanel.setMBeschreibung(sp.getMissionVonSpieler(ownSpieler).getBeschreibung());
					statistikPanel.statistikAktualisieren(laenderListe, spielerListe);
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
					statistikPanel.statistikPanelAktualisieren(laenderListe, spielerListe);
					spielerListPanel.setAktiverSpielerBorder(spielerListe.indexOf(aktiverSpieler));
					
					if(istAktiverSpieler) {
						missionPanel.klickEnablen();
						buttonPanel.phaseDisable();
						if(anzahlSetzbareEinheiten == 0) {
							anzahlSetzbareEinheiten = sp.bekommtEinheiten(ownSpieler);
						}
						consolePanel.textSetzen(aktiverSpieler.getName() + " du kannst " + anzahlSetzbareEinheiten + " Einheiten setzen.");
						buttonPanel.verteilenAktiv(anzahlSetzbareEinheiten);
					} else {
						missionPanel.klickDisablen();
						anzahlSetzbareEinheiten = 0;
						missionPanel.kartenAusgeben(ownSpieler, spielerListe);
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
						if(erobert) {
							sp.einheitenKarteZiehen(ownSpieler);
							erobert = false;
						}
					} else {
						buttonPanel.removeAll();
						consolePanel.textSetzen(aktiverSpieler.getName() + " darf nun Einheiten verschieben.");
					}
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
				case BEENDEN:
					if(!gewonnen) {

							JOptionPane.showMessageDialog(null, "Das Spiel wurde von " + gce.getSpieler().getName() + " beendet.");
					} else {
						frame.remove(gewonnenPanel);
						gewonnen = false;
					}
					frame.remove(spielfeld);
					frame.remove(infoPanel);
					frame.remove(spielerListPanel);
					frame.remove(statistikPanel);
					frame.remove(missionPanel);
					frame.remove(consolePanel);
					frame.remove(buttonPanel);
					frame.remove(menu);
					erstesPanelStartmenu();
					break;
				case GEWONNEN:
					gewonnen = true;
					gewonnenPanelAnzeigen();
					break;
				}
			infoPanel.changePanel(sp.getTurn() + "");
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
				if(aktiverSpieler.getName().equals(ownSpieler.getName())) {
					erobert = true;
				}
				break;
			case ANGRIFF:
				statistikPanel.statistikPanelAktualisieren(laenderListe, spielerListe);
				consolePanel.textSetzen(gae.getText());
			}
		}
			
		}else {
			JOptionPane.showMessageDialog(null, "Das Spiel wurde vom Admin beendet.");
			if(!gewonnen) {
				frame.remove(spielfeld);
				frame.remove(infoPanel);
				frame.remove(spielerListPanel);
				frame.remove(statistikPanel);
				frame.remove(missionPanel);
				frame.remove(consolePanel);
				frame.remove(buttonPanel);
				frame.remove(menu);

			} else {
				frame.remove(gewonnenPanel);
			}

			erstesPanelStartmenu();
		}
	}

	
	public void zurueckBtn(JPanel panel) {
		if(panel instanceof ErstellenPanel){
			frame.remove(erstellenPanel);
		}else if(panel instanceof LadenPanel){
			frame.remove(ladenPanel);
		}else{
			frame.remove(beitretenPanel);
		}
		erstesPanelStartmenu();
		
	}


}	