package parser;

import java.util.ArrayList;

class Polygon {
	ArrayList<Point> vertices;
	
	public Polygon() {
		vertices = new ArrayList<Point>();
	}
	
	public void add_vertex(Point a) {
		vertices.add(a);
	}
	
}