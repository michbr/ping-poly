package decomp;
//
//
// CUtils
//
//

import java.util.Vector;
import java.awt.Point;
import java.awt.Color;

class CUtils 
{

	public static Vector theVertices;
	//private static Debugger Dbg;

	/*public static void setDebugger (Debugger d)
	{
		//Dbg = d;
		//Dbg.show();
		//Dbg.reshape(500, 150, 200, 300);
	}*/

	public static void Debug (String msg)
	{
		//Dbg.Print(msg);
	}

	public static Vertex getVertex (int i)
	{
		return (Vertex) theVertices.elementAt(i);
	}

	public static Point getPoint (int i)
	{
		return getVertex(i).getPoint();
	}

	public static Color getPolyColor (int low, int high, int nSize)
	{
		int type;
		int r,g,b;
		int n;
		Color c;

/*
		type = (low % 8);
		r = (type & 1) * 255;
		g = (type & 2) * 255;
		b = (type & 4) * 255;
*/

		r = (low * 16) % 256;
		g = (nSize * 16) % 256;
		b = (high * 16) % 256;

		c = new Color(r,g,b);
		for (int i = 0; i < (low+high); i++)
			c.brighter();
		return c;
	}

	public static boolean isVisible (int i, int j, int nSize)
	{
		double aEx1,aEx2;
		Segment s,st;
		int temp1, temp2;

		// is the line (i,j) inside the polygon
		temp1 = (i-1+nSize) % nSize;
		temp2 = (i+1) % nSize;
		aEx1 = getAngleEx(temp1, i, temp2);
		aEx2 = getAngleEx(temp1, i, j);
		CUtils.Debug("aEx1 = " + String.valueOf(aEx1));
		CUtils.Debug("aEx2 = " + String.valueOf(aEx2));
		boolean bVisible = (aEx1 > aEx2);
		if (!bVisible)
			return false;

		// is the line intersecting another line (i.e. - not visible)
		s = new Segment(CUtils.getPoint(i), CUtils.getPoint(j));
		int k2;
		for (int k = 0; k < nSize; k++)
		{
			k2 = (k+1) % nSize;
			if ((k != i) && (k != j) && (k2 != i) && (k2 != j))
			{
				st = new Segment(CUtils.getPoint(k), CUtils.getPoint(k2));
				if (s.isIntersecting(st))
				{
					CUtils.Debug("Intersection: (" + String.valueOf(i) + "," + String.valueOf(j) + "),(" + String.valueOf(k) + "," + String.valueOf(k2) + ")");
					//bVisible = false;
					return false;
				}
			}
		}
		//CUtils.Debug("Is Visible: " + String.valueOf(i) + "," + String.valueOf(j) + " - " + String.valueOf(bVisible));
		//return bVisible;
		return true;
	}

	// good old pitagoras
	public static double getDistance(Point pSrc, Point pDst)
	{
		return Math.sqrt(Math.pow((pDst.x - pSrc.x),2) + Math.pow((pDst.y - pSrc.y),2));
	}

	// returns the angle in degrees.
	public static double getAngle (Point pSrc, Point pDst)
	{
		double dist = getDistance(pSrc, pDst);
		//CUtils.Debug("Dist=" + String.valueOf(dist));
		double width  = (pDst.x - pSrc.x);
		//CUtils.Debug("Width=" + String.valueOf(width));
		double rads = Math.acos(width / dist); // angle in radians
		if (pSrc.y < pDst.y)
			rads = 2*Math.PI - rads;
		return rads/Math.PI*180; // translate to degrees
	}

