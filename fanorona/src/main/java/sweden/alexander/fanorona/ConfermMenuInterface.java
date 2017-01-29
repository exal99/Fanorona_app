package sweden.alexander.fanorona;

import java.util.Map;
import java.util.concurrent.Callable;

import controlP5.Button;
import controlP5.ControlFont;
import processing.core.PApplet;

public class ConfermMenuInterface extends MenuInterface {
	
	private String[] lines;
	private float[][] linesPositions;
	private float fontSize;
	private boolean debugTextboxes;
	private float rotation;
	private ControlFont font;
	

	public ConfermMenuInterface(PApplet parrent, Map<String, Callable<Object>> buttons, String text, float rotation) {
		super(parrent, buttons);
		lines = text.split("\n");
		linesPositions = new float[lines.length][2];
		onResize();
		debugTextboxes = false;
		this.rotation = rotation;
		font = null;
	}
	
	public float getRotation() {
		return rotation;
	}
	
	private float fitFontSizeToHeight(float height) {
		parrent.textSize(12);
		float textHeight = (parrent.textAscent() + parrent.textDescent()) * lines.length;
		float percentOfHeight = textHeight/height;
		return 12 * 1/percentOfHeight;
	}
	
	private float fitFontSizeToWidth(float width, String text) {
		parrent.textSize(12);
		float textWidth = parrent.textWidth(text);
		float percentOfHeight = textWidth/width;
		return 12 * 1/percentOfHeight;
	}
	
	@Override
	public void draw() {
		parrent.pushMatrix();
		if (rotation != 0) {
			parrent.translate(parrent.width, parrent.height);
			parrent.rotate(rotation);
		}
		for (int line = 0; line < lines.length; line++) {
			parrent.textSize(fontSize);
			String text = lines[line];
			parrent.text(text, linesPositions[line][0], linesPositions[line][1]);
			if (debugTextboxes) {
				parrent.fill(255, 100);
				parrent.rect(linesPositions[line][0], linesPositions[line][1] - parrent.textAscent(), parrent.textWidth(text), parrent.textAscent() + parrent.textDescent());
			}
		}
		super.draw();
		parrent.popMatrix();
	}
	
	@Override
	public void onResize() {
		if (lines != null) {
			fontSize = fitFontSizeToHeight(parrent.height * 0.25f);
			if (fontSize == 0) {
				throw new RuntimeException();
			}
			parrent.textSize(fontSize);
			float lineHeight = parrent.textAscent() + parrent.textDescent();
			float yStart = (parrent.height/2 - (parrent.textAscent() + parrent.textDescent()) * lines.length)/2 + parrent.textAscent();
			for (int line = 0; line < lines.length; line++) {
				String textLine = lines[line];
				float textWidth = parrent.textWidth(textLine);
				float xPos = (parrent.width - textWidth)/2;
				float yPos = yStart + lineHeight * line;
				float[] textPos = {xPos, yPos};
				linesPositions[line] = textPos; 
			}
			int buttonHeight = (int) (parrent.height * 0.25f);
			int buttonWidth = (int) (parrent.width * 0.3f);
			font = getControlFont();
			buttonFontSize = getButtonFontSize();
			for (Button b : buttons) {
				b.setHeight(buttonHeight);
				b.setWidth(buttonWidth);
				if (font != null) {
					b.setFont(font);
				}
			}
			float yPos = parrent.height/2 + buttonHeight/2;
			float offset = parrent.width * 0.1f;
			
			buttons.get(0).setPosition(offset, yPos);
			buttons.get(1).setPosition(parrent.width - (offset + buttonWidth), yPos);
			
			buttonFontSize = (int) Math.min(fitFontSizeToHeight(buttonHeight),
								   Math.min(fitFontSizeToWidth(buttonWidth * 0.9f, buttons.get(0).getCaptionLabel().getText()),
										    fitFontSizeToWidth(buttonWidth * 0.9f, buttons.get(1).getCaptionLabel().getText())));
			buttons.get(0).setFont(new ControlFont(parrent.createFont("cour.ttf", buttonFontSize)));
			buttons.get(1).setFont(new ControlFont(parrent.createFont("cour.ttf", buttonFontSize)));
		}
	}

}
