package sweden.alexander.fanorona;

import processing.core.PApplet;

public class Fanorona extends PApplet {
	private PlayingField p;

	public void settings() {
		fullScreen();
		orientation(LANDSCAPE);
	}
	
	public void setup() {
		p = new PlayingField(this);
	}
	
	public void draw() {
		background(255, 159, 2);
		p.draw();
	}
	
	public void mousePressed() {
		p.mousePressed(mouseX, mouseY);
	}


}
