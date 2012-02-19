package mvc;

import java.util.HashMap;

public class Character extends MultiSprite {
	
	HashMap<String, Sprite> sprites;
//	private Sprite curSprite;	//Current sprite;
	
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
				System.out.println("Step");
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
}
