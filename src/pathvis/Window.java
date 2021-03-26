package pathvis;
	
import java.awt.MouseInfo;
import java.awt.Point;
import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class Window extends Application {
	private static final int MOUSE_POLL_INTERVAL = 16;
	public static final int GRID_SIZE = 15;
	
	private Stage stage;
	private GridPane gp;
	private Rectangle[][] mazeGrid; //pointers to gridpane elements
	private VBox options;
	private HBox controls;
	private Solver solver;
	private Controls ctls;
	private Options opts;

	private Maze maze = new Maze();
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		Timeline t = new Timeline();
		
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 200 + GRID_SIZE * 30, 50 + GRID_SIZE * 30);
		
		FXMLLoader optsLoader = new FXMLLoader(getClass().
				                               getResource("Options.fxml"));
		options = optsLoader.load();
		opts = optsLoader.getController();
		opts.setWindow(this);
		
		FXMLLoader controlsLoader = new FXMLLoader(getClass().
				                                   getResource("Controls.fxml"));
		controls = controlsLoader.load();
		ctls = controlsLoader.getController();
		ctls.setWindow(this);
		ctls.setSolver(this.solver);
		
		gp = new GridPane();
		root.setCenter(gp);
		gp.addEventHandler(MouseEvent.MOUSE_PRESSED, ev -> {
			if (opts.getClickAction() != 0) return;
			
			//draw walls/path using the mouse			
			Point p = MouseInfo.getPointerInfo().getLocation();
			
			Color rColor;
			CellType mazeVal;
			if (ev.getButton().compareTo(MouseButton.PRIMARY) == 0) {
				rColor = Color.WHITE;
				mazeVal = CellType.PATH;
			} else {
				rColor = Color.BLACK;
				mazeVal = CellType.WALL;
			}
			
			//find how far the gridpane is offset on the screen
			int xOffset = p.x - (int)ev.getX();
			int yOffset = p.y - (int)ev.getY();
			t.getKeyFrames().add(new KeyFrame(
								 Duration.millis(MOUSE_POLL_INTERVAL), a -> {
				Point pos = MouseInfo.getPointerInfo().getLocation();
				int gridX = (pos.x - xOffset) / GRID_SIZE;
				int gridY = (pos.y - yOffset) / GRID_SIZE;
				
				if (gridX >= 0 && gridX < maze.grid[0].length && 
					gridY >= 0 && gridY < maze.grid.length) {
					mazeGrid[gridY][gridX].setFill(rColor);
					maze.grid[gridY][gridX] = mazeVal;
				}
			}));
			t.setCycleCount(Timeline.INDEFINITE);
			t.play();
		});
		
		gp.addEventHandler(MouseEvent.MOUSE_RELEASED, ev -> {
			if (opts.getClickAction() == 0) {
				t.stop();
				t.getKeyFrames().clear();
				return;
			}
			
			int gridX = (int)(ev.getX() / GRID_SIZE);
			int gridY = (int)(ev.getY() / GRID_SIZE);
			if (gridX >= 0 && gridX < maze.grid[0].length && 
					gridY >= 0 && gridY < maze.grid.length) {
				if (opts.getClickAction() == 1) {
					if (maze.start != null) {
						maze.grid[maze.start[1]][maze.start[0]] = CellType.PATH;
						mazeGrid[maze.start[1]][maze.start[0]].setFill(Color.WHITE);
					} else {
						maze.start = new int[2];
					}
					
					maze.start[0] = gridX;
					maze.start[1] = gridY;
					mazeGrid[gridY][gridX].setFill(Color.GREEN);
					maze.grid[gridY][gridX] = CellType.START;
					opts.setStartSelected(false);
				} else {
					if (maze.end != null) {
						maze.grid[maze.end[1]][maze.end[0]] = CellType.WALL;
						mazeGrid[maze.end[1]][maze.end[0]].setFill(Color.WHITE);
					} else {
						maze.end = new int[2];
					}
					
					maze.end[0] = gridX;
					maze.end[1] = gridY;
					mazeGrid[gridY][gridX].setFill(Color.RED);
					maze.grid[gridY][gridX] = CellType.END;
					opts.setEndSelected(false);
				}
			}
			opts.setClickAction(0);
		});
		
		root.setLeft(options);
		//shift controls section rightwards
		BorderPane.setMargin(controls, new Insets(0, 0, 0, 250));
		root.setTop(controls);
		
		stage = primaryStage;
		stage.setScene(scene);
		stage.show();
		stage.sizeToScene();
	}

	   
	public void displayMaze() {
		mazeGrid = new Rectangle[maze.grid.length][maze.grid[0].length];
		for (int i = 0; i < maze.grid.length; i++) {
			for (int j = 0; j < maze.grid[0].length; j++) {
				Rectangle r = new Rectangle(Window.GRID_SIZE, Window.GRID_SIZE);
				gp.add(r, j, i);
				mazeGrid[i][j] = r;
				if (maze.grid[i][j] == CellType.PATH) {
					r.setFill(Color.WHITE);
				} else if (maze.grid[i][j] == CellType.WALL) {
					r.setFill(Color.BLACK);
				}
			}
		}
	}
		
	public void resize() {
		int width = (int)Math.max(controls.getWidth(), 
				options.getWidth() + GRID_SIZE * maze.grid[0].length);
		int height = (int)Math.max(options.getHeight(), 
				controls.getHeight() + GRID_SIZE * maze.grid.length);
		stage.setWidth(width + 20);
		stage.setHeight(height + 40);
	}

	public void reset(Solver solver) {
		
		for (int i = 0; i < maze.grid.length; i++) {
			for (int j = 0; j < maze.grid[0].length; j++) {
				if (maze.grid[i][j] == CellType.VISITED || 
					maze.grid[i][j] == CellType.OPEN || 
					maze.grid[i][j] == CellType.RETURN) {
					maze.grid[i][j] = CellType.PATH;
					mazeGrid[i][j].setFill(Color.WHITE);
				}
			}
		}
		ctls.reset();
		this.solver = solver;
		ctls.setSolver(solver);
	}
	
	public static void go(String[] args) {
		launch(args);
	}
	
	public Rectangle[][] getMazeGrid() {
		return this.mazeGrid;
	}
	
	public void announce(String str) {
		opts.announce(str);
	}
	
	public Maze getMaze() {
		return this.maze;
	}
	
}