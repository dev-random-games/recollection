package mvc;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.newdawn.slick.util.ResourceLoader;

public class Chunk extends Sprite{

	public static final int CHUNKDIMENSION = 32; // The number of walls/pixels on each side of the chunk
	public static final int WALLDIMENSION = 10;	 // The number of pixels on each side of a wall.
	
	public int[][] tilesA, tilesB;
	
	public boolean tileState = true; // True = tilesA, false = tilesB
	
	/*
	 * Definitions of tile values
	 */
	public static final int SPACE = 0;
	public static final int WALL = 1;
	
	int x, y;
	
	private HashMap<String, String> properties;
	
	public Chunk(int x, int y){
		this.x = x;
		this.y = y;
		tilesA = new int[CHUNKDIMENSION][CHUNKDIMENSION];
		tilesB = new int[CHUNKDIMENSION][CHUNKDIMENSION];
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
				if (tile == WALL){
					new Wall(this.x + WALLDIMENSION * x, this.y + WALLDIMENSION * y, WALLDIMENSION, WALLDIMENSION).draw();
				} else if (tile == SPACE){
//					new Wall(this.x + WALLDIMENSION * x, this.y + WALLDIMENSION * y, WALLDIMENSION, WALLDIMENSION).draw();
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
						tilesA[x][y] = WALL;
					}
				}
			}
			/*
			 * Load tilesB
			 */
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return this;
	}
	
}
