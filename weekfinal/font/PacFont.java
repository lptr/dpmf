import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;


class fontWindow extends JFrame {

	fontWindow() {

		setSize(640,480);
		JPanel contentPane = new JPanel();

		Font specFont=null;
		try { specFont=Font.createFont(Font.TRUETYPE_FONT,new FileInputStream(new File("pacfont.ttf"))); }
		catch(Exception e) { System.err.println("Missing or corrupt font file!!"); System.exit(-2);}

		JLabel f; 
		contentPane.add(BorderLayout.CENTER,f = new JLabel("pACmAN"));
		f.setFont(specFont.deriveFont((float)72));
		setContentPane(contentPane);
		pack();
	}
};

class PacFont extends Object {
	public static void main(String[] args) {
		fontWindow prog = new fontWindow();
		prog.setVisible(true);
	}
}

