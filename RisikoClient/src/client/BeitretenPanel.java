package client;

import java.rmi.RemoteException;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import exceptions.SpielBereitsErstelltException;
import net.miginfocom.swing.MigLayout;

public class BeitretenPanel extends JPanel{
	private BeitretenButtonClicked handler = null;
	private JLabel nameLab;
	private JTextField nameText;
	
	public interface BeitretenButtonClicked{
		public void hauptspielStarten(String name, int anzahl, String dateiPfad) throws RemoteException, SpielBereitsErstelltException;
		public void zurueckBtn(JPanel panel);
	}
	
	public BeitretenPanel(BeitretenButtonClicked handler) {
		this.handler = handler;
		initialize();
	}
	
	public void initialize() {
		this.setLayout(new MigLayout(" wrap2","[][300]","[][]")); 
		//Objekte erstellen
		nameLab = new JLabel("Name:");
		nameText = new JTextField();
		
		int randomNumber = new Random().nextInt(999) + 111;
		nameText.setText("Spieler_" + randomNumber);
		
		JButton startBtn = new JButton("Spiel beitreten");
		JButton zurueckBtn = new JButton("Zurück");
		
		zurueckBtn.addActionListener(zurueck -> handler.zurueckBtn(this));
		//Actionlistener
		startBtn.addActionListener(start -> {
			try {
					handler.hauptspielStarten(nameText.getText(),-1,null);
			} catch (RemoteException | SpielBereitsErstelltException e) {
				JOptionPane.showMessageDialog(null, "Server nicht gestartet.", "Server Fehler", JOptionPane.WARNING_MESSAGE);
			}
		});
		this.add(nameLab,"left");
		this.add(nameText,"left,growx");
		this.add(startBtn,"left,growx");
		this.add(zurueckBtn,"right,growx");
		
	}
}
