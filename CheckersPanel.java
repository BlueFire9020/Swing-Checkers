import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class CheckersPanel extends JPanel
{
	private int size = 8;
	private int space = 20;
	private CheckersBoard checkers;
	private int boxSize;
	private static BufferedImage whitePawn, blackPawn, whiteKing, blackKing, border;
	private static HashMap<Integer, BufferedImage> boards;
	private Pawn movingPawn;
	private Point movingTile;
	private Point movingPoint;
	private Point destinationPoint;
	private Point movementPoint;

	static {
		try {			
			border = ImageIO.read(new File("src/borders/border.png"));

			whitePawn = ImageIO.read(new File("src/pawns/pawn-white.png"));
			blackPawn = ImageIO.read(new File("src/pawns/pawn-black.png"));

			whiteKing = ImageIO.read(new File("src/pawns/king-white.png"));
			blackKing = ImageIO.read(new File("src/pawns/king-black.png"));

			boards = new HashMap<Integer, BufferedImage>();
			boards.put(4, ImageIO.read(new File("src/boards/board-4.png")));
			boards.put(8, ImageIO.read(new File("src/boards/board-8.png")));
			boards.put(10, ImageIO.read(new File("src/boards/board-10.png")));
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	};

	public CheckersPanel(int size)
	{
		this.size = size;

		this.checkers = new CheckersBoard(size);

		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if (Math.min(checkers.getTurns() % 2, 1) == 0)
				{
					int xPos = ((e.getX() - space) / boxSize);
					int yPos = ((e.getY() - space) / boxSize);
					Point target = new Point(xPos, yPos);
					Point selected = checkers.getSelectedPawn();
					if (checkers.getPossibleSelectedMoves().contains(target))
					{
						if (movingPawn == null)
						{
							Pawn pawn = checkers.getPawn(checkers.playMove(checkers.getSelectedPawn(), new Point(target)));
							movingPawn = pawn;
							//movingPoint = new Point(selected.x * boxSize + space, selected.y * boxSize + space);
							destinationPoint = new Point(target.x * boxSize + space, target.y * boxSize + space);
							movementPoint = new Point((int)Math.abs(destinationPoint.getX() - movingPoint.getX()), (int)Math.abs(destinationPoint.getY() - movingPoint.getY()));
						}
					}
					else
					{
						checkers.selectPawn(new Point(target));	
					}

					repaint();	
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
				// run ai move when player is done
				if (Math.min(checkers.getTurns() % 2, 1) == 1)
				{
					if (movingPawn == null)
					{
						movementPoint = checkers.playMove();	
					}
				}
				//uncomment and comment above to have a complete AI game
				//checkers.playMove();
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
		space = (scale / (checkers.boardSize + 2));

		page.setColor(Color.gray);
		page.fillRect(0, 0, width, height);

		page.drawImage(border, 0, 0, scale, scale, null);
		page.setColor(new Color(84, 68, 37));
		page.fillRect(space - 5, space - 5, space * size + 10, space * size + 10);

		Font font = page.getFont();

		font = font.deriveFont(Font.BOLD, space / 3);

		for (int i = 0; i < size; i++)
		{

			page.setColor(new Color(79, 54, 26));	
			drawCenteredString(page, (i + 1) + "", new Rectangle(((i + 1) * space), 0, space, space), font);
			drawCenteredString(page, (char)(65 + i) + "", new Rectangle(0, ((i + 1) * space), space, space), font);
		}	

		boxSize = ((scale - space * 2) / size);

		int increment = (checkers.boardSize % 10 == 0) ? 10 : (checkers.boardSize % 8) == 0 ? 8 : 4;

		for (int y = 0; y < size; y += increment)
		{
			for (int x = 0; x < size; x += increment)
			{
				page.drawImage(boards.get(increment), x * boxSize + space, y * boxSize + space, boxSize * increment, boxSize * increment, null);
			}
		}
		
		if (movingPawn != null) 
		{
			int xDir = (destinationPoint.x - movingPoint.x);
			int yDir = (destinationPoint.y - movingPoint.y);
			
			if (xDir == 0 && yDir == 0)
			{
				movementPoint = null;
				movingPawn = null;
				movingPoint = null;
				destinationPoint = null;
			}
			else
			{
				//normalize direction
				xDir = xDir / Math.abs(xDir);
				yDir = yDir / Math.abs(yDir);
				
				movingPoint.x += xDir;
				movingPoint.y += yDir;
				//System.out.println(movingPoint.x + " " + movingPoint.y);	
				
				if (movingPawn.getTeam() == 0)
				{
					if (movingPawn.isKing())
					{
						page.drawImage(whiteKing, movingPoint.x, movingPoint.y, boxSize, boxSize, null);
					}
					else
					{
						page.drawImage(whitePawn, movingPoint.x, movingPoint.y, boxSize, boxSize, null);						
					}
				}
				else
				{
					if (movingPawn.isKing())
					{
						page.drawImage(blackKing, movingPoint.x, movingPoint.y, boxSize, boxSize, null);
					}
					else
					{
						page.drawImage(blackPawn, movingPoint.x, movingPoint.y, boxSize, boxSize, null);						
					}
				}
			}
		}

		for (Point p : checkers.getPossibleSelectedMoves())
		{
			page.setColor(Color.white);
			page.drawRect(p.x * boxSize + space, p.y * boxSize + space, boxSize, boxSize);
		}
		for (Point p : checkers.getPawnTiles())
		{
			Pawn pawn = checkers.getPawn(p);
			if (!pawn.equals(movingPawn))
			{
				if (pawn.getTeam() == 0)
				{
					if (pawn.isKing())
					{
						page.drawImage(whiteKing, p.x * boxSize + space, p.y * boxSize + space, boxSize, boxSize, null);
					}
					else
					{
						page.drawImage(whitePawn, p.x * boxSize + space, p.y * boxSize + space, boxSize, boxSize, null);						
					}
				}
				else
				{
					if (pawn.isKing())
					{
						page.drawImage(blackKing, p.x * boxSize + space, p.y * boxSize + space, boxSize, boxSize, null);
					}
					else
					{
						page.drawImage(blackPawn, p.x * boxSize + space, p.y * boxSize + space, boxSize, boxSize, null);						
					}
				}	
			}
			//page.fillRect(p.x * boxSize, p.y * boxSize, boxSize, boxSize);
		}

		Point selected = checkers.getSelectedPawn();
		if (selected != null)
		{
			page.setColor(Color.green);
			page.drawRect(selected.x * boxSize + space, selected.y * boxSize + space, boxSize, boxSize);	
		}
	}


	public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
		// Get the FontMetrics
		FontMetrics metrics = g.getFontMetrics(font);
		// Determine the X coordinate for the text
		int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
		// Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
		int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
		// Set the font
		g.setFont(font);
		// Draw the String
		g.drawString(text, x, y);
	}
}
