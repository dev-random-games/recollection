package mvc;

import java.io.IOException;

import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;


/**
 * 
 * Initialize the MVC system and begin all of the separate threads. And static
 * utility functions and variables should be written here as well.
 * 
 * @author Dylan Swiggett
 * 
 */
public class Main extends Thread {
	Model model;
	View view;
	Controller controller;

	public static void main(String[] args) {
		new Main().start();
	}

	public void run() {
		
		model = new Model();
		view = new View(model);
		controller = new Controller(model, view);
		
		model.start();
		controller.start();
		view.start();
		
		try {
//			Audio sound = AudioLoader.getStreamingAudio("OGG", ResourceLoader.getResource("/data/audio/lemons.ogg"));
//			sound.playAsSoundEffect(1.0f, 1.0f, true);S
			Audio oggEffect = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("/data/audio/crazytales.ogg"));
			oggEffect.playAsSoundEffect(1.0f, 1.0f, false);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while (true){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
//			view.repaint();
		}
	}

}
