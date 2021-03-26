package pathvis;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;

public class Options {
	private Window window;
	
	private Solver solver;
	
	/* flag designating what should happen when clicking on the maze grid
	 * 0: create/delete walls
	 * 1: set start point
	 * 2: set end point
	 */
	private int mazeClickAction = 0;
	
	@FXML
	private ComboBox<String> mazeSelect;
	@FXML
	private TextField widthField;
	@FXML
	private TextField heightField;
	@FXML
	private Label perlinScaleLabel;
	@FXML
	private Slider perlinScaleSlider;
	@FXML
	private Label perlinCutoffLabel;
	@FXML
	private Slider perlinCutoffSlider;
	@FXML
	private Label cutLabel;
	@FXML
	private TextField cutField;
	@FXML
	private ToggleButton setStart;
	@FXML
	private ToggleButton setEnd;
	@FXML
	private ComboBox<String> solverSelect;
	@FXML
	private Label aStarLabel;
	@FXML
	private Slider aStarSlider;
	@FXML
	private Label announceLabel;
	

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    
    @FXML
    void generateClick(MouseEvent e) {
    	Maze maze = window.getMaze();
		maze.start = maze.end = null;
		
    	String mazeType = mazeSelect.getValue();
    	int width = Integer.parseInt(widthField.getText());
    	int height = Integer.parseInt(heightField.getText());
    	
    	switch (mazeType) {
		case "Perlin Noise":
			maze.genPerlinMaze(
								width, height, 
								perlinScaleSlider.getValue(),
								perlinCutoffSlider.getValue());
			break;
		case "Depth-first Tree":
			maze.genDepthMaze(
					width, height,
					Integer.parseInt(cutField.getText()));
			break;
		}
    	
    	window.displayMaze();
    	window.resize();
    }
    
    @FXML
    void solveClick(MouseEvent e) {
    	Maze maze = window.getMaze();
    	if (maze == null) return;
    	
		if (maze.start == null) {
			this.announce("SET START SQUARE");
			return;
		}
		if (maze.end == null) {
			this.announce("SET END SQUARE");
			return;
		}
		
		switch(solverSelect.getValue()) {
		case "Dijkstra":
			solver = new DijkstraSolver(maze, 0, window.getMazeGrid());
			break;
		case "A*":
			solver = new DijkstraSolver(maze, 
					                           aStarSlider.getValue(), 
					                           window.getMazeGrid());
			break;
		case "Depth-first":
			solver = new DepthFirstSolver(maze, window.getMazeGrid());
			break;
		case "Right Hand":
			solver = new RightHandSolver(maze, window.getMazeGrid());
		}
		window.reset(solver);
    }
    
    @FXML
    void setStartClick(MouseEvent e) {
    	if (setStart.isSelected()) {
        	mazeClickAction = 1;
        	setEnd.setSelected(false);
    	} else {
    		mazeClickAction = 0;
    	}
    }
    
    @FXML
    void setEndClick(MouseEvent e) {
    	if (setEnd.isSelected()) {
        	mazeClickAction = 2;
        	setStart.setSelected(false);
    	} else {
    		mazeClickAction = 0;
    	}
    }
    

    @FXML
    void genMazeChange(ActionEvent e) {
    	boolean isPerlin = mazeSelect.getValue().equals("Perlin Noise");

    	cutLabel.setVisible(!isPerlin);
    	cutLabel.setManaged(!isPerlin);
    	cutField.setVisible(!isPerlin);
    	cutField.setManaged(!isPerlin);
    	
    	perlinScaleLabel.setVisible(isPerlin);
    	perlinScaleLabel.setManaged(isPerlin);	
    	perlinScaleSlider.setVisible(isPerlin);
    	perlinScaleSlider.setManaged(isPerlin);
    	
    	perlinCutoffLabel.setVisible(isPerlin);
    	perlinCutoffLabel.setManaged(isPerlin);
    	perlinCutoffSlider.setVisible(isPerlin);
    	perlinCutoffSlider.setManaged(isPerlin);
    }


    @FXML
	void solverChange(ActionEvent e) {
    	boolean isAStar = solverSelect.getValue().equals("A*");

    	aStarLabel.setVisible(isAStar);
    	aStarLabel.setManaged(isAStar);
    	aStarSlider.setVisible(isAStar);
    	aStarSlider.setManaged(isAStar);
    }
    
    
    public Options() {
    	
    }
    
    public void setWindow(Window window) {
    	this.window = window;
    }
    
    public void setSolver(Solver solver) {
    	this.solver = solver;
    }
    
    public void setStartSelected(boolean b) {
    	setStart.setSelected(b);
    }
    
    public void setEndSelected(boolean b) {
    	setEnd.setSelected(b);
    }
    
    public int getClickAction() {
    	return this.mazeClickAction;
    }
    
    public void setClickAction(int action) {
    	this.mazeClickAction = action;
    }
    
    public void announce(String str) {
    	announceLabel.setText(str);
    }
    
    

    @FXML
    void initialize() {
    	cutLabel.setVisible(false);
    	cutLabel.setManaged(false);
    	cutField.setVisible(false);
    	cutField.setManaged(false);

    	aStarLabel.setVisible(false);
    	aStarLabel.setManaged(false);
    	aStarSlider.setVisible(false);
    	aStarSlider.setManaged(false);
    	
    	//from https://stackoverflow.com/a/36436243
    	UnaryOperator<Change> filter = ch -> {
    	    String text = ch.getText();
    	    if (text.matches("[0-9]*")) {
    	        return ch;
    	    }
    	    return null;
    	};
    	
    	TextFormatter<String> tf1 = new TextFormatter<String>(filter);
    	TextFormatter<String> tf2 = new TextFormatter<String>(filter);
    	TextFormatter<String> tf3 = new TextFormatter<String>(filter);
    	widthField.setTextFormatter(tf1);
    	heightField.setTextFormatter(tf2);
    	cutField.setTextFormatter(tf3);
    }
}
