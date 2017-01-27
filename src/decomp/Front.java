package decomp;

import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.*;
import parser.Point;

public class Front {
	
	String status;
	Vector m_Points;
	ArrayList<ArrayList<Point>> result;
	private final int RADIUS_TO_CLOSE = 10;
	
	public Front () {
		m_Points = new Vector();
		result = new ArrayList<ArrayList<Point>>();
	}
	
	private void print_points() {
		for (int i = 0; i < result.size(); i++) {
			for (int j = 0; j < result.get(i).size(); j++) {
				System.out.println(result.get(i).get(j));
			}
			System.out.println();
		//	System.out.println(m_Points.elementAt(i));
		}
		//for (int i = 0)
	}
	
	public void add(int x, int y)	{

		boolean isIntersecting = isIntersectionFormed(new java.awt.Point(x,y));

		if (!isIntersecting) {
			System.out.println("adding point");
			m_Points.addElement(new java.awt.Point(x,y));
		}
	}
	
	public void run() {
		setPolyClockwise();
		NewAlgorithm blah = new NewAlgorithm(this, m_Points);
		
		blah.algRun();
		while (status == null || !status.equals("All done !")) {
			try {
				Thread.sleep(500);
			}
			catch (Exception e) {
				
			}
		}
		MD solution = blah.get_solution();
		Vector polys = solution.get_polys();
		for (int i = 0; i < polys.size(); i ++) {
			Vector vertices = ((ConvexPoly)polys.elementAt(i)).get_vertices();
			result.add(new ArrayList<Point>());
			for (int j = 0; j < vertices.size(); j++) {
				//System.out.println(m_Points.elementAt(  (Integer)vertices.elementAt(j)));
				//System.out.print(" ");
				java.awt.Point p = (java.awt.Point)m_Points.elementAt(  (Integer)vertices.elementAt(j));
				//System.out.println(p);
				result.get(i).add(new Point(p.x, p.y));
			}
			//System.out.println();
		//	System.out.println( (ConvexPoly)(polys.elementAt(i)) );
		}
		//print_points();
		//solution.print();
	}
	
	public ArrayList<ArrayList<Point>> get_results() {
		return result;
	}
	
	public void setStatus(String stat) {
		status = stat;
	}
	
	public void notifyAlgDone ()
	{
		setStatus("All done !");
	}
	
	private void setPolyClockwise ()
	{
		java.awt.Point pTemp, pBest;
		int i = 1, j, nBest;
		int nSize = m_Points.size();

		nBest = 0;
		pBest = (java.awt.Point) m_Points.elementAt(0);
		for (i = 1; i < nSize; i++)
		{
			pTemp = (java.awt.Point) m_Points.elementAt(i);
			if ( (pTemp.y > pBest.y) || ((pTemp.y == pBest.y) && (pTemp.x < pBest.x)) )
			{
				nBest = i;
				pBest = pTemp;
			}
		}

		j = (nBest == 0) ? nSize-1 : nBest-1 ;
		java.awt.Point pPrev = (java.awt.Point) m_Points.elementAt(j);

		j = (nBest == nSize-1) ? 0 : nBest+1 ;
		java.awt.Point pNext = (java.awt.Point) m_Points.elementAt(j);

		double a1 = CUtils.getAngle(pBest, pPrev);
		double a2 = CUtils.getAngle(pBest, pNext);
		if (a1 > a2)
		{
			Vector vTemp = new Vector(nSize);
			for (i = nSize-1; i >= 0; i--)
				vTemp.addElement(m_Points.elementAt(i));
			m_Points = vTemp;
		}
	}
	
	private boolean isPointClosingPolygon (java.awt.Point pNew) {
		if (m_Points.isEmpty())
			return false;

		java.awt.Point p = (java.awt.Point) m_Points.firstElement();

		if ((Math.abs(p.x-pNew.x) < RADIUS_TO_CLOSE) && (Math.abs(p.y-pNew.y) < RADIUS_TO_CLOSE))
			return true;

		return false;
	}
	
		private boolean isIntersectionFormed (java.awt.Point pNew)
	{
		Segment currSegment, lastSegment;
		boolean bFound = false;
		
		if (3 > m_Points.size())
			return false;

		lastSegment = new Segment(
							(java.awt.Point) m_Points.lastElement(),
							pNew );

		for (int i = 1; !bFound && (i < m_Points.size()-1); i++)
		{
			currSegment = new Segment(
								(java.awt.Point) m_Points.elementAt(i-1),
								(java.awt.Point) m_Points.elementAt(i) );
			bFound = lastSegment.isIntersecting(currSegment);
		}
		return bFound;
	}

	public static void main(String [] args) {
		Front test = new Front();
		test.add(10,10);
		test.add(10,50);
		test.add(30,40);
		test.add(50,50);
		test.add(50,10);

		test.run();
	}
}