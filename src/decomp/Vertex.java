package decomp;

//
//
// Vertex
//
//

import java.awt.Point;
import java.util.Vector;

class Vertex 
{
	public int m_ID;
	protected Point m_Point;
	public boolean m_isNotch = false;
	public boolean m_isReference = false;

	public Vertex (int index, Point p) 
	{
		m_ID = index;
		m_Point = p;
	}

	public Point getPoint()
	{
		return m_Point;
	}

	public void setNotch()
	{
		m_isNotch = true;
		m_isReference = true;
	}
}

