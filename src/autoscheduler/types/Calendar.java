package autoscheduler.types;

public class Calendar {

	public Day[] days;
	public final static int HOURS_A_DAY = 8;

	public Calendar(int nDays) {
		days = new Day[nDays];
		for (int i = 0; i < nDays; i++) {
			days[i] = new Day(i);
		}
	}

	@Override
	public String toString() {
		String msg = "";
		for (Day day : days) {
			if (day.workTask != null) {
				msg += day.toString() + " " + day.workTask.toString() + "\n";
			} else {
				msg += day.toString() + " nothing to do\n";
			}
		}
		return msg;
	}
}
