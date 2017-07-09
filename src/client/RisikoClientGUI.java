//TODO Angriffsphase erweitern (Yannik)
//TODO Karten ausgeben
//TODO Backend aufraeumen
//TODO Angreifen mit nur einer Einheit funtkioniert nicht
//TODO Javadoc
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
import java.util.List;
import java.util.Vector;

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
import client.StartPanel.StartButtonClickHandler;
import local.domain.Spielfeld;
import local.domain.exceptions.KannLandNichtBenutzenException;
import local.domain.exceptions.KeinGegnerException;
import local.domain.exceptions.KeinNachbarlandException;
import local.domain.exceptions.LandBereitsBenutztException;
import local.domain.exceptions.NichtGenugEinheitenException;
import local.domain.exceptions.SpielerExistiertBereitsException;
import local.valueobjects.Angriff;
import local.valueobjects.AngriffRueckgabe;
import local.valueobjects.Land;
import local.valueobjects.Spieler;
import net.miginfocom.swing.MigLayout;

public class RisikoClientGUI extends JFrame implements MapClickHandler, ButtonClickHandler, StartButtonClickHandler, ErstellenButtonClicked, KarteClickedHandler {
	
	Spielfeld sp = new Spielfeld();
	int anzahlSpieler;
	private SpielerPanel spielerListPanel;
	private MissionPanel missionPanel;
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

	private JFrame frame;


	private RisikoClientGUI() {
		this.start();
	}

	public static void main(String[] args) {
		new RisikoClientGUI();
	}

	private void start() {
		//Schriften für alle Panel
		uberschrift = new Font(Font.SERIF, Font.BOLD, 25);
		schrift = new Font(Font.SANS_SERIF, Font.PLAIN, 17);

		//Spielmenu Fenster erstellen
		this.setTitle("Spiel starten");
		this.setSize(330, 350);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		startPanel = new StartPanel(this);
		this.add(startPanel);
		this.setResizable(true);
		this.setVisible(true);
	}

	private void spielErstellen() {
		
		//Spieler erstellen Fenster erstellen
		this.setTitle("Spiel erstellen");
		this.setSize(280, 200);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		erstellenPanel = new ErstellenPanel(this);
		this.add(erstellenPanel);
		this.setVisible(true);
		this.repaint();
		this.revalidate();
	}

	public void spielErstellen(String name, int anzahl) {
		//von Spiel erstellen zu Spiel wechseln
		spiel(name, anzahl);
	}

	private void spiel(String name, int anzahlSpieler) {
		this.anzahlSpieler = anzahlSpieler;
		
		//Spiel erzeugen
		try {
			//Spieler erstellen
			sp.erstelleSpieler(name);
			this.remove(erstellenPanel);
			for (int i = 1; i < anzahlSpieler; i++) {
				neuerSpieler();
			}
			aktiverSpieler = sp.getAktiverSpieler();
			this.setTitle("Risiko");
			this.setSize(1250, 817);
			this.setLocationRelativeTo(null);
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
			
			//Fenster mit Layout und Paneln füllen
			this.setLayout(new MigLayout("debug, wrap2", "[1050][]", "[][][]"));
			spielfeld = new MapPanel(this, schrift,1050, 550);
			spielerListPanel = new SpielerPanel(schrift, uberschrift);
			missionPanel = new MissionPanel(uberschrift, schrift,this);
			infoPanel = new InfoPanel(sp.getTurn() + "", aktiverSpieler.getName(), schrift, uberschrift);
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
			MenuItem laden = new MenuItem("Laden");
			MenuItem schliessen = new MenuItem("Schließen");
			Menu aufloesung = new Menu("Aufloesung");
			MenuItem aufloesung1 = new MenuItem("1920x1080");
			MenuItem aufloesung2 = new MenuItem("1280x800");
			MenuItem aufloesung3 = new MenuItem("3.Auflösung");
			datei.add(speichern);
			datei.add(laden);
			datei.add(schliessen);
			grafik.add(aufloesung);
			aufloesung.add(aufloesung1);
			aufloesung.add(aufloesung2);
			aufloesung.add(aufloesung3);
			aufloesung1.addActionListener(ausfuehren -> aufloesungAendern(1920, 1080));
			aufloesung2.addActionListener(ausfuehren -> aufloesungAendern(1280, 800));
			laden.addActionListener(load -> spielLaden());
			speichern.addActionListener(save -> spielSpeichern());
			schliessen.addActionListener(close -> System.exit(0));
			menu.setFont(schrift);
			this.setMenuBar(menu);

			//Layout anpassen
			this.add(spielfeld, "left,spany 3,grow");
			this.add(infoPanel, "left,growx");
			this.add(spielerListPanel, "growx");
			this.add(statistikPanel, "left,top,growx,spany 2");
//			this.add(missionPanel, "left,top,split3,wmin 300, wmax 300");
			this.add(missionPanel, "left,top,split3");
			this.add(consolePanel, "left, top");
//			this.add(buttonPanel, "right,growy, wmin 180, wmax 180");
			this.add(buttonPanel, "right,growy");
			this.setResizable(false);
			this.setVisible(true);
			this.pack();
			//Spiel beginnen
			sp.setTurn("STARTPHASE");
			anzahlSetzbareEinheiten = sp.checkAnfangsEinheiten();
			consolePanel.textSetzen(aktiverSpieler.getName()
					+ " du kannst nun deine ersten Einheiten setzen. Es sind " + anzahlSetzbareEinheiten);
		} catch (SpielerExistiertBereitsException sebe) {
			JOptionPane.showMessageDialog(null, sebe.getMessage(), "Name vergeben", JOptionPane.WARNING_MESSAGE);
		}
	}


