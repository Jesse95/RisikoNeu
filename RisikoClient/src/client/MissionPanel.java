package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;
import valueobjects.Einheitenkarten;
import valueobjects.Spieler;

public class MissionPanel extends JPanel {

	public interface KarteClickedHandler{
		public void karteEintauschen(Boolean erfolgreich, ArrayList<String> tauschKarten);
	}
	
	private List<JLabel> kartenListe = new Vector<JLabel>();
	private KarteClickedHandler handler;
	private JLabel mBeschreibung = new JLabel();
	private Font schrift;
	private Font uberschrift;
	private ImageIcon soldatImg = null;
	private ImageIcon pferdImg = null;
	private ImageIcon panzerImg = null;
	private ImageIcon jokerImg = null;
	private JComponent karten;
	private Border border = null;
	private List<String> kartenWahl = new Vector<String>();
	private String kartenSpeicher1 = null;
	private String kartenSpeicher2 = null;
	private String kartenSpeicher3 = null;
	private boolean klick = false;
	private BufferedImage iconEinheiten;
	private BufferedImage iconPferd;
	private BufferedImage iconKanone;
	private BufferedImage iconJoker;
	
	/**
	 * Konstruktor Mission Panel
	 * @param uberschrift
	 * @param schrift
	 * @param handler
	 */
	public MissionPanel(Font uberschrift, Font schrift, KarteClickedHandler handler){
		this.handler = handler;
		this.schrift = schrift;
		this.uberschrift = uberschrift;
		initialize();
	}

	/**
	 * Initialisiert MissionsPanel in welchem Missionen und Karten in zwei Tabs angezeigt werden
	 */
	public void initialize(){
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.setFont(uberschrift);
		JComponent mission = new JPanel();
		mission.setLayout(new MigLayout("wrap1","[]","[]"));
		mBeschreibung.setText("Missionsbeschreibung");
		mBeschreibung.setFont(schrift);
		mission.add(mBeschreibung,"center, growx, growy");
		mission.setPreferredSize(new Dimension(280,140));

		karten = new JPanel();
		karten.setLayout(new MigLayout("wrap5","[][][][][]","[][][]"));

		try{
			iconEinheiten = ImageIO.read(new File("./Bilder/soldat.png"));
			iconPferd = ImageIO.read(new File("./Bilder/pferd.png"));
			iconKanone = ImageIO.read(new File("./Bilder/panzer.png"));
			iconJoker = ImageIO.read(new File("./Bilder/joker.png"));
		} catch (IOException e){}

		soldatImg = new ImageIcon(iconEinheiten.getScaledInstance(40, 40, Image.SCALE_FAST));
		pferdImg = new ImageIcon(iconPferd.getScaledInstance(40, 40, Image.SCALE_FAST));
		panzerImg = new ImageIcon(iconKanone.getScaledInstance(40, 40, Image.SCALE_FAST));
		jokerImg = new ImageIcon(iconJoker.getScaledInstance(40, 40, Image.SCALE_FAST));
		border = BorderFactory.createLineBorder(Color.BLUE, 2);
		karten.add(new JLabel("Du hast folgende Karten:"),"center,spanx 5");
		karten.setPreferredSize(new Dimension(280,140));
		tabbedPane.addTab("Mission",mission);
		tabbedPane.addTab("Karten", karten);
		this.add(tabbedPane);
	}

	/**
	 * setzt Beschreibungstext bei Missionen
	 * @param beschreibung
	 */
	public void setMBeschreibung(String beschreibung) {
		mBeschreibung.setText(beschreibung);
	}

	/**
	 * ermöglicht dads Klicken dreier Karten hintereinander zum einlösen
	 */
	public void kartenTauschen() {	
		for(String kartenString : kartenWahl){
			String karte = kartenString;
			if(kartenSpeicher1 == null){
				kartenSpeicher1 = karte;
			}else if(kartenSpeicher2 == null){
				kartenSpeicher2 = karte;
			}else if(kartenSpeicher3 == null){
				kartenSpeicher3 = karte;
				if(kartenSpeicher1 == kartenSpeicher2 && kartenSpeicher1 == kartenSpeicher3 || kartenSpeicher1 != kartenSpeicher2 && kartenSpeicher2 != kartenSpeicher3 && kartenSpeicher1 != kartenSpeicher3  || kartenSpeicher1 == kartenSpeicher2 && kartenSpeicher3 == "Joker" || kartenSpeicher2 == kartenSpeicher3 && kartenSpeicher1 == "Joker" || kartenSpeicher1 == kartenSpeicher3 && kartenSpeicher2 == "Joker" ||  kartenSpeicher1 == "Joker" && kartenSpeicher2 == "Joker" ||  kartenSpeicher2 == "Joker" && kartenSpeicher3 == "Joker" ||  kartenSpeicher1 == "Joker" && kartenSpeicher3 == "Joker"){
					ArrayList<String> kartenUebergabe = new ArrayList<String>();
					kartenUebergabe.add(kartenSpeicher1);
					kartenUebergabe.add(kartenSpeicher2);
					kartenUebergabe.add(kartenSpeicher3);
					handler.karteEintauschen(true, kartenUebergabe);
				}else{
					handler.karteEintauschen(false, null);		
				}
			}
		}
		
		for(JLabel l : kartenListe){
			l.setBorder(null);
		}
		kartenSpeicher1 = null;
		kartenSpeicher2 = null;
		kartenSpeicher3 = null;
		while(kartenWahl.size() > 0){
			kartenWahl.remove(0);
		}
	}

