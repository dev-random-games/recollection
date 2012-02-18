package mvc;

import java.awt.Graphics;
import java.util.ArrayList;

/**
 * 
 * A basic R-tree system, supporting adding, updating (both regenerating and
 * resizing), and rectangular and circular searches. refresh() regenerates the
 * entire R-tree, optimizing future efficiency but with a heavy cost. update()
 * resizes all rectangles in the R-tree, optimizing present efficiency with
 * almost no cost, but needs to be called every frame if point locations are
 * changing. Takes a Point as input - this can be replaced with any Point class
 * that contains public double values x and y.
 * 
 * @author Dylan Swiggett
 */
public class Rtree {

	// Private variables
	private int maxCount;
	private Point[] points;
	private Rtree[] subTrees;
	private int x, y, x1, y1;
	private boolean leaf;

	protected int expandTotal;

	public Rtree(int maxCount) {
		this.maxCount = maxCount;
		points = new Point[maxCount + 1];
		leaf = true;
	}

	/**
	 * Regenerate the entire R-tree, pulling all of the points out and then
	 * re-adding from scratch.
	 */
	public void refresh() {
		ArrayList<Point> pointList = getPoints(new ArrayList<Point>());
		subTrees = null;
		points = new Point[maxCount + 1];
		leaf = true;
		for (Point p : pointList) {
			add(p);
		}
	}

	/**
	 * Return all points within the R-tree and all of its children in an
	 * ArrayList.
	 * 
	 * @param pointCollector
	 * @return ArrayList<Point>
	 */
	public ArrayList<Point> getPoints(ArrayList<Point> pointCollector) {
		if (leaf) {
			for (Point p : points) {
				if (p == null) {
					break;
				}
				pointCollector.add(p);
			}
		} else {
			for (Rtree tree : subTrees) {
				pointCollector = tree.getPoints(pointCollector);
			}
		}
		return pointCollector;
	}

	/**
	 * Subdivide the R-tree into multiple sub-trees, one for each point. Will be
	 * called automatically if maxCount is reached.
	 */
	public void subdivide() {
		subTrees = new Rtree[maxCount];
		for (int i = 0; i < maxCount; i++) {
			subTrees[i] = new Rtree(maxCount);
			subTrees[i].add(points[i]);
		}
		points = null;
		leaf = false;
	}

	/**
	 * Select the tree requiring the least enlargement to add the point.
	 * 
	 * @param point
	 * @return
	 */
	public Rtree chooseLeaf(Point point) {
		Rtree expandTree = subTrees[0];
		expandTree.expandAmount(point);
		int minAmount = expandTree.expandTotal;
		for (Rtree tree : subTrees) {
			tree.expandAmount(point);
			if (tree.expandTotal < minAmount) {
				expandTree = tree;
				minAmount = tree.expandTotal;
			}
		}
		return expandTree;
	}

	/**
	 * Recursively expand or shrink to fit all points and sub-leafs. Much less
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
	 * point.
	 * 
	 * @param p
	 * @return
	 */
	public int[] expandAmount(Point p) {
		int[] amount = new int[] { 0, 0, 0, 0 };
		if (p.x < x) {
			amount[0] = (int) (x - p.x);
		} else if (p.x > x1) {
			amount[2] = (int) (p.x - x1);
		}

		if (p.y < y) {
			amount[1] = (int) (y - p.y);
		} else if (p.y > y1) {
			amount[3] = (int) (p.y - y1);
		}
		// expandTotal = (amount[0] + amount[2]) * (amount[1] + amount[3]);
		expandTotal = amount[0] + amount[2] + amount[1] + amount[3];
		return amount;
	}

	/**
	 * Expand R-tree to contain specified point.
	 * 
	 * @param p
	 */
	public void expandLeaf(Point p) {
		int[] amount = expandAmount(p);
		x -= amount[0];
		y -= amount[1];
		x1 += amount[2];
		y1 += amount[3];
	}

	/**
	 * Expand R-tree leaf to contain all points within it.
	 */
	public void expandLeaf() {
		x = (int) points[0].x - 1;
		y = (int) points[0].y - 1;
		x1 = x + 2;
		y1 = y + 2;
		for (Point p : points) {
			if (p == null) {
				break;
			}
			expandLeaf(p);
		}
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

	private ArrayList<Point> _getPointsInCircle(int x, int y, int radius, ArrayList<Point> points) {
		if (leaf) {
			for (Point point : this.points) {
				if (point == null) {
					break;
				} else if (distance(point.x, point.y, x, y) <= radius) {
					points.add(point);
				}
			}
		} else {
			for (Rtree tree : subTrees) {
				if (x - radius < tree.x1 && x + radius > tree.x && y - radius < tree.y1 && y + radius > tree.y) {
					points = tree._getPointsInCircle(x, y, radius, points);
				}
			}
		}
		return points;
	}

	public ArrayList<Point> getPointsInCircle(int x, int y, int radius) {
		return _getPointsInCircle(x, y, radius, new ArrayList<Point>());
	}

	private ArrayList<Point> _getPointsInRectangle(int x, int y, int width, int height, ArrayList<Point> points) {
		if (leaf) {
			for (Point point : this.points) {
				if (point == null) {
					break;
				} else if (x < point.x && x + width > point.x && y < point.y && y + height > point.y) {
					points.add(point);
				}
			}
		} else {
			for (Rtree tree : subTrees) {
				if (x < tree.x1 && x + width > tree.x && y < tree.y1 && y + height > tree.y) {
					points = tree._getPointsInRectangle(x, y, width, height, points);
				}

			}
		}
		return points;
	}

	public ArrayList<Point> getPointsInRectangle(int x, int y, int width, int height) {
		return _getPointsInRectangle(x, y, width, height, new ArrayList<Point>());
	}

	/**
	 * Insert new point into R-tree. Takes one point as input. If the maximum
	 * point count is reached, subdivide the R-tree in maxCount sub-R-trees.
	 * Otherwise, add the new point to points. To regenerate the whole tree,
	 * call refresh();
	 * 
	 * @param point
	 */
	public void add(Point point) {
		if (leaf) {
			int loc;
			for (loc = 0; loc < maxCount; loc++) {
				if (points[loc] == null) {
					points[loc] = point;
					break;
				}
			}
			if (loc >= maxCount - 1) {
				expandLeaf(point);
				subdivide();
			} else if (loc == 0) {
				// Add first point to new R-tree.
				x = (int) point.x - 1;
				y = (int) point.y - 1;
				x1 = (int) point.x + 1;
				y1 = (int) point.y + 1;
			} else {
				expandLeaf(point);
			}
		} else {
			chooseLeaf(point).add(point);
			expandBranch();
		}
	}

	/**
	 * Draws the current R-tree and all children to the graphics object as
	 * rectangles.
	 * 
	 * @param g
	 */
	public void draw(Graphics g) {
		g.drawRect(x, y, x1 - x, y1 - y);
		if (!leaf) {
			for (Rtree tree : subTrees) {
				tree.draw(g);
			}
		}
	}
}
