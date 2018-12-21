package autoscheduler.types;

import java.util.Vector;

public class Task {

	public String name;
	public int initHours;
	public int remHours;
	public Vector<Task> depTasks = new Vector<>();
	private boolean isStartTask;
	private boolean isDeadline;
	private Day start = null;
	private Day deadline = null;
	public Vector<TaskSequence> taskSequence = new Vector<>();;
	
	public Task(String name, int initHours) {
		this.name = name;
		this.initHours = initHours;
		this.remHours = initHours;
	}
	
	public void addDepTask(Task task) {
		depTasks.add(task);
	}
	
	public void setStartDay(Day day) {
		start = day;
		isStartTask = true;
	}
	
	public void setDeadline(Day day) {
		deadline = day;
		isDeadline = true;
	}
	
	public Day getStartDay() {
		return start;
	}
	
	public Day getDeadline() {
		return deadline;
	}
	
	public boolean isStartTask() {
		return isStartTask;
	}
	
	public boolean isDeadline() {
		return isDeadline;
	}

	public void addPriorities(Vector<TaskSequence> priorities) {
		this.taskSequence.addAll(priorities);
	}
	
	@Override
	public String toString() {
		return name;
	}
}
