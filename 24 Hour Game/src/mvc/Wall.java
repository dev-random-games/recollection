package mvc;

import org.newdawn.slick.Color;

public class Wall extends ExtrudeSprite{

	public Wall(float x, float y, float w, float h) {
		super(x, y, w, h, View.INITDISTANCE + 2000, Color.black);
//		super(x, y, w, h, View.INITDISTANCE + 200, "/data/textures/gradient.png");
		// TODO Auto-generated constructor stub
	}

}
