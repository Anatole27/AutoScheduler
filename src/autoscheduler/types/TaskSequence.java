package autoscheduler.types;

import java.util.Vector;

public class TaskSequence {

	public Vector<Task> tasks = new Vector<>();
	int idx = 0;

	@Override
	public String toString() {
		String msg = "";
		for (Task task : tasks) {
			msg = task.toString() + " -> " + msg;
		}
		return msg;
	}

	public Task getFirstTask() {
		return tasks.lastElement();
	}

	private boolean hasNext() {
		if (idx > 0) {
			return true;
		} else {
			return false;
		}
	}

	private Task next() {
		if (hasNext()) {
			idx--;
			return tasks.get(idx);
		} else {
			return null;
		}
	}

	private void rewind() {
		idx = tasks.size();
	}

	public TaskSequence copy() {
		TaskSequence taskSeq = new TaskSequence();
		taskSeq.idx = this.idx;
		taskSeq.tasks = new Vector<>();
		for (Task task : this.tasks) {
			taskSeq.tasks.add(task);
		}
		return taskSeq;
	}

	/**
	 * Returns true if:
	 * 
	 * <ul>
	 * <li>Sequence start date has passed</li>
	 * <li>Current task start date has passed</li>
	 * <li>The remaining hours > 0</li>
	 * </ul>
	 * 
	 * @param today
	 * @return
	 */
	public boolean isRunning(Day today) {
		System.out.println(this);
		Task task = getCurrentTask();
		return (getStartDay().getDayNumber() <= today.getDayNumber()
				&& task.getStartDay().getDayNumber() <= today.getDayNumber() && getRemainingHours() > 0);
	}

	private int getRemainingHours() {
		int remainingHours = 0;
		for (Task task : tasks) {
			remainingHours += task.remHours;
		}
		return remainingHours;
	}

	public boolean isLate(Day today) {
		if (getDeadline().getDayNumber() < today.getDayNumber())
			return true;
		else
			return false;
	}

	/**
	 * Returns the priority of the sequence. The priority is:
	 * <ul>
	 * <li>left work hours / remaining time IF the remaining time is > 0</li>
	 * <li>1 + remaining time / project duration IF the remaining time is <= 0</li>
	 * </ul>
	 * 
	 * @param today
	 * @return
	 */
	public double getPriority(Day today) {
		int remainingHours = getRemainingHours();
		int leftDuration = getDeadline().getDayNumber() - today.getDayNumber() + 1;
		double priority;
		if (leftDuration > 0)
			priority = (double) remainingHours / (double) Calendar.HOURS_A_DAY / (double) leftDuration;
		else {
			int totalDuration = getDeadline().getDayNumber() - getStartDay().getDayNumber();
			priority = 1 + remainingHours / (double) Calendar.HOURS_A_DAY / totalDuration;
		}

		return priority;
	}

	private Day getStartDay() {
		return getFirstTask().getStartDay();
	}

	public Day getDeadline() {
		return tasks.get(0).getDeadline();
	}

	/**
	 * Returns the first task with remaining hours
	 * 
	 * @return
	 */
	public Task getCurrentTask() {
		rewind();
		Task task;
		task = next();
		while (task.remHours <= 0) {
			task = next();
		}
		return task;
	}
}
