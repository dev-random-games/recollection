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
import java.util.Random;

import javax.imageio.ImageIO;

import org.newdawn.slick.Color;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk extends Sprite{

	public static final int CHUNKDIMENSION = 32; // The number of walls/pixels on each side of the chunk
	public static final int WALLDIMENSION = 15;	 // The number of pixels on each side of a wall.
	
	public int[][] tilesA, tilesB;
	
	public boolean tileState = true; // True = tilesA, false = tilesB
	
	Hashtable<String, Boolean> entrySwitches;	//Changes to other chunks that will change when this one is entered
	
//	private Audio entrySound;
	private boolean soundPlayed = false;
	
	/*
	 * Definitions of tile values
	 */
	public static final int STONE = 0;
	public static final int WALL = 1;
	public static final int BLOOD = 3;
	public static final int GRADIENT = 2;
	
	/*
	 * Buffered sprites for drawing tiles
	 */
	private Sprite wallSprite;
	private Sprite stoneSprite;
	private Sprite bloodSprite;
	private Sprite gradientSprite;
	
	int x, y;
	
	Hashtable<String, String> properties;
	
	public Chunk(int x, int y){
		id();
		
		this.x = x;
		this.y = y;
		tilesA = new int[CHUNKDIMENSION][CHUNKDIMENSION];
		tilesB = new int[CHUNKDIMENSION][CHUNKDIMENSION];
		
		wallSprite = new Wall(0, 0, WALLDIMENSION, WALLDIMENSION);
//		wallSprite = new TextureExtrudeSprite(0, 0, WALLDIMENSION, WALLDIMENSION, 1000, "data/textures/stone.png");
		stoneSprite = new TextureSprite(0, 0, WALLDIMENSION, WALLDIMENSION, 0, "/data/textures/stone.png");
		bloodSprite = new TextureSprite(0, 0,WALLDIMENSION, WALLDIMENSION, 0, "/data/scenery/blood" + new Random().nextInt(4) + ".png");
		gradientSprite = new TextureSprite(0, 0, WALLDIMENSION, WALLDIMENSION, 0, "/data/textures/gradient.png");
		
		entrySwitches = new Hashtable<String, Boolean>();
		properties = new Hashtable<String, String>();
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
		
		if (properties.containsKey("floorTex")){
			stoneSprite = new TextureSprite(0, 0, WALLDIMENSION, WALLDIMENSION, 0, "/data/textures/" + properties.get("floorTex") + ".png");
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
				bloodSprite.move(x1, y1);
				
				if (tile == WALL){
//					new Wall(x1, y1, WALLDIMENSION, WALLDIMENSION).draw();
					wallSprite.draw();
				} else if (tile == STONE){
					stoneSprite.draw();
//					new RectSprite(x1, y1, WALLDIMENSION, WALLDIMENSION, 0, Color.gray).draw();
				} else if (tile == BLOOD){
					bloodSprite.draw();
				}
			}
		}
	}
	
	public int[][] getTiles(){
		if (tileState){
			return tilesA;
		} else {
			return tilesB;
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
					} else if (red == 255 && blue == 0 && green == 0){
						tilesA[x][CHUNKDIMENSION - 1 - y] = BLOOD;
					} else if (red == 255 && blue == 255 && green == 0){
						Spectre spectre = new Spectre(new TextureSprite(x * WALLDIMENSION + this.x, (CHUNKDIMENSION - y) * WALLDIMENSION + this.y, 30, 30, 10, "/data/char/spectre.png"), "default");
						System.out.println("SPECTRE: " + spectre.spectrePosition.getX() + ", " + spectre.spectrePosition.getY());
						Model.spectres.add(spectre);
						Model.sprites.add(spectre);
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
					} else if (red == 255 && blue == 0 && green == 0){
						tilesB[x - 33][CHUNKDIMENSION - 1 - y] = BLOOD;
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
	
	public String getState(){
		if (tileState){
			return "A";
		} else {
			return "B";
		}
	}
	
	/*
	 * Activate all switches for the current location
	 */
	public void activateSwitches(Chunk[][] chunks){
		
		/*
		 * Play sound if not already played
		 */
		if (!soundPlayed && properties.containsKey("playSound")){
			soundPlayed = true;
			if (Model.background != null){
				Model.background.stop();
			}
			try {
				Model.background = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("/data/audio/" + properties.get("playSound") + ".ogg"));
				Model.background.playAsSoundEffect(1.0f, 1.0f, false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (properties.containsKey("stopSound")){
			if (Model.background != null){
				Model.background.stop();
			}
		}
		
		if (properties.containsKey("win")){
			if (Model.win == -1){
				Model.win = 1000;
				Spectre.speed = .05f;
				Spectre.strength = 5;
			}
		}
		
		/*
		 * Iterating through a HashMap is complicated.
		 * This sets the x value for each sprite in sprites.
		 */
		Iterator<Entry<String, Boolean>> it = entrySwitches.entrySet().iterator();
		while (it.hasNext()){
			Map.Entry<String, Boolean> pairs = (Map.Entry<String, Boolean>)it.next();
			String loc = pairs.getKey();
			String[] coords = loc.split("_");
			//System.out.println(pairs.getValue());
			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			if (!chunks[x][y].getState().equals(pairs.getValue() ? "A" : "B")){
				chunks[x][y].setState(pairs.getValue());
				System.out.println("CHANGE");
				Random random = new Random();
				try {
					AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("/data/audio/effects/rock-slide-" + random.nextInt(5) + ".ogg")).playAsSoundEffect(1.0f, 1.0f, false);
					Model.rumble = random.nextInt(50) + 50;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
}
