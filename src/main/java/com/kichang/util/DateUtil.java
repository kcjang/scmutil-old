package com.kichang.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {

		public static Date getDateWithDayOfWeek(Date value, int dayOfWeek) {
			Calendar cal = new GregorianCalendar();
			cal.setTime(value);
			cal.add(Calendar.DAY_OF_WEEK, dayOfWeek - cal.get(Calendar.DAY_OF_WEEK));
			
			return cal.getTime();
		}

}
