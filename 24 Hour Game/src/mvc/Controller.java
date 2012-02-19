package mvc;

import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;

/**
 * 
 * Controller component of the MVC system. This handles all user input, and
 * transmits it to the engine. The primary functionality should be setting up
 * all event listeners.
 * 
 * @author Dylan Swiggett
 * 
 */
public class Controller extends Thread {
	Model model;	//Has one way access to the Model
	View view;		//Has one way access to the View
	
	KeyboardFocusManager keyManager;
	
	boolean mouseDown;
	
	boolean[] keysPressed;
	
	float rotSensitivity;
	float moveSensitivity;
	
	public Controller(Model model, View view){
		this.model = model;
		this.view = view;
		
		/*
		 * A Key Dispatcher doesn't rely on the focus.1
		 * All key events will be captured.
		 */
		keyManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		
		keysPressed = new boolean[255];
		
		mouseDown = false;
		
		rotSensitivity = 2f;
		moveSensitivity = .06f;
	}
	
	public void run(){
		while (true){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			/*
			 * Catch all keyboard events
			 */
			while (Keyboard.next()){
				if (Keyboard.getEventKeyState()){
					keyPressed(Keyboard.getEventKey());
				} else {
					keyReleased(Keyboard.getEventKey());
				}
			}
			
			if (!view.splashMode) {
				/*
				 * Catch mouse events
				 */
				if (Mouse.isButtonDown(0)){
					if (!mouseDown){
						mouseDown = true;
						mousePressed(Mouse.getX(), Mouse.getY());
					}
				} else if (mouseDown){
					mouseDown = false;
					mouseReleased(Mouse.getX(), Mouse.getY());
				}
				
				if (keysPressed[Keyboard.KEY_UP]){
					Vector3D movementVector = new Vector3D(0, moveSensitivity, 0);
					Vector3D up = new Vector3D(0, 0, 1);
					model.character.characterVelocity = model.character.characterVelocity.add(movementVector.multiply(up.rotationM((float) (model.character.rot * Math.PI / 180))));
				}
	//			if (keysPressed[Keyboard.KEY_A]){
	//				model.character.characterVelocity = model.character.characterVelocity.add(new Vector3D(-1, 0, 0));
	//			}
	//			if (keysPressed[Keyboard.KEY_S]){
	//				model.character.characterVelocity = model.character.characterVelocity.add(new Vector3D(0, -1, 0));
	//			}
	//			if (keysPressed[Keyboard.KEY_D]){
	//				model.character.characterVelocity = model.character.characterVelocity.add(new Vector3D(1, 0, 0));
	//			}
				if (keysPressed[Keyboard.KEY_LEFT]){
					model.character.setRot(model.character.rot + rotSensitivity);
				}
	//			iSf (keysPressed[Keyboard.KEY_UP]){
	//				model.character.characterVelocity = model.character.characterVelocity.add(new Vector3D(0, 1, 0));
	//			}
				if (keysPressed[Keyboard.KEY_RIGHT]){
					model.character.setRot(model.character.rot - rotSensitivity);
				}
				if (keysPressed[Keyboard.KEY_DOWN]){
					
				}
				if (keysPressed[Keyboard.KEY_E]){
					model.creepyTales.playAsSoundEffect(1.0f, 1.0f, false);
				}
			}
			if (keysPressed[Keyboard.KEY_SPACE]){
				view.splashMode = false;
				view.viewTranslation = model.character.characterPosition.add(new Vector3D(15, 15, 50));
			}
			if (keysPressed[Keyboard.KEY_ESCAPE]){
				AL.destroy();
				System.exit(0);
			}
		}
	}
	
	/**
	 * Called by the Mouse Listener when the mouse is pressed.
	 * 
	 * @param evt
	 */
	public void mousePressed(int x, int y){
		System.out.println(x + ", " + y);
		
		int worldX = x + (int) view.viewTranslation.getX() - view.WIDTH / 2;
		int worldY = y + (int) view.viewTranslation.getY() - view.HEIGHT / 2;
		
		view.focalPoint = new Vector3D(worldX, worldY, view.focalPoint.getZ());
	}
	
	/**
	 * Called by the Mouse Listener when the mouse is released.
	 * 
	 * @param evt
	 */
	public void mouseReleased(int x, int y){
		
	}
	
	/**
	 * Called by the Mouse Listener when the mouse is clicked.
	 * 
	 * @param evt
	 */
	public void mouseClicked(MouseEvent evt){
		
	}
	
	/**
	 * Called by the Mouse Listener when the mouse is dragged.
	 * 
	 * @param evt
	 */
	public void mouseDragged(MouseEvent evt){
		
	}
	
	/**
	 * Called by the Key Listener when a key is pressed.
	 * 
	 * @param evt
	 */
	public void keyPressed(int key){
		keysPressed[key] = true;
	}
	
	/**
	 * Called by the Key Listener when a key is released.
	 * 
	 * @param evt
	 */
	public void keyReleased(int key){
		keysPressed[key] = false;
	}
}
