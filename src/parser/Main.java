package parser;

import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import decomp.Front;

public class Main {
	
	ArrayList<Point> vertices;
	ArrayList<Point> reflex;
	Point normal;
	Point center;
	
	Node head;
	BufferedImage image;
	String filename;
	
	final int [] black = {0,0,0,255};
	final	int [] white = {255,255,255,255};
	final	int [] green = {0,255,0,255};
	
	public Main(String filename) {
		this.filename = filename;
		vertices = new ArrayList<Point>();
	}

	
	private void parse_image() {
		try {
			File file = new File (filename);
			image = ImageIO.read(file);
			//System.out.println("successfully opened " + filename);
		}
		catch (Exception e){
			System.err.println("Failed to open: " + filename);
			return;
		}
		Raster raster = image.getData();
		int [] get = new int[4];

		for (int y = raster.getMinY(); y < raster.getHeight() + raster.getMinY(); y++) {
			for (int x = raster.getMinX(); x < raster.getWidth() + raster.getMinX(); x++) {
				raster.getPixel(x,y, get);
				Node temp;
				if (!is_background(get)) {
					temp = new Foreground(y, get);
				}
				else {
					temp = new Background(y, get);
				}
			}
		}
		head = (Node.get(0,0));
	}
	
	private void convert() {
		Node cur_y = head;
		Node cur = head;
		int y = 0;
		int x = 0;
		while (cur_y != null) {
			while (cur != null) {
				if (cur.get_type().equals("background")) {
					//System.out.println("setting red: " + x + ", " + y);
					//raster.setPixel(x, y, white);
				}
				else if (cur.is_vertex()) {
					//edges.push(cur.get_pos());
					vertices.add(cur.get_pos());
					//System.out.println("setting green: " + x + ", " + y);
					//raster.setPixel(x, y, green);
				}
				else {
					//raster.setPixel(x, y, black);
				}
				cur.set_color(white);
				cur = cur.get_right();
				//if (cur == null) {
					//System.out.println("null right");
				//}
				
				x++;
			}
			cur_y = cur_y.get_bot();
			cur = cur_y;
			y++;
			x =  0;
		}
		//vertices.add(new Point(0,0));
		//vertices.add(new Point(45,45));
		//image.setData(raster);
	}
	
	private void sort() {
		ArrayList<Point> sorted = new ArrayList<Point>();
		int num_vertices = vertices.size();
		sorted.add(vertices.get(0));
		vertices.remove(0);
		while (sorted.size() < num_vertices) {
			Point cur = sorted.get(sorted.size() - 1);
			double min_dist = 0;
			int mindex = 0;
			Point next;
			boolean has_min_dist = false;
			for (int i = 0; i < vertices.size(); i++){
				Node cur_node = Node.get_node(cur.get_x(), cur.get_y());
				
				if (cur_node.is_compatible(Node.get_node(vertices.get(i).get_x(), vertices.get(i).get_y()))) {
					double cur_dist = cur.distance(vertices.get(i));
					if (!has_min_dist || cur_dist < min_dist) {
						
						min_dist = cur_dist;
						mindex = i;
						has_min_dist = true;
					}
				}
				
			}
			//System.out.println("Adding: " + vertices.get(mindex) + " with distance: " + min_dist);
			sorted.add(vertices.get(mindex));
			vertices.remove(mindex);
		}
		vertices = sorted;
		//for (int i = 0; i < vertices.size(); i++){
		//	System.out.println(vertices.get(i));
		//}
	}
	
	private void strip() {
		int base = 0;
		int i = 0;
		int start = vertices.size();
		while(base < vertices.size()) {
			Point cur = vertices.get(base);
			Point middle = null;
			Point next = null;
			if (i < (vertices.size() - 1)) {
				middle = vertices.get(i+1);
			}
			else {
				middle = vertices.get(0);
			}
			
			if (i < (vertices.size() - 2)) {
				next = vertices.get(i+2);
			}
			else if (i < vertices.size() - 1) {
				next = vertices.get(0);
				//System.out.println("");
			}
			else {
				next = vertices.get(1);
			}
			
			//System.out.println("testing: " + cur + " and " + middle + " and " + next) ;
			
			double x1 = (double)cur.get_x();
			double y1 = (double)cur.get_y();
			
			double x2 = (double)middle.get_x();
			double y2 = (double)middle.get_y();
			
			double x3 = (double)next.get_x();
			double y3 = (double)next.get_y();
				
			double colinearity = (x3 - x1) * (y2 - y1) + (y3 - y1) * (x1 - x2);
			//System.out.println("\tColinearity is " + colinearity);
			if (Math.abs(colinearity) < .01) {
				vertices.remove(middle);
				//System.out.println("\tRemoving");
			}
			else {
				base++;
				i++;
			}
			//System.out.println();
			//i++;
			if (i == vertices.size()) i = 0;
		}
		System.out.println("Removed " + (start - vertices.size()) + " vertices.");
	}
	
