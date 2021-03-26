package pathvis;

import java.util.Arrays;
import java.util.LinkedList;

public class Maze {
	/* representation of maze
	 * 0: path
	 * 1: wall
	 * 2: start
	 * 3: end
	 * 4: visited
	 * 5: open set
	 * 6: return path
	 */
	public CellType[][] grid;
	public int[] start;
	public int[] end;
	
	public void genPerlinMaze(int width, int height, double scale, double cutoff) {
		grid = new CellType[height][width];
		double[][] tmp = genPerlinNoise(width, height, scale);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (tmp[i][j] > cutoff) {
					grid[i][j] = CellType.WALL;
				} else {
					grid[i][j] = CellType.PATH;
				}
			}
		}
	}
	
	private double[][] genPerlinNoise(int width, int height, double scale) {
		Vector[][] grads = new Vector[(int) Math.floor(height / scale) + 2]
									 [(int) Math.floor(width / scale) + 2];
		
		double[][] rv = new double[height][width];
		
		for (int i = 0; i < grads.length; i++) {
			for (int j = 0; j < grads[0].length; j++) {
				grads[i][j] = new Vector();
				double a = 2 * Math.PI * Math.random();
				grads[i][j].x = Math.cos(a);
				grads[i][j].y = Math.sin(a);
			}
		}
		
		int iScaled, jScaled;
		for (int i = 0; i < rv.length; i++) {
			for (int j = 0; j < rv[0].length; j++) {
				iScaled = (int)(i / scale);
				jScaled = (int)(j / scale);
				//pos. from upper-left, normalized to scale
				Vector scaled = new Vector((j % scale) / scale, 
						                   (i % scale) / scale); 
				
				Vector[] nearestGrads = 
					{grads[iScaled][jScaled], //top left
					 grads[iScaled][jScaled + 1], //top right
					 grads[iScaled + 1][jScaled], //bottom left
					 grads[iScaled + 1][jScaled + 1]}; //bottom right
				
				rv[i][j] = perlin(scaled, nearestGrads);
			}
		}
		
		return rv;
	}	
	
	private double perlin(Vector v, Vector grads[]) {
		v.fade();
		
		Vector[] offs = {new Vector(v.x, v.y),
						 new Vector(1 - v.x, v.y),
						 new Vector(v.x, 1 - v.y),
						 new Vector(1 - v.x, 1 - v.y)};
		
		
		double[] dots = new double[4];
		
		for (int i = 0; i < dots.length; i++) {
			dots[i] = Vector.dot(offs[i], grads[i]);
		}
		
		//weight dot products by position of v
		double w1 = dots[1] * v.x + dots[0] * (1-v.x);
		double w2 = dots[3] * v.x + dots[2] * (1-v.x);
		return w2 * v.y + w1 * (1-v.y) + .5;
	}
	
	//returns an array of ints, 0 if path 1 if wall
	//all points represented by length 2 arrays: {x, y}
	//erase cuts number of walls
	public void genDepthMaze(int width, int height, int cuts) {
		grid = new CellType[height][width];
		for (int i = 0; i < grid.length; i++) {
			Arrays.fill(grid[i], CellType.WALL);
		}
		int[] curr = {(int)(Math.random() * width), 
				      (int)(Math.random() * height)};
		grid[curr[1]][curr[0]] = CellType.PATH;
		LinkedList<int[]> neighbors = new LinkedList<int[]>();
		LinkedList<int[]> st = new LinkedList<int[]>();
		st.push(curr);

		while (!st.isEmpty()) {
			curr = st.pop();
			if (curr[0] - 2 >= 0 && grid[curr[1]][curr[0] - 2] == CellType.WALL) {
				int[] nb = {curr[0] - 2, curr[1]};
				neighbors.add(nb);
			}
			if (curr[0] + 2 < width && grid[curr[1]][curr[0] + 2] == CellType.WALL) {
				int[] nb = {curr[0] + 2, curr[1]};
				neighbors.add(nb);
			}
			if (curr[1] - 2 >= 0 && grid[curr[1] - 2][curr[0]] == CellType.WALL) {
				int[] nb = {curr[0], curr[1] - 2};
				neighbors.add(nb);
			}
			if (curr[1] + 2 < height && grid[curr[1] + 2][curr[0]] == CellType.WALL) {
				int[] nb = {curr[0], curr[1] + 2};
				neighbors.add(nb);
			}
			
			if (!neighbors.isEmpty()) {
				st.push(curr);
				int[] toAdd = neighbors.get((int)(Math.random() * 
						                          neighbors.size()));
				grid[toAdd[1]][toAdd[0]] = CellType.PATH;
				grid[(toAdd[1] + curr[1]) / 2]
					[(toAdd[0] + curr[0]) / 2] = CellType.PATH;
				st.push(toAdd);
				neighbors = new LinkedList<int[]>();
			}
		}
		
		int total = 8 * width * height;
		while (cuts > 0 && total > 0) {
			int[] cut = {(int)(Math.random() * width), 
					     (int)(Math.random() * height)};
			if (grid[cut[1]][cut[0]] == CellType.WALL) {
				grid[cut[1]][cut[0]] = CellType.PATH;
				cuts--;
			}
			total--;
		}
	}
	
}


class Vector {

	double x;
	double y;
	
	public static double dot(Vector v1, Vector v2) {
		return v1.x * v2.x + v1.y * v2.y;
	}
	
	public void fade() {
		x = x * x * x * (x * (x * 6 - 15) + 10);
		y = y * y * y * (y * (y * 6 - 15) + 10);
		
	}
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public Vector() {}
}
