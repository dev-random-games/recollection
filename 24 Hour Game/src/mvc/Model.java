package mvc;

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
	public Audio creepyTales;
	
	/*
	 * Audio *out* port.
	 */
	static Port audioOut;
	
	Rtree rtree;
	
	Chunk[][] chunks;
	
	boolean rtreeGen = false;
	
	public Model() {
		sprites = new ArrayList<Sprite>();
		lights = new ArrayList<Light>();
		rtree = new Rtree(2);
		
		character = new Character(new TextureSprite(0, 0, 32, 32, 10, "/data/char/goodWalk5.png"), "standing");
		
		AnimationSprite walking = new AnimationSprite(0, 0, 32, 32, 10, 3, "/data/char/goodWalk1.png");
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
		
//		testChunk.tilesA[0][20] = 1; 
//		testChunk.tilesA[1][21] = 1; 

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
//		try {
//			creepyTales = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("/data/audio/lemons.wav"));
//		} catch (IOExcepStion e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		creepyTales.playAsSoundEffect(1.0f, 1.0f, true);
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
	public void run() {
		while (true) {
			character.characterVelocity = character.characterVelocity.scale(character.characterSensitivity);
			character.characterPosition = character.characterPosition.add(character.characterVelocity);
			
			if (character.characterVelocity.length() > 0.1){
				character.setSprite("walking");
				System.out.println("test");
			} else {
				character.setSprite("standing");
			}
			System.out.println(character.curSpriteName);
			
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
						System.out.println(character.getBoundingBox().getX());
					}
				}
			}
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
