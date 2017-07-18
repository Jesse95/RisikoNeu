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

import net.miginfocom.swing.MigLayout;

public class LadenPanel extends JPanel{
	private DefaultListModel<String> games;
	private JList<DefaultListModel<String>> gameList;
	private LadenButtonClicked handler;
	public interface LadenButtonClicked{
		public void spielLaden(String dat)  throws RemoteException, IOException;
	}
	
	public LadenPanel(LadenButtonClicked handler) {
		this.handler = handler;
		initialize();
	}
	
	public void initialize() {
		this.setLayout(new MigLayout(" wrap2","[][300]","[][100][]")); 

		JLabel savedGamesLab = new JLabel("Speicherstände:");
		games = new DefaultListModel<>();
		gameList = new JList(games);
		gameList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		gameList.setLayoutOrientation(JList.VERTICAL);
		gameList.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(gameList);
		listScroller.setPreferredSize(new Dimension(250, 80));
		JButton ladenBtn = new JButton("Spiel laden");

		ladenBtn.addActionListener(load -> {
				try {
					handler.spielLaden("spielstand1.txt");
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		});
		
		this.add(savedGamesLab,"left,spanx2");
		this.add(listScroller,"growx,growy,spanx2");
		this.add(ladenBtn,"center,spanx2");
	}
	
	public void speicherstaendeAnzeigen(ArrayList<String> speicherstaende) {

		for(String speicherstand : speicherstaende) {
			//.txt für die Ausgabe entfernen
			speicherstand = speicherstand.substring( 0, speicherstand.length() - 4 );
			games.addElement(speicherstand);
		}
		gameList = new JList(games);
	}
}
