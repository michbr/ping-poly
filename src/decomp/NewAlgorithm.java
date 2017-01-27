package decomp;

//
//
// Algorithm
//
//

import java.awt.Point;
import java.awt.Graphics;
import java.awt.Color;
import java.util.Vector;
import java.util.Enumeration;
import java.util.NoSuchElementException;

class NewAlgorithm implements Runnable
{
	private final int DELAY = 1000;

	private final int STATUS_NOTREADY = 0;
	private final int STATUS_READY = 1;
	private final int STATUS_PREPROCESSING = 2;
	private final int STATUS_DPPROC = 3;
	private final int STATUS_DONE = 4;

	private Front m_theApp;        // A reference to the applet
	private Thread m_Thread = null; // The algorithm's thread
	private boolean m_IsPaused = false;
	//private boolean m_Detail;
	private int m_Status = STATUS_NOTREADY;
	private boolean m_SingleStep = true;
	private int m_nDpStep = 0;

	private int m_CurrSubPoly = -1; // for painting
	private MD m_CurrMD;
	private BaseTriangle m_CurrBT;
	private VisPair m_CurrVP;

	private Vector m_Vertices;
	private int m_n;  // number of vertices
	
	private Vector m_BT[][]; // Base Trianlges
	
	private Vector m_XL[][]; // MD Sets
	private Vector m_XR[][];
	
	private Vector m_VisPairs; // Visibility pairs

//------------------------------------------------------------

	// Algorithm constructor - allocates and inits vars
	public NewAlgorithm (Front front, Vector thePoints)
	//     ~~~~~~~~~
	{
		int i,j;
		Vertex v;

		CUtils.Debug("Alg::Construct start");

		m_theApp = front;
		//m_Detail = detail;
//		CUtils.Debug(String.valueOf(m_Detail));
		m_n = thePoints.size();
		m_Vertices = new Vector(m_n);
		m_Vertices.setSize(m_n);
		
		m_BT = new Vector[m_n][m_n];
		m_XL = new Vector[m_n][m_n];
		m_XR = new Vector[m_n][m_n];
		for (i = 0; i < m_n; i++)
		{
			for (j = 0; j < m_n; j++)
			{
				m_BT[i][j] = new Vector();
				m_XL[i][j] = new Vector();
				m_XR[i][j] = new Vector();
			}
		}

		for (i = 0; i < m_n; i++)
		{
			v = new Vertex(i, (Point) thePoints.elementAt(i));
			m_Vertices.setElementAt(v, i);
		}

		CUtils.theVertices = m_Vertices;

		m_VisPairs = new Vector();

		m_Status = STATUS_NOTREADY;

		m_Thread = new Thread(this);
		m_Thread.start();

		CUtils.Debug("Alg::Construct end");
	}

	public MD get_solution() {
		return m_CurrMD;
	}

//------------------------------------------------------------

	// returns true if the edge (p1,p2) is suitable to be an edge
	// of a base triangle
	private boolean isTriangleEdge (int p1, int p2)
	//              ~~~~~~~~~~~~~~
	{
		// is (p1,p2) an original side of the polygon
		if ((p2-p1) == 1)
			return true;

		VisPair vp;
		for (int i=0; i < m_VisPairs.size(); i++)
		{
			vp = (VisPair) m_VisPairs.elementAt(i);
			// is (p1,p2) a base edge of a valid sub-polygon
			if ((p1 == vp.low) && (p2 == vp.high))
				return true;
		}

		return false;
	}

//------------------------------------------------------------