	public void aufloesungAendern(int breite, int hoehe) {
		
		
		this.setSize(breite, hoehe);
		spielfeld.neuMalen(1000, 600);
		this.repaint();
		this.revalidate();
		
		this.setLocationRelativeTo(null);
	}

	public void neuerSpieler() {
		JFrame frame = new JFrame("Spieler erstellen");
		frame.setSize(150, 300);
		frame.setLayout(new MigLayout("wrap 2", "[][100]", "[][]"));
		JLabel nameLab = new JLabel("Name:");
		JTextField nameText = new JTextField();
		JButton erstellenBtn = new JButton("Erstellen");
	
		erstellenBtn.addActionListener(erstellen -> spielerErstellen(frame, nameText.getText()));
	
		frame.add(nameLab, "right");
		frame.add(nameText, "left,growx");
		frame.add(erstellenBtn, "center,spanx 2");
		frame.setVisible(true);
	}

	private void spielerErstellen(JFrame frame, String name) {
		//Spieler erstellen
		try {
			sp.erstelleSpieler(name);
			frame.dispose();
		} catch (SpielerExistiertBereitsException sebe) {
			JOptionPane.showMessageDialog(null, sebe.getMessage(), "Name vergeben", JOptionPane.WARNING_MESSAGE);
		}
	
		//Wenn alle Spieler erstellt sind, dann Welt und Missionen erstellen und aufteilen
		if (sp.getSpielerList().size() == anzahlSpieler) {
			try {
				sp.laenderErstellen();
				sp.laenderverbindungenUndKontinenteErstellen();
				sp.missionsListeErstellen();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.WARNING_MESSAGE);
			}
			sp.missionenVerteilen();
			sp.laenderAufteilen();
			
			farbenVerteilen();
			spielfeld.fahnenVerteilen(sp.getLaenderListe());
			int spielerNr = 1;
			for (Spieler s : sp.getSpielerList()) {
				spielerListPanel.setLabel(spielerNr, s.getName(), s.getFarbe());
				spielerNr++;
			}
			statistikPanel.statistikAktualisieren();
			missionPanel.setMBeschreibung(sp.getMissionVonAktivemSpieler().getBeschreibung());
		}
	}

	private void farbenVerteilen() {
		List<String> farben = new Vector<String>();
		farben.add("rot");
		farben.add("gruen");
		farben.add("blau");
		farben.add("gelb");
		farben.add("orange");
		farben.add("cyan");
	
		for (Spieler s : sp.getSpielerList()) {
			s.setFarbe(farben.get(0));
			farben.remove(0);
		}
	}


	private void spielLaden(){
			try{
			sp.spielLaden("Game2.txt");
			} catch(Exception e) {
				consolePanel.textSetzen("Kann nicht geladen werden");
			}
	}
	
	private void spielSpeichern() {
		try{
			sp.spielSpeichern("Game2.txt");
		}catch(IOException e){
			consolePanel.textSetzen("Spiel konnte nicht gespeichert werden" + e.getMessage());
		}
	}

	private void landWaehlen(String landcode) {
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
		statistikPanel.statistikPanelAktualisieren();
	}


	private void verteilen(String landstring, Land land) {
		//eine Einheit verteilen in Startphase oder Verteilen-Phase
		try {
			sp.landWaehlen(landstring, aktiverSpieler);
			
			if (anzahlSetzbareEinheiten > 0) {
				missionPanel.klickDisablen();
				consolePanel.textSetzen("Du kannst nun keine Einheitenkarten mehr tauschen");
				sp.einheitenPositionieren(1, land);
				anzahlSetzbareEinheiten--;
				spielfeld.labelsSetzen("", land.getEinheiten(), "");
				spielfeld.fahneEinheit(land.getEinheitenLab());
				statistikPanel.statistikPanelAktualisieren();
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

	private void angreifen(String landstring, Land land, Spieler spieler) {
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

	private void verschieben(String landstring, Land land) {
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

	private void angriff(boolean genugEinheiten, Spieler aSpieler) throws KeinNachbarlandException {
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
			spielfeld.fahneEinheit(land1.getEinheitenLab());
			spielfeld.fahneEinheit(land2.getEinheitenLab());
			land1 = null;
			land2 = null;
			buttonPanel.angreifenAktiv("angreifendes Land", "verteidigendes Land");
		} else {
			//bei Eroberung
			vLand.setFahne(aSpieler.getFarbe());
			spielfeld.fahnenVerteilen(sp.getLaenderListe());
			consolePanel.textSetzen(aLand.getBesitzer().getName() + " hat das Land erobert.");
			genugEinheiten = false;
			// Verschieben nach Eroberung
			if (aLand.getEinheiten() == 2) {
				//wenn nur zwei Einheiten auf ANgriffsland sind
				consolePanel.textSetzen("Eine Einheit wird auf " + vLand.getName() + " gesetzt.");
				sp.eroberungBesetzen(aLand, vLand, 1);
				genugEinheiten = true;
				spielfeld.fahneEinheit(land1.getEinheitenLab());
				spielfeld.fahneEinheit(land2.getEinheitenLab());
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

	
	private void istSpielerRaus(){
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
		this.remove(spielfeld);
		this.remove(spielerListPanel);
		this.remove(missionPanel);
		this.remove(infoPanel);
		this.remove(statistikPanel);
		this.remove(consolePanel);
		this.remove(buttonPanel);
		frame.setLayout(new MigLayout("wrap1","[]","[][]"));
		frame.setForeground(Color.black);

		JLabel gewinner = new JLabel("Spieler" + " hat gewonnen.");
		gewinner.setFont(uberschrift);
		gewinner.setForeground(Color.white);
		JLabel firework = new JLabel(new ImageIcon("./firework.gif"));
		this.add(gewinner, "center");
		this.add(firework, "center");
		this.setBackground(Color.BLACK);
		this.repaint();
		this.revalidate();
	}
	
	public void karteEintauschen(List<String> tauschKarten) {
		//Karten eintauschen
		anzahlSetzbareEinheiten += sp.kartenEinloesen(aktiverSpieler, tauschKarten);
		missionPanel.kartenAusgeben(aktiverSpieler);
		buttonPanel.setEinheitenVerteilenLab(anzahlSetzbareEinheiten);
	}

	public void tauschFehlgeschlagen() {
		consolePanel.textSetzen("Die Karten konnten nicht eingetauscht werden.");
	}

	public void processMouseClick(Color color) {
		//Farbcode auslesen
		String landcode = color.getRed() + "" + color.getGreen() + "" + color.getBlue();
		landWaehlen(landcode);
	
	}

	public void startButtonClicked() {
		//von Anfangsmenü zu Spieler erstellen wechseln
		this.remove(startPanel);
		spielErstellen();
	}

	public void phaseButtonClicked() {
		//Wenn Mission erfüllt, dann gewonnen aufrufen
		if(sp.getSpielerMission(aktiverSpieler).istAbgeschlossen()){
			gewonnen();
		}
		sp.nextTurn();
		aktiverSpieler = sp.getAktiverSpieler();
		missionPanel.kartenAusgeben(aktiverSpieler);
		//Rahmen auf aktiven Spieler
		spielerListPanel.setAktiverSpieler(sp.getSpielerList().indexOf(aktiverSpieler) + 1);
		
		switch (sp.getTurn()) {
		case STARTPHASE:
			buttonPanel.phaseDisable();
			anzahlSetzbareEinheiten = sp.checkAnfangsEinheiten();
			buttonPanel.setEinheitenVerteilenLab(anzahlSetzbareEinheiten);
			consolePanel.textSetzen(aktiverSpieler.getName() + " du kannst nun deine ersten Einheiten setzen. Es sind " + anzahlSetzbareEinheiten);
			missionPanel.setMBeschreibung(sp.getMissionVonAktivemSpieler().getBeschreibung());
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
			missionPanel.setMBeschreibung(sp.getMissionVonAktivemSpieler().getBeschreibung());
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
	}

	public void angriffClicked() {
		//Angreifen Button klicken
		try {
			angriff(true, aktiverSpieler);
		} catch (KeinNachbarlandException e) {
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
			spielfeld.fahneEinheit(land1.getEinheitenLab());
			spielfeld.labelsSetzen("", land2.getEinheiten(), "");
			spielfeld.fahneEinheit(land2.getEinheitenLab());
			land1 = null;
			land2 = null;
			buttonPanel.verschiebenAktiv("erstes Land", "zweites Land");
			buttonPanel.verschiebenDisabled();
		} catch (NichtGenugEinheitenException ngee) {
			consolePanel.textSetzen(ngee.getMessage());
		}
	}

	public void verschiebenNAClicked(int einheiten) {
		//nach angreifen verteilen klicken
		try {
			sp.checkEinheiten(land1.getName(), einheiten);
			sp.eroberungBesetzen(land1, land2, einheiten);
			spielfeld.fahneEinheit(land1.getEinheitenLab());
			spielfeld.fahneEinheit(land2.getEinheitenLab());
			land1 = null;
			land2 = null;
			buttonPanel.angreifenAktiv("erstes Land", "zweites Land");
		} catch (NichtGenugEinheitenException ngee) {
			consolePanel.textSetzen(ngee.getMessage());
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
}	