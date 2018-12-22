package autoscheduler.checks.test;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import autoscheduler.checks.TaskWebWalker;
import autoscheduler.checks.exceptions.NoStartDayTaskTipException;
import autoscheduler.checks.exceptions.TaskDependencyLoopException;
import autoscheduler.checks.exceptions.UnconnectedTaskException;
import autoscheduler.types.Calendar;
import autoscheduler.types.Project;
import autoscheduler.types.Task;

class TaskWebWalkerTest {

	@Test
	void testAllGood() {
		Calendar calendar = new Calendar(300);

		Task[] tasks = new Task[15];

		tasks[0] = new Task("01-UFS3", "DAMAV", 15 * Calendar.HOURS_A_DAY);
		tasks[1] = new Task("02-Comm", "DAMAV", 5 * Calendar.HOURS_A_DAY);

		tasks[0].setStartDay(calendar.days[0]);
		tasks[1].addDepTask(tasks[0]);
		tasks[1].setDeadline(calendar.days[92]);

		tasks[2] = new Task("01-UFSTD3", "VISIO", 5 * Calendar.HOURS_A_DAY);
		tasks[3] = new Task("02-Integration", "VISIO", 10 * Calendar.HOURS_A_DAY);
		tasks[4] = new Task("03-Rel pos impl", "VISIO", 5 * Calendar.HOURS_A_DAY);
		tasks[5] = new Task("04-Rel pos anal", "VISIO", 3 * Calendar.HOURS_A_DAY);
		tasks[6] = new Task("05-Nav sim anal", "VISIO", 1 * Calendar.HOURS_A_DAY);

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
		tasks[10] = new Task("WP220", "MMACT", 196);
		tasks[11] = new Task("WP310", "MMACT", 120);
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

		try {
			TaskWebWalker.checkDependencies(tasks);
		} catch (Exception e) {
			fail("The case shall produce no exception: " + e.getMessage());
		}

	}

	@Test
	void testLoop() {
		Calendar calendar = new Calendar(300);

		Task[] tasks = new Task[15];

		tasks[0] = new Task("01-UFS3", "DAMAV", 15 * Calendar.HOURS_A_DAY);
		tasks[1] = new Task("02-Comm", "DAMAV", 5 * Calendar.HOURS_A_DAY);

		tasks[0].setStartDay(calendar.days[0]);
		tasks[1].addDepTask(tasks[0]);
		tasks[1].setDeadline(calendar.days[92]);

		tasks[2] = new Task("01-UFSTD3", "VISIO", 5 * Calendar.HOURS_A_DAY);
		tasks[3] = new Task("02-Integration", "VISIO", 10 * Calendar.HOURS_A_DAY);
		tasks[4] = new Task("03-Rel pos impl", "VISIO", 5 * Calendar.HOURS_A_DAY);
		tasks[5] = new Task("04-Rel pos anal", "VISIO", 3 * Calendar.HOURS_A_DAY);
		tasks[6] = new Task("05-Nav sim anal", "VISIO", 1 * Calendar.HOURS_A_DAY);

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
		tasks[10] = new Task("WP220", "MMACT", 196);
		tasks[11] = new Task("WP310", "MMACT", 120);
		tasks[12] = new Task("WP320", "MMACT", 90);
		tasks[13] = new Task("WP330", "MMACT", 40);
		tasks[14] = new Task("WP410", "MMACT", 32);

		tasks[7].setStartDay(calendar.days[0]);
		tasks[7].addDepTask(tasks[14]);
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

		try {
			TaskWebWalker.checkDependencies(tasks);
		} catch (TaskDependencyLoopException e) {
			System.out.println(e.getMessage());
			return;
		} catch (Exception e) {
			fail("Wrong exception: " + e.getMessage());
		}
		fail("No exception raised");
	}

