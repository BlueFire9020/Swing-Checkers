import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

public class CheckersGame
{
	private int boardSize;
	private HashMap<Point, Pawn> board;
	private Point selectedPawn;
	private int turns;

	public CheckersGame(int boardSize)
	{
		this.boardSize = boardSize;
		setupGame();
	}

	public void setupGame()
	{
		//initialize board
		board = new HashMap<Point, Pawn>();

		//reset turns
		turns = 0;

		for (int i = 0; i < boardSize; i += 2)
		{
			//place both player and computer pawns for first column
			board.put(new Point(i, boardSize - 1), new Pawn(true));
			board.put(new Point(i, boardSize - 3), new Pawn(true));
			board.put(new Point(i, 1), new Pawn(false));

			//place both player and computer pawns for second column
			board.put(new Point(i + 1, 0), new Pawn(false));
			board.put(new Point(i + 1, 2), new Pawn(false));
			board.put(new Point(i + 1, boardSize - 2), new Pawn(true));
		}

	}
	public void playMove()
	{
		//computer is playing, since we don't have an algorithm just play randomly.
		ArrayList<Point> possibleMoves = new ArrayList<Point>();
		ArrayList<Point> pawns = getPointsForTeam(1);

		while (possibleMoves.size() < 1 && pawns.size() > 1)
		{
			Point moveTarget = pawns.get((int)(Math.random() * pawns.size()));

			possibleMoves = getPossibleMoves(moveTarget);
			System.out.println(possibleMoves.size());
			if (possibleMoves.size() > 1)
			{
				Point move = possibleMoves.get((int)(Math.random() * possibleMoves.size()));
				System.out.println(moveTarget + " -> " + move);
				playMove(moveTarget, move);	
			}
		}

	}

	public void playMove(Point origin, Point target)
	{
		//check if position is odd tile
		//if the row is odd, the column must be even
		//if the row is even, the column must be odd
		boolean rowEven = (target.y % 2) == 0;
		boolean columnEven = (target.x % 2) == 0;

		Pawn targetPawn = board.get(origin);

		System.out.println("turns 1 " + turns);
		System.out.println("turns " + Math.min(turns % 2, 1));
		//ensure that we're moving a pawn on our team, and that the pawn exists
		if (targetPawn != null && targetPawn.getTeam() == Math.min(turns % 2, 1))
		{
			//if it's an odd tile, we can play
			if (rowEven && !columnEven || !rowEven && columnEven && target.x < boardSize && target.y < boardSize)
			{
				//ensure that the movement distance is exactly correct
				int distance = (int)(Math.pow(target.x - origin.x, 2) + Math.pow(target.y - origin.y, 2));


				Point midpoint = null;
				if (target.y - origin.y < 0 && turns % 2 == 0 || target.y - origin.y > 0 && turns % 2 == 1 || targetPawn.isKing())
				{
					if (distance == 8)
					{
						midpoint = new Point((target.x + origin.x) / 2, (target.y + origin.y) / 2);	
					}
					else if (distance == 2)
					{
						midpoint = target;
					}	
				}


				//check if there's a piece under the hop and that the piece is enemy
				if (midpoint != null)
				{
					if (board.containsKey(midpoint) && board.get(midpoint).getTeam() != Math.min(turns % 2, 1))
					{
						//kill the pawn there
						board.remove(midpoint);

						targetPawn = board.remove(origin);
						board.put(target, targetPawn);
						selectPawn(target);
						/*now, we must check surrounding tiles to see if any consecutives may be played
						 * before we end our turn
						 */
						Point topRight = new Point(target.x + 1, target.y - 1);
						Point topLeft = new Point(target.x - 1, target.y - 1);
						Point botRight = new Point(target.x + 1, target.y + 1);
						Point botLeft = new Point(target.x - 1, target.y + 1);

						if (board.containsKey(topRight) && !board.containsKey(new Point(target.x + 2, target.y - 2)))
						{
							playMove(target, new Point(target.x + 2, target.y - 2));
						}
						if (board.containsKey(topLeft) && !board.containsKey(new Point(target.x - 2, target.y - 2)))
						{
							playMove(target, new Point(target.x - 2, target.y - 2));
						}

						//Only allowed to move backwards if king
						if (targetPawn.isKing())
						{
							if (board.containsKey(botRight) && !board.containsKey(new Point(target.x + 2, target.y + 2)))
							{
								playMove(target, new Point(target.x + 2, target.y + 2));
							}
							if (board.containsKey(botLeft) && !board.containsKey(new Point(target.x - 2, target.y + 2)))
							{
								playMove(target, new Point(target.x - 2, target.y + 2));
							}	
						}

						if (midpoint.y == boardSize * Math.min(turns % 2, 1))
						{
							targetPawn.king();
						}

						turns++;
					}
					else if (!board.containsKey(midpoint))
					{
						//perform a standard move, to assist computer algorithms
						targetPawn = board.remove(origin);
						board.put(midpoint, targetPawn);
						selectPawn(midpoint);
						if (midpoint.y == boardSize * Math.min(turns % 2, 1))
						{
							targetPawn.king();
						}

						turns++;
					}
				}
			}
		}
	}
	public ArrayList<Point> getPossibleSelectedMoves()
	{
		return getPossibleMoves(selectedPawn);
	}

