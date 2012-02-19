package mvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
	
	public static Chunk[][] loadChunks(String path, String dataFile, ArrayList<Sprite> sprites){
		
		/*
		 * Load da data file bro
		 */
		BufferedReader dataIn = new BufferedReader(new InputStreamReader(ResourceLoader.getResourceAsStream(path + dataFile)));
		
		int rows = Integer.parseInt(getNextLine(dataIn));
		int columns = Integer.parseInt(getNextLine(dataIn));
		Chunk[][] chunks = new Chunk[columns][rows];
		
		for (int row = 0; row < rows; row++){
			String rowDat = getNextLine(dataIn);
			String[] colsDat = rowDat.split(" ");
			for (int col = 0; col < columns; col++){
				String colDat = colsDat[col];
				String[] nameSplit = colDat.split(":");
				
				int chunkWidth = Chunk.WALLDIMENSION * Chunk.CHUNKDIMENSION;
				
				Chunk newChunk = new Chunk(col * chunkWidth, row * chunkWidth).loadChunk(path + nameSplit[0] + ".png");
				
				chunks[col][row] = newChunk;
				
				if (nameSplit.length > 1){
					String[] entrySwitches = nameSplit[1].split(",");
					
					/*
					 * Calculate all of the requests for chunk switches when the chunk is entered, then set them all up
					 */
					for (String entrySwitch : entrySwitches){
						String[] entryNameSplit = entrySwitch.split("->");
						newChunk.entrySwitches.put(entryNameSplit[0], !entryNameSplit.equals("B"));	//Default to chunk version A
					}
				}
				
				sprites.add(newChunk);
			}
		}
		
		return chunks;
	}
}
