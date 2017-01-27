package decomp;

import java.awt.Graphics;



//
//
// BaseTriangle
//
//

class BaseTriangle 
{
	int low, high, med;

	BaseTriangle (int i, int m, int j)
	{
		low = i;
		high = j;
		med = m;
	}

	public String toString ()
	{
		return "(" + String.valueOf(low) + "," + String.valueOf(med) + "," + String.valueOf(high) + ")";
	}

	public boolean canMergeLeft (MD md)
	{
		double a1,a2;
		ConvexPoly base = md.getBasePoly();

		a1 = CUtils.getAngleEx(base.getLeftEdgeIndex(), low, high);
		a2 = CUtils.getAngleEx(base.getRightEdgeIndex(), med, high);
		return (a1 < 180) && (a2 < 180);
	}

	public boolean canMergeRight (MD md)
	{
		double a1,a2;
		ConvexPoly base = md.getBasePoly();

		a1 = CUtils.getAngleEx(base.getLeftEdgeIndex(), med, low);
		a2 = CUtils.getAngleEx(base.getRightEdgeIndex(), high, low);
		return (a1 < 180) && (a2 < 180);
	}
}