	private void doPreProcessing ()
	//           ~~~~~~~~~~~~~~~
	{
		int i,j,k,m;
		Vertex v, vPrev, vNext;
		VisPair vp;
		Vertex vI, vJ;
		double vAngle;

		CUtils.Debug("Alg::PreProcessing start");

		// Step 1: Find out which vertex is a notch.
		// mark notches and vertices 0,n-1 as "reference" vertices.
		for (i = 0; i < m_n; i++)
		{
			v = (Vertex) m_Vertices.elementAt(i);
			vPrev = (Vertex) m_Vertices.elementAt((i+m_n-1) % m_n);
			vNext = (Vertex) m_Vertices.elementAt((i+1) % m_n);

			vAngle = CUtils.getAngle(v.getPoint(), vNext.getPoint()) - CUtils.getAngle(v.getPoint(), vPrev.getPoint());
			if (vAngle < 0)
				vAngle += 360;
			
			CUtils.Debug(String.valueOf(vAngle));

			if (vAngle > 180)
				v.setNotch();

			if ((i == 0) || (i == m_n-1))
				v.m_isReference = true;
		}

		// Step 2: determine visibility pairs.
		CUtils.Debug("Visibility Pairs:");
		for (i = 0; i < m_n; i++)
		{
			for (j = i+2; j < m_n; j++)
			{
				vI = CUtils.getVertex(i);
				vJ = CUtils.getVertex(j);
				if ((vI.m_isReference || vJ.m_isReference) && (CUtils.isVisible(i,j, m_n)))
				{
					vp = new VisPair(i,j);
					CUtils.Debug(String.valueOf(i) + "," + String.valueOf(j));
					// add to list of visibility pairs
					m_VisPairs.addElement(vp);
					m_CurrVP = vp;
					sleep();
				}
			}
		}

		// Step 3: sort list of visibility pirs
		CUtils.sortVector(m_VisPairs, true, 0);

/*
		//Debug - print the sorted list
		CUtils.Debug("Sorted list:");
		VisPair vp1;
		for (int y=0; y < m_VisPairs.size(); y++)
		{
			vp1 = (VisPair) m_VisPairs.elementAt(y);
			CUtils.Debug(String.valueOf(vp1.low) + "," + String.valueOf(vp1.high));
		}
*/
		
		// Step 4: form base triangles sets
		CUtils.Debug("Base Triangles:");
		BaseTriangle bt;
		for (k = 0; k < m_VisPairs.size(); k++)
		{
			vp = (VisPair) m_VisPairs.elementAt(k);
			i = vp.low;
			j = vp.high;
			for (m = i+1; m < j; m++)
			{
				if (isTriangleEdge(i,m) && isTriangleEdge(m,j))
				{
					bt = new BaseTriangle(i,m,j);
					CUtils.Debug(String.valueOf(i) + "," + String.valueOf(m) + "," + String.valueOf(j));
					m_BT[i][j].addElement(bt);
				}
			}
		}

		CUtils.Debug("Alg::PreProcessing end");

	}

//------------------------------------------------------------

	// removes items for the set to have the RR (LR) property
	private void makeRepresentativeVector (Vector v, int nSortMode)
	{
		MD md;

		CUtils.Debug("RR Start");
		CUtils.sortVector(v, true, nSortMode);
		double tmpAngle, tmpAngle2;

		Enumeration e = v.elements();
		if (e.hasMoreElements())
		{
			md = (MD) e.nextElement();
			tmpAngle = (MD.SORT_R_ANGLE == nSortMode ? md.getLeftAngle() : md.getRightAngle());
			while (e.hasMoreElements())
			{
				md = (MD) e.nextElement();
				tmpAngle2 = (MD.SORT_R_ANGLE == nSortMode ? md.getLeftAngle() : md.getRightAngle());
				if (tmpAngle2 >= tmpAngle)
				{
					v.removeElement(md);
				}
				else // tmpAngle2 < tmpAngle
				{
					tmpAngle = tmpAngle2;
				}
			}
		}
		CUtils.Debug("RR End");
	}

//------------------------------------------------------------

