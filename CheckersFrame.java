import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.BevelBorder;

public class CheckersFrame 
{
	public static void main(String[] args)
	{
		JFrame cframe = new JFrame("Checkers 0.1");
		
		CheckersPanel checkers = new CheckersPanel(10);
		checkers.setFocusable(true);
		checkers.requestFocus(true);
		
		cframe.add(checkers);
		
		cframe.pack();
		cframe.setSize(500, 500);
		
		cframe.setVisible(true);
		cframe.setFocusable(true);
		cframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
