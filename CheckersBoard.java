import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

public class CheckersBoard extends Board
{
	private Point selectedPawn;
	private boolean justHopped;
	
	/*
	 * Constructor for the CheckersBoard object, which 
	 * intializes the board to the specified {@code boardSize}. 
	 * 
	 * @param An integer value representing the size of the board.
	 */
	public CheckersBoard(int boardSize)
	{
		this.boardSize = boardSize;
		setupBoard();
	}

	/*
	 * Sets up the board for this game by clearing it,
	 * setting the turn counter to zero, and placing both
	 * black and white pawns on the board in their respective
	 * positions.
	 */
	public void setupBoard()
	{
		//initialize board
		board = new HashMap<Point, Piece>();

		//reset turns
		setTurns(0);

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
		
		justHopped = false;

	}

	/*
	 * Resets the board for this game by clearing it,
	 * setting the turn counter to zero, and placing both
	 * black and white pawns on the board in their respective
	 * positions.
	 */
	public void resetBoard()
	{
		setupBoard();
	}

	/*
	 * @return A Move representative of the calculated computer Move 
	 * based on current piece positions. The optimal move is currently calculated
	 * by prioritizing kill moves. 
	 */
	public Move calculateComputerMove()
	{
		//TODO: add algorithm to the computer (make it so it makes all hops first, then moves sequentially?)
		//computer is playing, since we don't have an algorithm just play randomly.
		//TODO: Determine number of pieces killed for each hop and make computer difficulties accordingly
		ArrayList<Point> possibleMoves = new ArrayList<Point>();
		ArrayList<Point> pawns = getPointsForTeam(Math.min(turns % 2, 1));
		
		//TODO: This is a bad way of doing it, two for each loops is inefficient. Refactor later!
		for (Point point : pawns)
		{
			//check if any kill moves exist, and play those first
			ArrayList<Point> possibleHops = getPossibleHops(point);
			ArrayList<Point> moveChain = new ArrayList<Point>();
			
			//add the starting point as the origin of the move
			moveChain.add(point);
			
			while (possibleHops.size() > 0)
			{
				moveChain.add(possibleHops.get(0));
				possibleHops = getPossibleHops(possibleHops.get(0));
			}
			
			if (moveChain.size() > 1)
			{
				System.out.println("chain size:" + moveChain.size());
				System.out.println(moveChain.get(0) + " versus " + point);
				return new Move(getPawn(point), moveChain);
			}
		}
		for (Point point : pawns)
		{
			possibleMoves = getPossibleMoves(point);
			if (possibleMoves.size() > 0)
			{
				Point move = possibleMoves.get((int)(Math.random() * possibleMoves.size()));
				return new Move(getPawn(point), point, move);
			}
		}

		return null;
	}
	
	/*
	 * Execute the provided Move object, given it 
	 * is valid, legal, and the correct turn for the piece
	 * being moved. 
	 * 
	 * @param A Move object representing the desired move.
	 * @return A Point representing the final move made, also known as
	 * the new reference key for the Pawn moved in this method.
	 */
	public Point playMove(Move move)
	{
		boolean justCrowned = false;

		Pawn targetPawn = move.getPawn();
		ArrayList<Point> points = move.getPoints();
		
		//already checked to generate the move, redundant?
		//ArrayList<Point> legalMoves = getPossibleMoves(origin);
		
		if (targetPawn != null && targetPawn.getTeam() == Math.min(turns % 2, 1))
		{
			for (int i = 1; i < move.getPoints().size(); i++)
			{
				Point previous = points.get(i - 1);
				Point target = points.get(i);
				
				Point midpoint = new Point((target.x + previous.x) / 2, (target.y + previous.y) / 2);	
				ArrayList<Point> legalHops = getPossibleHops(previous);
				
				if (legalHops.contains(target))
				{
					justHopped = true;
					board.remove(midpoint);	
				}		
				if (target.y == (boardSize - 1) * Math.min(turns % 2, 1))
				{
					targetPawn.king();
					justCrowned = true;
					break;
				}
			}
			targetPawn = movePawn(points.get(0), points.get(points.size() - 1));
		}
		if (getPossibleHops(points.get(points.size() - 1)).size() == 0 || justHopped == false)
		{
			justHopped = false;
			passTurn();	
		}
		
		return points.get(points.size() - 1);
	}

	/*public Point playMove(Point origin, Point target)
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
				System.out.println("WE'RE SETTING A PAWN TO NULL HERE @PLAYMOVE P2P");
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
				System.out.println("WE'RE SETTING A PAWN TO NULL HERE @PLAYMOVE P2P");
				movePawn(origin, target);
			}	
		}
		turns++;
		return target;
	}

	public Point playMove2(Point origin, Point target)
	{
		System.out.println("MOVING " + turns);
		System.out.println("PIECE FOR TEAM " + board.get(origin).getTeam());
		System.out.println("TRANSFER FROM x" + origin.x + " y" + origin.y + " TO x" + target.x + " y" + target.y);
		System.out.println("---------------");
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
						System.out.println("WE'RE SETTING A PAWN TO NULL HERE @PLAYMOVE2");
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
	}*/

	/*
	 * Shell method which mimics the {@code getPossibleMoves()} method,
	 * except automatically defaulting to the selectedPawn.
	 * 
	 * @return A list of Points indicative of the possible moves that the 
	 * selected Pawn can make.
	 * @see getPossibleMoves()
	 */
	public ArrayList<Point> getPossibleSelectedMoves()
	{
		if (justHopped)
		{
			return getPossibleHops(selectedPawn);
		}
		else
		{
			return getPossibleMoves(selectedPawn);	
		}
	}

