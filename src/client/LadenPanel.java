package client;

import java.awt.Dimension;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import local.domain.exceptions.SpielBereitsErstelltException;
import net.miginfocom.swing.MigLayout;

public class LadenPanel extends JPanel{
	private DefaultListModel<String> games;
	private JList<String> gameList;
	private LadenButtonClicked handler;
	public interface LadenButtonClicked{
		public void hauptspielStarten(String name, int anzahlSpieler, String dateiPfad)  throws RemoteException, SpielBereitsErstelltException;
	}
	
	public LadenPanel(LadenButtonClicked handler) {
		this.handler = handler;
		initialize();
	}
	
	public void initialize() {
		this.setLayout(new MigLayout(" wrap2","[][300]","[][100][]")); 
	}
	
	public void aktuelleSpeicherstandAuswahlAnzeigen(ArrayList<String> speicherstaende) {
		
		games = new DefaultListModel<>();
		for(String speicherstand : speicherstaende) {
			//.txt für die Ausgabe entfernen
			speicherstand = speicherstand.substring( 0, speicherstand.length() - 4 );
			games.addElement(speicherstand);
		}
		gameList = new JList<String>(games);
		gameList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		gameList.setLayoutOrientation(JList.VERTICAL);
		gameList.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(gameList);
		listScroller.setPreferredSize(new Dimension(250, 80));
		JLabel savedGamesLab = new JLabel("Speicherstände:");
		JButton ladenBtn = new JButton("Spiel laden");

		ladenBtn.addActionListener(load -> {
				try {
					final String selected = gameList.getSelectedValue();
					System.out.println(selected);
					handler.hauptspielStarten("", -1,selected + ".txt");
				} catch (IOException | SpielBereitsErstelltException e) {
					e.printStackTrace();
				}
		});
		
		this.add(savedGamesLab,"left,spanx2");
		this.add(listScroller,"growx,growy,spanx2");
		this.add(ladenBtn,"center,spanx2");
	}
}
