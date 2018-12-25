package autoscheduler.schedulers;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import autoscheduler.checks.TaskWebWalker;
import autoscheduler.export.ExportToCsv;
import autoscheduler.types.Calendar;
import autoscheduler.types.Day;
import autoscheduler.types.Task;
import autoscheduler.types.TaskSequence;

public class ScheduleComputer {

	public static void main(String[] args) {
		Calendar calendar = new Calendar(300);

		/* Tasks */
		Task[] tasks = new Task[15];

		tasks[0] = new Task("01-UFS3", "DAMAV", 15 * Calendar.HOURS_A_DAY);
		tasks[1] = new Task("02-Comm", "DAMAV", 5 * Calendar.HOURS_A_DAY);

		tasks[0].setStartDay(calendar.days[0]);
		tasks[1].addDepTask(tasks[0]);
		tasks[1].setDeadline(calendar.days[92]);

		tasks[2] = new Task("01-UFSTD3", "VISIO", 10 * Calendar.HOURS_A_DAY);
		tasks[3] = new Task("02-Integration", "VISIO", 15 * Calendar.HOURS_A_DAY);
		tasks[4] = new Task("03-Rel pos impl", "VISIO", 10 * Calendar.HOURS_A_DAY);
		tasks[5] = new Task("04-Rel pos anal", "VISIO", 5 * Calendar.HOURS_A_DAY);
		tasks[6] = new Task("05-Nav sim anal", "VISIO", 5 * Calendar.HOURS_A_DAY);

		tasks[2].setStartDay(calendar.days[0]);
		tasks[3].setStartDay(calendar.days[93]);
		tasks[3].addDepTask(tasks[2]);
		tasks[3].addDepTask(tasks[4]);
		tasks[4].setStartDay(calendar.days[93]);
		tasks[5].addDepTask(tasks[4]);
		tasks[6].addDepTask(tasks[2]);
		tasks[3].setDeadline(calendar.days[153]);
		tasks[5].setDeadline(calendar.days[153]);
		tasks[6].setDeadline(calendar.days[153]);

		tasks[7] = new Task("Biblio RN", "MMACT", 60); // 70 + 86
		tasks[8] = new Task("Def propu", "MMACT", 40);
		tasks[9] = new Task("WP210", "MMACT", 180); // 180 + 196
		tasks[10] = new Task("WP220", "MMACT", 60);
		tasks[11] = new Task("WP310", "MMACT", 120); // 120, 90, 40, 32
		tasks[12] = new Task("WP320", "MMACT", 90);
		tasks[13] = new Task("WP330", "MMACT", 40);
		tasks[14] = new Task("WP410", "MMACT", 32);

		tasks[7].setStartDay(calendar.days[0]);
		tasks[8].setStartDay(calendar.days[0]);
		tasks[7].setDeadline(calendar.days[38]);
		tasks[8].setDeadline(calendar.days[38]);
		tasks[9].addDepTask(tasks[7]);
		tasks[10].addDepTask(tasks[8]);
		tasks[9].setDeadline(calendar.days[95]);
		tasks[10].setDeadline(calendar.days[95]);
		tasks[11].addDepTask(tasks[9]);
		tasks[12].addDepTask(tasks[11]);
		tasks[13].addDepTask(tasks[12]);
		tasks[14].addDepTask(tasks[13]);
		tasks[14].setDeadline(calendar.days[180]);

		// Computer
		try {
			ScheduleComputer.compute(calendar, tasks);
		} catch (Exception e) {
			throw e;
		}

		// Display work
//		System.out.println(calendar.toString());

		// Export
		try {
			ExportToCsv.export(calendar, tasks,
					new File("/home/anatole/Documents/Boulot/Projets/Java/AutoScheduler/out/schedule_data.csv"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void compute(Calendar calendar, Task[] tasks) {

		Vector<TaskSequence> allTaskSequences = TaskWebWalker.getTaskSequences(tasks);

		for (Day day : calendar.days) {

			/* Assign day work */
			// Look for highest priority of the day
			Task mostUrgentTask = getMostUrgentTask(allTaskSequences, day);

			// Set day work
			day.workTask = mostUrgentTask;
			if (mostUrgentTask != null) {
				mostUrgentTask.remHours -= Calendar.HOURS_A_DAY;
				if (mostUrgentTask.remHours < 0) {
					mostUrgentTask.remHours = 0;
				}
			}
		}
	}

	private static Task getMostUrgentTask(Vector<TaskSequence> allTaskSequences, Day today) {
		// Find most urgent task sequence
		Iterator<TaskSequence> itTaskSeq = allTaskSequences.iterator();
		TaskSequence mostUrgentSequence = null;
		do {
			if (itTaskSeq.hasNext()) {
				mostUrgentSequence = itTaskSeq.next();
			} else {
				return null; // No running sequence
			}
		} while (!mostUrgentSequence.isRunning(today));

		while (itTaskSeq.hasNext()) {
			TaskSequence curTaskSeq = itTaskSeq.next();
			if (curTaskSeq.isRunning(today)) {
				if (mostUrgentSequence.getPriority(today) < curTaskSeq.getPriority(today)) {
					mostUrgentSequence = curTaskSeq;
				}
			}
		}

		return mostUrgentSequence.getCurrentTask();
	}

}
