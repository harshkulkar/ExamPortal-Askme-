package com.hcoders.portal.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.hcoders.portal.model.FormView;

public class DateTimeUtils {

	public static Map<String, Long> getResultsInMonths(List<FormView> results) {
		Map<String, Long> numberOfResultsInMonths = new LinkedHashMap<>();
		numberOfResultsInMonths.put("Jan.", 0L);
		numberOfResultsInMonths.put("Feb.", 0L);
		numberOfResultsInMonths.put("Mar.", 0L);
		numberOfResultsInMonths.put("Apr.", 0L);
		numberOfResultsInMonths.put("May",  0L);
		numberOfResultsInMonths.put("Jun.", 0L);
		numberOfResultsInMonths.put("Jul.", 0L);
		numberOfResultsInMonths.put("Aug.", 0L);
		numberOfResultsInMonths.put("Sep.", 0L);
		numberOfResultsInMonths.put("Oct.", 0L);
		numberOfResultsInMonths.put("Nov.", 0L);
		numberOfResultsInMonths.put("Dec.", 0L);

		for (FormView resultFormView : results) {
			Date date = resultFormView.getEndDate();

			LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			int month = localDate.getMonthValue();

			switch (month) {
			case 1:
				long resultsNumberJan = numberOfResultsInMonths.get("Jan.");
				resultsNumberJan += 1;
				numberOfResultsInMonths.put("Jan.", resultsNumberJan);
				break;
			case 2:
				long resultsNumberFeb = numberOfResultsInMonths.get("Feb.");
				resultsNumberFeb += 1;
				numberOfResultsInMonths.put("Feb.", resultsNumberFeb);
				break;
			case 3:
				long resultsNumberMar = numberOfResultsInMonths.get("Mar.");
				resultsNumberMar += 1;
				numberOfResultsInMonths.put("Mar.", resultsNumberMar);
				break;
			case 4:
				long resultsNumberApr = numberOfResultsInMonths.get("Apr.");
				resultsNumberApr += 1;
				numberOfResultsInMonths.put("Apr.", resultsNumberApr);
				break;
			case 5:
				long resultsNumberMay = numberOfResultsInMonths.get("May");
				resultsNumberMay += 1;
				numberOfResultsInMonths.put("May", resultsNumberMay);
				break;
			case 6:
				long resultsNumberJun = numberOfResultsInMonths.get("Jun.");
				resultsNumberJun += 1;
				numberOfResultsInMonths.put("Jun.", resultsNumberJun);
				break;
			case 7:
				long resultsNumberJul = numberOfResultsInMonths.get("Jul.");
				resultsNumberJul += 1;
				numberOfResultsInMonths.put("Jul.", resultsNumberJul);
				break;
			case 8:
				long resultsNumberAug = numberOfResultsInMonths.get("Aug.");
				resultsNumberAug += 1;
				numberOfResultsInMonths.put("Aug.", resultsNumberAug);
				break;
			case 9:
				long resultsNumberSep = numberOfResultsInMonths.get("Sep.");
				resultsNumberSep += 1;
				numberOfResultsInMonths.put("Sep.", resultsNumberSep);
				break;
			case 10:
				long resultsNumberOct = numberOfResultsInMonths.get("Oct.");
				resultsNumberOct += 1;
				numberOfResultsInMonths.put("Oct.", resultsNumberOct);
				break;
			case 11:
				long resultsNumberNov = numberOfResultsInMonths.get("Nov.");
				resultsNumberNov += 1;
				numberOfResultsInMonths.put("Nov.", resultsNumberNov);
				break;
			case 12:
				long resultsNumberDec = numberOfResultsInMonths.get("Dec.");
				resultsNumberDec += 1;
				numberOfResultsInMonths.put("Dec.", resultsNumberDec);
				break;
			default:
				break;

			}
		}

		return numberOfResultsInMonths;
	}

}
