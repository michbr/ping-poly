package parser;

public class Background extends Node {
	
	boolean is_outside;
	
	public Background(int y, int [] color) {
		super(y, color);
		type = "background";
		is_outside = false;
	}
	
}