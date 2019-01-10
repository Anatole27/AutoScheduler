package autoscheduler.export;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import autoscheduler.types.WorkCalendar;
import autoscheduler.types.Day;
import autoscheduler.types.Task;

public class ExportToCsv {

	public static void export(WorkCalendar calendar, Task[] tasks, File file) throws IOException {
		FileWriter fileWriter = new FileWriter(file);
		StringBuffer data = new StringBuffer();
		String del = ",";

		writeDayWork(calendar, tasks, data, del);
		data.append("\n");
		writeCumulatedWork(calendar, tasks, data, del);
		data.append("\n");
		writeGantt(calendar, tasks, data, del);

		fileWriter.write(data.toString());

		fileWriter.close();
	}

	private static void writeDayWork(WorkCalendar calendar, Task[] tasks, StringBuffer data, String del)
			throws IOException {

		// Write title
		data.append("Day work\n");

		// Write header
		String header = "Tasks";
		for (Day day : calendar.days) {
			header += del + day.getDayNumber();
		}
		header += "\n";
		data.append(header);

		// Write lines
		for (Task task : tasks) {
			String line = task.toString().replaceAll(del, "");
			for (Day day : calendar.days) {
				if (day.workTask == task) {
					line += del + WorkCalendar.HOURS_A_DAY;
				} else {
					line += del;
				}
			}
			line += "\n";
			data.append(line);
		}
	}

	private static void writeCumulatedWork(WorkCalendar calendar, Task[] tasks, StringBuffer data, String del)
			throws IOException {

		// Write title
		data.append("Cumulated work\n");

		// Write header
		String header = "Tasks";
		for (Day day : calendar.days) {
			header += del + day.getDayNumber();
		}
		header += "\n";
		data.append(header);

		// Write lines
		for (Task task : tasks) {
			String line = task.toString().replaceAll(del, "");
			int cumWork = 0;
			for (Day day : calendar.days) {
				if (day.workTask == task) {
					cumWork += WorkCalendar.HOURS_A_DAY;
					line += del + cumWork;
				} else {
					line += del;
				}
			}
			line += "\n";
			data.append(line);
		}
	}

	private static void writeGantt(WorkCalendar calendar, Task[] tasks, StringBuffer data, String del) throws IOException {

		// Write title
		data.append("Cumulated work\n");

		// Write header
		String header = "Tasks";
		for (Day day : calendar.days) {
			header += del + day.getDayNumber();
		}
		header += "\n";
		data.append(header);

		// Write lines
		for (Task task : tasks) {
			String line = task.toString().replaceAll(del, "");
			int startDay = Integer.MAX_VALUE;
			int endDay = Integer.MAX_VALUE;

			// Find first day of work and last day of work
			boolean taskBegan = false;
			for (Day day : calendar.days) {
				if (day.workTask == task) {
					endDay = day.getDayNumber();
					if (!taskBegan) {
						taskBegan = true;
						startDay = day.getDayNumber();
					}
				}
			}

			// Fill 1 when the task is running
			// Fill > at start date
			// Fill X at deadline
			// Markers
			String deadline = "X";
			String start = ">";
			for (Day day : calendar.days) {
				String mark = "";
				if (task.isStartTask()) {
					if (task.getStartDay().getDayNumber() == day.getDayNumber()) {
						mark += start;
					}
				}
				if (day.getDayNumber() >= startDay && day.getDayNumber() <= endDay) {
					mark += 1;
				}
				if (task.isDeadline()) {
					if (task.getDeadline().getDayNumber() == day.getDayNumber()) {
						mark += deadline;
					}
				}
				line += del + mark;
			}

			line += "\n";
			data.append(line);
		}
	}
}
