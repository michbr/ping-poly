package parser;

import java.util.ArrayList;

public abstract class Node {
	
	static ArrayList<ArrayList<Node>> map = new ArrayList<ArrayList<Node>>();
	
	static Node get_node(int x, int y) {
		return map.get(y).get(x);
	}
	
	int [] color;
	Point pos;
	Node left, right, top, bot;
	String type;
	
	public Node(int y, int [] color) {
		if (y >= map.size()) {
			map.add(new ArrayList<Node>());
		}
		set_left(y);
		set_top(y);
		add_as_right(y);
		add_as_bot(y);
		right = null;
		bot = null;
		pos = new Point (map.get(y).size(), y);
		map.get(y).add(this);
		this.color = color;
		
	}
	
	private void set_left(int y) {
		if (map.get(y).size() > 0) {
			left = map.get(y).get(map.get(y).size()- 1);
		}
		else {
			left = null;
		}
	}
	
	private void set_top(int y) {
		if (y > 0) {
			top = map.get(y-1).get(map.get(y).size());
		}
		else {
			top = null;
		}
	}

	private void add_as_right(int y) {
		if (map.get(y).size() > 0) {
			map.get(y).get(map.get(y).size()- 1).right = this;
		}
	}
	

	
	private void add_as_bot(int y) {
		if (y > 0) {
			map.get(y-1).get(map.get(y).size()).bot = this;
		}
	}
	
	public void set_color(int [] color) {
		this.color = color;
	}
	
	public boolean is_edge() {
		return (left == null || right == null || top == null || bot == null);
	}
	
	public boolean is_vertex() {
		if (left != null && left.get_type() == "background") {
			return true;
		}
		if (right != null && right.get_type() == "background") {
			return true;
		}
		if (top != null && top.get_type() == "background") {
			return true;
		}
		if (bot != null && bot.get_type() == "background") {
			return true;
		}
		return false;
	}
	
	public boolean is_compatible(Node a) {
		if ((left.get_type() == "background") && (a.left.get_type() == "background")) {
			return true;
		}
		if ((right.get_type() == "background") && (a.right.get_type() == "background")) {
			return true;
		}	
		if ((top.get_type() == "background") && (a.top.get_type() == "background")) {
			return true;
		}
		if ((bot.get_type() == "background") && (a.bot.get_type() == "background")) {
			return true;
		}
		return false;

	}
	
	public Point get_pos() {
		return new Point(pos.get_x(), pos.get_y());
	}
	
	public int [] get_color() {
		return color;
	}
	
	public Node get_left (){
		return left;
	}
	
	public Node get_right (){
		return right;
	}
	
	public Node get_top (){
		return top;
	}
	
	public Node get_bot (){
		return bot;
	}
	
	public String get_type() {
		return type;
	}
	
	public static Node get(int x, int y) {
		return map.get(y).get(x);
	}
}