package server;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

public class ConsolePanel extends JPanel{
	private JTextArea consoleText;
	
	public ConsolePanel() {
		initialize();
	}
	
	public void initialize() {
		this.setLayout(new MigLayout("wrap1","[250]","[][350]"));
		JLabel header = new JLabel("Benachrichtigung:");
		consoleText = new JTextArea();
		JScrollPane consoleScrollBar = new JScrollPane(consoleText);
		consoleText.setLineWrap(true);
		consoleText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN,15));
		this.add(header,"left, growx");
		this.add(consoleScrollBar,"growx,growy");
	}
	
	public void textSetzen(String text) {
		consoleText.setText(consoleText.getText() + "\n" + text);
	}
}
