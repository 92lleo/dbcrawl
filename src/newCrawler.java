import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class newCrawler {

	private final String mBahnUrl;
	private Document mBahn;
	private String date, time, start, dest;
	private ArrayList<Departure> departures;

	public newCrawler() {
		mBahnUrl = "http://mobile.bahn.de/bin/mobil/query.exe/dox?country=DEU&rt=1&use_realtime_filter=1&webview=&searchMode=NORMAL";
		setTestData();
		sendRequest();
		cleanAndParseResults();
		// test();
		for (Departure d : departures) {
			System.out.println(d);
		}
	}

	/**
	 * Testdata
	 */
	private void setTestData() {
		Date today = new Date();
		DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT,
				Locale.GERMANY);
		date = formatter.format(today);
		SimpleDateFormat ft = new SimpleDateFormat("HH:mm", Locale.GERMANY);
		time = ft.format(today);
		start = "Feldmoching";
		dest = "Eching";

		System.out.println(date + "-" + time + ", " + start + " to " + dest);
	}

	/**
	 * 
	 */
	private void cleanAndParseResults() {
		if (!mBahn.title().contains("Ihre Auskunft")) {
			throw new NoSuchElementException("No results to parse");
		}
		mBahn.getElementsByClass("ovTable clicktable");
		// get departures table and split into elemets
		Elements departuresTable = mBahn
				.select("table[class=ovTable clicktable]").first().children();

		// remove headlines and footer stuff
		departuresTable = departuresTable.get(1).children();
		departuresTable.remove(5);

		departures = new ArrayList<Departure>();
		for (Element departureTable : departuresTable) {
			// split to sinlge departures
			Elements single = departureTable.children();
			Iterator<Element> elements = single.iterator();
			Departure departure = new Departure();
			String[] current = elements.next().text().split(" ");
			departure.setTimes(current[0], current[1]);
			departure.setLocations(start, dest);
			current = elements.next().text().split(" ");
			if (current.length > 2) {
				current[0] = current[0] + " " + current[1];
				current[1] = current[2] + " " + current[3];
			}
			if (current[0].trim().length() < 2) {
				current[0] = "-0";
			}
			departure.setDelay(current[0]);
			current = elements.next().text().split(" ");
			departure.setDuration(current[1]);
			departure.setType((elements.next().text().split(" "))[0]);
			departures.add(departure);
		}
	}

	/**
	 * prepare and send request to {@value mBahnUrl} and save output in {@value
	 * mBahn}
	 */
	private void sendRequest() {
		try {
			mBahn = Jsoup
					.connect(mBahnUrl)
					.data("queryPageDisplayed", "yes")
					.data("REQ0JourneyStopsS0A", "1")
					.data("REQ0JourneyStopsS0G", start)
					// start
					.data("REQ0JourneyStopsS0ID", "")
					.data("locationErrorShownfrom", "yes")
					.data("REQ0JourneyStopsZ0A", "1")
					.data("REQ0JourneyStopsZ0G", dest)
					// dest
					.data("REQ0JourneyStopsZ0ID", "")
					.data("locationErrorShownto", "yes")
					.data("REQ0JourneyDate", date)
					// date
					.data("REQ0JourneyTime", time)
					.data("REQ0HafasSearchForw", "1")
					.data("REQ0Tariff_TravellerType.1", "E")
					.data("REQ0Tariff_TravellerReductionClass.1", "0")
					.data("REQ0Tariff_Class", "2")
					.data("REQ0JourneyStops1.0A", "1")
					.data("REQ0JourneyStops1.0G", "")
					.data("REQ0JourneyStops2.0A", "1")
					.data("REQ0JourneyStops2.0G", "")
					.data("REQ0HafasChangeTime:0", "1")
					.data("existOptimizePrice", "1")
					.data("REQ0HafasOptimize1:0", "1")
					.data("existOptionBits", "yes")
					.data("immediateAvail", "ON").data("start", "Suchen")
					.userAgent("Mozilla").post();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchElementException e) {
			System.err.println("Not enough entries");
		}
	}

	/**
	 * Creates new Crawler (jsoup alternative)
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		new newCrawler();
	}
}