package client;

import java.awt.Font;
import java.io.File;
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

import exceptions.SpielBereitsErstelltException;
import net.miginfocom.swing.MigLayout;

public class LadenPanel extends JPanel{
	private DefaultListModel<String> games;
	private JList<String> gameList;
	private LadenButtonClicked handler;
	public interface LadenButtonClicked{
		public void hauptspielStarten(String name, int anzahlSpieler, String dateiPfad)  throws RemoteException, SpielBereitsErstelltException;
		public void zurueckBtn(JPanel panel);
	}
	
	public LadenPanel(LadenButtonClicked handler) {
		this.handler = handler;
		initialize();
	}
	
	public void initialize() {
		this.setLayout(new MigLayout(" wrap2","[150][150]","[][120][]")); 
	}
	
	public void aktuelleSpeicherstandAuswahlAnzeigen() {
		ArrayList<String> speicherstaende = speicherstaendeLaden();
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
//		listScroller.setPreferredSize(new Dimension(250, 100));
		JLabel savedGamesLab = new JLabel("Speicherstände:");
		savedGamesLab.setFont(new Font(Font.SERIF, Font.BOLD, 25));
		JButton ladenBtn = new JButton("Spiel laden");
		JButton zurueckBtn = new JButton("Zurück");
		
		zurueckBtn.addActionListener(zurueck -> handler.zurueckBtn(this));
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
		this.add(ladenBtn,"left, growx");
		this.add(zurueckBtn,"right, growx");
	}

	public ArrayList<String> speicherstaendeLaden() {
		File[] fs = new File("./Speicher/").listFiles();
        ArrayList<String> games = new ArrayList<String>();
        try {
	        for (File f : fs) {
	            if (f.isFile()) {
	                games.add(f.getName());
	            }
	        }
        } catch (Exception e) {
			System.out.println("Fehler beim Laden der Speicherstände.");
		}
        return games;
	}
}
