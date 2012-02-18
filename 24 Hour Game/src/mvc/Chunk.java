package mvc;

import java.awt.Rectangle;
import java.util.HashMap;

public class Chunk extends Sprite{

	public static final int CHUNKDIMENSION = 32; // The number of walls/pixels on each side of the chunk
	public static final int WALLDIMENSION = 10;	 // The number of pixels on each side of a wall.
	
	int[][] tiles;
	
	/*
	 * Definitions of tile values
	 */
	public static final int SPACE = 0;
	public static final int WALL = 1;
	
	int x, y;
	
	private HashMap<String, String> properties;
	
	public Chunk(){
		tiles = new int[CHUNKDIMENSION][CHUNKDIMENSION];
	}
	
	@Override
	public Rectangle getBoundingBox() {
		return new Rectangle(x, y, CHUNKDIMENSION * WALLDIMENSION, CHUNKDIMENSION * WALLDIMENSION);
	}

	@Override
	public void draw() {
		
	}

}
