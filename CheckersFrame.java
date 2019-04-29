import java.awt.Frame;

public class CheckersFrame 
{
	public static void main(String[] args)
	{
		Frame cframe = new Frame("Checkers 0.1");
		CheckersPanel checkers = new CheckersPanel(8);
		checkers.setFocusable(true);
		checkers.requestFocus(true);
		cframe.add(checkers);
		cframe.pack();
		cframe.setSize(500, 500);
		cframe.setVisible(true);
		cframe.setFocusable(true);
	}
}
