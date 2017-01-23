package sweden.alexander.fanorona;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.res.Resources;

import processing.core.PApplet;

public class Parser {
	private static ArrayList<Character> CHARACTERS;
	static {
		CHARACTERS = new ArrayList<Character>();
		CHARACTERS.add('W');
		CHARACTERS.add('B');
		CHARACTERS.add('-');
		CHARACTERS.add('|');
		CHARACTERS.add('/');
		CHARACTERS.add('\\');
		CHARACTERS.add(' ');
	}
	
	public static MoveDirection[][] parseDirection(String file, PApplet parrent) throws ParseException {
		MoveDirection directions[][] = null;
		try {
			//List<String> lines = Files.readAllLines(Paths.get(file));
			String text = LoadFile(file,parrent);
			List<String> lines = Arrays.asList(text.split("\r\n"));
			directions = new MoveDirection[lines.size()][lines.get(0).length()];
			for (int line = 0; line < lines.size(); line++) {
				for (int pos = 0; pos < lines.get(line).length(); pos++) {
					if (pos > directions[0].length) {
						throw new ParseException("Uneaven length of lines at line: " + line, line);
					}
					MoveDirection token = MoveDirection.getToken(lines.get(line).charAt(pos));
					if (token != null) {
						directions[line][pos] = token;
					} else {
						throw new ParseException("Invalid token at line: " + line + " position: " + pos, pos);
					}
					if (pos == lines.get(line).length() - 1 && pos < directions[0].length - 1 && pos != 0) {
						throw new ParseException("Uneaven length of lines at line: " + line, pos);
					}
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return directions;
	}
	
	public static Piece[][] parsePieces(String file, PApplet parrent, PlayingField grid) throws ParseException {
		Piece pieces[][] = null;
		ArrayList<ArrayList<Piece>> listPieces = new ArrayList<ArrayList<Piece>>();
		try {
			String text = LoadFile(file,parrent);
			List<String> lines = Arrays.asList(text.split("\r\n"));

			pieces = new Piece[lines.size()][lines.get(0).length()];
			for (int line = 0; line < lines.size(); line++) {
				int tempColor = Piece.getColor(lines.get(line).charAt(0), parrent);
				if (tempColor != 0 || lines.get(line).charAt(0) == ' ') {
					listPieces.add(new ArrayList<Piece>());
					String l = lines.get(line);
					for (int pos = 0; pos < lines.get(line).length(); pos++) {
						if (pos > pieces[0].length) {
							throw new ParseException("Uneaven length of lines at line: " + line, line);
						}
						int color = Piece.getColor(lines.get(line).charAt(pos), parrent);
						if (color != 0) {
							int[] arrayPos = {line, pos};
							listPieces.get(listPieces.size() - 1).add(new Piece(parrent, color, arrayPos, grid));
						} else if (lines.get(line).charAt(pos) == ' ') {
							int[] arrayPos = {line, pos};
							listPieces.get(listPieces.size() - 1).add(new Piece(parrent, color, arrayPos, grid));
							listPieces.get(listPieces.size() - 1).get(listPieces.get(listPieces.size() - 1).size() - 1).setActive(false);
						} else if (!CHARACTERS.contains((Character) lines.get(line).charAt(pos))){
							throw new ParseException("Invalid character at line: " + line + " column: " + pos + " \"" + lines.get(line).charAt(pos) + "\"", line);
						}
						if (pos == lines.get(line).length() - 1 && pos < pieces[0].length - 1 && pos != 0) {
							throw new ParseException("Uneaven length of lines at line: " + line + " length: ", line);
						}
					}
				}
			}

			pieces = new Piece[listPieces.size()][listPieces.get(0).size()];
			for (int row = 0; row < listPieces.size(); row++) {
				for (int col = 0; col < listPieces.get(0).size(); col++) {

					pieces[row][col] = listPieces.get(row).get(col);
				}
			}
			return pieces;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}


	private static  String LoadFile(String fileName, PApplet parrent) throws IOException
	{
		//Create a InputStream to read the file into
		InputStream iS;
		Resources resources = parrent.getActivity().getResources();
		//get the file as a stream
		iS = resources.getAssets().open(fileName);

		//create a buffer that has the same size as the InputStream
		byte[] buffer = new byte[iS.available()];
		//read the text file as a stream, into the buffer
		iS.read(buffer);
		//create a output stream to write the buffer into
		ByteArrayOutputStream oS = new ByteArrayOutputStream();
		//write this buffer to the output stream
		oS.write(buffer);
		//Close the Input and Output streams
		oS.close();
		iS.close();

		//return the output stream as a String
		return oS.toString();
	}
}
