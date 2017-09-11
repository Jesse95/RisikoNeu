package client;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class InfoPanel extends JPanel {

	private String phaseString;
	private JLabel phaseLab;
	private Font schrift;
	private Font uberschrift;
	
	public InfoPanel(String phase, Font schrift,Font uberschrift){
		this.phaseString = phase;
		this.schrift = schrift;
		this.uberschrift = uberschrift;
		initialize();
	}
	
	/**
	 * Header und darunter ein Label für die derzeitige Phase werden dargestellt.
	 */
	public void initialize() {
		this.setLayout(new MigLayout("wrap1","[]","[][][][]"));
		JLabel header = new JLabel("Phase:");
		header.setFont(uberschrift);
		phaseLab = new JLabel(phaseString);
		phaseLab.setFont(schrift);
		this.add(header,"left");
		this.add(phaseLab,"left");

	}
	
	/** Info über die Phase wird gesettet
	 * @param phase
	 */
	public void setInfo(String phase) {
		phaseLab.setText(phase);
	}
	
	/**Je nach Phase wird das Info-Label angepasst.
	 * @param phase
	 */
	public void changePanel(String phase) {
		switch(phase){
		case "VERTEILEN":
			this.setInfo("Verteilen");
			break;
		case "ANGRIFF":
			this.setInfo("Angreifen");
			break;
		case "VERSCHIEBEN":
			this.setInfo("Verschieben");
			break;
		case "STARTPHASE":
			this.setInfo("Startphase");
		}
	}
}
