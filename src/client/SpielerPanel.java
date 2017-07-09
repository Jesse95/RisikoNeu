package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;

public class SpielerPanel extends JPanel{
	private JLabel spieler1 = new JLabel();
	private JLabel spieler2 = new JLabel();
	private JLabel spieler3 = new JLabel();
	private JLabel spieler4 = new JLabel();
	private JLabel spieler5 = new JLabel();
	private JLabel spieler6 = new JLabel();
	private JLabel spieler1Fahne = new JLabel();
	private JLabel spieler2Fahne = new JLabel();
	private JLabel spieler3Fahne = new JLabel();
	private JLabel spieler4Fahne = new JLabel();
	private JLabel spieler5Fahne = new JLabel();
	private JLabel spieler6Fahne = new JLabel();
	private BufferedImage fahneRotImg;
	private BufferedImage fahneBlauImg;
	private BufferedImage fahneGruenImg;
	private BufferedImage fahneGelbImg;
	private BufferedImage fahneOrangeImg;
	private BufferedImage fahneCyanImg;
	private Font schrift;
	private Font uberschrift;
	
	public SpielerPanel(Font schrift, Font uberschrift) {
		this.schrift = schrift;
		this.uberschrift = uberschrift;
		initialize();
	}
	
	public void initialize() {
		this.setLayout(new MigLayout("wrap2","[][]","[][][][][][][]"));
		JLabel header = new JLabel("Spieler:");
		header.setFont(uberschrift);
		spieler1.setFont(schrift);
		spieler2.setFont(schrift);
		spieler3.setFont(schrift);
		spieler4.setFont(schrift);
		spieler5.setFont(schrift);
		spieler6.setFont(schrift);
		
		try{
			fahneRotImg = ImageIO.read(new File("./Fahne_Rot.png"));
			fahneGruenImg = ImageIO.read(new File("./Fahne_Gruen.png"));
			fahneBlauImg = ImageIO.read(new File("./Fahne_Blau.png"));
			fahneGelbImg = ImageIO.read(new File("./Fahne_Gelb.png"));
			fahneOrangeImg = ImageIO.read(new File("./Fahne_Orange.png"));
			fahneCyanImg = ImageIO.read(new File("./Fahne_Cyan.png"));
		}catch (IOException e){}
		
		this.add(header,"center,growx,wrap");
		this.add(spieler1,"left");
		this.add(spieler1Fahne, "left");
		this.add(spieler2,"left");
		this.add(spieler2Fahne, "left");
		this.add(spieler3,"left");
		this.add(spieler3Fahne, "left");
		this.add(spieler4,"left");
		this.add(spieler4Fahne, "left");
		this.add(spieler5,"left");
		this.add(spieler5Fahne, "left");
		this.add(spieler6,"left");
		this.add(spieler6Fahne, "left");
	}
	
	public void setLabel(int nummer, String text, String farbe) {
		ImageIcon fahne = null;
		switch(farbe){
			case "rot":		fahne = new ImageIcon(fahneRotImg.getScaledInstance(40, 40, Image.SCALE_FAST));
			break;
			case "blau":	fahne = new ImageIcon(fahneBlauImg.getScaledInstance(40, 40, Image.SCALE_FAST));
			break;
			case "gruen":	fahne = new ImageIcon(fahneGruenImg.getScaledInstance(40, 40, Image.SCALE_FAST));
			break;
			case "gelb":	fahne = new ImageIcon(fahneGelbImg.getScaledInstance(40, 40, Image.SCALE_FAST));
			break;
			case "orange":	fahne = new ImageIcon(fahneOrangeImg.getScaledInstance(40, 40, Image.SCALE_FAST));
			break;
			case "cyan":	fahne = new ImageIcon(fahneCyanImg.getScaledInstance(40, 40, Image.SCALE_FAST));
			break;
		}
		
		switch(nummer){
			case 1: spieler1.setText(text);
					spieler1Fahne.setIcon(fahne);
					break;
			case 2: spieler2.setText(text);
					spieler2Fahne.setIcon(fahne);
					break;
			case 3: spieler3.setText(text);
					spieler3Fahne.setIcon(fahne);
					break;
			case 4: spieler4.setText(text);
					spieler4Fahne.setIcon(fahne);
					break;
			case 5: spieler5.setText(text);
					spieler5Fahne.setIcon(fahne);
					break;
			case 6: spieler6.setText(text);
					spieler6Fahne.setIcon(fahne);
					break;
		}
	}
	
	public void setAktiverSpieler(int nummer){
		Border border = BorderFactory.createLineBorder(Color.ORANGE, 2);
		removeBorder();
		
		switch(nummer){
			case 1: spieler1.setBorder(border);
					break;
			case 2: spieler2.setBorder(border);
					break;
			case 3: spieler3.setBorder(border);
					break;
			case 4: spieler4.setBorder(border);
					break;
			case 5: spieler5.setBorder(border);
					break;
			case 6: spieler6.setBorder(border);
					break;
		}
	}
	
	private void removeBorder(){
		spieler1.setBorder(null);
		spieler2.setBorder(null);
		spieler3.setBorder(null);
		spieler4.setBorder(null);
		spieler5.setBorder(null);
		spieler6.setBorder(null);
	}	
}
