package pathvis;

import java.util.Arrays;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RightHandSolver implements Solver {

	private static final int NUM_DIRS = 4;
	/* up: 0
	 * left: 1
	 * down: 2
	 * right: 3
	 */
	
	private Maze maze;
	private Rectangle[][] pane;
	private int x;
	private int y;
	private int from = -1;
	//-1 for unvisited
	private int visited[][];
	
	
	
	@Override
	public int traverseStep() {
		if (maze.grid[y][x] != CellType.START) {
			pane[y][x].setFill(Color.BLUE);
			maze.grid[y][x] = CellType.VISITED;
		}
		
		//cells neighbors, starting from top running CCW
		CellType[] nhbd = new CellType[NUM_DIRS];
		Arrays.fill(nhbd, CellType.WALL);
		if (y > 0) nhbd[0] = maze.grid[y - 1][x]; //top
		if (x > 0) nhbd[1] = maze.grid[y][x - 1]; //left
		if (y < maze.grid.length - 1) nhbd[2] = maze.grid[y + 1][x]; //bottom
		if (x < maze.grid[0].length - 1) nhbd[3] = maze.grid[y][x + 1]; //right
		
		if (from == -1) { //first node
			int wallIndex = -1;
			for (int i = 0; i < NUM_DIRS; i++) {
				if (nhbd[i] == CellType.WALL && 
					nhbd[(i + 1) % NUM_DIRS] != CellType.WALL) wallIndex = i;
			}
			
			if (wallIndex == -1) return 2;
			
			from = (wallIndex + 3) % NUM_DIRS; //come from square CW from wall
		}
		
		visited[y][x] = from;

		//starting at right wall, find first path and follow it
		int dir = -1;
		for (int i = 1; i < NUM_DIRS + 1; i++) {
			dir = (from + i) % 4;
			if (nhbd[dir] != CellType.WALL) break;
		}
		
		//set vals for next node
		x += (dir % 2) * (dir - 2);
		y += ((dir + 1) % 2) * (dir - 1);
		from = (dir + 2) % 4;
		
		
		
		
		
		if (visited[y][x] == from) return 2;
		if (maze.grid[y][x] == CellType.END) return 1;
		pane[y][x].setFill(Color.TURQUOISE);
		maze.grid[y][x] = CellType.OPEN;
		return 0;
	}

	@Override
	public void backtrack() {
		for (int i = 0; i < maze.grid.length; i++) {
			for (int j = 0; j < maze.grid[0].length; j++) {
				if (maze.grid[i][j] == CellType.VISITED) {
					maze.grid[i][j] = CellType.RETURN;
					pane[i][j].setFill(Color.LIME);
				}
			}
		}
	}
	
	public RightHandSolver(Maze maze, Rectangle[][] pane) {
		this.pane = pane;
		this.maze = maze;
		x = maze.start[0];
		y = maze.start[1];
		visited = new int[maze.grid.length][maze.grid[0].length];
		for (int i = 0; i <visited.length; i++) {
			Arrays.fill(visited[i], -1);
		}
	}
	
}
