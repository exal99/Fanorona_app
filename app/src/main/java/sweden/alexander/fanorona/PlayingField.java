package sweden.alexander.fanorona;

import java.text.ParseException;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

public class PlayingField {
	private PApplet parrent;
	private MoveDirection[][] directionsGrid;
	private Piece[][] pieceGrid;
	private Piece[][] actualPieceGrid;
	private Piece selected;
	private Piece lastMoved;
	
	private int currentPlayer;
	
	private int lastWidth;
	private int lastHeight;
	private float size;
	
	private boolean moved;
	private boolean mustConferm;
	private Piece toConfermTo;
	private ArrayList<int[]> walkedAlong;
	
	public PlayingField(PApplet parrent) {
		this.parrent = parrent;
		try {

			directionsGrid = Parser.parseDirection("board.txt", parrent);
			pieceGrid = Parser.parsePieces("board.txt", parrent, this);
			actualPieceGrid = new Piece[directionsGrid.length][directionsGrid[0].length];
			populatePieceGrid();
			lastWidth = 0;
			lastHeight = 0;
			size = 0;
			selected = null;
			currentPlayer = Piece.getColor('W', parrent);
			moved = false;
			walkedAlong = new ArrayList<int[]>();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	
	private void populatePieceGrid() {
		int actualRow = -1;
		int actualCol = 0;
		for (int row = 0; row < directionsGrid.length; row++) {
			boolean entireDirection = true;
			actualCol = 0;
			for (int col = 0; col < directionsGrid[0].length; col++) {
				if (directionsGrid[row][col] == MoveDirection.CONNECTION) {
					if (entireDirection) {
						actualRow++;
						entireDirection = false;
					}
					int[] pos = {actualRow, actualCol};
					actualPieceGrid[row][col] = pieceGrid[actualRow][actualCol].clone();
					actualPieceGrid[row][col].setPos(pos);
					
					actualCol++;
				}
			}
		}
	}
	
	public static int[] createPos(int a, int b) {
		int[] r = {a, b};
		return r;
	}
	
	public MoveDirection[][] getDirections() {
		return directionsGrid;
	}
	
	public Piece[][] getActualPieceGrid() {
		return actualPieceGrid;
	}
	
	public void draw() {
		PVector start = getStartPos();
		if (parrent.width != lastWidth || parrent.height != lastHeight || moved) {
			lastWidth = parrent.width;
			lastHeight = parrent.height;
			size = getSize();
			moved = false;
			for (int row = 0; row < pieceGrid.length; row++) {
				for (int col = 0; col < pieceGrid[0].length; col++) {
					Piece current = pieceGrid[row][col];
					current.setDisplayPos(PVector.add(start, new PVector(col * size, row * size)));
					current.setRadius((size/2) * 2/3);
					actualPieceGrid[current.getPos()[0]][current.getPos()[1]].setDisplayPos(current.getDisplayPos());
				}
			}
		}
		
		for (int row = 0; row < directionsGrid.length; row++) {
			for (int col = 0; col < directionsGrid[0].length; col++) {
				if (directionsGrid[row][col] != MoveDirection.CONNECTION && directionsGrid[row][col] != null) {
					drawLine(directionsGrid[row][col], createPos(row, col));
				}
			}
		}
		for (int row = 0; row < pieceGrid.length; row++) {
			for (int col = 0; col < pieceGrid[0].length; col++) {
				Piece current = pieceGrid[row][col];
				current.draw();
			}
		}
		
		setPossibleSelect();
	}
	
	public boolean mustBeCapture() {
		for (Piece[] row : pieceGrid) {
			for (Piece p : row) {
				if (p.getColor() == currentPlayer && p.isActive()) {
					if (p.canCapture()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public Piece getPiece(int row, int col) {
		if (row >= 0 && row < actualPieceGrid.length && col >= 0 && col < actualPieceGrid[0].length) {
			return actualPieceGrid[row][col];
		} else {
			return null;
		}
	}
	
	public void mousePressed(int mouseX, int mouseY) {
		Piece found = null;
		for (Piece[] row : pieceGrid) {
			for (Piece p : row) {
				if (p.isClicked(mouseX, mouseY) && (p.getColor() == currentPlayer || (selected != null && !p.isActive()))) {
					//something
					found = p;
				} else if (p.isClicked(mouseX, mouseY) && mustConferm) {
					toConfermTo.conferm(p);
					mustConferm = toConfermTo.requiresConfermation();
					if (!mustConferm) {
						toConfermTo = null;
					} if (!selected.canCapture()) {
						nextTurn();
					}
				}
			}
		}
		if (selected != null && found != null && selected.getColor() == currentPlayer) {
			makeMove(found);
		}
		if (lastMoved == null) {
			selected = found;
			for (Piece[] row : pieceGrid) {
				for (Piece p : row) {
					if (p != found) {
						p.setSelected(false);
					}
				}
			}
		} else {
			for (Piece[] row : pieceGrid) {
				for (Piece p : row) {
					if (p != selected) {
						p.setSelected(false);
					}
				}
			}
		}
	}
	
	public void setPossibleSelect() {
		if (lastMoved == null) {
			ArrayList<Piece> all = new ArrayList<Piece>();
			ArrayList<Piece> capture = new ArrayList<Piece>();
			for (Piece[] row : pieceGrid) {
				for (Piece p : row) {
					if (p.getColor() == currentPlayer && p.isActive()) {
						if (p.canCapture()) {
							capture.add(p);
						} else {
							all.add(p);
						}
					}
					p.setCanSelect(false);
				}
			}
			if (capture.size() > 0) {
				for (Piece p : capture) {
					p.setCanSelect(true);
				}
			} else {
				for (Piece p: all) {
					p.setCanSelect(true);
				}
			}
		} else {
			for (Piece[] row : pieceGrid) {
				for (Piece p : row) {
					if (p != lastMoved) {
						p.setCanSelect(false);
					}
				}
			}
		}
	}
	
	public void dissablePiece(int row, int col) {
		pieceGrid[row][col].setActive(false);
	}
	
	public void dissablePiece(int[] pos) {
		dissablePiece(pos[0], pos[1]);
	}
	
	private void makeMove(Piece toMoveTo) {
		if (selected.canMoveTo(toMoveTo) && !mustConferm) {
			if (mustBeCapture()) {
				if (selected.isCaptureMove(toMoveTo)) {
					move(selected, toMoveTo);
				}
			} else {
				int tempPlayer = currentPlayer;
				move(selected, toMoveTo);
				if (tempPlayer == currentPlayer) {
					nextTurn();
				}
			}
		}
	}
	
	public Piece getCorospondingPiece(Piece p) {
		if (p != null && p.getPos()[0] >= 0 && p.getPos()[0] < pieceGrid.length &&
						 p.getPos()[1] >= 0 && p.getPos()[1] < pieceGrid[0].length){
			return pieceGrid[p.getPos()[0]][p.getPos()[1]];
		} else {
			return null;
		}
	}
	
	private void move(Piece from, Piece to) {
		if (from.isCaptureMove(to)) {
			from.capture(to);
		}
		int[] fActualPos = from.getPos();
		int[] tActualPos = to.getPos();
		int[] fPos = actualPieceGrid[fActualPos[0]][fActualPos[1]].getPos();
		int[] tPos = actualPieceGrid[tActualPos[0]][tActualPos[1]].getPos();
		int[] direction = {(tActualPos[0] - fActualPos[0])/2, (tActualPos[1] - fActualPos[1])/2};
		int[] linePos = {fActualPos[0] + direction[0], fActualPos[1] + direction[1]};
		walkedAlong.add(linePos);
		pieceGrid[fPos[0]][fPos[1]] = to;
		pieceGrid[tPos[0]][tPos[1]] = from;
		to.setPos(fActualPos);
		from.setPos(tActualPos);
		Piece temp = actualPieceGrid[fActualPos[0]][fActualPos[1]];
		actualPieceGrid[fActualPos[0]][fActualPos[1]] = actualPieceGrid[tActualPos[0]][tActualPos[1]];
		actualPieceGrid[tActualPos[0]][tActualPos[1]] = temp;
		actualPieceGrid[fActualPos[0]][fActualPos[1]].setPos(fPos);
		temp.setPos(tPos);
		mustConferm = from.requiresConfermation();
		moved = true;
		if (from.canCapture()) {
			lastMoved = from;
		} if (mustConferm) {
			toConfermTo = from;
		} if (!from.canCapture() && !mustConferm) {
			nextTurn();
		}
	}
	
	private boolean containsPos(int[] pos) {
		for (int[] p : walkedAlong) {
			if (p[0] == pos[0] && p[1] == pos[1]) {
				return true;
			}
		}
		 return false;
	}
	
	private void nextTurn() {
		lastMoved = null;
		currentPlayer = (currentPlayer == Piece.getColor('W', parrent)) ? Piece.getColor('B', parrent) : Piece.getColor('W', parrent);
		for (Piece[] row : pieceGrid) {
			for (Piece p : row) {
				p.resetMovement();
			}
		}
		
		for (Piece[] row : actualPieceGrid) {
			for (Piece p : row) {
				if (p != null) {
					p.resetMovement();
				}
			}
		}
		walkedAlong.clear();
	}
	
	private void drawLine(MoveDirection direction, int[] pos) {
		int[] xyDelta = direction.getDelta();
		parrent.strokeWeight(7);
		parrent.stroke(containsPos(pos) ? parrent.color(255, 0, 0) : 100);
		Piece from = actualPieceGrid[pos[0] - xyDelta[0]][pos[1] - xyDelta[1]];
		Piece to   = actualPieceGrid[pos[0] + xyDelta[0]][pos[1] + xyDelta[1]];
		parrent.line(from.getDisplayPos().x, from.getDisplayPos().y, to.getDisplayPos().x, to.getDisplayPos().y);
	}
	
	private float getSize() {
		int numHeight = pieceGrid.length;
		int numWidth = pieceGrid[0].length;
		float rectWidth = parrent.width / ((float) numWidth);
		float rectHeight = parrent.height / ((float) numHeight);
		return (rectWidth < rectHeight) ? rectWidth  : rectHeight;
	}
	
	private PVector getStartPos() {
		int numHeight = pieceGrid.length;
		int numWidth = pieceGrid[0].length;
		float rectWidth = parrent.width / ((float) numWidth);
		float rectHeight = parrent.height / ((float) numHeight);
		if (rectWidth < rectHeight) {
			return new PVector(rectWidth/2, (parrent.height - pieceGrid.length * rectWidth) / 2 + rectWidth/2);
		} else {
			return new PVector((parrent.width - pieceGrid[0].length * rectHeight)/2 + rectHeight/2, rectHeight/2); 
		}
	}
}
