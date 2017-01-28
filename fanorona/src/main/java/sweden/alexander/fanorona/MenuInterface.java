package sweden.alexander.fanorona;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;

import controlP5.Button;
import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlFont;
import controlP5.ControlP5;
import processing.core.PApplet;
import processing.core.PFont;

public class MenuInterface extends ControlP5 {
	
	private ArrayList<Button> buttons;
	private PApplet parrent;
	private float lastWidth;
	private float lastHeight;
	private float yPadding;
	private float buttonHeightMultiplyer;
	private float yPaddingScale;

	public MenuInterface(PApplet parrent, Map<String, Callable<Object>> buttons) {
		super(parrent);
		this.buttons = new ArrayList<Button>();
		setAutoDraw(false);
		this.parrent = parrent;
		createButtons(buttons);
		buttonHeightMultiplyer = 0.4f;
		yPaddingScale = 1;
		yPadding = calcYPadding();
		onResize();
	}

	public void setButtonHeightScale(float newPadding) {
		buttonHeightMultiplyer = newPadding;
		onResize();
	}

	public void setYPaddingScale(float newScale) {
		yPaddingScale = newScale;
		onResize();
	}

	private float calcYPadding() {
		return lastHeight / buttons.size() * (1 - buttonHeightMultiplyer)/2 * yPaddingScale;
	}
	
	private void createButtons(final Map<String, Callable<Object>> buttons) {
		String[] keySet = buttons.keySet().toArray(new String[0]);
		for (int i = 0; i < keySet.length; i++) {
			Button b = addButton(keySet[i]);
			final String key = keySet[i];
			b.addCallback(new CallbackListener() {
				
				@Override
				public void controlEvent(CallbackEvent event) {
					switch(event.getAction()) {
					case ControlP5.ACTION_PRESS:
						try {
							buttons.get(key).call();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					}	
				}
			});
			this.buttons.add(b);
		}
		
	}
	
	@Override
	public void draw() {
		if (lastWidth != parrent.width || lastHeight != parrent.height) {
			lastWidth = parrent.width;
			lastHeight = parrent.height;
			onResize();
		}
		super.draw();

	}
	
	public void onResize() {
		int numButtons = buttons.size();
		float buttonHeight = lastHeight / numButtons * buttonHeightMultiplyer;
		float buttonWidth = lastWidth * 0.3f;
		yPadding = calcYPadding();
		float yOffset = (lastHeight - ((numButtons) * (buttonHeight) + (numButtons - 1) * yPadding))/2;
		int maxLen = 0;
		String maxLable = "";
		for (int i = 0; i < numButtons; i ++) {
			Button b = buttons.get(i);
			b.setHeight((int) buttonHeight);
			b.setWidth((int) buttonWidth);
			b.setPosition(lastWidth/2 - buttonWidth/2, yOffset+  (i) * (buttonHeight) + (i) * yPadding);
			if (b.getLabel().length() > maxLen) {
				maxLen = b.getLabel().length();
				maxLable = b.getLabel();
			}
		}
		if (buttonHeight != 0) {
			parrent.textSize(12);
			float textHeight = parrent.textAscent() + parrent.textDescent();
			float percent = textHeight / (buttonHeight * 0.5f);
			parrent.textSize(12 * 1 / percent);
			if (parrent.textWidth(maxLable) > buttonWidth) {
				parrent.textSize(12);
				percent = parrent.textWidth(maxLable) / (buttonWidth * 0.9f);
				parrent.textSize(12 * 1 / percent);
			}
			for (Button b : buttons)  {
				if (buttonHeight != 0) {
					PFont font = parrent.createFont("cour.ttf", (int) (12 * 1 / percent));
					b.setFont(new ControlFont(font, (int) (12 * 1 / percent)));
				}
			}
		}


		
	}

}
