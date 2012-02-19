package mvc;

import java.util.HashMap;

public class Character extends MultiSprite {
	
	HashMap<String, Sprite> sprites;
//	private Sprite curSprite;	//Current sprite;
	
	Vector3D characterVelocity, characterPosition;
	float characterSensitivity;
	float rot;
	
	public Character(Sprite initialSprite, String spriteName) {
		super(initialSprite, spriteName);
		
		sprites = new HashMap<String, Sprite>();
		sprites.put(spriteName, initialSprite);
		
		curSprite = initialSprite;
		
		characterSensitivity = 0.9f;
		characterVelocity = new Vector3D(0, 0, 0);
		characterPosition = new Vector3D(0, 0, 0);
		rot = 0;
	}
	
	@Override
	public void draw() {
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
