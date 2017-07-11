package client;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.ErstellenPanel.ErstellenButtonClicked;
import net.miginfocom.swing.MigLayout;

public class BeitretenPanel extends JPanel{
	private BeitretenButtonClicked handler = null;
	public interface BeitretenButtonClicked{
		public void spielErstellen(String name, int anzahl);
	}
	
	public BeitretenPanel(BeitretenButtonClicked handler) {
		this.handler = handler;
		initialize();
	}
	
	public void initialize() {
		this.setLayout(new MigLayout(" wrap2","[][150]","[][][][][]")); 
		//Objekte erstellen
		JLabel nameLab = new JLabel("Name:");
		JTextField nameText = new JTextField();
		JLabel ipLab = new JLabel("IP:");
		JTextField ipText = new JTextField();
		JLabel portLab = new JLabel("Port:");
		JTextField portText = new JTextField();
		JButton startBtn = new JButton("Spiel beitreten");
		//Actionlistener
		startBtn.addActionListener(start -> handler.spielErstellen(nameText.getText(),10));
		this.add(nameLab,"right");
		this.add(nameText,"left,growx");
		this.add(ipLab,"right");
		this.add(ipText,"left,growx");
		this.add(portLab,"right");
		this.add(portText,"left,growx");
		this.add(startBtn,"center,spanx2");
	}
}
