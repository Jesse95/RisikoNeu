package server;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import local.valueobjects.Land;
import local.valueobjects.Spieler;
import net.miginfocom.swing.MigLayout;

public class AdminPanel extends JPanel{
	private ArrayList<Spieler> spielerListe;
	private ArrayList<Land> laenderListe;
	private JButton einheitenSetzenBtn = new JButton("Einheiten setzen");
	private JButton besitzerSetzenBtn = new JButton("Besitzer setzen");
	private JButton phaseSetzenBtn = new JButton("Phase setzen");
	private JButton aktiverSpielerSetzenBtn = new JButton("Aktiver Spieler setzen");
	
	
	public AdminPanel() {
		initialize();
	}
	
	public void initialize() {
		this.setLayout(new MigLayout("wrap1","[]","[][][][]"));
		JLabel header = new JLabel("Adminpanel:");
		
		einheitenSetzenBtn.addActionListener(einheitenSetzen -> einheitenSetzen());
		
		this.add(header,"center, growx");
		this.add(einheitenSetzenBtn);
		this.add(besitzerSetzenBtn);
		this.add(phaseSetzenBtn);
		this.add(aktiverSpielerSetzenBtn);
	}
	
	public void listenSetzen(ArrayList<Spieler> spielerListe, ArrayList<Land> laenderListe) {
		this.spielerListe = spielerListe;
		this.laenderListe = laenderListe;
	}
	public void einheitenSetzen(){
		String[] laenderArray = new String[laenderListe.size()];
		for(int i = 0; i < laenderListe.size(); i++){
			laenderArray[i] = laenderListe.get(i).getName();
		}
		JComboBox<String> laenderWahl = new JComboBox<String>(laenderArray);
		removeAll();
		this.add(laenderWahl);
		this.repaint();
		this.revalidate();
	}
	public void removeAll(){
		this.remove(einheitenSetzenBtn);
		this.remove(besitzerSetzenBtn);
		this.remove(phaseSetzenBtn);
		this.remove(aktiverSpielerSetzenBtn);
	}
}
