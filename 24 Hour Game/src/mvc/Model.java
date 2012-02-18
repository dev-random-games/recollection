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
	
	/*
	 * audio files
	 */
	public Audio creepyTales;
	
	/*
	 * Audio *out* port.
	 */
	static Port audioOut;
	
	AnimationSprite animSprite;
	
	MultiSprite background;
	
	Rtree rtree;
	
	Vector3D characterVelocity;
	
	public Model() {
		sprites = new ArrayList<Sprite>();
		lights = new ArrayList<Light>();
		rtree = new Rtree(2);
		
		sprites.add(new TextureExtrudeSprite(- 200, - 200, 50, 400, 200, "/data/textures/stone.png"));
		sprites.add(new TextureExtrudeSprite(- 200, 200, 450, 50, 200, "/data/textures/stone.png"));
		sprites.add(new TextureExtrudeSprite(- 200, - 200, 400, 50, 200, "/data/textures/stone.png"));
		sprites.add(new TextureExtrudeSprite(200, - 200, 50, 400, 200, "/data/textures/stone.png"));
		
		sprites.add(new TextureSprite(-199, -199, 440, 440, 150, "/data/bg/water.png"));
				
		background = new MultiSprite(new TextureSprite(-1600, -800, 3200, 1600, 0, "/data/bg/water.png"), "water");
		background.addSprite(new TextureSprite(-1600, -800, 3200, 1600, 0, "/data/bg/lavabg.png"), "lava");
		
		background.setSprite("lava");
		sprites.add(background);
		
		animSprite = new AnimationSprite(-100, -100, 200, 200, 151, 10, "/data/char/sticky1.png");
		animSprite.addFrame("/data/char/sticky1.png");
		animSprite.addFrame("/data/char/sticky1.png");
		animSprite.addFrame("/data/char/sticky2.png");
		animSprite.addFrame("/data/char/sticky3.png");
		animSprite.addFrame("/data/char/sticky4.png");
		animSprite.addFrame("/data/char/sticky4.png");
		animSprite.addFrame("/data/char/sticky4.png");
		animSprite.addFrame("/data/char/sticky3.png");
		animSprite.addFrame("/data/char/sticky2.png");

		
		sprites.add(animSprite);
		
		sprites.add(new TextSprite(300, 300, 2000, 100, "testing testing 1 2 3. Does this expand well? Yes it does!"));

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
			creepyTales = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("/data/audio/lemons.wav"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		creepyTales.playAsSoundEffect(1.0f, 1.0f, true);
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
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Rtree newTree = new Rtree(rtree.maxCount);
			for (Sprite sprite : sprites){
				newTree.add(sprite);
			}
			rtree = newTree;
			
			System.out.println(rtree.getIntersectingSprites(animSprite).size());
			
			animSprite.r += .1f;
		}
	}
}
