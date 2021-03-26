package pathvis;

import java.util.Arrays;
import java.util.PriorityQueue;

import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

import java.util.Comparator;

public class DijkstraSolver implements Solver {

	//check against maze array values for visited status
	private Maze maze;
	
	private Rectangle[][] pane;
	
	private PriorityQueue<int[]> unvisited;
	private int[] curr;
	private double[][] dists = null;
	private int[][][] from = null;
	
	
	public DijkstraSolver(Maze maze, double bias, Rectangle[][] pane) {
		this.pane = pane;
		this.maze = maze;
		
		Comparator<int[]> cmp = new Comparator<int[]>() {
				public int compare(int[] n1, int[] n2) {
					double f1 = dists[n1[1]][n1[0]] + 
								heuristic(n1, maze.end, bias);
					double f2 = dists[n2[1]][n2[0]] + 
								heuristic(n2, maze.end, bias);
					if (Math.abs(f1 - f2) < .0000001) {
						return 0;
					} if (f1 < f2) {
						return -1;
					}
					return 1;
				}
			};
		unvisited = new PriorityQueue<int[]>(20, cmp);
		
		curr = null;
		from = new int[maze.grid.length][maze.grid[0].length][2];
		dists = new double[maze.grid.length][maze.grid[0].length];
		
		for (int i = 0; i < dists.length; i++) {
			Arrays.fill(dists[i], Integer.MAX_VALUE);
		}
		
		dists[maze.start[1]][maze.start[0]] = 0;
		unvisited.add(maze.start.clone());
	}

	
	public int traverseStep() {
		if (unvisited.isEmpty()) return 2;
		curr = unvisited.remove();
		if (maze.grid[curr[1]][curr[0]] == CellType.VISITED) return 0;
		if (Arrays.equals(curr, maze.end)) return 1;
		
		if (maze.grid[curr[1]][curr[0]] != CellType.START) {
			maze.grid[curr[1]][curr[0]] = CellType.VISITED;
			pane[curr[1]][curr[0]].setFill(Color.BLUE);
		}
		double currDist = dists[curr[1]][curr[0]];
		
		if (curr[1] > 0) 
			addNode(curr[0], curr[1] - 1, currDist);
		if (curr[1] < maze.grid.length - 1) 
			addNode(curr[0], curr[1] + 1, currDist);
		if (curr[0] > 0) 
			addNode(curr[0] - 1, curr[1], currDist);
		if (curr[0] < maze.grid[0].length - 1) 
			addNode(curr[0] + 1, curr[1], currDist);
		                              
		return 0;
	}
	
	public void backtrack() {
		curr = maze.end.clone();
		while (!Arrays.equals(curr, maze.start)) {
			curr = from[curr[1]][curr[0]];
			maze.grid[curr[1]][curr[0]] = CellType.RETURN;
			pane[curr[1]][curr[0]].setFill(Color.LIME);
		}
		maze.grid[curr[1]][curr[0]] = CellType.START;
		pane[curr[1]][curr[0]].setFill(Color.GREEN);
	}
	
	private double heuristic(int[] st, int[] end, double bias) {
		return bias * Math.sqrt((st[0] - end[0]) * (st[0] - end[0]) + 
						 		(st[1] - end[1]) * (st[1] - end[1]));
	}
	
	private void addNode(int x, int y, double currDist) {
		double tentativeDistance = currDist +
				   Math.sqrt((y - curr[1]) * (y - curr[1]) +
							 (x - curr[0]) * (x - curr[0]));
		
		if ((maze.grid[y][x] == CellType.PATH || 
			 maze.grid[y][x] == CellType.OPEN || 
			 maze.grid[y][x] == CellType.END) && 
									    dists[y][x] > tentativeDistance) {
			dists[y][x] = tentativeDistance;
			from[y][x] = curr.clone();
			int[] node = {x, y};
			unvisited.add(node);
			if (maze.grid[y][x] != CellType.END) {
				pane[y][x].setFill(Color.TURQUOISE);
				maze.grid[y][x] = CellType.OPEN;
			}
		}
	}
}
