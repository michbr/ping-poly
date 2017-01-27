package decomp;
//
//
// ConvexPoly - holds a vector of vertex indexes sorted ascendingly
//
//

import java.util.Vector;
import java.util.Enumeration;

class ConvexPoly implements Cloneable
{
	protected Vector m_VertIDs; // the indexes of this poly's vertices

	// creates a new poly with the passed index ids
	ConvexPoly (Vector theIDs)
	{
		m_VertIDs = theIDs;
	}
	// creates a new poly from a triangle
	ConvexPoly (BaseTriangle bt)
	{
		m_VertIDs = new Vector(3);
		m_VertIDs.addElement(new Integer(bt.low));
		m_VertIDs.addElement(new Integer(bt.med));
		m_VertIDs.addElement(new Integer(bt.high));
	}

	// creates a new poly which is a merge of the 2 passed polys
	ConvexPoly (ConvexPoly c1, ConvexPoly c2)
	{
		Enumeration e1 = c1.indexes();
		Enumeration e2 = c2.indexes();
		int i1, i2;
		m_VertIDs = new Vector();

		i1 = -1;
		i2 = -1;
		while (e1.hasMoreElements() && e2.hasMoreElements()) 
		{
			if (-1 == i1)
				i1 = ((Integer) e1.nextElement()).intValue();
			if (-1 == i2)
				i2 = ((Integer) e2.nextElement()).intValue();

			if (i1 < i2)
			{
				m_VertIDs.addElement(new Integer(i1));
				i1 = -1;
			}
			else if (i1 == i2)
			{
				m_VertIDs.addElement(new Integer(i1));
				i1 = -1;
				i2 = -1;
			}
			else // i1 > i2
			{
				m_VertIDs.addElement(new Integer(i2));
				i2 = -1;
			}
		}

		while (e1.hasMoreElements())
		{
			i1 = ((Integer) e1.nextElement()).intValue();
			m_VertIDs.addElement(new Integer(i1));
		}

		while (e2.hasMoreElements())
		{
			i2 = ((Integer) e2.nextElement()).intValue();
			m_VertIDs.addElement(new Integer(i2));
		}
	}

	public String toString ()
	{
		String s = new String("(");

		for (Enumeration e = indexes(); e.hasMoreElements(); )
		{
			s = s + String.valueOf(((Integer) e.nextElement()).intValue());
			s = s + (e.hasMoreElements() ? "," : ")");
		}
		return s;
	}

	public ConvexPoly getCopy ()
	{
		int n = m_VertIDs.size();

		Vector theIDs = new Vector(n);
		for (int i = 0; i < n; i++)
		{
			theIDs.addElement(new Integer(val(i)));
		}

		return new ConvexPoly(theIDs);
	}

	// returns the indexes of this poly's vertices
	public Enumeration indexes ()
	{
		return m_VertIDs.elements();
	}

	// returns is this the base polygon of an MD of (low,high)
	public boolean isBasePoly (int low, int high)
	{
		return (val(0) == low) && (val(m_VertIDs.size()-1) == high);
	}

	// returns the index of the left-edge vertex which is not on the base line
	public int getLeftEdgeIndex ()
	{
		return val(1);
	}

	// returns the index of the right-edge vertex which is not on the base line
	public int getRightEdgeIndex ()
	{
		return val(m_VertIDs.size()-2);
	}

	public double getLeftAngle ()
	{
		int low,high,aux;
		low  = val(0);
		aux  = val(1);
		high = val(m_VertIDs.size()-1);
		return CUtils.getAngleEx(aux, low, high);
	}

	public double getRightAngle ()
	{
		int low,high,aux;
		low  = val(0);
		aux  = val(m_VertIDs.size()-2);
		high = val(m_VertIDs.size()-1);
		return CUtils.getAngleEx(low, high, aux);
	}
	
	public Vector get_vertices() {
		return m_VertIDs;		
	}

	//------------------------------------------------------------
	// Private functions

	// returns the vertex-id of the index'th element in the vector
	private int val (int index)
	{
		return ((Integer) m_VertIDs.elementAt(index)).intValue();
	}
}

