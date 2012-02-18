package mvc;

import java.awt.Rectangle;
import java.util.HashMap;

public class Chunk extends Sprite{

	public static final int CHUNKDIMENSION = 32; // The number of walls/pixels on each side of the chunk
	public static final int WALLDIMENSION = 10;	 // The number of pixels on each side of a wall.
	
	int x, y;
	
	private HashMap<String, String> properties;
	
	public Chunk(){
		
	}
	
	@Override
	public Rectangle getBoundingBox() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		
	}

}
