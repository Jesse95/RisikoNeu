package client;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class StartPanel extends JPanel{
	private StartButtonClickHandler handler = null;
	private LoadHandler loadHandler = null;
	
	public StartPanel(StartButtonClickHandler handler){
		this.handler = handler;
		this.loadHandler = (LoadHandler) handler;
		initialize();
	}
	
	public interface StartButtonClickHandler {
		public void startButtonClicked();
	}
	
	public interface LoadHandler {
		public void spielLaden();
		public void spielBeitreten();
	}
	
	public void initialize() {
		this.setLayout(new MigLayout("wrap1","[]","[][][][][][]"));

		//Logo wird eingebunden
		BufferedImage logoImg;
		JLabel logo = new JLabel();
		try {
			logoImg = ImageIO.read(new File("./logo.jpeg"));
			logo = new JLabel(new ImageIcon(logoImg.getScaledInstance(300, 150, Image.SCALE_FAST)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JButton startBtn = new JButton("Spiel erstellen");
		JButton ladenBtn = new JButton("Spiel laden");
		JButton beitretenBtn = new JButton("Spiel beitreten");
		JButton optionBtn = new JButton("Optionen");
		JButton beendenBtn = new JButton("Beenden");
		
		startBtn.addActionListener(start -> handler.startButtonClicked());
		ladenBtn.addActionListener(load -> loadHandler.spielLaden());
		beitretenBtn.addActionListener(beitreten -> loadHandler.spielBeitreten());
		beendenBtn.addActionListener(close -> System.exit(0));
		
		this.add(logo,"center");
		this.add(startBtn,"center,growx");
		this.add(ladenBtn,"center,growx");
		this.add(beitretenBtn,"center, growx");
		this.add(optionBtn,"center,growx");
		this.add(beendenBtn,"center,growx");
	}	
}