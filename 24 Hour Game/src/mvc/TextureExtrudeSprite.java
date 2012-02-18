package mvc;

import java.awt.Rectangle;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class TextureExtrudeSprite extends Sprite{

	float w, h;
	Color color;
	private Texture texture;
	private String texturePath;
	
	public TextureExtrudeSprite(float x, float y, float w, float h, float depth, String texturePath){
		id();
		
		this.p = new Vector3D(x, y, depth);
		this.w = w;
		this.h = h;
		this.texturePath = texturePath;
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
		if (texture == null){
			try {
				texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(texturePath));
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		float xDist = w / 2;
		float yDist = h / 2;
		
		GL11.glColor3f(1, 1, 1);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
		texture.bind();
		
		/*
		 * Draw shape
		 */
		
		/*
		 * Front
		 */
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(p.getX(), p.getY(), p.getZ());
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(p.getX() + w, p.getY(), p.getZ());
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(p.getX() + w, p.getY() + h, p.getZ());
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(p.getX(), p.getY() + h, p.getZ());
		GL11.glEnd();
		/*
		 * Bottom
		 */
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(p.getX(), p.getY(), p.getZ());
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(p.getX() + w, p.getY(), p.getZ());
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(p.getX() + w, p.getY(), 0);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(p.getX(), p.getY(), 0);
		GL11.glEnd();
		/*
		 * Top
		 */
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(p.getX(), p.getY() + h, p.getZ());
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(p.getX() + w, p.getY() + h, p.getZ());
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(p.getX() + w, p.getY() + h, 0);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(p.getX(), p.getY() + h, 0);
		GL11.glEnd();
		/*
		 * Left
		 */
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(p.getX(), p.getY(), p.getZ());
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(p.getX(), p.getY() + h, p.getZ());
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(p.getX(), p.getY() + h, 0);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(p.getX(), p.getY(), 0);
		GL11.glEnd();
		/*
		 * Right
		 */
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glVertex3f(p.getX() + w, p.getY(), p.getZ());
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(p.getX() + w, p.getY() + h, p.getZ());
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(p.getX() + w, p.getY() + h, 0);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(p.getX() + w, p.getY(), 0);
		GL11.glTexCoord2f(0, 0);
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
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}

}
