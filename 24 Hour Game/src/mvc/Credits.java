package mvc;

import java.io.IOException;

import org.lwjgl.openal.AL;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Credits extends Thread{
	View view;

	public Credits(View view){
		this.view = view;
	}
	
	public void run(){
		view.splashMode = true;
		
		try {
			view.viewTranslation = new Vector3D(-19500, -19500, view.SPLASHDISTANCE);
			Thread.sleep(5000);
			
			try {
				AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("/data/audio/credits-song-1.ogg")).playAsSoundEffect(1.0f, 1.0f, false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			while (true){
				view.viewTranslation = new Vector3D(6610, 4950, view.SPLASHDISTANCE);
				Thread.sleep(5000);
				view.viewTranslation = new Vector3D(14610, 4950, view.SPLASHDISTANCE);
				Thread.sleep(5000);
				view.viewTranslation = new Vector3D(10610, 4950, view.SPLASHDISTANCE);
				Thread.sleep(5000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		AL.destroy();
//		System.exit(0);
	}
}
