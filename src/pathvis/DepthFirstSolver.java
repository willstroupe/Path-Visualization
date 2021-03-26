package pathvis;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Stack;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DepthFirstSolver implements Solver{
	private Maze maze;
	private int[][][] from = null;
	private Rectangle[][] pane;
	
	private int[] curr;
	private Stack<int[]> st;
	
	public DepthFirstSolver(Maze maze, Rectangle[][] pane) {
		this.maze = maze;
		this.pane = pane;
		
		st = new Stack<int[]>();
		st.add(this.maze.start);
		
		from = new int[maze.grid.length][maze.grid[0].length][2];
	}
	
	public int traverseStep() {
		if (st.isEmpty()) return 2;
		curr = st.pop();
		if (maze.grid[curr[1]][curr[0]] == CellType.VISITED) return 0;
		if (Arrays.equals(curr, maze.end)) return 1;
		
		if (maze.grid[curr[1]][curr[0]] != CellType.START) {
			maze.grid[curr[1]][curr[0]] = CellType.VISITED;
			pane[curr[1]][curr[0]].setFill(Color.BLUE);
		}
		
		LinkedList<int[]> nhbd = new LinkedList<int[]>();
		
		if (curr[1] > 0) addNode(curr[0], curr[1] - 1, nhbd);
		if (curr[1] < maze.grid.length - 1) addNode(curr[0], curr[1] + 1, nhbd);
		if (curr[0] > 0) addNode(curr[0] - 1, curr[1], nhbd);
		if (curr[0] < maze.grid[0].length - 1) addNode(curr[0] + 1, curr[1], nhbd);
		
		Collections.shuffle(nhbd);
		while (!nhbd.isEmpty()) {
			st.push(nhbd.remove());
		}
		
		return 0;
	}
	
	private void addNode(int x, int y, LinkedList<int[]> nhbd) {
		if (maze.grid[y][x] == CellType.PATH || maze.grid[y][x] == CellType.END) {
			int[] node = {x, y};
			nhbd.add(node);
			from[y][x] = curr.clone();
			if (maze.grid[y][x] != CellType.END) {
				pane[y][x].setFill(Color.TURQUOISE);
				maze.grid[y][x] = CellType.OPEN;
			}
		}
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
	
}
