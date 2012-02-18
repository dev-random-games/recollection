package mvc;

import java.awt.Rectangle;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class TextSprite extends Sprite {
	private float w, h, r;
	private Texture texture;
	private String text;

	public TextSprite(float x, float y, float width, float depth, String text) {
		this.p = new Vector3D(x, y, depth);
		this.w = width;
		this.h = width / text.length();
		this.text = text;
	}

	@Override
	public Rectangle getBoundingBox() {
		return new Rectangle((int) p.getX(), (int) p.getY(), (int) w, (int) h);
	}

	@Override
	public void draw() {
		/*
		 * Draw texture
		 */
		if (texture == null) {
			texture = TextureUtil.textureFromText(text);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		}

		// r = Math.abs(View.frameCount % 360 - 180);

		/*
		 * Rotate the object around the z axis, then translate it to its
		 * appropriate position in space, x or y + w or h / 2, so that the image
		 * rotates around its center.
		 */
		GL11.glPushMatrix();
		GL11.glTranslatef(p.getX() + w / 2, p.getY() + h / 2, 0);
		GL11.glRotatef(getRot(), 0, 0, 1);

		float xDist = w / 2;
		float yDist = h / 2;

		GL11.glColor3f(1, 1, 1);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		texture.bind();

		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(-xDist, -yDist, p.getZ());
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(xDist, -yDist, p.getZ());
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(xDist, yDist, p.getZ());
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(-xDist, yDist, p.getZ());
		GL11.glEnd();

		/*
		 * Draw outline
		 */

		GL11.glColor3f(0, 0, 0);

		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(-xDist, -yDist, p.getZ());
		GL11.glVertex3f(xDist, -yDist, p.getZ());
		GL11.glVertex3f(xDist, yDist, p.getZ());
		GL11.glVertex3f(-xDist, yDist, p.getZ());
		GL11.glEnd();

		GL11.glPopMatrix();

		GL11.glDisable(GL11.GL_TEXTURE_2D); // If I don't disable this,
											// non-textured sprites get really
											// screwy
	}

	/**
	 * @return the sprite's rotation
	 */
	public float getRot() {
		return r;
	}

	/**
	 * @param r
	 *            -- the new rotation
	 */
	public void setRot(float r) {
		this.r = r;
	}
}
