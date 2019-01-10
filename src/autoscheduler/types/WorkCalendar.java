package autoscheduler.types;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WorkCalendar {

	public Day[] days;
	public final static int HOURS_A_DAY = 8;
	public Date startDate;

	public WorkCalendar(String startDayString, int nDays) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		this.startDate = sdf.parse(startDayString);

		days = new Day[nDays];
		for (int i = 0; i < nDays; i++) {
			days[i] = new Day(i);
		}

		// Set weekends
		Calendar calendar = Calendar.getInstance();
		for (Day day : days) {
			Date date = getDate(day);
			calendar.setTime(date);
			if (calendar.get(Calendar.DAY_OF_WEEK) >= 6) {
				day.setIsHolidays(true);
			}
		}
	}

	public Date getDate(Day day) {
		Date date = new Date(startDate.getTime() + day.getDayNumber() * (1000 * 60 * 60 * 24));
		return date;
	}

	public Day getDay(String dateFormat) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dayDate = sdf.parse(dateFormat);
		int iDay = Math.round((dayDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
		if (iDay < 0) {
			throw new Exception("Date provided is before calendar start date");
		}
		if (iDay >= days.length) {
			throw new Exception("Calendar length does not cover provided date");
		}
		return days[iDay];
	}

	public void setHolidays(String startDate, String endDate) throws Exception {
		Day firstHolidayDay = getDay(startDate);
		Day lastHolidayDay = getDay(endDate);
		for (Day day : days) {
			if (day.getDayNumber() >= firstHolidayDay.getDayNumber()
					&& day.getDayNumber() <= lastHolidayDay.getDayNumber()) {
				day.setIsHolidays(true);
			}
		}
	}

	public int getNumberOfWorkingDays(String startDate, String endDate) throws Exception {
		int nWorkDays = 0;
		Day firstHolidayDay = getDay(startDate);
		Day lastHolidayDay = getDay(endDate);
		for (Day day : days) {
			if (day.getDayNumber() >= firstHolidayDay.getDayNumber()
					&& day.getDayNumber() <= lastHolidayDay.getDayNumber()) {
				nWorkDays++;
			}
		}
		return nWorkDays;
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
