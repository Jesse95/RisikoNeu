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
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import local.valueobjects.Land;

public class MapPanel extends JLayeredPane {

	public interface MapClickHandler {
		public void processMouseClick(Color color);
	}
	
	private List<JLabel> fahnenLabs = new Vector<JLabel>();
	private List<JLabel> einheitenLabs = new Vector<JLabel>();	
	private JLabel firework = null;
	private JLabel spielfeld = null;
	private JLabel weltKarteBuntLab = null;
	private MapClickHandler handler = null;
	private BufferedImage weltKarteBunt;
	private Font schrift;
	private JLabel landLab = null;
	private JLabel einheitenLab = null;
	private JLabel besitzerLab = null;
	private JLabel wuerfelLab = null;
	private BufferedImage wuerfelB1;
	private BufferedImage wuerfelB2;
	private BufferedImage wuerfelB3;
	private BufferedImage wuerfelB4;
	private BufferedImage wuerfelB5;
	private BufferedImage wuerfelB6;
	private BufferedImage wuerfelR1;
	private BufferedImage wuerfelR2;
	private BufferedImage wuerfelR3;
	private BufferedImage wuerfelR4;
	private BufferedImage wuerfelR5;
	private BufferedImage wuerfelR6;
	private JLabel wB1 = new JLabel();
	private JLabel wB2 = new JLabel();
	private JLabel wR1 = new JLabel();
	private JLabel wR2 = new JLabel();
	private JLabel wR3 = new JLabel();
	private int breite;
	private int hoehe;
	private  BufferedImage myPicture;
	
	
	
	public MapPanel(MapClickHandler handler,Font schrift,int breite, int hoehe) {
		this.handler = handler;
		this.schrift = schrift;
		this.breite = breite;
		this.hoehe = hoehe;
		wuerfelBilderLaden();
		initialize();
	}
	
	private void wuerfelBilderLaden() {
		try{
			wuerfelB1 = ImageIO.read(new File("./wuerfel/blau/wuerfelB1.png"));
			wuerfelB2 = ImageIO.read(new File("./wuerfel/blau/wuerfelB2.png"));
			wuerfelB3 = ImageIO.read(new File("./wuerfel/blau/wuerfelB3.png"));
			wuerfelB4 = ImageIO.read(new File("./wuerfel/blau/wuerfelB4.png"));
			wuerfelB5 = ImageIO.read(new File("./wuerfel/blau/wuerfelB5.png"));
			wuerfelB6 = ImageIO.read(new File("./wuerfel/blau/wuerfelB6.png"));
			wuerfelR1 = ImageIO.read(new File("./wuerfel/rot/wuerfelR1.png"));
			wuerfelR2 = ImageIO.read(new File("./wuerfel/rot/wuerfelR2.png"));
			wuerfelR3 = ImageIO.read(new File("./wuerfel/rot/wuerfelR3.png"));
			wuerfelR4 = ImageIO.read(new File("./wuerfel/rot/wuerfelR4.png"));
			wuerfelR5 = ImageIO.read(new File("./wuerfel/rot/wuerfelR5.png"));
			wuerfelR6 = ImageIO.read(new File("./wuerfel/rot/wuerfelR6.png"));
		}catch (IOException e){
				System.out.println("nicht geladen");
			}
	}

