package mvc;

import java.awt.Container;
import java.awt.Font;
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
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import static org.lwjgl.opengl.GL11.*;

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

	public static final int WIDTH = 800;
	public static final int HEIGHT = 800;
	public static final int INITDISTANCE = 1000;	//Initial distance of view from z=0
	
	// If true and available, full screen mode will be used.
	private static final boolean FULLSCREENENABLED = false;
	public Vector3D viewTranslation;// Vector specifying the translation of the
									// view in 2D space.

	public static int frameCount = 0;
	
	private static final int padding = 100; // Amount of padding on a default
											// window.
	
	TextureLoader textureLoader;
	
	TrueTypeFont defaultFont;

	/* 
	 * Stuff for smooth camera movement and automatic camera movement
	 */
	public static final float CAMERAVELOCITYPRESERVATION = 0.90f; //camera velocity to be preserved at each frame
	//attraction to focal point (0 will result in no attraction; higher values may make the view jumpy)
	public static final float FOCALPOINTATTRACTION = 0.01f;
	Vector3D cameraVelocity;
	Vector3D focalPoint;
	
	@SuppressWarnings("deprecation")
	public View(Model model){
		this.model = model;
		
		viewTranslation = new Vector3D(0, 0, INITDISTANCE);
		
		if (FULLSCREENENABLED){
			setDisplayMode(Display.getWidth(), Display.getHeight(), true);
		} else {
			setDisplayMode(WIDTH, HEIGHT, false);
		}
		
		textureLoader = new TextureLoader();
		
		cameraVelocity = new Vector3D(0, 0, 0);
		focalPoint = new Vector3D(0, 0, INITDISTANCE);
	}
	
	/*
	 * Set up the camera position for a new frame
	 */
	public void setCamera(){
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); 
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();

		/*
		 * Calculate camera velocity from user input, focal point and residue 
		 * then add it to the view translation.
		 */
		this.cameraVelocity = (this.cameraVelocity.scale(CAMERAVELOCITYPRESERVATION)).add(this.focalPoint.subtract(this.viewTranslation).scale(FOCALPOINTATTRACTION));
		this.viewTranslation = this.viewTranslation.add(this.cameraVelocity);
		
		float whRatio = (float) WIDTH / (float) HEIGHT;
		GLU.gluPerspective(45, whRatio, 1, 1000);
		GLU.gluLookAt(viewTranslation.getX(), viewTranslation.getY(), viewTranslation.getZ(),
					viewTranslation.getX(), viewTranslation.getY(), 0, 0, 1, 0);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
	}
	
	/**
	 * Set the display mode to be used 
	 * 
	 * Stoled from the LWJGL website, because I can't bother to write all this on my own.
	 * 
	 * @param width The width of the display required
	 * @param height The height of the display required
	 * @param fullscreen True if we want fullscreen mode
	 */
	public void setDisplayMode(int width, int height, boolean fullscreen) {

	    // return if requested DisplayMode is already set
	    if ((Display.getDisplayMode().getWidth() == width) && 
	        (Display.getDisplayMode().getHeight() == height) && 
		(Display.isFullscreen() == fullscreen)) {
		    return;
	    }

	    try {
	        DisplayMode targetDisplayMode = null;
			
		if (fullscreen) {
		    DisplayMode[] modes = Display.getAvailableDisplayModes();
		    int freq = 0;
					
		    for (int i=0;i<modes.length;i++) {
		        DisplayMode current = modes[i];
						
			if ((current.getWidth() == width) && (current.getHeight() == height)) {
			    if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
			        if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
				    targetDisplayMode = current;
				    freq = targetDisplayMode.getFrequency();
	                        }
	                    }

			    // if we've found a match for bpp and frequence against the 
			    // original display mode then it's probably best to go for this one
			    // since it's most likely compatible with the monitor
			    if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
	                        (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
	                            targetDisplayMode = current;
	                            break;
	                    }
	                }
	            }
	        } else {
	            targetDisplayMode = new DisplayMode(width,height);
	        }

	        if (targetDisplayMode == null) {
	            System.out.println("Failed to find value mode: "+width+"x"+height+" fs="+fullscreen);
	            return;
	        }

	        Display.setDisplayMode(targetDisplayMode);
	        Display.setFullscreen(fullscreen);
				
	    } catch (LWJGLException e) {
	        System.out.println("Unable to setup mode "+width+"x"+height+" fullscreen="+fullscreen + e);
	    }
	}
	
	/*
	 * Update the display, then push the changes to the screen
	 */
	@SuppressWarnings("deprecation")
	public void run(){
		/*
		 * Create a new display, which all of the GL11 commands will be applied to.
		 */
		try {
			Display.create();
		} catch (Exception e2) {
			e2.printStackTrace();
			System.exit(0);
		}
		
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
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		GL11.glClearColor(0f, 0f, 0f, 1f);
		
		// load a default font for on screen text
				Font awtFont = new Font("Arial", Font.BOLD, 24);
				defaultFont = new TrueTypeFont(awtFont, false);
		
//		try {
//			TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("/data/alot.png"));
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
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
			Rectangle viewRect = new Rectangle((int) viewTranslation.getX() - WIDTH / 2, (int) viewTranslation.getY() - HEIGHT / 2,
												WIDTH, HEIGHT);
			
			// TODO figure out what's wrong with viewRect and fix it
			for (Sprite sprite : model.sprites){
				if (sprite.getBoundingBox().intersects(viewRect)){
					sprite.draw();
				}
			}
			
			/* Lighting */
			for (Light light : model.lights) {
				glMaterial(GL_FRONT, GL_SPECULAR, light.matSpecular);				// sets specular material color
				glMaterialf(GL_FRONT, GL_SHININESS, 50.0f);					// sets shininess

				glLight(GL_LIGHT0, GL_POSITION, light.lightPosition);				// sets light position
				glLight(GL_LIGHT0, GL_SPECULAR, light.whiteLight);				// sets specular light to white
				glLight(GL_LIGHT0, GL_DIFFUSE, light.whiteLight);					// sets diffuse light to white
				glLightModel(GL_LIGHT_MODEL_AMBIENT, light.lModelAmbient);		// global ambient light 
			}
			
			glEnable(GL_LIGHTING);										// enables lighting
			glEnable(GL_LIGHT0);										// enables light0
			
			glEnable(GL_COLOR_MATERIAL);								// enables opengl to use glColor3f to define material color
			glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);			// tell opengl glColor3f effects the ambient and diffuse properties of material			
			Display.update();
		}
		Display.destroy();
		AL.destroy();
		System.exit(0);
	}
}
