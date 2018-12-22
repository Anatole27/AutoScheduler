package autoscheduler.types;

import java.util.Vector;

public class Project {

	Vector<Task> tasks;
	String projectName;

	public Project(Vector<Task> tasks, String projectName) {
		this.tasks = tasks;
		this.projectName = projectName;
	}

	public Day getFirstDay() {
		Day firstDay = null;
		for (Task task : tasks) {
			if (task.isStartTask()) {
				if (firstDay == null) {
					firstDay = task.getStartDay();
					continue;
				}
				if (firstDay.getDayNumber() > task.getStartDay().getDayNumber()) {
					firstDay = task.getStartDay();
				}
			}
		}

		return firstDay;
	}

	public Day getLastDeadline() {
		Day lastDay = null;
		for (Task task : tasks) {
			if (task.isDeadline()) {
				if (lastDay == null) {
					lastDay = task.getDeadline();
					continue;
				}
				if (lastDay.getDayNumber() > task.getDeadline().getDayNumber()) {
					lastDay = task.getDeadline();
				}
			}
		}

		return lastDay;

	}

	@Override
	public String toString() {
		return String.format("Project %s starting %s and finishing %s", projectName, getFirstDay(), getLastDeadline());
	}
}
