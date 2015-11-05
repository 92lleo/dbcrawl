package io.kuenzler.dbcrawl.control;

import io.kuenzler.dbcrawl.gui.MainGui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.RefreshHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

/**
 * @author Leonhard Künzler
 * @version 0.1
 * @date 15.10.31 23:00
 */
public class Crawler {

	MainGui frame;

	/**
	 * 
	 */
	public Crawler() {
		frame = new MainGui(this);
		// frame.refreshData();
	}

	/**
	 * 
	 * @param destination
	 * @param start
	 * @return
	 */
	public ArrayList<String> crawlData(String destination, String start) {

		// start = "Eching";
		// destination = "Moosach";
		// Create and initialize WebClient object
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX_38);
		// webClient.setThrowExceptionOnScriptError(false);
		webClient.setRefreshHandler(new RefreshHandler() {
			public void handleRefresh(Page page, URL url, int arg)
					throws IOException {
				System.out.println("handleRefresh");
			}

		});

		String url = "http://mobile.bahn.de/bin/mobil/query.exe/dox?country=DEU&rt=1&use_realtime_filter=1&webview=&searchMode=NORMAL";
		HtmlPage page;
		try {
			setUpdate("Opening m.bahn.de for query");
			page = (HtmlPage) webClient.getPage(url);
			HtmlForm form = page.getFormByName("formular");
			setUpdate("Setting start and destination");
			form.getInputByName("REQ0JourneyStopsS0G").setValueAttribute(start);
			form.getInputByName("REQ0JourneyStopsZ0G").setValueAttribute(
					destination);
			setUpdate("Waiting for response");
			page = (HtmlPage) form.getInputByValue("Suchen").click();
			setUpdate("Extracting information");
			HtmlTable table = (HtmlTable) page.getByXPath(
					"//table[@class='ovTable clicktable']").get(0);
			ArrayList<String> times = new ArrayList<String>();
			for (HtmlTableRow row : table.getRows()) {
				System.out.print("\n");
				List<HtmlTableCell> cells = row.getCells();
				int cellCurrent;
				String information, current;
				String[] breaks;
				cellCurrent = 0;
				information = "";
				current = cells.get(cellCurrent).asText();
				if (current.startsWith("Ab") || current.startsWith("Früher")
						|| current.startsWith("Später")) {
					continue;
				}
				current = cells.get(cellCurrent).asText();
				information += "Departure: ";
				current = replace(current);
				breaks = current.split(" ");
				information += breaks[0] + ", ";
				if (breaks[0].startsWith("ca")) {
					information += " " + breaks[1];
				}

				// next
				cellCurrent++;
				current = cells.get(cellCurrent).asText();
				information += "Delay: ";
				current = replace(current);
				breaks = current.split(" ");
				if (breaks[0].equals("")) {
					breaks[0] = "+0";
				}
				information += breaks[0];
				System.out.println(information);
				times.add(information);
			}
			setUpdate("Done");
			webClient.close();
			return times;
		} catch (IndexOutOfBoundsException e) {
			ArrayList<String> res = new ArrayList<String>();
			res.add("--You need to insert the exact location-- (iob)");
			setUpdate("Error");
			return res;
		} catch (FailingHttpStatusCodeException | IOException e) {
			ArrayList<String> res = new ArrayList<String>();
			res.add(e.toString());
			setUpdate("Error");
			return res;
		}
	}

	/**
	 * 
	 * @param s
	 * @return
	 */
	public String replace(String s) {
		String reg = " ";
		s = s.replace("\n", reg);
		s = s.replace("\r", reg);
		s = s.replace("<br>", reg);
		// while(s.contains("  ")){
		s.replace("  ", reg);
		// }
		return s;
	}

	/**
	 * 
	 * @param update
	 */
	private void setUpdate(String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setUpdate(update);
			}
		});
	}

	/**
	 * 
	 * @param array
	 */
	public void printArray(String[] array) {
		System.out.println("--------");
		for (String x : array) {
			System.out.println("newline" + x);
		}
		System.out.println("--------");
	}

	/**
	 * Creates new Crawler (htmlunit alternative)
	 * 
	 * @param args
	 *            not used
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO handle exception above
		new Crawler();
	}
}
