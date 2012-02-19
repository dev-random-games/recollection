package mvc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Character extends MultiSprite {
	
	HashMap<String, Sprite> sprites;
//	private Sprite curSprite;	//Current sprite;
	
	float health = 100;
	float strength = 10;
	
	Vector3D characterVelocity, characterPosition, characterLastPosition;
	float characterSensitivity;
	float rot;
	
	public Character(Sprite initialSprite, String spriteName) {
		super(initialSprite, spriteName);
		
		sprites = new HashMap<String, Sprite>();
		sprites.put(spriteName, initialSprite);
		
		curSprite = initialSprite;
		curSpriteName = spriteName;
		
		characterSensitivity = 0.9f;
		characterVelocity = new Vector3D(0, 0, 0);
		characterPosition = initialSprite.p;
		characterLastPosition = initialSprite.p;
		rot = 0;
	}
	
	@Override
	public void draw() {
		if (curSpriteName.equals("walking")){
			int f = ((AnimationSprite) curSprite).getFrameCount();
			int delay = ((AnimationSprite) curSprite).frameDelay;
//			System.out.println(f);
			if (f == 0 * delay || f == 9 * delay){
				try {
					Random random = new Random();
					AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("/data/audio/effects/footstep-" + random.nextInt(4) + ".ogg")).playAsSoundEffect(.7f, .3f, false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		curSprite.setRot(rot);
		curSprite.draw();
	}
	
	public void setRot(float rot){
		this.rot = rot;
	}
	
	public float getWidth() {
		return (float) curSprite.getBoundingBox().getWidth();
	}
	
	public float getHeight() {
		return (float) curSprite.getBoundingBox().getHeight();
	}
	
	public void hurt(float damage){
		health -= damage;
	}
	
	public float getDamage(){
		return strength;
	}
}
