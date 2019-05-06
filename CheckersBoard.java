import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

public class CheckersBoard extends Board
{
	private Point selectedPawn;

	public CheckersBoard(int boardSize)
	{
		this.boardSize = boardSize;
		setupBoard();
	}

	public void setupBoard()
	{
		//initialize board
		board = new HashMap<Point, Piece>();

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
	
	public void resetBoard()
	{
		setupBoard();
	}
	
	public Point playMove()
	{
		//TODO: add algorithm to the computer (make it so it makes all hops first, then moves sequentially?)
		//computer is playing, since we don't have an algorithm just play randomly.
		//TODO: Determine number of pieces killed for each hop and make computer difficulties accordingly
		ArrayList<Point> possibleMoves = new ArrayList<Point>();
		ArrayList<Point> pawns = getPointsForTeam(Math.min(turns % 2, 1));
		
		for (Point point : pawns)
		{
			//check if any kill moves exist, and play those first
			ArrayList<Point> possibleHops = getPossibleHops(point);
			if (possibleHops.size() > 0)
			{
				return playMove(point, possibleHops.get(0));
			}
		}
		for (Point point : pawns)
		{
			possibleMoves = getPossibleMoves(point);
			if (possibleMoves.size() > 0)
			{
				Point move = possibleMoves.get((int)(Math.random() * possibleMoves.size()));
				return playMove(point, move);	
			}
		}
		
		return null;

	}
	
	public Point playMove(Point origin, Point target)
	{
		boolean justCrowned = false;
		
		Pawn targetPawn = (Pawn) board.get(origin);
		
		ArrayList<Point> legalMoves = getPossibleMoves(origin);
		ArrayList<Point> legalHops = getPossibleHops(origin);
		
		if (targetPawn != null && targetPawn.getTeam() == Math.min(turns % 2, 1))
		{
			if (target.y == (boardSize - 1) * Math.min(turns % 2, 1))
			{
				targetPawn.king();
				justCrowned = true;
			}
			
			if (legalHops.contains(target)) 
			{		
				Point midpoint = new Point((target.x + origin.x) / 2, (target.y + origin.y) / 2);	
				
				board.remove(midpoint);
				movePawn(origin, target);
				
				ArrayList<Point> possibleHops = getPossibleHops(target);
				//TODO: Improve logic so that it chooses the hop path with the most possible kills
				if (possibleHops.size() > 0 && !justCrowned)
				{
					return playMove(target, possibleHops.get(0));
				}
			}
			else if (legalMoves.contains(target)) 
			{
				movePawn(origin, target);
			}	
		}
		turns++;
		return target;
	}

	public Point playMove2(Point origin, Point target)
	{
		/*System.out.println("MOVING " + turns);
		System.out.println("PIECE FOR TEAM " + board.get(origin).getTeam());
		System.out.println("TRANSFER FROM x" + origin.x + " y" + origin.y + " TO x" + target.x + " y" + target.y);
		System.out.println("---------------");*/
		boolean rowEven = (target.y % 2) == 0;
		boolean columnEven = (target.x % 2) == 0;
		boolean justCrowned = false;

		Pawn targetPawn = (Pawn) board.get(origin);
		Point move;
		//ensure that we're moving a pawn on our team, and that the pawn exists
		if (targetPawn != null && targetPawn.getTeam() == Math.min(turns % 2, 1))
		{
			ArrayList<Point> moves = getPossibleMoves(origin);
			//ensure that the movement distance is exactly correct
			int distance = (int)(Math.pow(target.x - origin.x, 2) + Math.pow(target.y - origin.y, 2));
			
			if (moves.contains(target))
			{
				if (target.y == (boardSize - 1) * Math.min(turns % 2, 1))
				{
					targetPawn.king();
					justCrowned = true;
				}
				movePawn(origin, target);
				if (distance == 8)
				{
					Point midpoint = new Point((target.x + origin.x) / 2, (target.y + origin.y) / 2);	
					board.remove(midpoint);
					ArrayList<Point> possibleHops = getPossibleHops(target);
					//TODO: Improve logic so that it chooses the hop path with the most possible kills
					if (possibleHops.size() > 0 && !justCrowned)
					{
						return playMove(target, possibleHops.get(0));
					}
					else
					{
						return target;
					}
				}
				if (Math.min(turns % 2, 1) == 0)					
				{
					selectPawn(target);	
				}
				if (selectedPawn == target)
				{
					deselectPawn();
				}
				turns++;
				return target;
			}
		}
		
		return null;
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

			for (int x = -1; x < 2; x += 2)
			{
				for (int y = -1; y < 2; y += 2)
				{
					if (Math.min(turns % 2, 1) == Math.min(y, 0) + 1 || ((Pawn)board.get(target)).isKing())
					{
						Point midpoint = new Point(target.x + x, target.y + y);
						Point hop = new Point(target.x + (x * 2), target.y + (y * 2));
						if (board.containsKey(midpoint) && board.get(midpoint).getTeam() != Math.min(turns % 2, 1))
						{
							if (!board.containsKey(hop) && insideBoard(hop))
							{
								moves.add(hop);
							}
						}
						else if (!board.containsKey(midpoint) && insideBoard(midpoint))
						{
							moves.add(midpoint);
						}		
					}
				}
			}

		}
		return moves;
	}
	
	public ArrayList<Point> getPossibleHops(Point target)
	{
		ArrayList<Point> moves = new ArrayList<Point>();
		if (target != null && board.get(target) != null)
		{

			for (int x = -1; x < 2; x += 2)
			{
				for (int y = -1; y < 2; y += 2)
				{
					if (Math.min(turns % 2, 1) == Math.min(y, 0) + 1 || ((Pawn)board.get(target)).isKing())
					{
						Point midpoint = new Point(target.x + x, target.y + y);
						Point hop = new Point(target.x + (x * 2), target.y + (y * 2));
						if (board.containsKey(midpoint) && board.get(midpoint).getTeam() != Math.min(turns % 2, 1))
						{
							if (!board.containsKey(hop) && insideBoard(hop))
							{
								System.out.println("NEW HOP");
								System.out.println("og" + target.x + " " + target.y);
								System.out.println(hop.x + " " + hop.y);
								System.out.println("x" + x + " y" + y);
								System.out.println(Math.min(turns % 2, 1) + " vs y" + Math.min(y, 0) + 1);
								moves.add(hop);
							}
						}
					}
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

		Pawn target = (Pawn)board.get(tile);	
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
	

	public void deselectPawn()
	{
		selectedPawn = null;
	}
	
	public Pawn addPawn(Point tile, boolean player)
	{
		return (Pawn)board.put(tile, new Pawn(player));
	}

	public Pawn getPawn(Point tile)
	{
		Pawn target = (Pawn)board.get(tile);	
		if (target != null)
		{	
			return target;
		}

		return null;
	}
	
	public Pawn movePawn(Point origin, Point target)
	{
		Pawn targetPawn = (Pawn) board.remove(origin);
		board.put(target, targetPawn);
		return targetPawn;
	}
	
	public Pawn removePawn(Point tile)
	{
		return (Pawn) board.remove(tile);
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
	
	private boolean insideBoard(Point p)
	{
		if (p.x < boardSize && p.y < boardSize)
		{
			if (p.x >= 0 && p.y >= 0)
			{
				return true;
			}
		}
		
		return false;
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
					Pawn p = (Pawn) board.get(new Point(x,y));
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
