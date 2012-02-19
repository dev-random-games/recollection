package mvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.newdawn.slick.util.ResourceLoader;

public class ChunkLoader {
	
	public static String getNextLine(BufferedReader in){
		String line = "";
		while (line.equals("") && line != null){
			try {
				line = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return line;
	}
	
	public static Chunk[][] loadChunks(String path, String dataFile){
		
		/*
		 * Load da data file bro
		 */
		BufferedReader dataIn = new BufferedReader(new InputStreamReader(ResourceLoader.getResourceAsStream(path + dataFile)));
		
		int rows = Integer.parseInt(getNextLine(dataIn));
		int columns = Integer.parseInt(getNextLine(dataIn));
		Chunk[][] chunks = new Chunk[columns][rows];
		
		
		
		return chunks;
	}
}
