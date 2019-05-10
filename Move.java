import java.awt.Point;
import java.util.ArrayList;

public class Move 
{
	//TODO: Make it so it isn't required to have Points be in pixels in order to interpolate, this shouldn't have to be required to use the class properly
	//FIXED THE ABOVE TODO :D
	private ArrayList<Point> points;
	private Point lerp;
	private int lerpIndex;
	private Pawn pawn;

	//TODO: Ensure implementation for this actually works properly
	private static double interpolationSpeed;

	/*
	 * Constructor for a Move which only has two points.
	 * @param pawn The Pawn object being moved.
	 * @param origin Point object representing the move's starting point.
	 * @param target Point object representing the end point of the move.
	 */
	public Move(Pawn pawn, Point origin, Point target)
	{
		points = new ArrayList<Point>();
		points.add(origin);
		points.add(target);
		this.pawn = pawn;
		lerp = origin;
	}

	/*
	 * Constructor for a Move which only has two points.
	 * @param pawn The Pawn object being moved.
	 * @param points ArrayList of Point objects which represents the path of the move.
	 */
	public Move(Pawn pawn, ArrayList<Point> points)
	{
		this.points = points;
		this.pawn = pawn;
		lerp = points.get(0);
	}

	/*
	 * Determines if the Move can still interpolate. Interpolation 
	 * is possible while the lerp Point has not successfully reached 
	 * all Point objects in the Move.
	 * @return A boolean representing if the Move object can continue interpolation.
	 */
	public boolean canInterpolate()
	{
		if (lerp.equals(points.get(points.size() - 1)))
		{
			return false;
		}

		return true;
	}

	/*
	 * Determines if this move is a 'hop' move in Checkers, or a move 
	 * in which the piece hops over another, killing it. This is
	 * calculated using a distance formula so hops longer than
	 * regulation will not return as a legal hop.
	 * @return A boolean representing whether or not this Move object is a 'hop' move.
	 */
	public boolean isHop()
	{
		int distance = (int)(Math.pow(points.get(points.size() - 1).x - points.get(0).x, 2) + Math.pow(points.get(points.size() - 1).y - points.get(0).y, 2));
		return (distance == 8) ? true : false;
	}

	/*
	 * This method interpolates the lerp Point between each Point 
	 * in this Move's list of Points, effectively traversing the 
	 * entire list in order. The lerp point will move in the direction
	 * of the next Point in the Move's Point list until it runs out of
	 * points, upon which this method will do nothing, hence the existence
	 * of the {@code canInterpolate()} method to ensure against redundant
	 * calling.
	 */
	public void interpolate()
	{
		//TODO: Ensure that the speed is even regardless of distance
		if (lerpIndex < points.size())
		{
			int xDir = (points.get(lerpIndex).x - lerp.x);
			int yDir = (points.get(lerpIndex).y - lerp.y);

			if (xDir == 0 && yDir == 0)
			{
				lerpIndex++;
			}
			else
			{
				xDir = xDir / Math.abs(xDir);
				yDir = yDir / Math.abs(yDir);

				lerp.x += xDir;
				lerp.y += yDir;
			}	
		}

	}

	/*
	 * @return A double representing the multiplier
	 * that is applied to all interpolation method calls
	 * run on every Move object.
	 */
	public static double getInterpolationSpeed()
	{
		return interpolationSpeed;
	}

	/*
	 * @param A double representing the new interpolation
	 * multiplier to be used when all Move objects interpolate.
	 */
	private static void setInterpolationSpeed(double newSpeed)
	{
		interpolationSpeed = newSpeed;
	}

	/*
	 * @param A list of Point objects to 
	 * replace the current Move's Point list.
	 * It should be noted that using this method
	 * will reset the current lerp of the Move to 
	 * the beginning, effectively restarting the move.
	 */
	public void setPoints(ArrayList<Point> newPoints)
	{
		points = newPoints;
		lerp = newPoints.get(0);
	}

	/*
	 * @return The Pawn object this move is being applied to.
	 */
	public Pawn getPawn()
	{
		return pawn;
	}

	/*
	 * @return A list of Point objects which represent the path of
	 * this Move.
	 */
	public ArrayList<Point> getPoints()
	{
		return points;
	}

	/*
	 * @return The Point object found at the absolute beginning
	 * of the Point list used for this Move.
	 */
	public Point getOrigin()
	{
		return points.get(0);
	}

	/*
	 * @return The Point object located at the final index of the Point
	 * list used for this Move, effectively the target position for the
	 * Pawn by the end of interpolation.
	 */
	public Point getTarget()
	{
		return points.get(points.size() - 1);
	}

	/*
	 * @return A Point which represents the current position of the
	 * interpolating Point object, also known as the lerp Point. This
	 * is the Point which travels between each Point object in the Move.
	 */
	public Point getInterpolation()
	{
		return lerp;
	}
}
