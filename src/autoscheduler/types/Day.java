package autoscheduler.types;

public class Day {

	private int dayNumber;
	public Task workTask;
	private boolean isHolidays = false;

	public Day(int iDay) {
		dayNumber = iDay;
	}

	public int getDayNumber() {
		return dayNumber;
	}

	@Override
	public String toString() {
		return "Day " + dayNumber;
	}

	public void setIsHolidays(boolean isHolidays) {
		this.isHolidays = isHolidays;
	}

	public boolean isHolidays() {
		return isHolidays;
	}

}
