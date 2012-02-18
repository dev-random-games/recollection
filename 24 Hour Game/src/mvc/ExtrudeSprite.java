package mvc;

import java.awt.Rectangle;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

public class ExtrudeSprite extends Sprite{

	float w, h;
	Color color;
	
	public ExtrudeSprite(float x, float y, float w, float h, float depth, Color color){
		this.p = new Vector3D(x, y, depth);
		this.w = w;
		this.h = h;
		this.color = color;
	}
	
	@Override
	public Rectangle getBoundingBox() {
		return new Rectangle((int) p.getX(), (int) p.getY(), (int) w, (int) h);
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
		GL11.glVertex3f(p.getX(), p.getY(), p.getZ());
		GL11.glVertex3f(p.getX() + w, p.getY(), p.getZ());
		GL11.glVertex3f(p.getX() + w, p.getY() + h, p.getZ());
		GL11.glVertex3f(p.getX(), p.getY() + h, p.getZ());
		GL11.glEnd();
		/*
		 * Bottom
		 */
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glVertex3f(p.getX(), p.getY(), p.getZ());
		GL11.glVertex3f(p.getX() + w, p.getY(), p.getZ());
		GL11.glVertex3f(p.getX() + w, p.getY(), 0);
		GL11.glVertex3f(p.getX(), p.getY(), 0);
		GL11.glEnd();
		/*
		 * Top
		 */
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glVertex3f(p.getX(), p.getY() + h, p.getZ());
		GL11.glVertex3f(p.getX() + w, p.getY() + h, p.getZ());
		GL11.glVertex3f(p.getX() + w, p.getY() + h, 0);
		GL11.glVertex3f(p.getX(), p.getY() + h, 0);
		GL11.glEnd();
		/*
		 * Left
		 */
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glVertex3f(p.getX(), p.getY(), p.getZ());
		GL11.glVertex3f(p.getX(), p.getY() + h, p.getZ());
		GL11.glVertex3f(p.getX(), p.getY() + h, 0);
		GL11.glVertex3f(p.getX(), p.getY(), 0);
		GL11.glEnd();
		/*
		 * Right
		 */
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glVertex3f(p.getX() + w, p.getY(), p.getZ());
		GL11.glVertex3f(p.getX() + w, p.getY() + h, p.getZ());
		GL11.glVertex3f(p.getX() + w, p.getY() + h, 0);
		GL11.glVertex3f(p.getX() + w, p.getY(), 0);
		GL11.glEnd();
		
		/*
		 * Draw outline
		 */
		
		GL11.glColor3f(0, 0, 0);
		
		/*
		 * Front
		 */
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(p.getX(), p.getY(), p.getZ());
		GL11.glVertex3f(p.getX() + w, p.getY(), p.getZ());
		GL11.glVertex3f(p.getX() + w, p.getY() + h, p.getZ());
		GL11.glVertex3f(p.getX(), p.getY() + h, p.getZ());
		GL11.glEnd();
		/*
		 * Bottom
		 */
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3f(p.getX(), p.getY(), 0);

		GL11.glVertex3f(p.getX(), p.getY(), p.getZ());
		GL11.glVertex3f(p.getX() + w, p.getY(), p.getZ());
		GL11.glVertex3f(p.getX() + w, p.getY(), 0);
		GL11.glEnd();
		/*
		 * Top
		 */
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3f(p.getX(), p.getY() + h, 0);
		GL11.glVertex3f(p.getX(), p.getY() + h, p.getZ());
		GL11.glVertex3f(p.getX() + w, p.getY() + h, p.getZ());
		GL11.glVertex3f(p.getX() + w, p.getY() + h, 0);
		GL11.glEnd();
		/*
		 * Left
		 */
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3f(p.getX(), p.getY(), 0);
		GL11.glVertex3f(p.getX(), p.getY(), p.getZ());
		GL11.glVertex3f(p.getX(), p.getY() + h, p.getZ());
		GL11.glVertex3f(p.getX(), p.getY() + h, 0);
		GL11.glEnd();
		/*
		 * Right
		 */
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3f(p.getX() + w, p.getY(), 0);
		GL11.glVertex3f(p.getX() + w, p.getY(), p.getZ());
		GL11.glVertex3f(p.getX() + w, p.getY() + h, p.getZ());
		GL11.glVertex3f(p.getX() + w, p.getY() + h, 0);
		GL11.glEnd();
	}

}
