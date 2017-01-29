package sweden.alexander.fanorona;

import java.util.LinkedHashMap;
import java.util.concurrent.Callable;

import processing.core.PApplet;
import processing.core.PFont;


public class Fanorona extends PApplet {
	public enum Screen {MAIN, DIFFICULITY, GAME};
	private PlayingField p;
	private MenuInterface mainMenu;
	private MenuInterface difficultSelection;
	private Screen currScreen;
	private int menuDelay;

	public void settings() {
		fullScreen();
		orientation(LANDSCAPE);
	}

	public void setup() {
		fill(255);
		makeMainMenu();
		makeDifficulityMenu();
		currScreen = Screen.MAIN;
		PFont font = createFont("cour.ttf", 12);
		textFont(font);
	}

	private void makeDifficulityMenu() {
		LinkedHashMap<String, Callable<Object>> menu = new LinkedHashMap<String, Callable<Object>>();
		menu.put("Very Easy", new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				startGame("super_easy.txt");
				return null;
			}

		});

		menu.put("Easy", new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				startGame("easy.txt");
				return null;
			}

		});

		menu.put("Normal", new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				startGame("normal.txt");
				return null;
			}

		});

		menu.put("Back", new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				back();
				return null;
			}
		});

		difficultSelection = new MenuInterface(this, menu);
		difficultSelection.setButtonHeightScale(0.5f);
		difficultSelection.setYPaddingScale(1.7f);
	}

	private void startGame(String board) {
		if (millis() - menuDelay > 250 || menuDelay == -1) {
			currScreen = Screen.GAME;
			p = new PlayingField(this, board);
		}
	}

	private void makeMainMenu() {
		LinkedHashMap<String, Callable<Object>> menu = new LinkedHashMap<String, Callable<Object>>();
		menu.put("Start", new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				menuDelay = millis();
				currScreen = Screen.DIFFICULITY;
				difficultSelection.onResize();
				return null;
			}
		});
		menu.put("Quit", new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				back();
				return null;
			}
		});
		mainMenu = new MenuInterface(this, menu);
	}

	public void draw() {
		background(255,159,2);
		switch (currScreen) {
			case MAIN:
				mainMenu.draw();
				break;
			case DIFFICULITY:
				difficultSelection.draw();
				break;
			case GAME:
				p.draw();
		}
	}

	public void mousePressed() {
		if (currScreen == Screen.GAME) {
			p.mousePressed(mouseX, mouseY);
		}
	}

	public Screen getCurrScreen() {
		return currScreen;
	}


	public void back() {
		switch (currScreen) {
			case DIFFICULITY:
				currScreen = Screen.MAIN;
				mainMenu.onResize();
				break;
			case GAME:
				currScreen = Screen.DIFFICULITY;
				difficultSelection.onResize();
				menuDelay = -1;
				break;
			case MAIN:
				exit();
				break;
		}
	}


}
