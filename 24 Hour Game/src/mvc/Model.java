package mvc;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.lwjgl.BufferUtils;
import org.newdawn.slick.Color;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 * 
 * The Model component of the MVC system. All direct interfacing with the engine
 * should be here.
 * 
 * @author Dylan Swiggett
 * 
 */
public class Model extends Thread {

	public ArrayList<Sprite> sprites;
	public ArrayList<Light> lights;
	public Character character;
	
	/*
	 * audio files
	 */
	public static Audio background;
	
	/*
	 * Audio *out* port.
	 */
	static Port audioOut;
	
	Rtree rtree;
	
	Chunk[][] chunks;
	
	boolean rtreeGen = false;
	public static int rumble = 0; 	//Enable an EARTHQUAKE for *rumble* frames
	
	public Model() {
		sprites = new ArrayList<Sprite>();
		lights = new ArrayList<Light>();
		rtree = new Rtree(2);
		
		AnimationSprite splash = new AnimationSprite(-3200, -2400, 1600, 1200, 1, 20, "/data/misc/titlesub.png");
		splash.addFrame("/data/misc/titlesubspace.png");
		sprites.add(splash);
		
		character = new Character(new TextureSprite(0, 0, 30, 30, 10, "/data/char/goodWalk5.png"), "standing");
		
		AnimationSprite walking = new AnimationSprite(0, 0, 30, 30, 10, 2, "/data/char/goodWalk1.png");
		walking.addFrame("/data/char/goodWalk2.png");
		walking.addFrame("/data/char/goodWalk3.png");
		walking.addFrame("/data/char/goodWalk4.png");
		walking.addFrame("/data/char/goodWalk5.png");
		walking.addFrame("/data/char/goodWalk6.png");
		walking.addFrame("/data/char/goodWalk7.png");
		walking.addFrame("/data/char/goodWalk8.png");
		walking.addFrame("/data/char/goodWalk9.png");
		walking.addFrame("/data/char/goodWalk9.png");
		walking.addFrame("/data/char/goodWalk8.png");
		walking.addFrame("/data/char/goodWalk7.png");
		walking.addFrame("/data/char/goodWalk6.png");
		walking.addFrame("/data/char/goodWalk5.png");
		walking.addFrame("/data/char/goodWalk4.png");
		walking.addFrame("/data/char/goodWalk3.png");
		walking.addFrame("/data/char/goodWalk2.png");
		walking.addFrame("/data/char/goodWalk1.png");
		
		character.addSprite(walking, "walking");
		
		sprites.add(character);
		
		chunks = new Chunk[0][0];
		
//		Chunk testChunk = new Chunk(0, 0);
		
//		testChunk.loadChunk("/data/levels/testChunk.png");
//		testChunk.setState(false);
		
//		sprites.add(testChunk);
		
		chunks = ChunkLoader.loadChunks("/data/chunks/", "chunkData.txt", sprites);
		
		character.characterPosition = new Vector3D(50, chunks[0].length * Chunk.CHUNKDIMENSION * Chunk.WALLDIMENSION - 80, 10);
//		character.characterPosition = new Vector3D(32 * 15 + 25 * 15, 14 * 15, 10);

		Light light = new Light();

		light.matSpecular = BufferUtils.createFloatBuffer(4);
		light.matSpecular.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();

		light.lightPosition = BufferUtils.createFloatBuffer(4);
		light.lightPosition.put(110.0f).put(110.0f).put(100.0f).put(0.0f).flip();

		light.whiteLight = BufferUtils.createFloatBuffer(4);
		light.whiteLight.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();

		light.lModelAmbient = BufferUtils.createFloatBuffer(4);
		light.lModelAmbient.put(0.5f).put(0.5f).put(0.5f).put(1.0f).flip();

		lights.add(light);
		
		/*
		 * Load audio files and play initial music
		 * 
		 * The following is a non-comprehensive list of ways to load audio files and the corresponding method that plays them
		 * 
		 * AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("file.ogg")); => playAsSoundEffect(1.0f, 1.0f, false);
		 * AudioLoader.getStreamingAudio("OGG", ResourceLoader.getResource("file.ogg")); => oggStream.playAsMusic(1.0f, 1.0f, true);
		 * 
		 * AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("file.wav")); => wavEffect.playAsSoundEffect(1.0f, 1.0f, false);
		 * 
		 */
		try {
			background = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("/data/audio/effects/bang-1.ogg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		background.playAsSoundEffect(1.0f, 1.0f, false);
	}
	
	/*
	 * Load the specified audio file to a clip, or return null if not found.
	 */
	public Clip getAudioClip(String path) {
		try {
			System.out.println("Loading audio " + path + "...");
			AudioInputStream stream = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(path));
			try {
				Clip clip = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, null));
				clip.open(stream);
				return clip;
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (UnsupportedAudioFileException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	/*
	 * Checks for headphones, then speakers, establishing an audio output port
	 * to whichever is available. Complains and returns false if no audio out is
	 * available.
	 */
	public boolean connectAudio() {
		if (AudioSystem.isLineSupported(Port.Info.HEADPHONE)) {
			try {
				audioOut = (Port) AudioSystem.getLine(Port.Info.HEADPHONE);
				audioOut.open();
				System.out.println("Connected headphones.");
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("No headphones found, checking speakers.");
			if (AudioSystem.isLineSupported(Port.Info.SPEAKER)) {
				try {
					audioOut = (Port) AudioSystem.getLine(Port.Info.SPEAKER);
					audioOut.open();
					System.out.println("Connected speakers.");
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Error -- no sound output available");
			}
		}
		return false;
	}

	/**
	 * This is the main loop of the program -- should call iterate() in APOPS.
	 */
	@SuppressWarnings("deprecation")
	public void run() {
		while (true) {
			//System.out.println(character.characterVelocity.toString());
			
			character.characterVelocity = character.characterVelocity.scale(character.characterSensitivity);
//			Character tempCharacter = new Character(null, null);
//			Vector3D tempCharacterPosition = character.characterPosition.add(character.characterVelocity);
//			tempCharacter.characterPosition = tempCharacterPosition;
//			ArrayList<Sprite> interChar = rtree.getIntersectingSprites(character);
//			boolean intersecting = false;
//			
//			Vector3D tempPosition = character.characterPosition.add(character.characterVelocity);
//			
//			//System.out.println(">>>" + Math.abs((int) tempPosition.getX() / Chunk.CHUNKDIMENSION) + ' ' + Math.abs((int) tempPosition.getY() / Chunk.CHUNKDIMENSION));
//			int chunkX = (int) tempPosition.getX() / (Chunk.CHUNKDIMENSION * Chunk.WALLDIMENSION);
//			int chunkY = (int) tempPosition.getY() / (Chunk.CHUNKDIMENSION * Chunk.WALLDIMENSION);
//			int inChunkPixelsX = ((int) tempPosition.getX()) % (Chunk.CHUNKDIMENSION * Chunk.WALLDIMENSION);
//			int inChunkPixelsY = ((int) tempPosition.getY()) % (Chunk.CHUNKDIMENSION * Chunk.WALLDIMENSION);
//			int inChunkTilesX = inChunkPixelsX / Chunk.WALLDIMENSION;
//			int inChunkTilesY = inChunkPixelsY / Chunk.WALLDIMENSION;
//			System.out.println("Tiles: " + inChunkTilesX + "," + inChunkTilesY + " Pixels: " + inChunkPixelsX + "," + inChunkPixelsY);
//			//Chunk c = chunks[Math.abs((int) tempPosition.getX() / (Chunk.CHUNKDIMENSION * Chunk.WALLDIMENSION))][Math.abs((int) tempPosition.getY() / (Chunk.CHUNKDIMENSION * Chunk.WALLDIMENSION))];
//			Chunk c = chunks[chunkX][chunkY];
//			//int inChunkX = (int) tempPosition.getX() % (Chunk.CHUNKDIMENSION * Chunk.WALLDIMENSION);
//			//int inChunkY = (int) tempPosition.getY() % (Chunk.CHUNKDIMENSION * Chunk.WALLDIMENSION);
//			boolean collidingWithA = (c.tilesA[inChunkTilesX][inChunkTilesY] == 1) && c.tileState;
//			boolean collidingWithB = (c.tilesB[inChunkTilesX][inChunkTilesY] == 1) && !c.tileState;
//			boolean colliding = collidingWithA || collidingWithB;
//			if (!colliding) {
//			//if (!(((c.tilesA[inChunkTilesX][inChunkTilesY] == 1) && c.tileState) || 
//					//((c.tilesB[inChunkTilesX][inChunkTilesY] == 1) && (!c.tileState)))) {
//				character.characterPosition = character.characterPosition.add(character.characterVelocity);
//				character.setX(character.characterPosition.getX());
//				character.setY(character.characterPosition.getY());
//			//}
//			}
			
			character.characterPosition = character.characterPosition.add(character.characterVelocity);
			
			if (character.characterVelocity.length() > 0.1){
				character.setSprite("walking");
				//System.out.println("test");
			} else {
				character.setSprite("standing");
			}
			//System.out.println(character.curSpriteName);
			
			character.setX(character.characterPosition.getX());
			character.setY(character.characterPosition.getY());

//			this.viewTranslation = this.viewTranslation.add(tShis.cameraVelocity);
			

			Rtree newTree = new Rtree(rtree.maxCount);
			for (Sprite sprite : sprites){
				newTree.add(sprite);
			}
			rtree = newTree;
			rtreeGen = true;

			for (Chunk[] chunkSublist : chunks){
				for (Chunk chunk : chunkSublist){
					if (chunk.getBoundingBox().intersects(character.getBoundingBox())){
						chunk.activateSwitches(chunks);
						/*
						 * Calculate the local bounding box for the player within the rectangle
						 */
						Rectangle playerRect = character.getBoundingBox();
						playerRect.move((int) (playerRect.getX() - chunk.getBoundingBox().getX()), (int) (playerRect.getY() - chunk.getBoundingBox().getY()));
						for (int x = 0; x < Chunk.CHUNKDIMENSION; x++){
							for (int y = 0; y < Chunk.CHUNKDIMENSION; y++){
								if (chunk.getTiles()[x][y] == Chunk.WALL){ 
									Rectangle tileRect = new Rectangle(x * Chunk.WALLDIMENSION, y * Chunk.WALLDIMENSION, Chunk.WALLDIMENSION, Chunk.WALLDIMENSION);
									if (playerRect.intersects(tileRect)){
										Rectangle collisionRect = tileRect.intersection(playerRect);
										if (collisionRect.getHeight() > collisionRect.getWidth()){
											if (collisionRect.getX() == playerRect.getX()){
												character.characterPosition = character.characterPosition.add(new Vector3D((int) collisionRect.getWidth(), 0, 0));
											} else {
												character.characterPosition = character.characterPosition.add(new Vector3D(- (int) collisionRect.getWidth(), 0, 0));
											}
										} else {
											if (collisionRect.getY() == playerRect.getY()){
												character.characterPosition = character.characterPosition.add(new Vector3D(0, (int) collisionRect.getHeight(), 0));
											} else {
												character.characterPosition = character.characterPosition.add(new Vector3D(0, - (int) collisionRect.getHeight(), 0));
											}
										}
									}
								}
							}
						}
					}
				}
			}
			
			//ArrayList<Sprite> interChar = rtree.getIntersectingSprites(character);
			
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
