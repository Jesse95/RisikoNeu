//TODO Javadoc
//TODO Laden bei SpielerMission
//TODO speichern/Laden mitten in zug
//TODO Optionen im Menü kann raus?

package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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

import client.BeitretenPanel.BeitretenButtonClicked;
import client.ButtonPanel.ButtonClickHandler;
import client.ErstellenPanel.ErstellenButtonClicked;
import client.LadenPanel.LadenButtonClicked;
import client.MapPanel.MapClickHandler;
import client.MissionPanel.KarteClickedHandler;
import client.StartPanel.StartHandler;
import local.domain.exceptions.KannEinheitenNichtVerschiebenException;
import local.domain.exceptions.KannLandNichtBenutzenException;
import local.domain.exceptions.KeinGegnerException;
import local.domain.exceptions.KeinNachbarlandException;
import local.domain.exceptions.LandBereitsBenutztException;
import local.domain.exceptions.NichtGenugEinheitenException;
import local.domain.exceptions.SpielBereitsErstelltException;
import local.domain.exceptions.SpielerExistiertBereitsException;
import local.domain.exceptions.SpielerGibtEsNichtException;
import local.domain.exceptions.SpieleranzahlErreichtException;
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
import local.valueobjects.Spielstand;
import net.miginfocom.swing.MigLayout;


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
	private FilePersistenceManager pm = new FilePersistenceManager();
	private Boolean erobert = false;
	private Boolean gewonnen = false;
	private boolean imSpiel = false;
	private boolean einheitenGeladen = false;
	private Registry registry;

	private RisikoClientGUI()throws RemoteException {
		erstesPanelStartmenu();
	}

	public static void main(String[] args)throws RemoteException{
		new RisikoClientGUI();
	}

	private void erstesPanelStartmenu() {
		imSpiel = false;
		//Schriften für alle Panel
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter(){

			public void windowClosing(WindowEvent we){
				if(imSpiel){
					try {
						sp.spielBeenden(ownSpieler);
					} catch (RemoteException e) {

					}
				}else{
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
		frame.setSize(320, 130);
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

	private void spielSpeichern() throws RemoteException {
		String name = "";

		name = JOptionPane.showInputDialog(frame, "Spiel speichern. Gebe einen Namen ein.");
		if(name.length() > 0){
			try {
				sp.spielSpeichern("./Speicher/" + name + ".txt");
			} catch (IOException e) {
				consolePanel.textSetzen("Spiel konnte nicht gespeichert werden. " + e.getMessage());
			}
		}else{
			consolePanel.textSetzen("Du musst einen Namen eingeben.");
		}
	}

	private void spielSpeichernNachEndeFrage() throws RemoteException {
		//Rückgabe = 0 ist JA, Rückgabe = 1 ist NEIN, Rückgabe 2 ist CANCEL
		if(JOptionPane.showConfirmDialog(frame, "Spiel speichern bevor es geschlossen wird?") == 0) {
			spielSpeichern();
		}
	}

	public void hauptspielStarten(String name, int anzahlSpieler, String dateiPfad) throws RemoteException, SpielBereitsErstelltException {
		boolean geladenesSpiel = false;
		if(dateiPfad != null) {
			geladenesSpiel = true;
		}
		serverVerbindungHerstellen(name);
		if(anzahlSpieler > 1){
			if(sp.getSpielerList().size() > 0){
				throw new SpielBereitsErstelltException();
			}
		}
		//Falls Spiel geladen wird
		Spielstand spielstand = null;
		if(geladenesSpiel) {

			try {
				spielstand = sp.spielLaden(dateiPfad);
				name = spielstand.getSpielerListe().get(0).getName();
				anzahlSetzbareEinheiten = spielstand.getSetzbareEinheitenVerteilen();
				einheitenGeladen = true;
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

			//			frame.addWindowListener(new WindowAdapter(){
			//
			//				public void windowClosing(WindowEvent we){
			//					try {
			//						sp.spielBeenden(ownSpieler);
			//					} catch (RemoteException e) {
			//						
			//					}
			//				}
			//			});
			//			list = new WindowAdapter(){
			//
			//				public void windowClosing(WindowEvent we){
			//					try {
			//						sp.spielBeenden(ownSpieler);
			//					} catch (RemoteException e) {
			//						
			//					}
			//				}
			//			};
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
			imSpiel = true;
		} catch (SpielerExistiertBereitsException sebe) {
			JOptionPane.showMessageDialog(null, sebe.getMessage(), "Name vergeben", JOptionPane.WARNING_MESSAGE);
		} catch (SpielerGibtEsNichtException sgene) {
			JOptionPane.showMessageDialog(null, sgene.getMessage(), "Gibts nicht", JOptionPane.WARNING_MESSAGE);
		} catch (SpieleranzahlErreichtException see) {
			JOptionPane.showMessageDialog(null, see.getMessage(), "Belegt", JOptionPane.WARNING_MESSAGE);

		}
	}

	private void serverVerbindungHerstellen(String name) throws RemoteException {
		try {
			String servicename = "GameServer";
			registry = LocateRegistry.getRegistry("127.0.0.1",4711);
			sp = (ServerRemote)registry.lookup(servicename);
			sp.addGameEventListener(this);
			sp.serverBenachrichtigung("Spieler registriert: " + name);
		} catch (NotBoundException nbe) {
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
		GewonnenPanel gewonnenPanel = new GewonnenPanel(aktiverSpieler, schrift, uberschrift);
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
			} catch (RemoteException e) {}
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
				sp.zeigeGewinner(aktiverSpieler);
			} else {
				sp.nextTurn();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
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
					land2.setEinheiten(0);
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

	public void verschiebenNachAngriffButtonClicked(int einheiten) {
		try {
			//TODO hier muss vorher die länder anzahl aktualisierst werden, da sie immer ein zu viel hat!
			sp.checkEinheitenAnzahlVerteilbar(land1, einheiten);
			sp.eroberungBesetzen(land1, land2, einheiten);
			land1 = null;
			land2 = null;
			buttonPanel.angreifenAktiv("erstes Land", "zweites Land");
		} catch (KannEinheitenNichtVerschiebenException kenve) {
			consolePanel.textSetzen(kenve.getMessage());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
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
		if(event.getSpieler() == null){
			frame.dispose();
			erstesPanelStartmenu();
			JOptionPane.showMessageDialog(null, "Der Server wurde vom Admin geschlossen.");
			registry = null;
		}else {
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
					if(!einheitenGeladen) {
						anzahlSetzbareEinheiten = sp.checkAnfangsEinheiten();
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
					missionPanel.klickEnablen();
					if(istAktiverSpieler) {
						buttonPanel.phaseDisable();
						if(!einheitenGeladen) {
							anzahlSetzbareEinheiten = sp.bekommtEinheiten(ownSpieler);
						}
						einheitenGeladen = false;
						consolePanel.textSetzen(aktiverSpieler.getName() + " du kannst " + anzahlSetzbareEinheiten + " Einheiten setzen.");
						buttonPanel.verteilenAktiv(anzahlSetzbareEinheiten);
					} else {
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
						if(istAktiverSpieler) {
							spielSpeichernNachEndeFrage();
						}
						JOptionPane.showMessageDialog(null, "Das Spiel wurde von " + gce.getSpieler().getName() + " beendet.");
					}
					frame.dispose();
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
		}
	}


}	