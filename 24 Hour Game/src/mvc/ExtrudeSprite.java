package mvc;

import java.awt.Color;
import java.awt.Rectangle;

import org.lwjgl.opengl.GL11;

public class ExtrudeSprite extends Sprite{

	float x, y, w, h, depth;
	Color color;
	
	public ExtrudeSprite(float x, float y, float w, float h, float depth, Color color){
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

		/*
		 * Draw shape
		 */
		
		GL11.glColor3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
		
		/*
		 * Front
		 */
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glVertex3f(x, y, depth);
		GL11.glVertex3f(x + w, y, depth);
		GL11.glVertex3f(x + w, y + h, depth);
		GL11.glVertex3f(x, y + h, depth);
		GL11.glEnd();
		/*
		 * Bottom
		 */
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glVertex3f(x, y, depth);
		GL11.glVertex3f(x + w, y, depth);
		GL11.glVertex3f(x + w, y, 0);
		GL11.glVertex3f(x, y, 0);
		GL11.glEnd();
		/*
		 * Top
		 */
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glVertex3f(x, y + h, depth);
		GL11.glVertex3f(x + w, y + h, depth);
		GL11.glVertex3f(x + w, y + h, 0);
		GL11.glVertex3f(x, y + h, 0);
		GL11.glEnd();
		/*
		 * Left
		 */
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glVertex3f(x, y, depth);
		GL11.glVertex3f(x, y + h, depth);
		GL11.glVertex3f(x, y + h, 0);
		GL11.glVertex3f(x, y, 0);
		GL11.glEnd();
		/*
		 * Right
		 */
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glVertex3f(x + w, y, depth);
		GL11.glVertex3f(x + w, y + h, depth);
		GL11.glVertex3f(x + w, y + h, 0);
		GL11.glVertex3f(x + w, y, 0);
		GL11.glEnd();
		
		/*
		 * Draw outline
		 */
		
		GL11.glColor3f(0, 0, 0);
		
		/*
		 * Front
		 */
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(x, y, depth);
		GL11.glVertex3f(x + w, y, depth);
		GL11.glVertex3f(x + w, y + h, depth);
		GL11.glVertex3f(x, y + h, depth);
		GL11.glEnd();
		/*
		 * Bottom
		 */
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3f(x, y, 0);

		GL11.glVertex3f(x, y, depth);
		GL11.glVertex3f(x + w, y, depth);
		GL11.glVertex3f(x + w, y, 0);
		GL11.glEnd();
		/*
		 * Top
		 */
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3f(x, y + h, 0);
		GL11.glVertex3f(x, y + h, depth);
		GL11.glVertex3f(x + w, y + h, depth);
		GL11.glVertex3f(x + w, y + h, 0);
		GL11.glEnd();
		/*
		 * Left
		 */
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3f(x, y, 0);
		GL11.glVertex3f(x, y, depth);
		GL11.glVertex3f(x, y + h, depth);
		GL11.glVertex3f(x, y + h, 0);
		GL11.glEnd();
		/*
		 * Right
		 */
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3f(x + w, y, 0);
		GL11.glVertex3f(x + w, y, depth);
		GL11.glVertex3f(x + w, y + h, depth);
		GL11.glVertex3f(x + w, y + h, 0);
		GL11.glEnd();
	}

}
