import java.awt.Frame;

public class CheckersFrame 
{
	public static void main(String[] args)
	{
		Frame cframe = new Frame("Checkers 0.1");
		cframe.add(new CheckersPanel(8));
		cframe.pack();
		cframe.setSize(600, 600);
		cframe.setVisible(true);
	}
}
