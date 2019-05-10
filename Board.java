import java.awt.Point;
import java.util.HashMap;

public abstract class Board 
{
	protected int boardSize;
	protected int turns;
	protected HashMap<Point, Piece> board;
		
	public abstract void setupBoard();
	
	public abstract void resetBoard();
	
	public HashMap<Point, Piece> getBoard()
	{
		return board;
	}
	
	public int getBoardSize()
	{
		return boardSize;
	}
	
	public void setBoardSize(int boardSize)
	{
		if (boardSize > 0)
		{
			this.boardSize = boardSize;
		}
	}	
	
	public int getTurns()
	{
		return turns;
	}
	
	public void setTurns(int value)
	{
		turns = Math.max(0, turns + value);
	}
	
	public void passTurn()
	{
		turns++;
	}
}
