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
	private Font schrift;
	private Font uberschrift;

	public GewonnenPanel(Spieler spieler, Font schrift, Font uberschrift) {
		this.spieler = spieler;
		this.schrift = schrift;
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
//		gewinner.setForeground(Color.white);
		
		this.add(gewinner, "center");
		this.add(pokal, "center");
//		this.setBackground(new Color(96,96,96));
		this.repaint();
		this.revalidate();
	}
}
