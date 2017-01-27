package parser;

public class Foreground extends Node {
	
	public Foreground(int y, int [] color) {
		super(y, color);
		type = "foreground";
	}
	
}