	/**
	 * zeigt aktuellte Karten an/aktualisiert bei neuer Karte
	 * @param spieler
	 * @param spielerliste
	 */
	public void kartenAusgeben(Spieler spieler, ArrayList<Spieler>spielerliste){

		for(JLabel k : kartenListe){
			karten.remove(k);
		}
		while(kartenListe.size() > 0){
			kartenListe.remove(0);
		}

		//SERVER-CLIENT SPIELER ÜBERTRAGUNG
		for(Spieler s :spielerliste){
			if(spieler.getName().equals(s.getName())){
				spieler = s;
			}
		}
		
		List<Einheitenkarten> kartenStapel = spieler.getEinheitenkarten();
		for(int i = 0; i < kartenStapel.size(); i++){
			int s = i;
			switch(kartenStapel.get(i).getKartenwert()){
			case "Soldat":
				kartenListe.add(new JLabel(soldatImg));
				kartenListe.get(i).setName("Soldat");
				kartenListe.get(i).addMouseListener(new MouseAdapter() {					
					
					public void mouseClicked(MouseEvent e) {
						if(klick == true){
							if(kartenListe.get(s).getBorder() != border){
								kartenListe.get(s).setBorder(border);
								if(kartenWahl.size() == 2){
									kartenWahl.add("Soldat");
									kartenTauschen();
								}else{
									kartenWahl.add("Soldat");
								}
							}else{
								kartenListe.get(s).setBorder(null);
								for(String kstring : kartenWahl){
									if(kstring == "Soldat"){
										kartenWahl.remove(kstring);
										break;
									}
								}
							}
						}
					}
				});
				break;
			case "Pferd":
				kartenListe.add(new JLabel(pferdImg));
				kartenListe.get(i).setName("Pferd");
				kartenListe.get(i).addMouseListener(new MouseAdapter() {					

					public void mouseClicked(MouseEvent e) {
						if(klick == true){
							if(kartenListe.get(s).getBorder() != border){
								kartenListe.get(s).setBorder(border);
								if(kartenWahl.size() == 2){
									kartenWahl.add("Pferd");
									kartenTauschen();
								}else{
									kartenWahl.add("Pferd");
								}
							}else{
								kartenListe.get(s).setBorder(null);
								for(String kstring : kartenWahl){
									if(kstring == "Pferd"){
										kartenWahl.remove(kstring);
										break;
									}
								}
							}
						}
					}
				});
				break;
			case "Panzer":
				kartenListe.add(new JLabel(panzerImg));
				kartenListe.get(i).addMouseListener(new MouseAdapter() {

					public void mouseClicked(MouseEvent e) {
						if(klick == true){
							if(kartenListe.get(s).getBorder() != border){
								kartenListe.get(s).setBorder(border);
								if(kartenWahl.size() == 2){
									kartenWahl.add("Panzer");
									kartenTauschen();
								}else{
									kartenWahl.add("Panzer");
								}
							}else{
								kartenListe.get(s).setBorder(null);
								for(String kstring : kartenWahl){
									if(kstring == "Panzer"){
										kartenWahl.remove(kstring);
										break;
									}
								}
							}
						}
					}
				});
				break;
			case "Joker":
				kartenListe.add(new JLabel(jokerImg));
				kartenListe.get(i).addMouseListener(new MouseAdapter() {

					public void mouseClicked(MouseEvent e) {
						if(klick == true){
							if(kartenListe.get(s).getBorder() != border){
								kartenListe.get(s).setBorder(border);
								if(kartenWahl.size() == 2){
									kartenWahl.add("Joker");
									kartenTauschen();
								}else{
									kartenWahl.add("Joker");
								}
							}else{
								kartenListe.get(s).setBorder(null);
								for(String kstring : kartenWahl){
									if(kstring == "Joker"){
										kartenWahl.remove(kstring);
										break;
									}
								}
							}
						}
					}
				});
				break;
			}

		}
		for(JLabel l : kartenListe) {
			karten.add(l);
		}
		karten.repaint();
	}

	/**
	 * gibt Klick auf Karten frei
	 */
	public void klickEnablen() {
		klick = true;
	}

	/**
	 * speert Klick auf Karten
	 */
	public void klickDisablen() {
		klick = false;
		for(JLabel l : kartenListe){
			l.setBorder(null);
		}
	}
}