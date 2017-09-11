package server;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import valueobjects.Land;
import valueobjects.Spieler;

/**
 * @author yanni
 *
 */
/**
 * @author yanni
 *
 */
public class AdminPanel extends JPanel{
	public interface AdminPanelButtons{
		public void einheitenSetzenBtn(String land, int einheiten);
		public void besitzerSetzenBtn(String land, String spieler);
		public void phaseSetzenBtn(String phase);
		public void aktiverSpielerSetzenBtn(String spieler);
	}
	private ArrayList<Spieler> spielerListe;
	private ArrayList<Land> laenderListe;
	private JButton einheitenSetzenMenuBtn = new JButton("Einheiten setzen");
	private JButton besitzerSetzenMenuBtn = new JButton("Besitzer setzen");
	private JButton phaseSetzenMenuBtn = new JButton("Phase setzen");
	private JButton aktiverSpielerSetzenMenuBtn = new JButton("Aktiver Spieler");
	private JButton zurueckBtn = new JButton("Zurueck");
	private JButton einheitenSetzenBtn = new JButton("Einheiten setzen");
	private JButton besitzerSetzenBtn = new JButton("Besitzer setzen");
	private JButton phaseSetzenBtn = new JButton("Phase setzen");
	private JButton aktiverSpielerSetzenBtn = new JButton("Aktiver Spieler");
	private JComboBox<String> laenderWahl = new JComboBox<String>();
	private JComboBox<String> spielerWahl = new JComboBox<String>();
	private JComboBox<String> phaseWahl = new JComboBox<String>();
	private JTextField einheitenZahl = new JTextField();
	private AdminPanelButtons handler = null;
	private String[] laenderArray;
	
	
	/**
	 * Konstruktor:
	 * Initialize wird aufgerufen
	 * @param handler
	 */
	public AdminPanel(AdminPanelButtons handler) {
		this.handler = handler;
		initialize();
	}
	
	
	/**
	 * Aussehen wird definiert.
	 * ActionListener werden den Buttons zugeordnet
	 */
	public void initialize() {
		this.setLayout(new MigLayout("wrap1","[]","[][][][]"));
		this.setSize(300, 400);
		
		einheitenSetzenMenuBtn.addActionListener(menuAufrufen -> einheitenSetzen());
		besitzerSetzenMenuBtn.addActionListener(menuAufrufen -> besitzerSetzen());
		phaseSetzenMenuBtn.addActionListener(menuAufrufen -> phaseSetzen());
		aktiverSpielerSetzenMenuBtn.addActionListener(menuAufrufen -> aktiverSpielerSetzen());
		zurueckBtn.addActionListener(zurueck -> startPanel());
		einheitenSetzenBtn.addActionListener(einheitenSetzen -> handler.einheitenSetzenBtn(besitzerSetzenVerarbeiten(), Integer.parseInt(einheitenZahl.getText())));
		besitzerSetzenBtn.addActionListener(besitzerSetzen -> handler.besitzerSetzenBtn(besitzerSetzenVerarbeiten(), spielerWahl.getSelectedItem() +""));
		phaseSetzenBtn.addActionListener(phaseSetzen -> handler.phaseSetzenBtn(phaseWahl.getSelectedItem() + ""));
		aktiverSpielerSetzenBtn.addActionListener(spielerSetzen -> handler.aktiverSpielerSetzenBtn(spielerWahl.getSelectedItem() + ""));
		
		laenderWahl.setPreferredSize(new Dimension(20,15));
		JLabel header = new JLabel("Adminpanel:");
		this.add(header,"center, growx");
		
		
	}
	
	/**
	 * Setzt die Spieler- und Länderlisten
	 * @param spielerListe
	 * @param laenderListe
	 */
	public void listenSetzen(ArrayList<Spieler> spielerListe, ArrayList<Land> laenderListe) {
		this.spielerListe = spielerListe;
		this.laenderListe = laenderListe;
	}
	
	/**
	 * Löscht alle komponenten im Frame und
	 * fügt dem Frame die nötigen Komponenten zum Einheiten setzen hinzu
	 */
	public void einheitenSetzen(){
		einheitenZahl = new JTextField("0");
		laenderUndBesitzer(this.laenderListe);
		removeAll();
		this.add(laenderWahl,"growx");
		this.add(einheitenZahl,"growx");
		this.add(einheitenSetzenBtn);
		this.add(zurueckBtn);
		this.repaint();
		this.revalidate();
	}
	
