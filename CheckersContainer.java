import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class CheckersContainer extends JPanel
{
	private int size;
	private static BufferedImage border;
	
	static {
		try {
			border = ImageIO.read(new File("src/borders/border.png"));
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	};
	
	public CheckersContainer(int size)
	{
		this.size = size;
	}
	
	@Override
	public void paintComponent(Graphics page)
	{
		int width = getWidth();
		int height = getHeight();
		System.out.println(width + " " + height);
		int area = width < height ? width : height;		
		int space = area / (size + 2);
				
		this.getComponents()[0].setSize(new Dimension(width - (space * 2), height - (space * 2)));

		page.drawImage(border, 0, 0, area, area, null);

		for (int i = 0; i < size; i++)
		{

			page.setColor(Color.black);	
			page.drawString((i + 1) + "", ((i + 1) * space), space);
			page.drawString((char)(65 + i) + "", space, ((i + 1) * space));
		}	
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		int width = getWidth();
		int height = getHeight();
		int scale = width < height ? width : height;
		
		return new Dimension(scale, scale);
		
	}
}
