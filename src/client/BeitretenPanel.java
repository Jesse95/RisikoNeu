package client;

import java.awt.Dimension;
import java.rmi.RemoteException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import net.miginfocom.swing.MigLayout;

public class BeitretenPanel extends JPanel{
	private BeitretenButtonClicked handler = null;
	private DefaultListModel<String> games;
	private JList<DefaultListModel<String>> gameList;
	
	public interface BeitretenButtonClicked{
		public void hauptspielStarten(String name, int anzahl) throws RemoteException;
	}
	
	public BeitretenPanel(BeitretenButtonClicked handler) {
		this.handler = handler;
		initialize();
	}
	
	public void initialize() {
		this.setLayout(new MigLayout(" wrap2","[][300]","[][][100][]")); 
		//Objekte erstellen
		JLabel nameLab = new JLabel("Name:");
		JTextField nameText = new JTextField();
		JLabel openGamesLab = new JLabel("Offene Spiele:");
		//hier funktionsprinzip erklärt https://docs.oracle.com/javase/tutorial/uiswing/components/list.html 
		games = new DefaultListModel<>();
		gameList = new JList(games);
		gameList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		gameList.setLayoutOrientation(JList.VERTICAL);
		gameList.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(gameList);
		listScroller.setPreferredSize(new Dimension(250, 80));
		JButton startBtn = new JButton("Spiel beitreten");
		//Actionlistener
		startBtn.addActionListener(start -> {
			try {
				handler.hauptspielStarten(nameText.getText(),-1);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		});
		this.add(nameLab,"right");
		this.add(nameText,"left,growx");
		this.add(openGamesLab,"left,spanx2");
		this.add(listScroller,"growx,growy,spanx2");
		this.add(startBtn,"center,spanx2");
	}
	
	public void zuSpielAnzeigeHinzufuegen(int anzahlSpieler, int belegtePlaetze, int port, String name) {
		String game = name + " - Port: " + port + " - Spieler: " + belegtePlaetze + " / " + anzahlSpieler;
		games.addElement(game);
		gameList = new JList(games);
	}
}
