
public class Pawn extends Piece
{	
	private boolean crowned;
	
	public Pawn()
	{
		crowned = false;
	}
	
	public Pawn(boolean playerPawn)
	{
		super(playerPawn == false ? 1 : 0);
		crowned = false;
	}
	
	public boolean isPlayerPawn()
	{
		return super.getTeam() == 1 ? false : true;
	}
	
	public void king()
	{
		crowned = true;
	}
	
	public boolean isKing()
	{
		return crowned;
	}
	
}