	//
	private int updateMDSet (int i, int j, BaseTriangle bt, int M, boolean isSetXR)
	{
		Vector dstVect, srcLeft, srcRight;
		boolean bFound;
		Vector newPolys;
		MD A,B,newMD,tempMD;
		int newSize;
		int m = bt.med;

		CUtils.Debug("updateMDSet start");

		if (isSetXR)
		{
			dstVect = m_XR[i][j];
			srcLeft = m_XL[i][m];
			srcRight = m_XR[m][j];
		}
		else
		{
			dstVect = m_XL[i][j];
			srcLeft = m_XL[i][m]; // ?
			srcRight = m_XR[m][j]; // ?
		}

		/* The left side */
		CUtils.Debug("Left side");

		newPolys = new Vector();
		bFound = false;
		A = null;
		
		// A will be the MD of (i,m) with the minimal left angle 
		// such that T(imj) can merge left with C(A).
		for (int t = srcLeft.size()-1; (t >= 0) && !bFound; t--)
		{
			A = (MD) srcLeft.elementAt(t);
			bFound = (bt.canMergeLeft(A));
		}

		if (A != null)
		{
			//A = A.getCopy();  generates an internal Error 
			tempMD = A.getCopy();
			A = null;
			A = tempMD;
		}

		if (bFound) // merge bt with A
		{
			m_theApp.setStatus("Merged " + bt.toString() + " to " + A.getBasePoly().toString());
			A.mergeTriangle(bt);
			sleep();
		}
		else // add the triangle as a standalone convex polygon
		{
			newPolys.addElement(new ConvexPoly(bt));
		}

		// Select B to be any MD of P(m,j)
		try { 
			B = (MD) srcRight.firstElement();
			tempMD = B.getCopy();
			B = null;
			B = tempMD;
		}
		catch (NoSuchElementException e) { B = null; }
		
		// create the new MD 
		if (A != null)
		{
			for (Enumeration e = A.polys(); e.hasMoreElements();)
			{	
				newPolys.addElement(((ConvexPoly) e.nextElement()).getCopy());
			}
		}
		if (B != null)
		{
			for (Enumeration e = B.polys(); e.hasMoreElements();)
			{
				newPolys.addElement(((ConvexPoly) e.nextElement()).getCopy());
			}	
		}

		newMD = new MD(i, j, newPolys);
		newSize = newMD.size();
		if (newSize == M)
		{
			dstVect.addElement(newMD);
			m_CurrMD = newMD;
			sleep();
		}
		else if (newSize < M)
		{
			dstVect.removeAllElements();
			dstVect.addElement(newMD);
			m_CurrMD = newMD;
			M = newSize;
			sleep();
		}

		/* End of left side */

		/* The right side */
		CUtils.Debug("Right side");
		m_theApp.setStatus("Checking Merge-Right");

		newPolys = new Vector();
		bFound = false;
		B = null;
		
		// B will be the MD of (m,j) with the minimal right angle 
		// such that T(imj) can merge right with C(A).
		for (int t = srcRight.size()-1; (t >= 0) && !bFound; t--)
		{
			B = (MD) srcRight.elementAt(t);
			bFound = (bt.canMergeRight(B));
		}

		if (B != null)
		{
			tempMD = B.getCopy();
			B = null;
			B = tempMD;
		}

		if (bFound) // merge bt with B
		{
			m_theApp.setStatus("Merged " + bt.toString() + " to " + B.getBasePoly().toString());
			B.mergeTriangle(bt);
			sleep();
		}
		else // add the triangle as a standalone convex polygon
		{
			newPolys.addElement(new ConvexPoly(bt));
		}

		// Select A to be any MD of P(i,m)
		try { 
			A = (MD) srcLeft.firstElement(); 
			tempMD = A.getCopy();
			A = null;
			A = tempMD;
		}
		catch (NoSuchElementException e) { A = null; }
		
		// create the new MD 
		if (A != null)
		{
			for (Enumeration e = A.polys(); e.hasMoreElements();)
			{	
				newPolys.addElement(((ConvexPoly) e.nextElement()).getCopy());
			}
		}
		if (B != null)
		{
			for (Enumeration e = B.polys(); e.hasMoreElements();)
			{
				newPolys.addElement(((ConvexPoly) e.nextElement()).getCopy());
			}	
		}

		newMD = new MD(i, j, newPolys);
		newSize = newMD.size();
		if (newSize == M)
		{
			dstVect.addElement(newMD);
			m_CurrMD = newMD;
			sleep();
		}
		else if (newSize < M)
		{
			dstVect.removeAllElements();
			dstVect.addElement(newMD);
			m_CurrMD = newMD;
			M = newSize;
			sleep();
		}

		/* End of right side */

		CUtils.Debug("updateMDSet end");
		return M;
	}

//------------------------------------------------------------

	// return true if the step done was a major break-point
	// i.e. a stop in the non-detail run.
	private boolean doDpProcStep ()
	{
		return true;
	}

//------------------------------------------------------------

	private void doDpProc (int index)
	//           ~~~~~~~~
	{
		CUtils.Debug("Alg::DpProc start " + String.valueOf(index));

		BaseTriangle bt;
		int M;

		VisPair v = (VisPair) m_VisPairs.elementAt(index);
		int j = v.high;
		int i = v.low;
		CUtils.Debug("i=" + String.valueOf(i) + ",j=" + String.valueOf(j));

		if (2 == (j - i))
		{
			// there has to be exactly one triangle
			bt = (BaseTriangle) m_BT[i][j].firstElement();
			m_CurrBT = bt;
			MD md = new MD(i, j, bt);
			m_CurrMD = md;
			m_XR[i][j].addElement(md);
			m_XL[i][j].addElement(md);

			sleep();
		}
		else
		{
			// Create XR Set of MD's
			M = m_n;
			for (int k = 0; k < m_BT[i][j].size(); k++)
			{
				//CUtils.Debug("k=" + String.valueOf(k));
				bt = (BaseTriangle) m_BT[i][j].elementAt(k);
				m_CurrBT = bt;
				//CUtils.Debug("bt");
				M = updateMDSet(i, j, bt, M, true);
				sleep();
				//CUtils.Debug("M");
			}
			//CUtils.Debug("Before RR");
			makeRepresentativeVector(m_XR[i][j], MD.SORT_R_ANGLE);
			//CUtils.Debug("After RR");

			// Create XL Set of MD's
			M = m_n;
			for (int k = 0; k < m_BT[i][j].size(); k++)
			{
				//CUtils.Debug("k=" + String.valueOf(k));
				bt = (BaseTriangle) m_BT[i][j].elementAt(k);
				m_CurrBT = bt;
				//CUtils.Debug("bt");
				M = updateMDSet(i, j, bt, M, false);
				sleep();
				//CUtils.Debug("M");
			}
			//CUtils.Debug("Before LR");
			makeRepresentativeVector(m_XL[i][j], MD.SORT_L_ANGLE);
			//CUtils.Debug("After LR");
		}

		CUtils.Debug("Alg::DpProc end");
	}

//------------------------------------------------------------

