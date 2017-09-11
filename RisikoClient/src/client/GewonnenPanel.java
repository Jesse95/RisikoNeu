package client;

import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import valueobjects.Spieler;

public class GewonnenPanel extends JPanel{
	private JLabel pokal = null;
	private Spieler spieler;
	private Font uberschrift;
	
	/**
	 * Konstruktor GewonnenPanel
	 * @param spieler
	 * @param schrift
	 * @param uberschrift
	 */
	public GewonnenPanel(Spieler spieler, Font uberschrift) {
		this.spieler = spieler;
		this.uberschrift = uberschrift;
		
		initialize();
	}

	/**
	 * Pokal wird dem Panel hinzugefügt und Gewinner wird angezeigt.
	 */
	public void initialize() {
		this.setLayout(new MigLayout("wrap1","[]","[][]"));
		pokal = new JLabel(new ImageIcon("./Bilder/pokal.gif"));
		
		JLabel gewinner = new JLabel(spieler.getName() + " hat gewonnen.");
		gewinner.setFont(uberschrift);
		
		this.add(gewinner, "center");
		this.add(pokal, "center");
		this.repaint();
		this.revalidate();
	}
}
