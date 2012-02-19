package mvc;

import java.util.HashMap;

public class Spectre extends MultiSprite{

	HashMap<String, Sprite> sprites;
//	private Sprite curSprite;	//Current sprite;
	
	Vector3D spectreVelocity, spectrePosition, spectreLastPosition;
	float spectreSensitivity;
	float rot;
	
	static float health = 50;
	static float strength = .1f;
	static float speed = .01f;
	
	public Spectre(Sprite initialSprite, String spriteName) {
		super(initialSprite, spriteName);
		
		sprites = new HashMap<String, Sprite>();
		sprites.put(spriteName, initialSprite);
		
		curSprite = initialSprite;
		curSpriteName = spriteName;
		
		spectreSensitivity = 0.9f;
		spectreVelocity = new Vector3D(0, 0, 0);
		spectrePosition = initialSprite.p;
		spectreLastPosition = initialSprite.p;
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
	
	public void hurt(float damage){
		health -= damage;
	}
	
	public float getDamage(){
		return strength;
	}
}
