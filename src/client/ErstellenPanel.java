package client;

import java.rmi.RemoteException;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import local.domain.exceptions.SpielBereitsErstelltException;
import net.miginfocom.swing.MigLayout;

public class ErstellenPanel extends JPanel {
	private ErstellenButtonClicked handler = null;
	public interface ErstellenButtonClicked{
		public void hauptspielStarten(String name, int anzahl, String dateiPfad) throws RemoteException, SpielBereitsErstelltException;
		public void zurueckBtn();
	}
	
	public ErstellenPanel(ErstellenButtonClicked handler) {
		this.handler = handler;
		initialize();
	}
	
	public void initialize() {
		this.setLayout(new MigLayout(" wrap2","[][150]","[][][][][]")); 
		//Objekte erstellen
		JLabel nameLab = new JLabel("Name:");
		JTextField nameText = new JTextField();
		int randomNumber = new Random().nextInt(999) + 111;
		nameText.setText("Spieler_" + randomNumber);
		JLabel ipLab = new JLabel("IP:");
		JTextField ipText = new JTextField();
		ipText.setText("127.0.0.1");
		ipText.setEnabled(false);
		JLabel portLab = new JLabel("Port:");
		JTextField portText = new JTextField();
		portText.setText("4711");
		portText.setEnabled(false);
		String[] zahlen = {"2","3","4","5","6"};
		JLabel anzahlLab = new JLabel("Spieler Anzahl:");
		JComboBox<String> anzahlCBox = new JComboBox<String>(zahlen);
		JButton zurueckBtn = new JButton("zurueck");
		JButton startBtn = new JButton("Spiel starten");
		//Actionlistener
		zurueckBtn.addActionListener(zurueck -> handler.zurueckBtn());
		startBtn.addActionListener(start -> {
			try {
				handler.hauptspielStarten(nameText.getText(),Integer.parseInt((String)anzahlCBox.getSelectedItem()),null);
			} catch (NumberFormatException | RemoteException e) {
				JOptionPane.showMessageDialog(null, "Server nicht gestartet.", "Server Fehler", JOptionPane.WARNING_MESSAGE);
			} catch (SpielBereitsErstelltException e) {
				JOptionPane.showMessageDialog(null, "Auf dem Server laeuft bereits ein Spiel", "Server Fehler", JOptionPane.WARNING_MESSAGE);
			}
		});
		this.add(nameLab,"right");
		this.add(nameText,"left,growx");
		this.add(ipLab,"right");
		this.add(ipText,"left,growx");
		this.add(portLab,"right");
		this.add(portText,"left,growx");
		this.add(anzahlLab,"left");
		this.add(anzahlCBox,"left");
		this.add(startBtn,"center,spanx2");
		this.add(zurueckBtn, "center, spanx2");
	}
}
