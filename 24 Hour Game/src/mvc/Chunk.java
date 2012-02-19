package mvc;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.newdawn.slick.Color;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk extends Sprite{

	public static final int CHUNKDIMENSION = 32; // The number of walls/pixels on each side of the chunk
	public static final int WALLDIMENSION = 15;	 // The number of pixels on each side of a wall.
	
	public int[][] tilesA, tilesB;
	
	public boolean tileState = true; // True = tilesA, false = tilesB
	
	Hashtable<String, Boolean> entrySwitches;	//Changes to other chunks that will change when this one is entered
	
	/*
	 * Definitions of tile values
	 */
	public static final int STONE = 0;
	public static final int WALL = 1;
	
	/*
	 * Buffered sprites for drawing tiles
	 */
	private Sprite wallSprite;
	private Sprite stoneSprite;
	
	int x, y;
	
	private HashMap<String, String> properties;
	
	public Chunk(int x, int y){
		id();
		
		this.x = x;
		this.y = y;
		tilesA = new int[CHUNKDIMENSION][CHUNKDIMENSION];
		tilesB = new int[CHUNKDIMENSION][CHUNKDIMENSION];
		
		wallSprite = new Wall(0, 0, WALLDIMENSION, WALLDIMENSION);
//		wallSprite = new TextureExtrudeSprite(0, 0, WALLDIMENSION, WALLDIMENSION, 1000, "data/textures/stone.png");
		stoneSprite = new TextureSprite(0, 0, WALLDIMENSION, WALLDIMENSION, 0, "/data/textures/stone.png");
		
		entrySwitches = new Hashtable<String, Boolean>();
	}
	
	@Override
	public Rectangle getBoundingBox() {
		return new Rectangle(x, y, CHUNKDIMENSION * WALLDIMENSION, CHUNKDIMENSION * WALLDIMENSION);
	}

	@Override
	public void draw() {
		int[][] tiles;	//Filled with whatever set of tiles the chunk is currently represented by - A or B
		if (tileState){
			tiles = tilesA;
		} else {
			tiles = tilesB;
		}
		for (int x = 0; x < CHUNKDIMENSION; x++){
			for (int y = 0; y < CHUNKDIMENSION; y++){
				int tile = tiles[x][y];
				int x1 = this.x + WALLDIMENSION * x;
				int y1 = this.y + WALLDIMENSION * y;
				/*
				 * Move all buffered sprites
				 */
				wallSprite.move(x1, y1);
				stoneSprite.move(x1, y1);
				
				if (tile == WALL){
//					new Wall(x1, y1, WALLDIMENSION, WALLDIMENSION).draw();
					wallSprite.draw();
				} else if (tile == STONE){
					stoneSprite.draw();
//					new RectSprite(x1, y1, WALLDIMENSION, WALLDIMENSION, 0, Color.gray).draw();
				}
			}
		}
	}

	/*
	 * Load the chunk image data into this chunk.
	 * 
	 * Loads images of size 65 x 32, representing two states.
	 * 
	 * BLACK = Wall
	 * 
	 */
	public Chunk loadChunk(String filePath){
		
		InputStream file = ResourceLoader.getResourceAsStream(filePath);
		try {
			BufferedImage chunkImg = ImageIO.read(file);
			/*
			 * Load tilesA
			 */
			for (int x = 0; x < 32; x ++){
				for (int y = 0; y < 32; y ++){
					int rgb = chunkImg.getRGB(x, y);
					int red = (rgb & 0x00ff0000) >> 16;
					int green = (rgb & 0x0000ff00) >> 8;
					int blue = rgb & 0x000000ff;
					
					if (red == 0 && blue == 0 && green == 0){
						tilesA[x][CHUNKDIMENSION - 1 - y] = WALL;	// Y implemented to eliminate vertical flip from BufferedImage->openGL y conversion
					}
				}
			}
			/*
			 * Load tilesB
			 */
			for (int x = 33; x < 65; x ++){
				for (int y = 0; y < 32; y ++){
					int rgb = chunkImg.getRGB(x, y);
					int red = (rgb & 0x00ff0000) >> 16;
					int green = (rgb & 0x0000ff00) >> 8;
					int blue = rgb & 0x000000ff;
					
					if (red == 0 && blue == 0 && green == 0){
						tilesB[x - 33][CHUNKDIMENSION - 1 - y] = WALL;	// Y implemented to eliminate vertical flip from BufferedImage->openGL y conversion
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return this;
	}
	
	/*
	 * Change the visible tile - true for A, false for B
	 */
	public void setState(boolean tileState){
		this.tileState = tileState;
	}
	
	/*
	 * Activate all switches for the current location
	 */
	public void activateSwitches(Chunk[][] chunks){
		/*
		 * Iterating through a HashMap is complicated.
		 * This sets the x value for each sprite in sprites.
		 */
		Iterator<Entry<String, Boolean>> it = entrySwitches.entrySet().iterator();
		while (it.hasNext()){
			System.out.println("ACTIVATING");
			Map.Entry<String, Boolean> pairs = (Map.Entry<String, Boolean>)it.next();
			String loc = pairs.getKey();
			String[] coords = loc.split("_");
			System.out.println(pairs.getValue());
			chunks[Integer.parseInt(coords[0])][Integer.parseInt(coords[1])].setState(pairs.getValue());
		}
	}
}
