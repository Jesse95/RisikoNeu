package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import valueobjects.Land;
import valueobjects.Spieler;

public class StatistikPanel extends JPanel{

	private BufferedImage iconLand;
	private BufferedImage iconEinheiten;
	private BufferedImage iconKarten;
	private List<JLabel> laenderVonSpielerLabel;
	private List<JLabel> einheitenVonSpielerLabel;
	private List<JLabel> kartenVonSpielerLabel;	
	private List<Integer> laenderVonSpieler;
	private List<Integer> einheitenVonSpieler;
	private List<Integer> kartenVonSpieler;
	private Font schrift;
	private Font uberschrift;
	
	public StatistikPanel(Font schrift,Font uberschrift){
		this.schrift = schrift;
		this.uberschrift = uberschrift;
		initialize();
	}
	
	public void initialize(){
		this.setLayout(new MigLayout("wrap3","[][][]","[][][][][][][][]"));
		JLabel header = new JLabel("Statistik:");
		header.setFont(uberschrift);
		
		try{
			iconLand = ImageIO.read(new File("./Bilder/land.png"));
			iconEinheiten = ImageIO.read(new File("./Bilder/soldat.png"));
			iconKarten = ImageIO.read(new File("./Bilder/karten.png"));
		}catch (IOException e){}
		
		JLabel icon1 = new JLabel(new ImageIcon(iconLand.getScaledInstance(40, 40, Image.SCALE_FAST)));
		JLabel icon2 = new JLabel(new ImageIcon(iconEinheiten.getScaledInstance(40, 40, Image.SCALE_FAST)));
		JLabel icon3 = new JLabel(new ImageIcon(iconKarten.getScaledInstance(40, 40, Image.SCALE_FAST)));
		
		laenderVonSpieler = new Vector<Integer>();
		einheitenVonSpieler = new Vector<Integer>();
		kartenVonSpieler = new Vector<Integer>();
		laenderVonSpielerLabel = new Vector<JLabel>();
		einheitenVonSpielerLabel = new Vector<JLabel>();
		kartenVonSpielerLabel = new Vector<JLabel>();

		this.add(header,"spanx 3,left");
		this.add(icon1,"left");
		this.add(icon2,"center");
		this.add(icon3,"right");
	}
	
	public void statistikAktualisieren(ArrayList<Land>laenderListe, ArrayList<Spieler>spielerListe) {
		System.out.println("StatistikPanel");
		 statistikPanelAktualisieren(laenderListe, spielerListe);
		for(int laenderAnzahl : laenderVonSpieler) {
			laenderVonSpielerLabel.add(new JLabel(laenderAnzahl + ""));
		}
		
		for(int einheitenAnzahl : einheitenVonSpieler) {
			einheitenVonSpielerLabel.add(new JLabel(einheitenAnzahl + ""));
		}
		
		for(int kartenAnzahl : kartenVonSpieler) {
			kartenVonSpielerLabel.add(new JLabel(kartenAnzahl + ""));
		}

		List<Color> farben = new Vector<>();
		farben.add(new Color(175,42,0));
		farben.add(new Color(133, 219, 24));
		farben.add(new Color(38, 50, 237));
		farben.add(new Color(255, 255, 26));
		farben.add(new Color(255, 140, 0));
		farben.add(new Color(3, 195, 235));
		
		int i = 0;
		for(JLabel lab : laenderVonSpielerLabel){
			this.add(laenderVonSpielerLabel.get(i),"center");
			this.add(einheitenVonSpielerLabel.get(i),"center");
			this.add(kartenVonSpielerLabel.get(i),"center");
					
			laenderVonSpielerLabel.get(i).setFont(schrift);
			einheitenVonSpielerLabel.get(i).setFont(schrift);
			kartenVonSpielerLabel.get(i).setFont(schrift);
			laenderVonSpielerLabel.get(i).setForeground(farben.get(i));
			einheitenVonSpielerLabel.get(i).setForeground(farben.get(i));
			kartenVonSpielerLabel.get(i).setForeground(farben.get(i));
			i++;
		}
		
		this.repaint();
		this.revalidate();
	}
	
	public void statistikPanelAktualisieren(ArrayList<Land>laenderListe, ArrayList<Spieler>spielerListe){
		laenderVonSpieler.clear();
		einheitenVonSpieler.clear();
		kartenVonSpieler.clear();
		
		int anzahlLaender;
		int anzahlEinheiten;
		int anzahlKarten;
		for(Spieler s : spielerListe)
		{
			anzahlLaender = 0;
			anzahlEinheiten = 0;
			anzahlKarten = s.getEinheitenkarten().size();
			
			for(Land l: laenderListe) {
				if(l.getBesitzer().equals(s)) {
					anzahlLaender++;
					anzahlEinheiten += l.getEinheiten();
				}
			}
			
			laenderVonSpieler.add(anzahlLaender);
			einheitenVonSpieler.add(anzahlEinheiten);
			kartenVonSpieler.add(anzahlKarten);
		}
	
		for(int i = 0; i < laenderVonSpielerLabel.size(); i++) {
			laenderVonSpielerLabel.get(i).setText(laenderVonSpieler.get(i) + "");
			einheitenVonSpielerLabel.get(i).setText(einheitenVonSpieler.get(i) + "");
			kartenVonSpielerLabel.get(i).setText(kartenVonSpieler.get(i)+ "");
		}
	}	
}