	// returns the angle in degrees between (i,j) and (j,k)
	// TODO: arrange the sequence i,j,k in a clockwise order.
	public static double getAngleEx (int i, int j, int k)
	{
		double a1, a2;
		int nTemp;
		Point pI, pJ, pK;

		if ((i < j) && (k < j))
		{
			if (i < k)
			{
				nTemp = i;
				i = k;
				k = nTemp;
			}
		}
		else if ((i > j) && (k > j))
		{
			if (i < k)
			{
				nTemp = i;
				i = k;
				k = nTemp;
			}
		}
		else if (i > j)
		{
			nTemp = i;
			i = k;
			k = nTemp;
		}

		pI = getPoint(i);
		pJ = getPoint(j);
		pK = getPoint(k);

		a1 = getAngle(pJ, pI);
		a2 = getAngle(pJ, pK);
		//CUtils.Debug("Angle between " + String.valueOf(j) + " and " + String.valueOf(i) + " is: " + String.valueOf(a1));
		//CUtils.Debug("Angle between " + String.valueOf(j) + " and " + String.valueOf(k) + " is: " + String.valueOf(a2));
		//CUtils.Debug("AngleEx returns " + String.valueOf((a2-a1+360) % 360));

		return (a2-a1+360) % 360;
	}

	// Quick sort algorithm
	private static void sortQuick (Vector v, int lb, int ub, boolean bAscending, int nPrm)
	{
		ICompare pPivot;
		Object pTemp;
		int nCmp, i, j;
		boolean bStop, bUntil;

		if (lb < ub)
		{
			int nRnd = lb;
			pPivot = (ICompare) v.elementAt(nRnd);
			i = lb-1;
			j = ub+1;
			bStop = false;
			while (!bStop)
			{
				do
				{
					j = j - 1;
					nCmp = ((ICompare) v.elementAt(j)).compareTo(pPivot, nPrm);
					bUntil = (bAscending && (nCmp <= 0)) || (!bAscending && (nCmp >= 0));
				}
				while (!bUntil);

				do
				{
					i = i + 1;
					nCmp = ((ICompare) v.elementAt(i)).compareTo(pPivot, nPrm);
					bUntil = (bAscending && (nCmp >= 0)) || (!bAscending && (nCmp <= 0));
				}
				while (!bUntil);

				if (i < j)
				{
					pTemp = v.elementAt(i);
					v.setElementAt(v.elementAt(j), i);
					v.setElementAt(pTemp, j);
				}
				else 
					bStop = true;
			}

			sortQuick(v, lb , j , bAscending, nPrm);
			sortQuick(v, j+1, ub, bAscending, nPrm);
		}
	}

	// a method for sorting a vector of objects that implement
	// the interface "ICompare".
	public static void sortVector (Vector v, boolean bAscending, int nPrm)
	{
		sortQuick(v, 0, v.size()-1, bAscending, nPrm);
	}

	public static void sortTrivialVector (Vector vectSource, boolean bAscending, int nPrm)
	{
		int i,j,best;
		ICompare pI, pJ, pBest;
		boolean bSwitch;

		Vector v = vectSource;

		CUtils.Debug("sortVector start");
		//debugVector(v);

		if (v.isEmpty() || (v.size() < 2))
		{
			CUtils.Debug("sortVector early quit");
			return;
		}

		for (i = 0; i < (v.size() - 1); i++) {
			pI = (ICompare) v.elementAt(i);
			best = i;
			pBest = pI;
			for (j = i+1; j < v.size(); j++) {
				pJ = (ICompare) v.elementAt(j);
				bSwitch = false;
				if (bAscending) {
					bSwitch = (pJ.compareTo(pBest, nPrm) < 0);
				}
				else {
					bSwitch = (pJ.compareTo(pBest, nPrm) > 0);
				}
				if (bSwitch) {
					best = j;
					pBest = pJ;
				}

			}
			pJ = pI;
			v.setElementAt(pBest, i);
			v.setElementAt(pJ, best);
		}
		CUtils.Debug("sortVector end");
	}

	public static void debugVector (Vector v)
	{
		Object o;

		Debug("Start Vector Dump");
		Debug("Size = " + String.valueOf(v.size()));
		for (int i = 0; i < v.size(); i++)
		{
			o = v.elementAt(i);
			if (o == null)
				Debug("Null at item " + String.valueOf(i));
			else
				if (o instanceof MD)
					((MD) o).dump();
				else
					Debug(o.toString());
		}
		Debug("End Vector Dump");
	}
}

