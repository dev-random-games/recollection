package mvc;


import java.awt.Rectangle;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

public class RectSprite extends Sprite{

	float w, h;
	public float r;
	Color color;
	
	public RectSprite(float x, float y, float w, float h, float depth, Color red){
		id();
		
		this.p = new Vector3D(x, y, depth);
		this.w = w;
		this.h = h;
		this.color = red;
	}
	
	@Override
	public Rectangle getBoundingBox() {
		return new Rectangle((int) p.getX(), (int) p.getY(), (int) w, (int) h);
	}

	@Override
	public void draw() {
		
		GL11.glPushMatrix();
		GL11.glTranslatef(p.getX() + w / 2, p.getY() + h / 2, p.getZ());
		GL11.glRotatef(r, 0, 0, 1);

		/*
		 * Draw shape
		 */
		GL11.glColor3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glVertex3f(- p.getX() / 2, - p.getY() / 2, 0);
		GL11.glVertex3f(p.getX() / 2, - p.getY() / 2, 0);
		GL11.glVertex3f(p.getX() / 2, p.getY() / 2, 0);
		GL11.glVertex3f(- p.getX() / 2, p.getY() / 2, 0);
		GL11.glEnd();
		
		/*
		 * Draw outline
		 */
		GL11.glColor3f(0, 0, 0);
		
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(- p.getX() / 2, - p.getY() / 2, 0);
		GL11.glVertex3f(p.getX() / 2, - p.getY() / 2, 0);
		GL11.glVertex3f(p.getX() / 2, p.getY() / 2, 0);
		GL11.glVertex3f(- p.getX() / 2, p.getY() / 2, 0);
		GL11.glEnd();
		
		GL11.glPopMatrix();
	}

}
