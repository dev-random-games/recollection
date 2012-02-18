package mvc;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

/**
 * 
 * A basic R-tree system, supporting adding, updating (both regenerating and
 * resizing), and rectangular and circular searches. refresh() regenerates the
 * entire R-tree, optimizing future efficiency but with a heavy cost. update()
 * resizes all rectangles in the R-tree, optimizing present efficiency with
 * almost no cost, but needs to be called every frame if Sprite locations are
 * changing. Takes a Sprite as input - this can be replaced with any Sprite class
 * that contains public double values x and y.
 * 
 * @author Dylan Swiggett
 */
public class Rtree {

	// Private variables
	int maxCount;
	private Sprite[] sprites;
	private Rtree[] subTrees;
	private int x, y, x1, y1;
	private boolean leaf;

	protected int expandTotal;

	public Rtree(int maxCount) {
		this.maxCount = maxCount;
		sprites = new Sprite[maxCount + 1];
		leaf = true;
	}

	/**
	 * Regenerate the entire R-tree, pulling all of the Sprites out and then
	 * re-adding from scratch.
	 */
	public void refresh() {
		ArrayList<Sprite> SpriteList = getSprites(new ArrayList<Sprite>());
		subTrees = null;
		sprites = new Sprite[maxCount + 1];
		leaf = true;
		for (Sprite p : SpriteList) {
			add(p);
		}
	}

	/**
	 * Return all Sprites within the R-tree and all of its children in an
	 * ArrayList.
	 * 
	 * @param SpriteCollector
	 * @return ArrayList<Sprite>
	 */
	public ArrayList<Sprite> getSprites(ArrayList<Sprite> SpriteCollector) {
		if (leaf) {
			for (Sprite p : sprites) {
				if (p == null) {
					break;
				}
				SpriteCollector.add(p);
			}
		} else {
			for (Rtree tree : subTrees) {
				SpriteCollector = tree.getSprites(SpriteCollector);
			}
		}
		return SpriteCollector;
	}

	/**
	 * Subdivide the R-tree into multiple sub-trees, one for each Sprite. Will be
	 * called automatically if maxCount is reached.
	 */
	public void subdivide() {
		subTrees = new Rtree[maxCount];
		for (int i = 0; i < maxCount; i++) {
			subTrees[i] = new Rtree(maxCount);
			subTrees[i].add(sprites[i]);
		}
		sprites = null;
		leaf = false;
	}

	/**
	 * Select the tree requiring the least enlargement to add the Sprite.
	 * 
	 * @param Sprite
	 * @return
	 */
	public Rtree chooseLeaf(Sprite Sprite) {
		Rtree expandTree = subTrees[0];
		expandTree.expandAmount(Sprite);
		int minAmount = expandTree.expandTotal;
		for (Rtree tree : subTrees) {
			tree.expandAmount(Sprite);
			if (tree.expandTotal < minAmount) {
				expandTree = tree;
				minAmount = tree.expandTotal;
			}
		}
		return expandTree;
	}

	/**
	 * Recursively expand or shrink to fit all Sprites and sub-leafs. Much less
	 * costly than refresh(), but also less effective.
	 */
	public void update() {
		if (leaf) {
			expandLeaf();
		} else {
			for (Rtree tree : subTrees) {
				tree.update();
				expandBranch();
			}
		}
	}

	/**
	 * Return the total amount necessary to expand the R-tree to contain the
	 * Sprite.
	 * 
	 * @param p
	 * @return
	 */
	public int[] expandAmount(Sprite sprite) {
		Rectangle rect = sprite.getBoundingBox();
		int[] amount = new int[] { 0, 0, 0, 0 };
		if (rect.getX() < x) {
			amount[0] = (int) (x - rect.getX());
		}
		if (rect.getX() + rect.getWidth() > x1) {
			amount[2] = (int) (rect.getX() + rect.getWidth() - x1);
		}

		if (rect.getY() < y) {
			amount[1] = (int) (y - rect.getY());
		} else if (rect.getY() + rect.getHeight() > y1) {
			amount[3] = (int) (rect.getY() + rect.getHeight() - y1);
		}
		// expandTotal = (amount[0] + amount[2]) * (amount[1] + amount[3]);
		expandTotal = amount[0] + amount[2] + amount[1] + amount[3];
		return amount;
	}

	/**
	 * Expand R-tree to contain specified Sprite.
	 * 
	 * @param p
	 */
	public void expandLeaf(Sprite p) {
		int[] amount = expandAmount(p);
		x -= amount[0];
		y -= amount[1];
		x1 += amount[2];
		y1 += amount[3];
	}

	/**
	 * Expand R-tree leaf to contain all Sprites within it.
	 */
	public void expandLeaf() {
//		x = (int) sprites[0].x - 1;
//		y = (int) sprites[0].y - 1;
//		x1 = x + 2;
//		y1 = y + 2;
		Rectangle r = sprites[0].getBoundingBox();
		for (Sprite p : sprites) {
			if (p == null) {
				break;
			}
//			expandLeaf(p);
			r = r.union(p.getBoundingBox());
		}
		x = (int) r.getX();
		y = (int) r.getY();
		x1 = (int) (r.getX() + r.getWidth());
		y1 = (int) (r.getY() + r.getHeight());
	}

