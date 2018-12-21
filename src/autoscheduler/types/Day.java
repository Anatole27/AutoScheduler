package autoscheduler.types;

public class Day {

	private int dayNumber;
	public Day tomorrow = null;
	public Day yesterday = null;
	public Task workTask;

	public Day(int iDay) {
		dayNumber = iDay;
	}
	
	public int getDayNumber() {
		return dayNumber;
	}
	
	@Override
	public String toString() {
		return "Day "+dayNumber;
	}

}
