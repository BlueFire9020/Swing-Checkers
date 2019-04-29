
public class Pawn 
{	
	private boolean crowned;
	private int team;
	
	public Pawn()
	{
		crowned = false;
	}
	
	public Pawn(boolean playerPawn)
	{
		this();
		this.team = playerPawn == false ? 1 : 0;
	}
	
	public boolean isPlayerPawn()
	{
		return team == 1 ? false : true;
	}
	
	public int getTeam()
	{
		return team;
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