	/**
	 *Löscht alle komponenten im Frame und
	 *und fügt die nötgen Komponenten zum Phase setzen hinzu 
	 */
	public void phaseSetzen(){
		removeAll();
		String[] phasenListe = {"VERTEILEN","ANGRIFF","VERSCHIEBEN"};
		phaseWahl = new JComboBox<String>(phasenListe);
		this.add(phaseWahl);
		this.add(phaseSetzenBtn);
		this.add(zurueckBtn);
		this.repaint();
		this.revalidate();
	}
	
	/**
	 *Löscht alle komponenten im Frame und
	 *und fügt die nötgen Komponenten zum Besitzer setzen hinzu  
	 */
	public void besitzerSetzen(){
		removeAll();
		laenderUndBesitzer(this.laenderListe);
		String[] spielerArray = new String[spielerListe.size()];
		for(int i = 0; i < spielerListe.size(); i++){
			spielerArray[i] = spielerListe.get(i).getName();
		}
		spielerWahl = new JComboBox<String>(spielerArray);
		
		this.add(laenderWahl);
		this.add(spielerWahl);
		this.add(besitzerSetzenBtn);	
		this.add(zurueckBtn);
		this.repaint();
		this.revalidate();
	}
	
	/**
	 *Löscht alle komponenten im Frame und
	 *und fügt die nötgen Komponenten zum aktiven Spieler setzen hinzu   
	 */
	public void aktiverSpielerSetzen(){
		removeAll();
		String[] spielerArray = new String[spielerListe.size()];
		for(int i = 0; i < spielerListe.size(); i++){
			spielerArray[i] = spielerListe.get(i).getName();
		}
		spielerWahl = new JComboBox<String>(spielerArray);
		this.add(spielerWahl);
		this.add(aktiverSpielerSetzenBtn);
		this.add(zurueckBtn);
		this.repaint();
		this.revalidate();
	}
	
	/**
	 * Entfernt alle Komponenten vom Frame
	 */
	public void removeAll(){
		this.remove(einheitenSetzenMenuBtn);
		this.remove(besitzerSetzenMenuBtn);
		this.remove(phaseSetzenMenuBtn);
		this.remove(aktiverSpielerSetzenMenuBtn);
		this.remove(aktiverSpielerSetzenBtn);
		this.remove(besitzerSetzenBtn);
		this.remove(einheitenSetzenBtn);
		this.remove(phaseSetzenBtn);
		this.remove(zurueckBtn);
		this.remove(laenderWahl);
		this.remove(spielerWahl);
		this.remove(phaseWahl);
		this.remove(einheitenZahl);
		
		this.repaint();
		
	}
	
	/**
	 *Löscht alle komponenten im Frame und
	 *und fügt die nötgen Komponenten für das Hauptmenü hinzu   
	 */
	public void startPanel(){
		removeAll();
		this.add(einheitenSetzenMenuBtn);
		this.add(besitzerSetzenMenuBtn);
		this.add(phaseSetzenMenuBtn);
		this.add(aktiverSpielerSetzenMenuBtn);
		this.repaint();
		this.revalidate();
	}
	
	/**
	 * Gibt den ausgewählten Namen vom Länder Dropdownmenü zurück.
	 * @return String
	 */
	private String besitzerSetzenVerarbeiten(){
		return laenderListe.get(laenderWahl.getSelectedIndex()).getName();

	}
	
	/**
	 * Gibt im Dropdownmenü die Ländernamen und die dazugehörigen Besitzer an
	 * @param laenderListe
	 */
	public void laenderUndBesitzer(ArrayList<Land> laenderListe){
		laenderArray = new String[laenderListe.size()];
		for(int i = 0; i < laenderListe.size(); i++){
			laenderArray[i] = "<html>" + laenderListe.get(i).getName() + " <br> " + laenderListe.get(i).getBesitzer().getName()+ "</html>";
		}
		laenderWahl = new JComboBox<String>(laenderArray);

	}
}
