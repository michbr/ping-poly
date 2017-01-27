package decomp;

//
//
// MD
//
//

import java.util.Vector;
import java.util.Enumeration;

class MD implements ICompare
{
	/* Vars */

	protected Vector m_Polys;
	int low,high;
	//double rAngle, lAngle;

	/* Constants */

	public static final int SORT_R_ANGLE = 1;
	public static final int SORT_L_ANGLE = 2;

	/* Methods */

	MD (int lowPrm, int highPrm, BaseTriangle bt)
	{
		m_Polys = new Vector();
		m_Polys.addElement(new ConvexPoly(bt));
		low = lowPrm;
		high = highPrm;
		//rAngle = CUtils.getAngleEx(bt.med, bt.high, bt.low);
		//lAngle = CUtils.getAngleEx(bt.med, bt.low, bt.high);
	}
	
	public Vector get_polys() {
		return m_Polys;
	}

	MD (int lowPrm, int highPrm, Vector thePolys)
	{
		low = lowPrm;
		high = highPrm;
		m_Polys = thePolys;
	}

	public MD getCopy ()
	{
		ConvexPoly cp;
		int n = m_Polys.size();
		Vector newPolys = new Vector(n);

		for (int i = 0; i < n; i++)
		{
			cp = ((ConvexPoly) m_Polys.elementAt(i)).getCopy();
			newPolys.addElement(cp);
		}

		return new MD(low, high, newPolys);
	}

	public String toString ()
	{
		return new String("MD of " + String.valueOf(low) + "," + String.valueOf(high));
	}
	
	public void print() {
		ConvexPoly cp;

		for (int i = 0; i < m_Polys.size(); i++) {
			ConvexPoly cur = (ConvexPoly)m_Polys.elementAt(i);
			System.out.println(cur);
		}
		//CUtils.Debug("Begin MD Dump");

		//for (Enumeration e = polys(); e.hasMoreElements();)
		//{
		//	cp = (ConvexPoly) e.nextElement();
		//	System.out.println(cp.toString());
		//}
	}

	public void dump ()
	{
		ConvexPoly cp;

		CUtils.Debug("Begin MD Dump");

		for (Enumeration e = polys(); e.hasMoreElements();)
		{
			cp = (ConvexPoly) e.nextElement();
			CUtils.Debug(cp.toString());
		}
		CUtils.Debug("End MD Dump");
	}

	public int size()
	{
		return m_Polys.size();
	}

	public Enumeration polys ()
	{
		return m_Polys.elements();
	}

	// returns the base convex polygon of this decomposition ( C(i,j) )
	public ConvexPoly getBasePoly()
	{
		ConvexPoly c;

		for (int i = 0; i < m_Polys.size(); i++)
		{
			c = (ConvexPoly) m_Polys.elementAt(i);
			if (c.isBasePoly(low, high))
				return c;
		}
		return null;
	}

	public double getLeftAngle ()
	{
		return getBasePoly().getLeftAngle();
	}

	public double getRightAngle ()
	{
		return getBasePoly().getRightAngle();
	}

	public void mergeTriangle (BaseTriangle bt)
	{
		CUtils.Debug("Merging Triangle " + bt.toString());
		CUtils.Debug("with poly " + getBasePoly().toString());
		ConvexPoly newPoly = new ConvexPoly(getBasePoly(), new ConvexPoly(bt));
		CUtils.Debug("result poly: " + newPoly.toString());
		m_Polys.removeElement(getBasePoly());
		m_Polys.addElement(newPoly);
		CUtils.Debug("Merging Triangle end");
	}

	public int compareTo (Object arg, int nPrm)
	{
		MD m = (MD) arg;

		if (SORT_R_ANGLE == nPrm)
		{
			return (int) (getRightAngle() - m.getRightAngle());
		}
		else
		{
			return (int) (getLeftAngle() - m.getLeftAngle());
		}
	}

}

