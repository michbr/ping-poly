package decomp;

//
//
// Segment
//
//

import java.awt.Point;

class Segment 
{
	public Point pA, pB;

	Segment (Point p1, Point p2)
	{
		pA = p1;
		pB = p2;
	}

	public Point getLeft()
	{
		if (pA.x <= pB.x)
			return pA;
		else
			return pB;
	}

	public Point getRight()
	{
		if (pA.x <= pB.x)
			return pB;
		else
			return pA;
	}

	public Point getUp()
	{
		if (pA.y <= pB.y)
			return pA;
		else
			return pB;
	}

	public Point getDown()
	{
		if (pA.y <= pB.y)
			return pB;
		else
			return pA;
	}

	public boolean isVertical()
	{
		return (pA.x == pB.x);
	}

	public boolean isHorizontal()
	{
		return (pA.y == pB.y);
	}

	public float getSlope()
	{
		return (float) (getRight().y-getLeft().y) / (float) (getRight().x-getLeft().x);
	}

	public boolean isIntersecting(Segment theSegment)
	{
		float y,x;
		Point pC = theSegment.getLeft();
		Point pD = theSegment.getRight();

		// TODO: this is not right !
		if (isVertical() && theSegment.isVertical())
		{   
			if (getLeft().x == theSegment.getLeft().x)
			{
				if ((getUp().y >= theSegment.getUp().y) &&
					(getUp().y <= theSegment.getDown().y) ||
					(getDown().y >= theSegment.getUp().y) &&
					(getDown().y <= theSegment.getDown().y))
					return true;
				else
					return false;
			}
			else
				return false;
		}
		else if (isVertical())
		{
			x = getLeft().x;
			if ((x < theSegment.getLeft().x) || (x > theSegment.getRight().x))
				return false;
			y = theSegment.getLeft().y+theSegment.getSlope()*(getLeft().x-theSegment.getLeft().x);
			return (y >= getUp().y) && (y <= getDown().y);
		}
		else if (theSegment.isVertical())
		{
			x = theSegment.getLeft().x;
			if ((x < getLeft().x) || (x > getRight().x))
				return false;
			y = getLeft().y+getSlope()*(theSegment.getLeft().x-getLeft().x);
			return (y >= theSegment.getUp().y) && (y <= theSegment.getDown().y);
		}

		float nTemp1 = (pC.y-getLeft().y) + getLeft().x*getSlope() - pC.x*theSegment.getSlope();
		float nTemp2 = getSlope() - theSegment.getSlope();
		float nTemp3 = nTemp1 / nTemp2;  // The X coord of the intersection

		if ((nTemp3 >= getLeft().x) && (nTemp3 <= getRight().x) && (nTemp3 >= pC.x) && (nTemp3 <= pD.x))
			return true;
		
		return false;
	}
}