	private void decompose() {
		Front test = new Front();
		for (int i = 0; i <  vertices.size(); i++) {
			Point p = vertices.get(i);
			System.out.println("Adding point: " + p.get_x() + ", " + p.get_y());
			test.add(p.get_x(), p.get_y());
			
		}
		test.run();
		ArrayList<ArrayList<Point>> results = test.get_results();
		for (int i = 0; i < results.size(); i++) {
			for (int j = 0; j < results.get(i).size(); j++) {
			Point a = results.get(i).get(j);
			Point b;
			if ( j < (results.get(i).size() - 1)) {
				b = results.get(i).get(j+1);	
			}
			else {
				b = results.get(i).get(0);
			}
			bresenham(a, b);
			//if (a.is_concave()) {
			//	//System.out.println("found concave");
			//	Node cur = Node.get_node(a.x, a.y);
			//	cur.set_color(black);
			//}
			}
		}
	}
	
	private void set_reflexivity() {
		Point start = vertices.get(0);
		int max_x = start.get_x();
		int max_y = start.get_y();
		int min_x = start.get_x();
		int min_y = start.get_y();
		for (int i = 1; i < vertices.size(); i++) {
			//System.out.println("Max: " + max_x + " " + max_y);
			//System.out.println("Min: " + min_x + " " + min_y);
			Point cur = vertices.get(i);
			if (cur.get_x() > max_x) {
			//	System.out.println("setting max_x to " + cur.get_x());
				max_x = cur.get_x();
			}
			if (cur.get_x() < min_x) {
			//	System.out.println("setting min_x to " + cur.get_x());
				min_x = cur.get_x();
			}
			if (cur.get_y() > max_y) {
		//		System.out.println("setting max_y to " + cur.get_y());
				max_y = cur.get_y();
			}
			if (cur.get_y() < min_y) {
		//		System.out.println("setting min_y to " + cur.get_y());
				min_y = cur.get_y();
			}
		}
	//	System.out.println("Max: " + max_x + " " + max_y);
	//	System.out.println("Min: " + min_x + " " + min_y);
		
		
		for (int i = 0; i < vertices.size(); i++) {
			Point cur = vertices.get(i);
			if (cur.get_x() == max_x) {
		//		System.out.println("Setting point: " + cur);
				cur.set_reflexive(true);
				cur.set_concave(false);
					//Point pos = vertices.get(i);
				//Node node = Node.get_node(cur.x, cur.y);
				//node.set_color(black);
			}
			if (cur.get_x() == min_x) {
		//		System.out.println("Setting point: " + cur);
				cur.set_reflexive(true);
				cur.set_concave(false);
				//Node node = Node.get_node(cur.x, cur.y);
				//node.set_color(black);
			}
			if (cur.get_y() == max_y) {
			//	System.out.println("Setting point: " + cur);
				cur.set_reflexive(true);
				cur.set_concave(false);
				//Node node = Node.get_node(cur.x, cur.y);
				//node.set_color(black);
			}
			if (cur.get_y() == min_y) {
			//	System.out.println("Setting point: " + cur);
				cur.set_reflexive(true);
				cur.set_concave(false);
				//Node node = Node.get_node(cur.x, cur.y);
				//node.set_color(black);
				
			}
		}
		center = new Point(min_x + ((max_x-min_x)/2), min_y + ((max_y-min_y)/2));
	}
	
