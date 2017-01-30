package sweden.alexander.fanorona;

import java.util.LinkedHashMap;
import java.util.concurrent.Callable;

import mobi.upod.timedurationpicker.TimeDurationPicker;
import mobi.upod.timedurationpicker.TimeDurationPickerDialog;
import processing.core.PApplet;
import processing.core.PFont;


public class Fanorona extends PApplet {
	public enum Screen {MAIN, DIFFICULITY, GAME_TYPE, GAME, GAME_OVER};
	private PlayingField p;
	private MenuInterface mainMenu;
	private MenuInterface difficultSelection;
	private MenuInterface gameSpeed;
	private ConfermMenuInterface gameOver;
	private Screen currScreen;
	private int menuDelay;
	
	private int[] whiteTime;
	private int[] blackTime;

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
				if (millis() - menuDelay > 250 || menuDelay == -1) {
					menuDelay = millis();
					gameSpeed = createTypeSelection("super_easy.txt");
					currScreen = Screen.GAME_TYPE;
					System.out.println(millis() - menuDelay + " "+ menuDelay + " " + millis());
				}
				return null;
			}

		});

		menu.put("Easy", new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				if (millis() - menuDelay > 250 || menuDelay == -1) {
					menuDelay = millis();
					gameSpeed = createTypeSelection("easy.txt");
					currScreen = Screen.GAME_TYPE;
				}
				return null;
			}

		});

		menu.put("Normal", new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				if (millis() - menuDelay > 250 || menuDelay == -1) {
					menuDelay = millis();
					gameSpeed = createTypeSelection("normal.txt");
					currScreen = Screen.GAME_TYPE;
				}
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

	private MenuInterface createTypeSelection(final String board) {
		LinkedHashMap<String, Callable<Object>> menu = new LinkedHashMap<String, Callable<Object>>();
		menu.put("Normal", new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				startGame(board, false);
				return null;
			}

		});

		menu.put("Blitz Mode", new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				startGame(board, true);
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

		return new MenuInterface(this, menu);

	}

	private void startGame(final String board, boolean blitz) {
		if (millis() - menuDelay > 250 || menuDelay == -1) {
			
			if (!blitz) {
				p = new PlayingField(this, board);
				currScreen = Screen.GAME;
			} else {
				PickerDialogFragment white = new PickerDialogFragment();
				white.setTitle("Select whites time");
				white.setDurationSetListener(new TimeDurationPickerDialog.OnDurationSetListener() {
					@Override
					public void onDurationSet(TimeDurationPicker view, long duration) {
						whiteTime = PickerDialogFragment.getTime(duration);
						PickerDialogFragment black = new PickerDialogFragment();
						black.setTitle("Select blacks time");
						black.setDurationSetListener(new TimeDurationPickerDialog.OnDurationSetListener() {
							@Override
							public void onDurationSet(TimeDurationPicker view, long duration) {
								blackTime = PickerDialogFragment.getTime(duration);
								startBlitz(board);
							}
						});
						
						black.show(getFragmentManager(), "dialog");
					}
				});
				white.show(getFragmentManager(), "dialog");
			}
		}
	}
	
	private void startBlitz(String board) {
		if (whiteTime != null && blackTime != null) {
			p = new PlayingField(this, board, whiteTime, blackTime);
			whiteTime = null;
			blackTime = null;
			currScreen = Screen.GAME;
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
			case GAME_TYPE:
				gameSpeed.draw();
				break;
			case GAME:
				p.draw();
				break;
			case GAME_OVER:
				gameOver.draw();
				break;
		}
		//textSize(100);
		//text(frameRate, 0, height - 100);
	}

	public void mousePressed() {
		if (currScreen == Screen.GAME) {
			p.mousePressed(mouseX, mouseY);
		}
		
		if (currScreen == Screen.GAME_OVER && gameOver.getRotation() != 0) {
			mouseY = height - mouseY;
			mouseX = width - mouseX;
		}
		
	}

	public Screen getCurrScreen() {
		return currScreen;
	}


	public void back() {
		switch (currScreen) {
			case DIFFICULITY:
				currScreen = Screen.MAIN;
				break;
			case GAME:
				currScreen = Screen.DIFFICULITY;
				break;
			case GAME_TYPE:
				currScreen = Screen.DIFFICULITY;
				break;
			case GAME_OVER:
				currScreen = Screen.DIFFICULITY;
				break;
			case MAIN:
				exit();
				break;
		}
	}

	public void showGameOver(String message, float rotation, final String board, final boolean blitz) {
		LinkedHashMap<String, Callable<Object>> menu = new LinkedHashMap<String, Callable<Object>>();
		menu.put("Rematch!", new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				startGame(board, blitz);
				return null;
			}
		});
		menu.put("Main Menu", new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				back();
				return null;
			}
		});
		gameOver = new ConfermMenuInterface(this, menu, message, rotation);
		currScreen = Screen.GAME_OVER;
	}


}
