package sweden.alexander.fanorona;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

public class Piece {
	private PApplet parrent;
	private PVector displayPos;
	private PVector newPos;
	private float radius;
	private float decreesingRadius;
	private int color;
	private int[] pos;
	private boolean active;
	private boolean selected;
	private PlayingField grid;
	private boolean canBeSelected;
	private ArrayList<int[]> visited;
	private int[] lastDirection;
	private boolean requireConferm;
	private Piece[] canConfermWith;
	private boolean confermOption;

	public Piece(PApplet parrent, int color, int[] pos, PlayingField grid) {
		this.parrent = parrent;
		this.color   = color;
		this.pos     = pos;
		this.grid 	 = grid;
		active 		 = true;
		displayPos   = new PVector(0,0);
		selected     = false;
		canBeSelected = false;
		visited 	 = new ArrayList<int[]>();
		lastDirection = null;
		requireConferm = false;
		canConfermWith = new Piece[2];
		confermOption = false;
		newPos = new PVector();
	}

	public void setDisplayPos(PVector newPos) {
		this.newPos = newPos;
	}

	public void setRadius(float newRadius) {
		radius = newRadius;
	}

	public void setActive(boolean newActive) {
		if (!newActive) {
			decreesingRadius = radius;
		}
		active = newActive;
	}

	public void setCanSelect(boolean newVal) {
		canBeSelected = newVal;
	}

	public boolean requiresConfermation() {
		return requireConferm;
	}

	public void draw() {
		if (active) {
			parrent.fill(color);
			parrent.noStroke();
			float moveSpeed = PApplet.dist(0, 0, parrent.width, parrent.height)/2.5f * 1/parrent.frameRate;
			if (displayPos.x == 0 && displayPos.y == 0) {
				System.out.println(parrent.width/moveSpeed);
			}
			if (selected) {
				parrent.strokeWeight(PApplet.dist(0, 0, parrent.width, parrent.height)/150);
				parrent.stroke(255 - parrent.brightness(color));
			}
			parrent.ellipse(displayPos.x, displayPos.y, radius * 2, radius * 2);
			if (canBeSelected) {
				int color = (getColor('W', parrent) == this.color) ? getColor('B', parrent) : getColor('W', parrent);
				parrent.fill(color);
				parrent.noStroke();
				parrent.ellipse(displayPos.x, displayPos.y, radius/2, radius/2);
			}
			if (confermOption) {
				int color = (getColor('W', parrent) == this.color) ? getColor('B', parrent) : getColor('W', parrent);
				parrent.fill(color);
				parrent.textSize(12);

				float textHeight = parrent.textAscent();
				float percentOfHeight = textHeight/((radius * 2) * 0.8f);
				parrent.textSize(12 * 1/percentOfHeight);
				float textWidth = parrent.textWidth("?");
				float x = (displayPos.x - radius) + ((radius * 2) - textWidth)/2;
				float y = (displayPos.y - radius) + ((radius * 2) - (parrent.textAscent()))/2 + parrent.textAscent();
				parrent.text("?", x, y);

			}
			if ((displayPos.x - newPos.x < -moveSpeed/2 || displayPos.x - newPos.x > moveSpeed/2) ||
					displayPos.y - newPos.y < -moveSpeed/2 || displayPos.y - newPos.y > moveSpeed/2) {
				PVector vect = PVector.sub(newPos, displayPos);
				vect.normalize();
				vect.mult(moveSpeed);
				displayPos.add(vect);
			} else if (displayPos.x != newPos.x || displayPos.y != newPos.y) {
				displayPos.x = newPos.x;
				displayPos.y = newPos.y;
			}
		} else if (decreesingRadius > 0) {
			parrent.fill(color);
			parrent.noStroke();
			if (selected) {
				parrent.strokeWeight(PApplet.dist(0, 0, parrent.width, parrent.height)/150);
				parrent.stroke(255 - parrent.brightness(color));
			}
			parrent.ellipse(displayPos.x, displayPos.y, decreesingRadius * 2, decreesingRadius * 2);
			decreesingRadius -= radius * 0.15;
		}
	}

	public PVector getDisplayPos() {
		return newPos;
	}

	public int[] getPos() {
		return pos;
	}

	public int getColor() {
		return color;
	}

	public void setPos(int[] newPos) {
		visited.add(pos);
		lastDirection = PlayingField.createPos(newPos[0] - pos[0], newPos[1] - pos[1]);
		pos = newPos;
	}

	public void resetMovement() {
		visited.clear();
		lastDirection = null;
	}

	public void setSelected(boolean newSelected) {
		selected = newSelected;
	}

	public boolean isClicked(int mouseX, int mouseY) {
		if (PApplet.dist(mouseX, mouseY, newPos.x, newPos.y) <= radius) {
			selected = !selected;
			return true;
		} else {
			return false;
		}
	}

	public boolean isActive() {
		return active;
	}

	public boolean canCapture() {
		ArrayList<int[]> possibleMoves = getAllPossibleMoves();
		for (int[] newPos : possibleMoves) {
			if (isCaptureMove(newPos[0], newPos[1])) {
				return true;
			}
		}
		return false;
	}

	private ArrayList<int[]> getAllPossibleMoves() {
		MoveDirection[][] directions = grid.getDirections();
		ArrayList<int[]> possibleMoves = new ArrayList<int[]>();
		for (int dRow = -1; dRow < 2; dRow++) {
			for (int dCol = -1; dCol < 2; dCol++) {
				if (!(dRow == 0 && dCol == 0) && pos[0] + dRow >= 0 && pos[0] + dRow < directions.length && pos[1] + dCol >= 0 && pos[1] + dCol < directions[0].length) {
					int[] directionPos = PlayingField.createPos(pos[0] + dRow, pos[1] + dCol);
					int[] newPos = directions[pos[0] + dRow][pos[1] + dCol].getNewPos(directionPos, pos);
					if (newPos != null && isValidMove(newPos[0], newPos[1])) {
						possibleMoves.add(newPos);
					}
				}
			}
		}
		return possibleMoves;
	}

