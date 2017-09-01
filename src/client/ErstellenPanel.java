package client;

import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class ErstellenPanel extends JPanel {
	private ErstellenButtonClicked handler = null;
	public interface ErstellenButtonClicked{
		public void hauptspielStarten(String name, int anzahl) throws RemoteException;
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
		JLabel ipLab = new JLabel("IP:");
		JTextField ipText = new JTextField();
		JLabel portLab = new JLabel("Port:");
		JTextField portText = new JTextField();
		String[] zahlen = {"2","3","4","5","6"};
		JLabel anzahlLab = new JLabel("Spieler Anzahl:");
		JComboBox<String> anzahlCBox = new JComboBox<String>(zahlen);
		JButton startBtn = new JButton("Spiel starten");
		//Actionlistener
		startBtn.addActionListener(start -> {
			try {
				handler.hauptspielStarten(nameText.getText(),Integer.parseInt((String)anzahlCBox.getSelectedItem()));
			} catch (NumberFormatException | RemoteException e) {
				JOptionPane.showMessageDialog(null, "Server nicht gestartet.", "Server Fehler", JOptionPane.WARNING_MESSAGE);
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
	}
}
