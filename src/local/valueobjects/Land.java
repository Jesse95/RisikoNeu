package local.valueobjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Land {
	private String name;
	private Spieler besitzer;
	private int einheiten;
	private String kurzel;
	private int fahneX;
	private int fahneY;
	private JLabel fahne;
	private JLabel einheitenLab = new JLabel();
	private BufferedImage fahneRotImg;
	private BufferedImage fahneBlauImg;
	private BufferedImage fahneGruenImg;
	private BufferedImage fahneGelbImg;
	private BufferedImage fahneOrangeImg;
	private BufferedImage fahneCyanImg;

	public Land(String name, Spieler besitzer, int einheiten, String kurzel, int fahneX, int fahneY) {
		this.name = name;
		this.besitzer = besitzer;
		this.einheiten = einheiten;
		this.kurzel = kurzel;
		this.fahneX = fahneX;
		this.fahneY = fahneY;
		try{
			fahneRotImg = ImageIO.read(new File("./Fahne_Rot.png"));
			fahneGruenImg = ImageIO.read(new File("./Fahne_Gruen.png"));
			fahneBlauImg = ImageIO.read(new File("./Fahne_Blau.png"));
			fahneGelbImg = ImageIO.read(new File("./Fahne_Gelb.png"));
			fahneOrangeImg = ImageIO.read(new File("./Fahne_Orange.png"));
			fahneCyanImg = ImageIO.read(new File("./Fahne_Cyan.png"));
		}catch (IOException e){}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Spieler getBesitzer() {
		return besitzer;
	}

	public void setBesitzer(Spieler besitzer) {
		this.besitzer = besitzer;
	}

	public int getEinheiten() {
		return einheiten;
	}

	public void setEinheiten(int einheiten) {
		this.einheiten = einheiten;
	}
	public String getKuerzel(){
		return kurzel;
	}
	
	public void setFahne(int x, int y){
		this.fahneX = x;
		this.fahneY = y;
	}
	
	public int getFahneX(){
		return fahneX;
	}
	
	public int getFahneY(){
		return fahneY;
	}
	
	public void setFahne(String farbe){
		switch(farbe){
			case "rot":		fahne = new JLabel(new ImageIcon(fahneRotImg.getScaledInstance(40, 40, Image.SCALE_FAST)));
			break;
			case "blau":	fahne = new JLabel(new ImageIcon(fahneBlauImg.getScaledInstance(40, 40, Image.SCALE_FAST)));
			break;
			case "gruen":	fahne = new JLabel(new ImageIcon(fahneGruenImg.getScaledInstance(40, 40, Image.SCALE_FAST)));
			break;
			case "gelb":	fahne = new JLabel(new ImageIcon(fahneGelbImg.getScaledInstance(40, 40, Image.SCALE_FAST)));
			break;
			case "orange":	fahne = new JLabel(new ImageIcon(fahneOrangeImg.getScaledInstance(40, 40, Image.SCALE_FAST)));
			break;
			case "cyan":	fahne = new JLabel(new ImageIcon(fahneCyanImg.getScaledInstance(40, 40, Image.SCALE_FAST)));
			break;
		}
		fahne.setBounds(fahneX + 15, fahneY -40, 40, 40);
	}
	
	public JLabel getEinheitenLab(){
		einheitenLab.setText(einheiten + "");
		if(einheiten < 10){
			einheitenLab.setBounds(fahneX +30, fahneY -42, 40, 25);
		}else if(einheiten < 100){
			einheitenLab.setBounds(fahneX +23, fahneY -42, 40, 25);
		}else{
			einheitenLab.setBounds(fahneX +18, fahneY -42, 40, 25);
		}
		einheitenLab.setFont(new Font("Courier New", Font.BOLD, 20));
	    einheitenLab.setForeground(Color.WHITE);
		return einheitenLab;
	}
	
	public JLabel getFahne(){
		return this.fahne;
	}
}
