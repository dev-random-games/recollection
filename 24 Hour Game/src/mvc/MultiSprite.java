package mvc;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class MultiSprite extends Sprite {
	
	HashMap<String, Sprite> sprites;
	
	protected Sprite curSprite;	//Current sprite;
	public String curSpriteName;
	
	public MultiSprite(Sprite initialSprite, String spriteName){
		id();
		
		sprites = new HashMap<String, Sprite>();
		
		sprites.put(spriteName, initialSprite);
		
		curSprite = initialSprite;
	}
	
	public void addSprite(Sprite newSprite, String spriteName){
		sprites.put(spriteName, newSprite);
	}
	
	public void setSprite(String spriteName){
		if (sprites.containsKey(spriteName)){
			curSprite = sprites.get(spriteName);
			curSpriteName = spriteName;
		} else {
			System.err.println("Error: could not find sprite " + spriteName);
		}
	}

	@Override
	public Rectangle getBoundingBox() {
		return curSprite.getBoundingBox();
	}

	@Override
	public void draw() {
		curSprite.draw();
	}

	public void setX(float x) {
		/*
		 * Iterating through a HashMap is complicated.
		 * This sets the x value for each sprite in sprites.
		 */
		Iterator<Entry<String, Sprite>> it = sprites.entrySet().iterator();
		while (it.hasNext()){
			Map.Entry<String, Sprite> pairs = (Map.Entry<String, Sprite>)it.next();
			Sprite sprite = pairs.getValue();
			sprite.p.setX(x);
		}
	}

	public void setY(float y) {
		Iterator<Entry<String, Sprite>> it = sprites.entrySet().iterator();
		while (it.hasNext()){
			Map.Entry<String, Sprite> pairs = (Map.Entry<String, Sprite>)it.next();
			Sprite sprite = pairs.getValue();
			sprite.p.setY(y);
		}
	}
	
	public void setDepth(float depth){
		Iterator<Entry<String, Sprite>> it = sprites.entrySet().iterator();
		while (it.hasNext()){
			Map.Entry<String, Sprite> pairs = (Map.Entry<String, Sprite>)it.next();
			Sprite sprite = pairs.getValue();
			sprite.p.setZ(depth);
		}
	}

}
