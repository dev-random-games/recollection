package mvc;

import java.awt.Color;
import java.awt.Rectangle;

import org.lwjgl.opengl.GL11;

public class RectSprite extends Sprite{

	float x, y, w, h, depth;
	public float r;
	Color color;
	
	public RectSprite(float x, float y, float w, float h, float depth, Color color){
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.depth = depth;
		this.color = color;
	}
	
	@Override
	public Rectangle getBoundingBox() {
		return new Rectangle((int) x, (int) y, (int) w, (int) h);
	}

	@Override
	public void draw() {
		
		GL11.glPushMatrix();
		GL11.glTranslatef(x + w / 2, y + h / 2, depth);
		GL11.glRotatef(r, 0, 0, 1);

		/*
		 * Draw shape
		 */
		GL11.glColor3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glVertex3f(- x / 2, - y / 2, 0);
		GL11.glVertex3f(x / 2, - y / 2, 0);
		GL11.glVertex3f(x / 2, y / 2, 0);
		GL11.glVertex3f(- x / 2, y / 2, 0);
		GL11.glEnd();
		
		/*
		 * Draw outline
		 */
		GL11.glColor3f(0, 0, 0);
		
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(- x / 2, - y / 2, 0);
		GL11.glVertex3f(x / 2, - y / 2, 0);
		GL11.glVertex3f(x / 2, y / 2, 0);
		GL11.glVertex3f(- x / 2, y / 2, 0);
		GL11.glEnd();
		
		GL11.glPopMatrix();
	}

}
