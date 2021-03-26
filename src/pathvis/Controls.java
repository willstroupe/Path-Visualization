package pathvis;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class Controls {
	
	private Window window;
	
	private Solver solver;
	
	private boolean done = false;
	
	private Timeline t;
	
	@FXML
	private TextField stepTimeField;
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;

    @FXML
    void startButtonClicked(MouseEvent event) {
    	int time = Integer.parseInt(stepTimeField.getText());
		t = new Timeline(new KeyFrame(Duration.millis(time), ev -> {
			int rv = solver.traverseStep();
			if (rv == 1) {
				done = true;
				t.stop();
    			window.announce("PATH FOUND");
    			solver.backtrack();
				
			} else if (rv == 2) {
				done = true;
				t.stop();
    			window.announce("NO PATH FOUND");
			}
		}));
		t.setCycleCount(Animation.INDEFINITE);
		t.play();
    }

    @FXML
    void stepButtonClicked(MouseEvent event) {
    	if (!done) {
    		int rv = solver.traverseStep();
    		if (rv == 1) {
    			done = true;
    			window.announce("PATH FOUND");
    			solver.backtrack();
    		} else if (rv == 2) {
    			done = true;
    			window.announce("NO PATH FOUND");
    		}
    	}
    }
    
    public void reset() {
    	done = false;
    	window.announce("");
    }
    
    public void setWindow(Window window) {
    	this.window = window;
    }
    
    public void setSolver(Solver solver) {
    	this.solver = solver;
    }

    @FXML
    void stopButtonClicked(MouseEvent event) {
    	if (t != null) 	t.stop();
    }

    @FXML
    void initialize() {

    }
}
