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
import java.util.Random;

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

	public static final int WIDTH = 1024;
	public static final int HEIGHT = 768;
	public static final int INITDISTANCE = 250;	//Initial distance of view from z=0
	public static final int SPLASHDISTANCE = 1000;
	
	// If true and available, full screen mode will be used.
	private static final boolean FULLSCREENENABLED = false;
	public Vector3D viewTranslation;// Vector specifying the translation of the
									// view in 2D space.

	public static int frameCount = 0;
	
	private static final int padding = 100; // Amount of padding on a default
											// window.
	
	TextureLoader textureLoader;
	
	TextSprite header;

	/* 
	 * Stuff for smooth camera movement and automatic camera movement
	 */
	public static final float CAMERAVELOCITYPRESERVATION = 0.90f; //camera velocity to be preserved at each frame
	//attraction to focal point (0 will result in no attraction; higher values may make the view jumpy)
	public static final float FOCALPOINTATTRACTION = 0.01f;
	Vector3D cameraVelocity;
	Vector3D focalPoint;
	Vector3D characterHover;
	
	boolean splashMode;
	
	@SuppressWarnings("deprecation")
	public View(Model model){
		this.model = model;
		
		viewTranslation = new Vector3D(-2580, -1550, SPLASHDISTANCE);
		
		if (FULLSCREENENABLED){
			setDisplayMode(Display.getWidth(), Display.getHeight(), true);
		} else {
			setDisplayMode(WIDTH, HEIGHT, false);
		}
		
		textureLoader = new TextureLoader();
		
		cameraVelocity = new Vector3D(0, 0, 0);
		focalPoint = new Vector3D(0, 0, INITDISTANCE);
		
		header = new TextSprite(0, 0, 500, 600, "Testing the Engine");
//		model.sprites.add(header);
		
		splashMode = true;
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
		if (!splashMode) {
			this.characterHover = model.character.characterPosition.add(new Vector3D(model.character.getWidth() / 2, model.character.getHeight() / 2, INITDISTANCE));
			this.cameraVelocity = (this.cameraVelocity.scale(CAMERAVELOCITYPRESERVATION)).add((characterHover.subtract(this.viewTranslation).scale(FOCALPOINTATTRACTION)));
			this.viewTranslation = this.viewTranslation.add(this.cameraVelocity);
		}
		
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
		
		while (!Display.isCloseRequested()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			/*
			 * All OpenGL Display code goes here!
			 */
//			focalPoint = model.characterPosition;
			if (!splashMode) { 
				if (Model.rumble > 2){
					viewTranslation = new Vector3D(viewTranslation.getX(), viewTranslation.getY(), INITDISTANCE + new Random().nextInt(Model.rumble / 2) - Model.rumble / 4);
					Model.rumble--;
				} else {
					Model.rumble = 0;
				}
			}
			
			setCamera(); // *DO NOT CHANGE THIS*
			float viewScale = 1;
			Rectangle viewRect = new Rectangle((int) (viewTranslation.getX() - WIDTH * viewScale / 2), (int) (viewTranslation.getY() - HEIGHT * viewScale / 2),
												(int) (WIDTH * viewScale), (int) (HEIGHT * viewScale));
			
			// TODO figure out what's wrong with viewRect and fix it
			/*
			 * Stuck some padding in there for good measure -- draws only sprites that fall within 100 of the viewRect
			 */
			for (Sprite sprite : model.rtree.getSpritesInRectangle((int) viewRect.getX() - 1, (int) viewRect.getY() - 1,
																	(int) viewRect.getWidth() + 2, (int) viewRect.getHeight() + 2)){
					sprite.draw();
			}
//			model.rtree.draw(400);
			
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
	
	
	public void rollCredits() throws InterruptedException {
		splashMode = true;
		
		viewTranslation = new Vector3D(-9500, -9500, SPLASHDISTANCE);
		Thread.sleep(8000);
		viewTranslation = new Vector3D(6610, 4950, SPLASHDISTANCE);
		Thread.sleep(5000);
		viewTranslation = new Vector3D(14610, 4950, SPLASHDISTANCE);
		Thread.sleep(5000);
		viewTranslation = new Vector3D(10610, 4950, SPLASHDISTANCE);
		Thread.sleep(5000);
		
		AL.destroy();
		System.exit(0);
	}
}