	public void algRun ()
	//          ~~~~~~
	{
		if (m_Status == STATUS_NOTREADY)
			m_Status = STATUS_READY;
		m_SingleStep = false;
	}

//------------------------------------------------------------

	public void algStop ()
	//          ~~~~~~~
	{
		if (m_Thread != null)
		{
			m_Thread.stop();
			m_Thread = null;
			m_Status = STATUS_NOTREADY;
		}
	}

//------------------------------------------------------------

	public boolean isAlgPaused ()
	//             ~~~~~~~~~~~
	{
		return m_IsPaused;
	}

//------------------------------------------------------------

	public void algPause ()
	//          ~~~~~~~~
	{
		if (m_Thread != null)
		{
			m_IsPaused = true;
			m_Thread.suspend();
		}
	}

//------------------------------------------------------------

	public void algResume ()
	//          ~~~~~~~~~
	{
		if (m_Thread != null)
		{
			m_IsPaused = false;
			m_Thread.resume();
		}
	}

//------------------------------------------------------------

	public void algStep ()
	//          ~~~~~~~
	{
		if (m_Status == STATUS_NOTREADY)
			m_Status = STATUS_READY;
		m_SingleStep = true;
		if (m_IsPaused)
			algResume();
		doStep();
	}

//------------------------------------------------------------

	private void setAlgStatus (String sStatus)
	//           ~~~~~~~~~~~~
	{
		m_theApp.setStatus(sStatus);
	}

//------------------------------------------------------------

	private void sleep ()
	//           ~~~~~
	{
		sleep(false);
	}

//------------------------------------------------------------

	private void sleep (boolean bMajorStop)
	//           ~~~~~
	{
		if (bMajorStop)
		{
			//m_theApp.update(m_theApp.getGraphics());
			//m_theApp.repaint();
			try {m_Thread.sleep(DELAY);}
			catch (InterruptedException e) {}
		}
	}

//------------------------------------------------------------

	private void doStep ()
	//           ~~~~~~
	{
		switch (m_Status)
		{
		case STATUS_NOTREADY:
			break;
		case STATUS_READY:
			CUtils.Debug("Status = Ready");
			m_Status = STATUS_PREPROCESSING;
			// fall through !
		case STATUS_PREPROCESSING:
			CUtils.Debug("Status = PreProcessing");
			// PreProcessing
			doPreProcessing();
			m_Status = STATUS_DPPROC;
			m_nDpStep = 0;
			//if (m_Detail)
			//	break;
			// else fall through !
		case STATUS_DPPROC:
			CUtils.Debug("Status = DpProc");
			// DP Procedure
			doDpProc(m_nDpStep);
			m_CurrSubPoly = m_nDpStep;

			m_nDpStep++;
			if (m_nDpStep == m_VisPairs.size())
			{
				try {m_CurrMD = (MD) m_XR[0][m_n-1].firstElement();}
				catch (NoSuchElementException e) {}
				m_CurrBT = null;
				m_Status = STATUS_DONE;
			}
			if (m_Status != STATUS_DONE)
				break;
			// else fall through !
		case STATUS_DONE:
			CUtils.Debug("Status = Done");
			m_Thread = null;
			m_theApp.notifyAlgDone();
			break;
		}

//		m_theApp.repaint();

/*
			// PreProcessing
			doPreProcessing();
	
			// DP Procedure
			for (int i = 0; i < m_VisPairs.size(); i++)
			{
				doDpProc(i);
				m_CurrSubPoly = i;
				m_theApp.repaint();
				try { m_Thread.sleep(DELAY); }
				catch (InterruptedException e) {}
			}
*/
	}

//------------------------------------------------------------

	public void run ()
	//          ~~~
	{
		CUtils.Debug("Algorithm::run start");
		while (m_Status != STATUS_DONE) {
			//if (!m_SingleStep)
			//{
				CUtils.Debug("Algorithm::run step");
				doStep();
			//}
			sleep(true);
		}
		CUtils.Debug("Algorithm::run end");
	}
}