	private void set_concavity() {
		for (int i = 0; i < vertices.size(); i++) {
			Point cur = vertices.get(i);
			Point previous;
			Point next;
			if (i == vertices.size() - 1) {
				next = vertices.get(0);
				previous = vertices.get(i-1);
			}
			else if (i == 0) {
				previous = vertices.get(vertices.size() - 1);
				next = vertices.get(i+1);
			}
			else {
				previous = vertices.get(i-1);
				next = vertices.get(i+1);
			}
			Point dir_next = next.subtract(cur);
			Point normal_next = dir_next.make_perpendicular();
			
			Point dir_prev = previous.subtract(cur);
			Point normal_prev = dir_prev.make_perpendicular();
			
			boolean prev_contact = false;
			boolean next_contact = false;
			for (int j = 0; j < vertices.size(); j++) {
				//System.out.println("Testing " + vertices.get(j));
				if (vertices.get(j) == cur || vertices.get(j) == next || vertices.get(j) == previous) {
					continue;
				}
				Point dir = vertices.get(j).subtract(cur);
				//Point dir_two = vertices.get(j).subtract(cur);
				//System.out.println("prev dot: " + dir.dot_product(normal_prev));
				//System.out.println("next dot: " + dir.dot_product(normal_next));
				if (dir.dot_product(normal_prev) > 0) {
					prev_contact = true;
				}
				if (dir.dot_product(normal_next) > 0) {
					next_contact = true;
				}
			}
			if (prev_contact && next_contact) {
				vertices.get(i).set_concave(true);
				//System.out.println("setting true");
				
			}
			else {
				vertices.get(i).set_concave(false);
			}
		}
	}
	
	/*private void decompose() {
		int count = 0;
		int chain = 0;
		int max_chain = 0;
		for (int i = 0; i < vertices.size(); i++) {
			int next;
			if (vertices.get(i).is_concave()) {
				count++;
				chain++;
			}
			if (i < (vertices.size() - 1)) {
				next = i + 1;
			}
			else {
				next = 0;
			}
			if (!vertices.get(next).is_concave()) {
				if (chain > max_chain) {
					max_chain = chain;
					chain = 0;
				}
			}
		}
		System.out.println("max: " + max_chain+ " count: " + count);
		if (max_chain == count) {
			
		}
	}*/
	
	private void test() {
		for (int i = 0; i < vertices.size(); i++) {
			Point a = vertices.get(i);
			Point b;
			if ( i < (vertices.size() - 1)) {
				b = vertices.get(i+1);	
			}
			else {
				b = vertices.get(0);
			}
			if (a.is_concave()) {
				//System.out.println("found concave");
				Node cur = Node.get_node(a.x, a.y);
				cur.set_color(black);
			}
			//bresenham(new Point(31,2), new Point(1,61));
			//bresenham(a, b);
		//	Point pos = vertices.get(i);
		//	Node cur = Node.get_node(pos.x, pos.y);
		//	cur.set_color(black);
		}
	}
	
	private void bresenham(Point a, Point b) {
		//System.out.println("Bresenham");
		int x1 = a.get_x();
		int x2 = b.get_x();
		
		int y1 = a.get_y();
		int y2 = b.get_y();
		
		int dx = Math.abs(x2 - x1);
		int dy = Math.abs(y2 - y1);
		
		int sx = (x1 < x2) ? 1 : -1;
		int sy = (y1 < y2) ? 1 : -1;
		
		int err = dx - dy;
		
		while (true) {
			Node.get_node(x1,y1).set_color(black);//framebuffer.setPixel(x1, y1, Vec3.one);
			
			if (x1 == x2 && y1 == y2) {
			    break;
			}
			
			int e2 = 2 * err;
			
			if (e2 > -dy) {
			    err = err - dy;
			    x1 = x1 + sx;
			}
			
			if (e2 < dx) {
			    err = err + dx;
			    y1 = y1 + sy;
			}
		}
    }

	
	
	private void render () {
		WritableRaster raster = image.getRaster();
		Node cur_y = head;
		Node cur = head;
		while (cur_y != null) {
			while (cur != null) {
				Point pos = cur.get_pos();
				raster.setPixel(pos.get_x(), pos.get_y(), cur.get_color());
				cur = cur.get_right();
			}
			cur_y = cur_y.get_bot();
			cur = cur_y;
		}
	}
	
	private void write_out(String file_name) {
		File outputfile = new File(file_name);
		
		try {
			ImageIO.write(image, "png", outputfile);
		}
		catch (Exception e) {
			System.err.println("Failed to write image: test2.png");
			return;
		}
	}
	
	private boolean is_background(int [] color) {
		if (color[0] == color[1] && color[1] == color[2]) {
			if (Math.sqrt(color[0]*color[0] + color[1]*color[1] + color[2]*color[2]) > 430) {
				return true;
			}		
		}
		return false;
	}
	
	public static void main(String args[]) {
		if (args.length < 1) {
			System.err.println("Usage: Main <filename.png>");
			return;
		}
		String filename = args[0];
		Main parser = new Main(filename);
		parser.parse_image();
		parser.convert();
		parser.sort();
		parser.strip();
		//parser.set_reflexivity();
		//parser.set_concavity();
		parser.decompose();
		//parser.test();
		parser.render();
		parser.write_out("test2.png");
		
	}
	
}