	public void initialize() {

        try {
			myPicture = ImageIO.read(new File("./weltkarte.jpg"));
			spielfeld = new JLabel(new ImageIcon(myPicture.getScaledInstance(breite, hoehe, Image.SCALE_FAST)));
			weltKarteBunt = ImageIO.read(new File("./weltkarte_bunt.png"));
			weltKarteBuntLab = new JLabel(new ImageIcon(weltKarteBunt));
		    firework = new JLabel(new ImageIcon("./firework.gif"));
        } catch (IOException e) {
			System.out.println(e.getMessage());
		}
        
        spielfeld.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent e) {
				int farbenInt =  weltKarteBunt.getRGB(e.getX(), e.getY());
				Color color = new Color(farbenInt, true);
				handler.processMouseClick(color);
			}
		});

        landLab = new JLabel("Land");
        landLab.setFont(schrift);
        einheitenLab = new JLabel("Einheiten");
        einheitenLab.setFont(schrift);
        besitzerLab = new JLabel("Besitzer");
        besitzerLab.setFont(schrift);
        landLab.setBounds(400,2,200,15);
        einheitenLab.setBounds(400, 27, 200, 15);
        besitzerLab.setBounds(400, 52, 200, 15);
        spielfeld.setBounds(0, 0, breite, hoehe);
        firework.setBounds(0, 0, 1050, 550);
        weltKarteBuntLab.setBounds(0, 0, breite, hoehe);
        weltKarteBuntLab.setVisible(false);
        this.add(spielfeld,new Integer(2), 1); 
        this.add(weltKarteBuntLab);
        this.add(landLab,new Integer(2), 0);
        this.add(einheitenLab,new Integer(2), 0);
        this.add(besitzerLab, new Integer(2), 0);
        this.setPreferredSize(new Dimension(breite, hoehe));   
	}
	
	public void fahnenVerteilen(List<Land> laender) {
		JLabel fahne = null;
		JLabel einheiten = null;
		
		for(JLabel lab : fahnenLabs) {
			this.remove(lab);
		}
		
		for(Land l : laender) {
			l.setFahne(l.getBesitzer().getFarbe());
			fahne = l.getFahne();	
			fahnenLabs.add(fahne);
			einheiten = l.getEinheitenLab();
			einheitenLabs.add(einheiten);
			this.add(fahne, new Integer(2), 0);
			this.add(einheiten, new Integer(2),0);
		}
	}
	
	public void fahneEinheit(JLabel einheitenNeu) {
		for(JLabel l : einheitenLabs) {
			if(l.equals(einheitenNeu)) {
				l = einheitenNeu;
			}
		}
	}
	
	public List<JLabel> getFahnenList() {
		return this.fahnenLabs;
	}
	

	public void labelsSetzen(String lName, int lEinheiten, String lBesitzer) {
		if(lName.length() > 0) {
			landLab.setText(lName);
		}
		if(lEinheiten != 9119) {
			einheitenLab.setText(lEinheiten + "");
		}
		if(lBesitzer.length() > 0) {
			besitzerLab.setText(lBesitzer);
		}
	}
	
	public void wuerfelAnzeigen(List<Integer> wuerfelAngreifer, List<Integer> wuerfelVerteidiger) {
		wuerfelEntfernen();
		//rote Angreifer Würfel
		wR1 = getWuerfelLabel("rot", wuerfelAngreifer.get(0));
		wR1.setBounds(20,420, 40, 40);
		this.add(wR1, new Integer(2), 0);
		
		if(wuerfelAngreifer.size() == 3 || wuerfelAngreifer.size() == 2) {
			wR2 = getWuerfelLabel("rot", wuerfelAngreifer.get(1));
			wR2.setBounds(wR1.getX() + 50,wR1.getY(), 40, 40);
			this.add(wR2, new Integer(2), 0);
		}
			
		if(wuerfelAngreifer.size() == 3) {
			wR3 = getWuerfelLabel("rot", wuerfelAngreifer.get(2));
			wR3.setBounds(wR2.getX() + 50,wR2.getY(), 40, 40);
			this.add(wR3, new Integer(2), 0);
		}
		
		//blaue Verteidiger Würfel
		wB1 = getWuerfelLabel("blau", wuerfelVerteidiger.get(0));
		wB1.setBounds(wR1.getX(),wR1.getY() + 50, 40, 40);
		this.add(wB1, new Integer(2), 0);
		
		if(wuerfelVerteidiger.size() == 2) {
			wB2 = getWuerfelLabel("blau", wuerfelVerteidiger.get(1));
			wB2.setBounds(wB1.getX() + 50,wB1.getY(), 40, 40);
			this.add(wB2, new Integer(2), 0);
		}	
	}
	
	public void wuerfelEntfernen() {
		remove(wB1);
		remove(wB2);
		remove(wR1);
		remove(wR2);
		remove(wR3);
	}
	
	public JLabel getWuerfelLabel(String farbe, int augenzahl){
		if(farbe.equals("blau")) {
			switch(augenzahl){
			case 1:	wuerfelLab = new JLabel(new ImageIcon(wuerfelB1.getScaledInstance(40, 40, Image.SCALE_FAST)));
			break;
			case 2:	wuerfelLab = new JLabel(new ImageIcon(wuerfelB2.getScaledInstance(40, 40, Image.SCALE_FAST)));
			break;
			case 3:	wuerfelLab = new JLabel(new ImageIcon(wuerfelB3.getScaledInstance(40, 40, Image.SCALE_FAST)));
			break;
			case 4:	wuerfelLab = new JLabel(new ImageIcon(wuerfelB4.getScaledInstance(40, 40, Image.SCALE_FAST)));
			break;
			case 5:	wuerfelLab = new JLabel(new ImageIcon(wuerfelB5.getScaledInstance(40, 40, Image.SCALE_FAST)));
			break;
			case 6:	wuerfelLab = new JLabel(new ImageIcon(wuerfelB6.getScaledInstance(40, 40, Image.SCALE_FAST)));
			break;
			}
		} else if(farbe.equals("rot")) {
			switch(augenzahl){
			case 1:	wuerfelLab = new JLabel(new ImageIcon(wuerfelR1.getScaledInstance(40, 40, Image.SCALE_FAST)));
			break;
			case 2:	wuerfelLab = new JLabel(new ImageIcon(wuerfelR2.getScaledInstance(40, 40, Image.SCALE_FAST)));
			break;
			case 3:	wuerfelLab = new JLabel(new ImageIcon(wuerfelR3.getScaledInstance(40, 40, Image.SCALE_FAST)));
			break;
			case 4:	wuerfelLab = new JLabel(new ImageIcon(wuerfelR4.getScaledInstance(40, 40, Image.SCALE_FAST)));
			break;
			case 5:	wuerfelLab = new JLabel(new ImageIcon(wuerfelR5.getScaledInstance(40, 40, Image.SCALE_FAST)));
			break;
			case 6:	wuerfelLab = new JLabel(new ImageIcon(wuerfelR6.getScaledInstance(40, 40, Image.SCALE_FAST)));
			break;
			}
		}
			return wuerfelLab;
		}

	public void gewonnen() {
		this.add(firework,new Integer(2), 0); 
	}
	
	public void aufloesungAendern(int breite, int hoehe) {
		this.breite = breite;
		this.hoehe = hoehe;
		this.repaint();
		this.revalidate();
	}
	public void neuMalen(int breite, int hoehe){
		this.breite = breite;
		this.hoehe = hoehe;
		try {
			myPicture = ImageIO.read(new File("./weltkarte.jpg"));
		
			spielfeld.setIcon(new ImageIcon(myPicture.getScaledInstance(breite, hoehe, Image.SCALE_FAST)));
			
			weltKarteBunt = ImageIO.read(new File("./weltkarte_bunt.png"));
			weltKarteBuntLab = new JLabel(new ImageIcon(weltKarteBunt));
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		spielfeld.setBounds(0, 0, breite, hoehe);
		spielfeld.repaint();
		spielfeld.revalidate();
		this.setSize(breite-30,hoehe);
		this.repaint();
		this.revalidate();
	}
}
