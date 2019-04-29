import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class CheckersPanel extends JPanel
{
	private int size = 8;
	private CheckersGame checkers;
	private int boxSize;

	public CheckersPanel(int size)
	{
		this.size = size;

		this.checkers = new CheckersGame(size);

		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if (Math.min(checkers.getTurns() % 2, 1) == 0)
				{
					int xPos = e.getX() / boxSize;
					int yPos = e.getY() / boxSize;
					System.out.println(checkers.getPossibleSelectedMoves().contains(new Point(xPos, yPos)));
					if (checkers.getPossibleSelectedMoves().contains(new Point(xPos, yPos)))
					{
						checkers.playMove(checkers.getSelectedPawn(), new Point(xPos, yPos));
						checkers.selectPawn(new Point(xPos, yPos));	
					}
					else
					{
						checkers.selectPawn(new Point(xPos, yPos));	
					}

					repaint();	
				}
				else
				{
					checkers.playMove();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}


		});

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// Your database code here
				repaint();
			}
		}, 10, 10);
	}


	@Override
	public void paintComponent(Graphics page)
	{
		int width = this.getWidth();
		int height = this.getHeight();

		int scale = width < height ? width : height;

		boxSize = (scale / size);

		for (int y = 0; y < size; y += 2)
		{
			for (int x = 0; x < size; x += 2)
			{
				page.setColor(Color.gray);
				page.fillRect(x * boxSize, y * boxSize, boxSize, boxSize);
				page.setColor(Color.black);
				page.fillRect(x * boxSize, y * boxSize + boxSize, boxSize, boxSize);
				page.setColor(Color.black);
				page.fillRect(x * boxSize + boxSize, y * boxSize, boxSize, boxSize);
				page.setColor(Color.gray);
				page.fillRect(x * boxSize + boxSize, y * boxSize + boxSize, boxSize, boxSize);
			}
		}

		for (Point p : checkers.getPossibleSelectedMoves())
		{
			page.setColor(Color.white);
			page.fillRect(p.x * boxSize, p.y * boxSize, boxSize, boxSize);
		}
		for (Point p : checkers.getPawnTiles())
		{
			Pawn pawn = checkers.getPawn(p);
			page.setColor(Color.red);

			if (pawn.isKing())
			{
				page.setColor(Color.cyan);
			}
			if (pawn.getTeam() == 0)
			{
				page.setColor(Color.blue);
			}

			page.fillRect(p.x * boxSize, p.y * boxSize, boxSize, boxSize);
		}

		Point selected = checkers.getSelectedPawn();
		if (selected != null)
		{
			page.setColor(Color.green);
			page.fillRect(selected.x * boxSize, selected.y * boxSize, boxSize, boxSize);	
		}
	}
}