	@Test
	void testNoStartTip() {
		Calendar calendar = new Calendar(300);

		Task[] tasks = new Task[15];

		tasks[0] = new Task("01-UFS3", "DAMAV", 15 * Calendar.HOURS_A_DAY);
		tasks[1] = new Task("02-Comm", "DAMAV", 5 * Calendar.HOURS_A_DAY);

		tasks[0].setStartDay(calendar.days[0]);
		tasks[1].addDepTask(tasks[0]);
		tasks[1].setDeadline(calendar.days[92]);

		tasks[2] = new Task("01-UFSTD3", "VISIO", 5 * Calendar.HOURS_A_DAY);
		tasks[3] = new Task("02-Integration", "VISIO", 10 * Calendar.HOURS_A_DAY);
		tasks[4] = new Task("03-Rel pos impl", "VISIO", 5 * Calendar.HOURS_A_DAY);
		tasks[5] = new Task("04-Rel pos anal", "VISIO", 3 * Calendar.HOURS_A_DAY);
		tasks[6] = new Task("05-Nav sim anal", "VISIO", 1 * Calendar.HOURS_A_DAY);

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
		tasks[10] = new Task("WP220", "MMACT", 196);
		tasks[11] = new Task("WP310", "MMACT", 120);
		tasks[12] = new Task("WP320", "MMACT", 90);
		tasks[13] = new Task("WP330", "MMACT", 40);
		tasks[14] = new Task("WP410", "MMACT", 32);

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

		try {
			TaskWebWalker.checkDependencies(tasks);
		} catch (NoStartDayTaskTipException e) {
			System.out.println(e.getMessage());
			return;
		} catch (Exception e) {
			fail("Wrong exception: " + e.getMessage());
		}
		fail("No exception raised");
	}

	@Test
	void testUnconnectedTask() {
		Calendar calendar = new Calendar(300);

		Task[] tasks = new Task[15];

		tasks[0] = new Task("01-UFS3", "DAMAV", 15 * Calendar.HOURS_A_DAY);
		tasks[1] = new Task("02-Comm", "DAMAV", 5 * Calendar.HOURS_A_DAY);

		tasks[0].setStartDay(calendar.days[0]);
		tasks[1].addDepTask(tasks[0]);

		tasks[2] = new Task("01-UFSTD3", "VISIO", 5 * Calendar.HOURS_A_DAY);
		tasks[3] = new Task("02-Integration", "VISIO", 10 * Calendar.HOURS_A_DAY);
		tasks[4] = new Task("03-Rel pos impl", "VISIO", 5 * Calendar.HOURS_A_DAY);
		tasks[5] = new Task("04-Rel pos anal", "VISIO", 3 * Calendar.HOURS_A_DAY);
		tasks[6] = new Task("05-Nav sim anal", "VISIO", 1 * Calendar.HOURS_A_DAY);

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
		tasks[10] = new Task("WP220", "MMACT", 196);
		tasks[11] = new Task("WP310", "MMACT", 120);
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

		try {
			TaskWebWalker.checkDependencies(tasks);
		} catch (UnconnectedTaskException e) {
			System.out.println(e.getMessage());
			return;
		} catch (Exception e) {
			fail("Wrong exception: " + e.getMessage());
		}
		fail("No exception raised");
	}

	@Test
	void testExtractProjects() {

		Calendar calendar = new Calendar(300);

		Task[] tasks = new Task[15];

		tasks[0] = new Task("01-UFS3", "DAMAV", 15 * Calendar.HOURS_A_DAY);
		tasks[1] = new Task("02-Comm", "DAMAV", 5 * Calendar.HOURS_A_DAY);

		tasks[0].setStartDay(calendar.days[0]);
		tasks[1].addDepTask(tasks[0]);
		tasks[1].setDeadline(calendar.days[92]);

		tasks[2] = new Task("01-UFSTD3", "VISIO", 5 * Calendar.HOURS_A_DAY);
		tasks[3] = new Task("02-Integration", "VISIO", 10 * Calendar.HOURS_A_DAY);
		tasks[4] = new Task("03-Rel pos impl", "VISIO", 5 * Calendar.HOURS_A_DAY);
		tasks[5] = new Task("04-Rel pos anal", "VISIO", 3 * Calendar.HOURS_A_DAY);
		tasks[6] = new Task("05-Nav sim anal", "VISIO", 1 * Calendar.HOURS_A_DAY);

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
		tasks[10] = new Task("WP220", "MMACT", 196);
		tasks[11] = new Task("WP310", "MMACT", 120);
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

		try {
			Project[] projects = TaskWebWalker.getProjects(tasks);
			for (Project project : projects) {
				System.out.println(project);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
