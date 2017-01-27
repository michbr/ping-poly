package decomp;

/*
	Interface ICompare - used by COurUtil::sortVector to compare
		objects for sorting.
*/

public interface ICompare {
	// return value is > 0 if this object is "larger", 
	// < 0 if this object is "smaller", and = 0 if equal;
	abstract public int compareTo (Object arg, int nPrm);
}