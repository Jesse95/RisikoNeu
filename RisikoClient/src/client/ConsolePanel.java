package client;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

public class ConsolePanel extends JPanel{
	private JTextArea consoleText;
	private Font schrift;
	
	/**
	 * Konstruktor ConsolePanel
	 * @param schrift
	 */
	public ConsolePanel(Font schrift) {
		this.schrift = schrift;
		initialize();
	}
	
	/**
	 * Dem ConsolePanel wird ein entsprechender Header, ein Textfeld und eine Scrollbar eingefügt.
	 */
	public void initialize() {
		this.setLayout(new MigLayout("wrap1","[600]","[][140]"));
		JLabel header = new JLabel("Benachrichtigung:");
		consoleText = new JTextArea();
		JScrollPane consoleScrollBar = new JScrollPane(consoleText);
		consoleText.setLineWrap(true);
		header.setFont(schrift);
		consoleText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN,15));
		this.add(header,"left, growx");
		this.add(consoleScrollBar,"growx,growy");
	}
	
	/** Übergebener Text erscheint auf der Console
	 * @param text
	 */
	public void textSetzen(String text) {
			consoleText.setText(consoleText.getText() + "\n" + text);		
	}
}
