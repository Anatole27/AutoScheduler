package autoscheduler.checks.exceptions;

public class TaskDependencyLoopException extends Exception {

	public TaskDependencyLoopException(String string) {
		super(string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4040107605794462779L;

}