	/**
	 * Expand branch of the R-tree to contain all sub-rectangles.
	 */
	public void expandBranch() {
		int x, y, x1, y1;
		x = subTrees[0].x;
		y = subTrees[0].y;
		x1 = subTrees[0].x1;
		y1 = subTrees[0].y1;
		for (Rtree tree : subTrees) {
			if (tree.x < x)
				x = tree.x;
			if (tree.y < y)
				y = tree.y;
			if (tree.x1 > x1)
				x1 = tree.x1;
			if (tree.y1 > y1)
				y1 = tree.y1;
		}
		this.x = x;
		this.y = y;
		this.x1 = x1;
		this.y1 = y1;
	}

	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}

//	private ArrayList<Sprite> _getSpritesInCircle(int x, int y, int radius, ArrayList<Sprite> Sprites) {
//		if (leaf) {
//			for (Sprite Sprite : this.sprites) {
//				if (Sprite == null) {
//					break;
//				} else if (distance(Sprite.x, Sprite.y, x, y) <= radius) {
//					Sprites.add(Sprite);
//				}
//			}
//		} else {
//			for (Rtree tree : subTrees) {
//				if (x - radius < tree.x1 && x + radius > tree.x && y - radius < tree.y1 && y + radius > tree.y) {
//					Sprites = tree._getSpritesInCircle(x, y, radius, Sprites);
//				}
//			}
//		}
//		return Sprites;
//	}
//
//	public ArrayList<Sprite> getSpritesInCircle(int x, int y, int radius) {
//		return _getSpritesInCircle(x, y, radius, new ArrayList<Sprite>());
//	}

	private ArrayList<Sprite> _getSpritesInRectangle(int x, int y, int width, int height, ArrayList<Sprite> Sprites) {
		if (leaf) {
			for (Sprite sprite : this.sprites) {
				if (sprite != null){
					Rectangle rect = sprite.getBoundingBox();
					if (x < rect.getX() + rect.getWidth() && x + width > rect.getX() && y < rect.getY() + rect.getHeight() && y + height > rect.getY()) {
						Sprites.add(sprite);
					}
				} else {
					break;
				}
			}
		} else {
			for (Rtree tree : subTrees) {
				if (x < tree.x1 && x + width > tree.x && y < tree.y1 && y + height > tree.y) {
					Sprites = tree._getSpritesInRectangle(x, y, width, height, Sprites);
				}

			}
		}
		return Sprites;
	}

	public ArrayList<Sprite> getSpritesInRectangle(int x, int y, int width, int height) {
		return _getSpritesInRectangle(x, y, width, height, new ArrayList<Sprite>());
	}
	
	public ArrayList<Sprite> getSpritesInRectangle(Rectangle rect) {
		return getSpritesInRectangle((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
	}
	
	public ArrayList<Sprite> getIntersectingSprites(Sprite sprite) {
		ArrayList<Sprite> intersections = getSpritesInRectangle(sprite.getBoundingBox());
		/*
		 * Remove duplicate sprites.
		 */
		ArrayList<Sprite> nonEqualIntersections = new ArrayList<Sprite>();
		for (Sprite sprite1 : intersections){
			if (!sprite1.equals(sprite)){
				nonEqualIntersections.add(sprite1);
			}
		}
		return nonEqualIntersections;
	}

	/**
	 * Insert new Sprite into R-tree. Takes one Sprite as input. If the maximum
	 * Sprite count is reached, subdivide the R-tree in maxCount sub-R-trees.
	 * Otherwise, add the new Sprite to Sprites. To regenerate the whole tree,
	 * call refresh();
	 * 
	 * @param Sprite
	 */
	public void add(Sprite Sprite) {
		if (leaf) {
			int loc;
			for (loc = 0; loc < maxCount; loc++) {
				if (sprites[loc] == null) {
					sprites[loc] = Sprite;
					break;
				}
			}
			if (loc >= maxCount - 1) {
				expandLeaf(Sprite);
				subdivide();
			} else if (loc == 0) {
				// Add first Sprite to new R-tree.
				Rectangle rect = Sprite.getBoundingBox();
				x = (int) rect.getX();
				y = (int) rect.getY();
				x1 = (int) (rect.getX() + rect.getWidth());
				y1 = (int) (rect.getY() + rect.getHeight());
//				x = (int) Sprite.x - 1;
//				y = (int) Sprite.y - 1;
//				x1 = (int) Sprite.x + 1;
//				y1 = (int) Sprite.y + 1;
			} else {
				expandLeaf(Sprite);
			}
		} else {
			chooseLeaf(Sprite).add(Sprite);
			expandBranch();
		}
	}

	/**
	 * Draws the current R-tree and all children to the graphics object as
	 * rectangles.
	 * 
	 * @param g
	 */
	public void draw(float depth) {
//		g.drawRect(x, y, x1 - x, y1 - y);
		
		/*
		 * Draw shape
		 */
		GL11.glColor3f(1f, 1f, 1f);
		
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(x, y, depth);
		GL11.glVertex3f(x1, y, depth);
		GL11.glVertex3f(x1, y1, depth);
		GL11.glVertex3f(x, y1, depth);
		GL11.glEnd();
		
		if (!leaf) {
			for (Rtree tree : subTrees) {
				tree.draw(depth);
			}
		}
	}
}
