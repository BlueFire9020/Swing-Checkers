
public abstract class Piece 
{
	private int team;
	
	public Piece()
	{
		team = 0;
	}
	
	public Piece(int team)
	{
		this.team = team;
	}
	
	public int getTeam()
	{
		return team;
	}
	
	public void setTeam(int team)
	{
		this.team = team;
	}
}
