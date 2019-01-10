package autoscheduler.schedulers;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import autoscheduler.checks.TaskWebWalker;
import autoscheduler.export.ExportToCsv;
import autoscheduler.types.Day;
import autoscheduler.types.Task;
import autoscheduler.types.TaskSequence;
import autoscheduler.types.WorkCalendar;

public class ScheduleComputer {

	public static void main(String[] args) throws Exception {
		WorkCalendar calendar;
		calendar = new WorkCalendar("01/01/2019", 300);

		/* Tasks */
		Task[] tasks = new Task[15];

		tasks[0] = new Task("01-UFS3", "DAMAV", 5 * WorkCalendar.HOURS_A_DAY);
		tasks[1] = new Task("02-Comm", "DAMAV", 3 * WorkCalendar.HOURS_A_DAY);

		tasks[0].setStartDay(calendar.getDay("01/01/2019"));
		tasks[1].addDepTask(tasks[0]);
		tasks[1].setDeadline(calendar.getDay("31/03/2019"));

		tasks[2] = new Task("01-UFSTD3", "VISIO", 5 * WorkCalendar.HOURS_A_DAY);
		tasks[3] = new Task("02-Integration", "VISIO", 5 * WorkCalendar.HOURS_A_DAY);
		tasks[4] = new Task("03-Rel pos impl", "VISIO", 5 * WorkCalendar.HOURS_A_DAY);
		tasks[5] = new Task("04-Rel pos anal", "VISIO", 2 * WorkCalendar.HOURS_A_DAY);
		tasks[6] = new Task("05-Nav sim anal", "VISIO", 2 * WorkCalendar.HOURS_A_DAY);

		tasks[2].setStartDay(calendar.days[0]);
		tasks[3].setStartDay(calendar.getDay("01/04/2019"));
		tasks[3].addDepTask(tasks[2]);
		tasks[3].addDepTask(tasks[4]);
		tasks[4].setStartDay(calendar.getDay("01/04/2019"));
		tasks[5].addDepTask(tasks[4]);
		tasks[6].addDepTask(tasks[2]);
		tasks[3].setDeadline(calendar.getDay("31/05/2019"));
		tasks[5].setDeadline(calendar.getDay("31/05/2019"));
		tasks[6].setDeadline(calendar.getDay("31/05/2019"));

		tasks[7] = new Task("Biblio", "MMACT", 60); // 70 + 86
		tasks[8] = new Task("Def propu", "MMACT", 0);
		tasks[9] = new Task("WP210", "MMACT", 120); // 180 + 196
		tasks[10] = new Task("WP220", "MMACT", 40);
		tasks[11] = new Task("WP310", "MMACT", 120); // 120, 90, 40, 32
		tasks[12] = new Task("WP320", "MMACT", 90);
		tasks[13] = new Task("WP330", "MMACT", 40);
		tasks[14] = new Task("WP410", "MMACT", 32);

		tasks[7].setStartDay(calendar.getDay("01/01/2019"));
		tasks[8].setStartDay(calendar.getDay("01/01/2019"));
		tasks[7].setDeadline(calendar.getDay("08/02/2019"));
		tasks[8].setDeadline(calendar.getDay("08/02/2019"));
		tasks[9].addDepTask(tasks[7]);
		tasks[10].addDepTask(tasks[8]);
		tasks[9].setDeadline(calendar.getDay("01/04/2019"));
		tasks[10].setDeadline(calendar.getDay("01/04/2019"));
		tasks[11].addDepTask(tasks[9]);
		tasks[12].addDepTask(tasks[11]);
		tasks[13].addDepTask(tasks[12]);
		tasks[14].addDepTask(tasks[13]);
		tasks[14].setDeadline(calendar.getDay("01/06/2019"));

		/* Holidays */
		// Tignes
		calendar.setHolidays("21/01/2019", "25/01/2019");

		// Frequent holidays
		calendar.setHolidays("21/04/2019", "27/04/2019");
		calendar.setHolidays("21/05/2019", "27/05/2019");
		calendar.setHolidays("21/06/2019", "27/06/2019");
		calendar.setHolidays("21/07/2019", "27/07/2019");
		calendar.setHolidays("21/08/2019", "27/08/2019");

		// Computer
		try {
			ScheduleComputer.compute(calendar, tasks);
		} catch (Exception e) {
			throw e;
		}

		// Display work
		// System.out.println(calendar.toString());

		// Export
		try {
			ExportToCsv.export(calendar, tasks,
					new File("/home/anatole/Documents/Boulot/Projets/Java/AutoScheduler/out/schedule_data.csv"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void compute(WorkCalendar calendar, Task[] tasks) {

		Vector<TaskSequence> allTaskSequences = TaskWebWalker.getTaskSequences(tasks);

		for (Day day : calendar.days) {

			if (!day.isHolidays()) {

				/* Assign day work */
				// Look for highest priority of the day
				Task mostUrgentTask = getMostUrgentTask(allTaskSequences, calendar, day);

				// Set day work
				day.workTask = mostUrgentTask;
				if (mostUrgentTask != null) {
					mostUrgentTask.remHours -= WorkCalendar.HOURS_A_DAY;
					if (mostUrgentTask.remHours < 0) {
						mostUrgentTask.remHours = 0;
					}
				}
			}
		}
	}

	private static Task getMostUrgentTask(Vector<TaskSequence> allTaskSequences, WorkCalendar calendar, Day today) {
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
				if (mostUrgentSequence.getPriority(today, calendar) < curTaskSeq.getPriority(today, calendar)) {
					mostUrgentSequence = curTaskSeq;
				}
			}
		}

		return mostUrgentSequence.getCurrentTask();
	}

}