	public boolean canMoveTo(Piece p) {
		if (!p.isActive()) {
			return isValidMove(p.pos[0], p.pos[1]);
		} else {
			return false;
		}
	}

	private boolean isValidMove(int newX, int newY) {
		int[] direction = {newX - pos[0], newY - pos[1]};
		MoveDirection move = grid.getDirections()[pos[0] + direction[0] / 2][pos[1] + direction[1] / 2];
		if (move.validMove(PlayingField.createPos(pos[0] + direction[0] / 2,pos[1] + direction[1] / 2), pos) &&
			!grid.getActualPieceGrid()[newX][newY].isActive() && !containsPos(PlayingField.createPos(newX, newY)) &&
			validDirection(direction)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean containsPos(int[] pos) {
		for (int[] posToCheck : visited) {
			if (posToCheck[0] == pos[0] && posToCheck[1] == pos[1]) {
				return true;
			}
		}
		return false;
	}

	private boolean validDirection(int[] direction) {
		return (lastDirection == null) || (lastDirection[0] != direction[0] || lastDirection[1] != direction[1]);
	}

	private boolean isCaptureMove(int newX, int newY) {
		int[] direction = {newX - pos[0], newY - pos[1]};
		Piece pushPiece = grid.getPiece(newX + direction[0], newY + direction[1]);
		Piece pullPiece = grid.getPiece(pos[0] - direction[0], pos[1] - direction[1]);
		if ((pushPiece != null && pushPiece.isActive() && pushPiece.color != color) ||
			(pullPiece != null && pullPiece.isActive() && pullPiece.color != color)) {
			return true;
		}
		return false;
	}

	public boolean isCaptureMove(Piece p) {
		if (!p.isActive()) {
			return isCaptureMove(p.pos[0], p.pos[1]);
		} else {
			return false;
		}
	}

	public void capture(Piece p) {
		int newX = p.getPos()[0];
		int newY = p.getPos()[1];
		int[] direction = {newX - pos[0], newY - pos[1]};
		Piece pushPiece = grid.getPiece(newX + direction[0], newY + direction[1]);
		Piece pullPiece = grid.getPiece(pos[0] - direction[0], pos[1] - direction[1]);
		if (!((pushPiece != null && pushPiece.isActive() && pushPiece.color != color) && (pullPiece != null && pullPiece.isActive() && pullPiece.color != color))){
			if (pushPiece != null && pushPiece.isActive() && pushPiece.color != color) {
				Piece currPiece = pushPiece;
				int multiplyer = 2;
				while(currPiece != null && currPiece.isActive() && currPiece.color != color) {
					grid.dissablePiece(currPiece.getPos());
					currPiece.setActive(false);
					currPiece = grid.getPiece(newX + multiplyer * direction[0], newY + multiplyer * direction[1]);
					multiplyer += 1;
				}
			}
			if (pullPiece != null && pullPiece.isActive() && pullPiece.color != color) {
				Piece currPiece = pullPiece;
				int multiplyer = 2;
				while(currPiece != null && currPiece.isActive() && currPiece.color != color) {
					grid.dissablePiece(currPiece.getPos());
					currPiece.setActive(false);
					currPiece = grid.getPiece(pos[0] - multiplyer * direction[0], pos[1] - multiplyer * direction[1]);
					multiplyer += 1;
				}
			}
		} else {
			requireConferm = true;
			canConfermWith[0] = grid.getCorospondingPiece(pullPiece);
			canConfermWith[1] = grid.getCorospondingPiece(pushPiece);
			canConfermWith[0].confermOption = true;
			canConfermWith[1].confermOption = true;
		}
	}

	public void conferm(Piece p) {
		if (canConfermWith[0] == p || canConfermWith[1] == p) {
			int newX = p.getPos()[0];
			int newY = p.getPos()[1];
			int[] direction = {newX - pos[0], newY - pos[1]};
			if (direction[0] == -4 || direction[0] == 4 || direction[1] == -4 || direction[1] == 4) {
				direction = PlayingField.createPos(direction[0] / 2, direction[1] / 2);
			}
			Piece currPiece = p;
			int multiplyer = 1;
			while(currPiece != null && currPiece.isActive() && currPiece.color != color) {
				currPiece.setActive(false);
				grid.getPiece(currPiece.getPos()[0], currPiece.getPos()[1]).setActive(false);
				currPiece = grid.getCorospondingPiece(grid.getPiece(newX + multiplyer * direction[0], newY + multiplyer * direction[1]));
				multiplyer += 1;
			}
			requireConferm = false;
			canConfermWith[0].confermOption = false;
			canConfermWith[1].confermOption = false;
			canConfermWith = new Piece[2];
		}
	}

	@Override
	public Piece clone() {
		int[] newPos = {pos[0], pos[1]};
		Piece res = new Piece(parrent, color, newPos, grid);
		res.active = active;
		return res;
	}

	@Override
	public String toString() {
		if (!active) {
			return " ";
		}
		if (color == parrent.color(0)) {
			return "B";
		}else {
			return "W";
		}
	}

	public static int getColor(char letter, PApplet applet) {
		switch (letter) {
		case 'B':
			return applet.color(0);
		case 'W':
			return applet.color(255);
		default:
			return 0;
		}
	}
}
