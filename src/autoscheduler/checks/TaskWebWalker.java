package autoscheduler.checks;

import java.util.Vector;

import autoscheduler.checks.exceptions.NoStartDayTaskTipException;
import autoscheduler.checks.exceptions.TaskDependencyLoopException;
import autoscheduler.checks.exceptions.UnconnectedTaskException;
import autoscheduler.types.Project;
import autoscheduler.types.Task;
import autoscheduler.types.TaskSequence;

/**
 * This class is used to check if the connections between tasks is consistent.
 * 
 * @author anatole
 *
 */
public class TaskWebWalker {

	/**
	 * Several checks to verify the consistency of the task dependency web
	 * 
	 * @param tasks
	 * @return
	 * @throws Exception
	 */
	public static boolean checkDependencies(Task[] tasks) throws Exception {
		Vector<Task> deadlineTasks = getDeadlineTasks(tasks);

		// Check the absence of loops
		for (Task endTask : deadlineTasks) {
			Vector<Task> loopPath = isLoop(endTask, new Vector<>());
			if (loopPath != null) {
				String msg = "";
				for (Task visitTask : loopPath) {
					msg += " -> " + visitTask;
				}
				throw new TaskDependencyLoopException("A loop is present following the dependency path: \n" + msg);
			}
		}

		// Check that all deadline tasks are linked to a start task
		for (Task endTask : deadlineTasks) {
			Vector<Task> noStartTipTasks = new Vector<>();
			boolean isStartTip = isStartTip(endTask, noStartTipTasks);
			if (!isStartTip) {
				String msg = "";
				for (Task noStartTipTask : noStartTipTasks) {
					msg += noStartTipTask + "\n";
				}
				throw new NoStartDayTaskTipException(
						"The following tasks have neither a task dependency, nor a start date: \n" + msg);
			}
		}

		// Check that all tasks are visited once
		Vector<TaskSequence> taskSeqs = getTaskSequences(tasks);
		Vector<Task> visitedTasks = new Vector<>();
		for (TaskSequence taskSeq : taskSeqs) {
			for (Task task : taskSeq.tasks) {
				visitedTasks.add(task);
			}
		}
		for (Task task : tasks) {
			if (!visitedTasks.contains(task)) {
				throw new UnconnectedTaskException("Task " + task + " has no deadline descendent");
			}
		}

		return true;

	}

	/**
	 * Recursive method to test if every task sequence tip is a start task
	 * 
	 * @param endTask
	 * @param noStartTipTasks
	 * @return
	 */
	private static boolean isStartTip(Task endTask, Vector<Task> noStartTipTasks) {

		// If the task is a tip, test if it is a start task
		if (endTask.depTasks.size() == 0) {
			if (endTask.isStartTask()) {
				return true;
			} else {
				noStartTipTasks.add(endTask);
				return false;
			}
		}

		// Otherwise, test its children
		else {
			boolean isStartEverywhere = true;
			for (Task depTask : endTask.depTasks) {
				boolean isStartTipBool = isStartTip(depTask, noStartTipTasks);
				if (!isStartTipBool) {
					isStartEverywhere = false;
				}
			}
			return isStartEverywhere;
		}
	}

	/**
	 * Recursive method to find loop. If a loop is found by descending the
	 * dependency, it returns true.
	 * 
	 * @param endTask
	 * @param visitedTasks
	 * @return the looped path if a loop happened, null otherwise
	 */
	private static Vector<Task> isLoop(Task endTask, Vector<Task> visitedTasks) {
		if (visitedTasks.contains(endTask)) {
			visitedTasks.add(endTask);
			return visitedTasks;
		} else {
			visitedTasks.add(endTask);
			for (Task task : endTask.depTasks) {
				Vector<Task> directPath = new Vector<>();
				directPath.addAll(visitedTasks);
				directPath = isLoop(task, directPath);
				if (directPath != null) {
					return directPath;
				}
			}
			return null;
		}
	}

	public static Vector<TaskSequence> getTaskSequences(Task[] tasks) {

		/* List deadlines */
		Vector<Task> deadlineTasks = getDeadlineTasks(tasks);

		Vector<TaskSequence> allTaskSequences = new Vector<>();
		for (Task deadlineTask : deadlineTasks) {
			Vector<TaskSequence> taskSequences = getTaskSequencesRec(deadlineTask, new TaskSequence());
			allTaskSequences.addAll(taskSequences);
		}

		return allTaskSequences;
	}

	private static Vector<Task> getDeadlineTasks(Task[] tasks) {
		Vector<Task> deadlineTasks = new Vector<>();
		for (Task task : tasks) {
			if (task.isDeadline()) {
				deadlineTasks.add(task);
			}
		}
		return deadlineTasks;
	}

	public static Project[] getProjects(Task[] tasks) {
		Vector<TaskSequence> taskSeqs = new Vector<>();

		taskSeqs = getTaskSequences(tasks);

		// Fuse groups
		for (int i = 0; i < taskSeqs.size(); i++) {
			TaskSequence seq1 = taskSeqs.get(i);
			for (int j = i + 1; j < taskSeqs.size(); j++) {
				TaskSequence seq2 = taskSeqs.get(j);
				for (Task task : seq1.tasks) {
					if (seq2.tasks.contains(task)) {
						/*
						 * Same task has been found in both sequences Transfer all tasks in seq2 to
						 * seq1, except for those already present in seq 2
						 */
						for (Task taskToTransfer : seq2.tasks) {
							if (!seq1.tasks.contains(taskToTransfer)) {
								seq1.tasks.add(taskToTransfer);
							}
						}

						// Erase seq2
						taskSeqs.remove(j);
						j--;

						break;
					}
				}
			}
		}

		// Extract projects
		Project[] projects = new Project[taskSeqs.size()];
		for (int i = 0; i < taskSeqs.size(); i++) {
			Vector<Task> taskGroup = taskSeqs.get(i).tasks;
			projects[i] = new Project(taskGroup, taskGroup.get(0).projectName);
		}
		return projects;
	}

	/**
	 * Recursive method to explore the dependencies of each task
	 * 
	 * @param task
	 * @param forwardSequence
	 * @return
	 */
	private static Vector<TaskSequence> getTaskSequencesRec(Task task, TaskSequence forwardSequence) {

		forwardSequence.tasks.add(task);
		Vector<TaskSequence> taskSequences = new Vector<>();

		// If a start date is assigned to the task, add a sequence beginning here
		if (task.isStartTask()) {
			taskSequences.add(forwardSequence);
		}

		// Look for children branches
		for (Task childTask : task.depTasks) {
			Vector<TaskSequence> childSequences = getTaskSequencesRec(childTask, forwardSequence.copy());
			taskSequences.addAll(childSequences);
		}

		return taskSequences;
	}
}
