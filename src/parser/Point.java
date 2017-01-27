package parser;

public class Point {
	
	protected int x, y;
	
	boolean is_concave;
	boolean is_reflexive;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int get_x() {
		return x;
	}
	
	public int get_y() {
		return y;
	}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	public double distance(Point a) {
		double x_dist = (double)x - (double)a.get_x();
		double y_dist = (double)y - (double)a.get_y();
		return Math.sqrt(x_dist*x_dist + y_dist*y_dist);
	}
	
	public void set_reflexive(boolean a) {
		is_reflexive = a;
	}

	public void set_concave(boolean a) {
		is_concave = a;
	}
	
	public boolean is_reflexive() {
		return is_reflexive;
	}

	public boolean is_concave() {
		return is_concave;
	}
	
	public Point subtract(Point a) {
		return new Point(this.x - a.x, this.y - a.y);
	}
	
	public Point make_perpendicular() {
		return new Point(y, -x);
	}
	
	public int dot_product(Point a) {
		return a.x*this.x + a.y*this.y;
	}
	
}