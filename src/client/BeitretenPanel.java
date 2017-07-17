package client;

import java.awt.Font;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class BeitretenPanel extends JPanel{
	private BeitretenButtonClicked handler = null;
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
		JTextArea gameList = new JTextArea();
		JScrollPane gameListScrollBar = new JScrollPane(gameList);
		gameList.setLineWrap(true);
		gameList.setFont(new Font(Font.SANS_SERIF, Font.PLAIN,15));
		
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
		this.add(gameListScrollBar,"growx,growy,spanx2");
		this.add(startBtn,"center,spanx2");
	}
}
