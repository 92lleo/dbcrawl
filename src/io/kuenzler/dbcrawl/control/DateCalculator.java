/**
 * 
 */
package io.kuenzler.dbcrawl.control;

import io.kuenzler.dbcrawl.model.Departure;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @author Leonhard Künzler
 * @version 0.5
 * @date 15.11.05 16:45
 */
public class DateCalculator {

	public DateCalculator() {
		// TODO sth here?
	}

	/**
	 * Calculates difference between date 1 and date 2
	 * 
	 * @param target
	 *            target date
	 * @param start
	 *            start date
	 * @return time in ms to target
	 */
	public long getDateDifference(Date target, Date start) {
		return target.getTime() - start.getTime();
	}

	/**
	 * Returns date difference from now
	 * 
	 * @param target
	 *            different date
	 * @return time in ms to target
	 */
	public long getDateDifferenceFromNow(Date target) {
		return getDateDifference(target, new Date(System.currentTimeMillis()));
	}

	/**
	 * Splits a date (long ms) in nice String
	 * 
	 * @param date
	 *            date as long (ms)
	 * @return single values as real numbers
	 */
	public long[] splitDate(long date) {
		long[] splitted = new long[4];
		splitted[3] = date / 1000 % 60; // seconds
		splitted[2] = date / (60 * 1000) % 60; // mins
		splitted[1] = date / (60 * 60 * 1000) % 24; // hours
		splitted[0] = date / (24 * 60 * 60 * 1000); // days
		return splitted;
	}

	/**
	 * Simple version
	 * 
	 * @param date
	 */
	public void printDate(long date) {
		printDate(date, false, true, true, true);
	}

	/**
	 * 
	 * @param date
	 *            date as long (ms)
	 * @param day
	 *            show date
	 * @param hour
	 *            show hours
	 * @param min
	 *            show minutes
	 * @param sec
	 *            show seconds
	 */
	public void printDate(long date, boolean day, boolean hour, boolean min,
			boolean sec) {
		long[] splitted = splitDate(date);

		String time = "";
		if (day) {
			time += splitted[0] + ".";
		}
		if (hour) {
			if (String.valueOf(splitted[1]).length() == 1) {
				time += 0;
			}
			time += splitted[1] + ":";
		}
		if (min) {
			if (String.valueOf(splitted[2]).length() == 1) {
				time += 0;
			}
			time += splitted[2] + ":";
		}
		if (String.valueOf(splitted[3]).length() == 1) {
			time += 0;
		}
		time += splitted[3];

		System.out.println(time);
	}

	/**
	 * Extracts start time and delay from departure and starts contdown
	 * 
	 * @param departure
	 *            the dep. to count down to
	 */
	public void countToDeparture(Departure departure) {
		String time = departure.getTimeStart();
		String[] splittedTime = time.split(":");

		System.out.println("\nCalculating time departure at " + time + " ("
				+ departure.getDelay() + ")");

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splittedTime[0]));
		cal.set(Calendar.MINUTE, Integer.parseInt(splittedTime[1]));
		int delay = Integer.parseInt(departure.getDelay().substring(1));
		long departureTime = cal.getTimeInMillis() + (delay * 60000);
		Date date = new Date(departureTime);
		countDown(getDateDifference(date));
	}

	/**
	 * Counts down time in seconds and prints
	 * 
	 * @param distance
	 *            time as long (ms)
	 */
	public void countDown(long distance) {
		final Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			long counterValue = distance;

			public void run() {
				printDate(counterValue);
				counterValue -= 1000;
				if (counterValue <= 0) {
					System.out.println(("--alert--"));
					timer.cancel();
				}
			}
		}, 0, 1000);
	}
}
