package mvc;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 * 
 * View component of the MVC system. This handles the entire graphical
 * front-end. The main window should ALWAYS be resizable, so keep this in mind
 * whenever writing code for this class.
 * 
 * @author Dylan Swiggett
 * 
 */
public class View extends Thread {
	Model model;
	JFrame frame;

	public static final int WIDTH = 700;
	public static final int HEIGHT = 700;

	public Vector3D viewTranslation;// Vector specifying the translation of the
									// view in 2D space.

	public static int frameCount = 0;

	// If true and available, full screen mode will be used.
	private static final boolean FULLSCREENENABLED = false;
	private static final int padding = 100; // Amount of padding on a default
											// window.
	
	TextureLoader textureLoader;

	
	public View(Model model){
		this.model = model;
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		viewTranslation = new Vector3D(0, 0, HEIGHT);
		
		textureLoader = new TextureLoader();
	}
	
	/*
	 * Set up the camera position for a new frame
	 */
	public void setCamera(){
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); 
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();

		float whRatio = (float) WIDTH / (float) HEIGHT;
		GLU.gluPerspective(45, whRatio, 1, 1000);
		GLU.gluLookAt(viewTranslation.getX(), viewTranslation.getY(), viewTranslation.getZ(),
					viewTranslation.getX(), viewTranslation.getY(), 0, 0, 1, 0);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
	}
	
	/*
	 * Update the display, then push the changes to the screen
	 */
	public void run(){
		try {
			/*
			 * Initialize display
			 */
			Display.create();
			
			GL11.glViewport(0, 0, WIDTH, HEIGHT);
			
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDepthFunc(GL11.GL_LEQUAL);
			GL11.glShadeModel(GL11.GL_SMOOTH); // Enables Smooth Shading
			/*
			 * Enable masking transparency.
			 */
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);	
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glAlphaFunc(GL11.GL_GREATER,0.1f);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
			GL11.glClearColor(0f, 0f, 0f, 1f);
			
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			
			try {
				TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("/data/alot.png"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			while (!Display.isCloseRequested()) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				/*
				 * All OpenGL Display code goes here!
				 */
				setCamera(); // *DO NOT CHANGE THIS*
				
				for (Sprite sprite : model.sprites){
					sprite.draw();
				}
				
				Display.update();
			}
		} catch (LWJGLException e1) {
			e1.printStackTrace();
		}
		Display.destroy();
		System.exit(0);
	}
}
