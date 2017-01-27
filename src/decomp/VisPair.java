package decomp;

//
//
// VisPair
//
//
class VisPair implements ICompare
{
	public int low, high;

	VisPair (int i, int j)
	{
		low = Math.min(i,j);
		high = Math.max(i,j);
	}

	public int compareTo (Object toArg, int nPrm)
	{
		VisPair v = (VisPair) toArg;
		return (high - low) - (v.high - v.low);
	}

}