	public ArrayList<Point> getPossibleMoves(Point target)
	{
		ArrayList<Point> moves = new ArrayList<Point>();
		if (target != null && board.get(target) != null)
		{
			Point topRight = new Point(target.x + 1, target.y - 1);
			Point topLeft = new Point(target.x - 1, target.y - 1);
			Point botRight = new Point(target.x + 1, target.y + 1);
			Point botLeft = new Point(target.x - 1, target.y + 1);

			if (Math.min(turns % 2, 1) == 0 && !board.get(target).isKing())
			{
				if (!board.containsKey(topRight))
				{
					moves.add(topRight);
				}
				else if (!board.containsKey(new Point(target.x + 2, target.y - 2)) && board.get(topRight).getTeam() != Math.min(turns % 2, 1))
				{
					moves.add(new Point(target.x + 2, target.y - 2));
				}
				if (!board.containsKey(topLeft))
				{
					moves.add(topLeft);
				}
				else if (!board.containsKey(new Point(target.x - 2, target.y - 2)) && board.get(topLeft).getTeam() != Math.min(turns % 2, 1))
				{
					moves.add(new Point(target.x - 2, target.y - 2));
				}
			}
			//Only allowed to move backwards if king
			else if (board.get(target).isKing() || Math.min(turns % 2, 1) == 1)
			{

				if (!board.containsKey(botRight))
				{
					moves.add(botRight);
				}
				else if (!board.containsKey(new Point(target.x + 2, target.y + 2)) && board.get(botRight).getTeam() != Math.min(turns % 2, 1))
				{
					moves.add(new Point(target.x + 2, target.y + 2));
				}
				if (!board.containsKey(botLeft))
				{
					moves.add(botLeft);
				}
				else if (!board.containsKey(new Point(target.x - 2, target.y + 2)) && board.get(botLeft).getTeam() != Math.min(turns % 2, 1))
				{
					moves.add(new Point(target.x - 2, target.y + 2));
				}	
			}

		}
		return moves;
	}
	public ArrayList<Point> getPointsForTeam(int team)
	{
		ArrayList<Point> pawns = new ArrayList<Point>();

		for (Point point : board.keySet())
		{
			if (board.get(point).getTeam() == team)
			{
				pawns.add(point);	
			}
		}

		return pawns;
	}

	public Pawn selectPawn(Point tile)
	{

		Pawn target = board.get(tile);	
		if (target != null && target.isPlayerPawn())
		{
			//target is a player pawn, allow select
			selectedPawn = tile;
			return target;
		}

		return null;
	}

	public Point getSelectedPawn()
	{
		return selectedPawn;
	}

	public Pawn getPawn(Point tile)
	{
		Pawn target = board.get(tile);	
		if (target != null)
		{	
			return target;
		}

		return null;
	}

	public void deselectPawn()
	{
		selectedPawn = null;
	}

	public ArrayList<Point> getPawnTiles()
	{
		return new ArrayList<Point>(board.keySet());
	}

	public int getBoardSize()
	{
		return boardSize;
	}

	public int getTurns()
	{
		return turns;
	}

	public String toString()
	{
		String boardStr = "";

		for (int y = 0; y < boardSize; y++)
		{
			for (int x = 0; x < boardSize; x++)
			{
				if (board.containsKey(new Point(x,y)))
				{
					//print special
					Pawn p = board.get(new Point(x,y));
					if (p.getTeam() == 0)
					{
						boardStr += " " + " PL ";	
					}
					else
					{
						boardStr += " " + " AI ";	
					}
				}
				else
				{
					boardStr += " " + String.format("%02d", x) + String.format("%02d", y);
				}
			}	
			boardStr += "\n";
		}

		return boardStr;
	}
}
