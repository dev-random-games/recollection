package mvc;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public abstract class Sprite{
	public Vector3D p;	//Position (x, y) in 2D space, and z for rendering preference.
	public double r;	//Rotation around the z axis (in radians).
	
	/*
	 * Return the rectangle that surrounds the sprite, for collisions
	 */
	public abstract Rectangle getBoundingBox();
	
	/*
	 * Rotate the image and draw as appropriate, assuming that rendering preference has already
	 * been handled.
	 */
	public abstract void draw();
}
