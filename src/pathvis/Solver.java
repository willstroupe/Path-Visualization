package pathvis;

public interface Solver {
	/* returns result of step:
	 * 0: default (continue)
	 * 1: at the end
	 * 2: end not found
	 */
	public int traverseStep();
	
	public void backtrack();

}
