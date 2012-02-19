package mvc;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class AnimationSprite extends Sprite{
	
	public float w, h, r;
	
	private ArrayList<Texture> frames;
	private Queue<String> texturePathBuffer;
	
	private int frameCount;	// Counts the number of elapsed frames.
	private int frameDelay;	// Time between frames, in # of times drawn.
	
	
	public AnimationSprite(float x, float y, float w, float h, float depth, int speed, String texturePath){
		id();
		
		frames = new ArrayList<Texture>();
		texturePathBuffer = new LinkedList<String>();
		
		texturePathBuffer.add(texturePath);
		
		frameCount = 0;
		frameDelay = speed;
		
		this.p = new Vector3D(x, y, depth);
		this.w = w;
		this.h = h;
	}
	
	public void addFrame(String texturePath){
		texturePathBuffer.add(texturePath);
	}

	@Override
	public Rectangle getBoundingBox() {
		return new Rectangle((int) p.getX(), (int) p.getY(), (int) w, (int) h);
	}

	@Override
	public void draw() {
		while(texturePathBuffer.size() > 0){
			try {
				Texture texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(texturePathBuffer.poll()));
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
				frames.add(texture);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("ANIM");
		
		Texture currentTexture = frames.get((int) (frameCount / frameDelay));
		
		/*
		 * Rotate the object around the z axis, then translate it to its appropriate position in space,
		 * x or y + w or h / 2, so that the image rotates around its center.
		 */
		GL11.glPushMatrix();
		GL11.glTranslatef(p.getX() + w / 2, p.getY() + h / 2, 0);
		GL11.glRotatef(r, 0, 0, 1);
		
		float xDist = w / 2;
		float yDist = h / 2;
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		GL11.glColor3f(1, 1, 1);
		
		currentTexture.bind();
		
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
		
		frameCount += 1;
		if (frameCount >= frameDelay * frames.size()){
			frameCount = 0;
		}
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	public void setRot(float r){
		this.r = r;
	}

}
