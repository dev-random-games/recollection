package mvc;

import java.util.HashMap;

public class Character extends MultiSprite {
	
	HashMap<String, TextureSprite> sprites;
	private TextureSprite curSprite;	//Current sprite;
	
	Vector3D characterVelocity, characterPosition;
	float characterSensitivity;
	float rot;
	
	public Character(TextureSprite initialSprite, String spriteName) {
		super(initialSprite, spriteName);
		
		sprites = new HashMap<String, TextureSprite>();
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
}