	/*
	 * Returns all possible and legal moves for the provided Point,
	 * based on the current turn, state of the Pawn (king, etc), and 
	 * presence of other nearby pawns. 
	 * 
	 * This will return both 'hop' moves and standard moves. 
	 * 
	 * @param A Point object representing the Point to calculate possible
	 * moves from.
	 * 
	 * @return A list of Point objects representing the possible moves the 
	 * Pawn at the provided Point can make.
	 */
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
								moves.addAll(getPossibleHops(hop));
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

	/*
	 * Returns all possible and legal 'hop' moves for the provided Point,
	 * based on the current turn, state of the Pawn (king, etc), and 
	 * presence of other nearby pawns. 
	 * 
	 * This will return only 'hop' moves, and not standard moves.
	 * 
	 * @see getPossibleMoves() for the all-inclusive version of this method.
	 * 
	 * @param A Point object representing the Point to calculate possible
	 * moves from.
	 * 
	 * @return A list of Point objects representing the possible moves the 
	 * Pawn at the provided Point can make.
	 */
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
								moves.add(hop);
							}
						}
					}
				}
			}

		}
		return moves;
	}
	
	/*
	 * Determines how many Pawns the provided team has left.
	 * Valid teams are 0 and 1, with 0 being the default player,
	 * respectively. 
	 * 
	 * @param An integer value representing the team to check
	 * Pawns for.
	 * 
	 * @return A list of Point objects representing the locations
	 * of all the Pawns the provided team possesses.
	 */
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

	/*
	 * Selects the pawn at the provided Point, assuming there
	 * is a Pawn there and the Pawn is a player Pawn. 
	 * 
	 * @param A Point object to attempt selection of.
	 * 
	 * @return A Pawn object located at the point provided, null 
	 * if the Pawn is not the player's or doesn't exist.
	 */
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

	/*
	 * Shell method which returns the Point location
	 * of the player's currently selected Pawn, if not
	 * null.
	 * 
	 * @return A Point value representing the location of
	 * the player's currently selected Pawn.
	 */
	public Point getSelectedPoint()
	{
		return selectedPawn;
	}

	/*
	 * Deselect the currently selected pawn, if one is selected.
	 * If no pawns are selected by the player this method does nothing.
	 */
	public void deselectPawn()
	{
		selectedPawn = null;
	}

	/*
	 * Place a pawn on the board, removing any existing pawns
	 * at that location. 
	 * 
	 * @param A Point representing where to place the Pawn.
	 * @param A boolean value representing if the pawn belongs
	 * to the player or not.
	 * 
	 * @return A Pawn representing the new Pawn created at the
	 * providing location with the provided team. 
	 */
	public Pawn addPawn(Point tile, boolean player)
	{
		if (board.containsKey(tile))
		{
			board.remove(tile);
		}
		return (Pawn)board.put(tile, new Pawn(player));
	}

	/*
	 * Returns the Pawn located at the provided tile, if there
	 * is one there. If there isn't a Pawn at the provided tile
	 * this method returns null.
	 * 
	 * @param The Point to get a Pawn object from.
	 * 
	 * @return The Pawn at the point, if it exists.
	 */
	public Pawn getPawn(Point tile)
	{
		Pawn target = (Pawn)board.get(tile);	
		if (target != null)
		{	
			return target;
		}

		return null;
	}

	/*
	 * Move a Pawn from one Point to another.
	 * 
	 * @param The Point at which the Pawn is currently located.
	 * @param The Point that the Pawn will move to.
	 * 
	 * @return A Pawn object representing the now-moved Pawn reference.
	 */
	public Pawn movePawn(Point origin, Point target)
	{
		Pawn targetPawn = (Pawn) board.remove(origin);
		board.put(target, targetPawn);
		return targetPawn;
	}

	/*
	 * Remove a pawn from the board, if a Pawn exists there.
	 * 
	 * @param The Point to attempt removing a Pawn at.
	 * 
	 * @return The Pawn that was removed, if removal was
	 * successful. 
	 */
	public Pawn removePawn(Point tile)
	{
		return (Pawn) board.remove(tile);
	}

	/*
	 * Return the current Point locations of every piece 
	 * on the board.
	 * 
	 * @return A list of Point objects representing every point
	 * currently on the board as of the method call.
	 */
	public ArrayList<Point> getPiecePoints()
	{
		return new ArrayList<Point>(board.keySet());
	}

	/*
	 * Returns the size of the current board. The board
	 * is always square, so width will never not equal 
	 * height and vice versa.
	 * 
	 * @return An integer representing the size of the current board.
	 */
	public int getBoardSize()
	{
		return boardSize;
	}

	/*
	 * Returns the number of turns that have passed so far in 
	 * the game. This will be always be greater than zero.
	 * 
	 * @return An integer representing the number of turns passed.
	 */
	public int getTurns()
	{
		return turns;
	}

	/*
	 * Check if the provided Point is legally inside of the board. 
	 * Used to prevent out-of-bounds movement or any improper
	 * Point math. 
	 * 
	 * @param A Point to check whether or not it is inside the board.
	 * 
	 * @return A boolean representing if the Point is inside of the 
	 * board bounds.
	 */
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

	/*
	 * Returns a String representation of the board,
	 * for use in console output and debugging.
	 * 
	 * @return A String which represents the current state 
	 * of the board in String form.
	 */
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
