package com.kichang.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Converter {
	protected static Log logger = LogFactory.getLog(Converter.class);

	public Converter() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public static List arrayToList(Object[] arrays) {
		List list = Arrays.asList(arrays);
		return list;
	}
	public static List arrayToSubList(Object[] arrays, int fromIndex, int toIndex) {
		List list = Arrays.asList(arrays);
		return list.subList(fromIndex, toIndex);
	}
	public static BigDecimal intToBigDecimal(int num) {
		BigDecimal decimal = new BigDecimal((double)num);
		return decimal;
	}
	public static BigDecimal integerToBigDecimal(Integer num) {
		BigDecimal decimal = new BigDecimal(num.toString());
		return decimal;
	}
	
	public static int bigDecimalToint(BigDecimal num) {
		if (num == null)
			return 0;
		else
			return num.intValue();
	}
	public static Integer bigDecimalToInteger(BigDecimal num) {
		if (num == null)
			return null;
		else
			return new Integer(num.toString());
	}
	public static BigDecimal longToBigDecimal(Long num) {
		return new BigDecimal(num.toString());
	}
	
	public static int longToInt(Long num) {
		return num.intValue();
	}
	
	
	public static String dateToString(Date date) {
		SimpleDateFormat formatter
			= new SimpleDateFormat("yyyy.MM.dd HH:mm");
		String dateString = "0000.00.00 00:00";
		if (date != null)
			dateString = formatter.format(date);
		
		if(logger.isDebugEnabled()) {
			if (logger.isDebugEnabled()) {
				//logger.debug("date       : " + date);
				//logger.debug("dateString : " + dateString);
			}
			
			
		}

		return dateString;
	}
	
	public static String dateToStringFull(Date date) {
		SimpleDateFormat formatter
			= new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		String dateString = "0000.00.00 00:00:00";
		if (date != null)
			dateString = formatter.format(date);
		
		if(logger.isDebugEnabled()) {
			if (logger.isDebugEnabled()) {
				//logger.debug("date       : " + date);
				//logger.debug("dateString : " + dateString);
			}
			
			
		}

		return dateString;
	}	
	
	public static String dateToOnlyTimeString(Date date) {
		SimpleDateFormat formatter
			= new SimpleDateFormat("HH:mm");
		String dateString = "00:00";
		if (date != null)
			dateString = formatter.format(date);

		return dateString;
	}
	
	
	public static String dateToStringNonTime(Date date) {
		SimpleDateFormat formatter
			= new SimpleDateFormat("yyyy.MM.dd");
		String dateString = "0000.00.00";
		if (date != null)
			dateString = formatter.format(date);

		return dateString;
	}
	public static Date stringToDate(String strDate) {
		SimpleDateFormat formatter
			= new SimpleDateFormat("yyyy.MM.dd HH:mm");		
		
		Date date = formatter.parse(strDate, new ParsePosition(0));	
		
		if (date == null)
			formatter = new SimpleDateFormat("yyyy.MM.dd");
		date = formatter.parse(strDate, new ParsePosition(0));	
		
		return date;
	}	
	
	public static String crToBr(String source) {
		String ret = StringUtils.replace(source,"\n","<BR>");
		ret = StringUtils.replace(source,"\t","&nbsp;&nbsp;&nbsp;&nbsp;");
		return ret;
		
	}
	
	public static Date stringToSimpleDate(String strDate){
		SimpleDateFormat formatter
		= new SimpleDateFormat("yyyy.MM.dd");		
	
		Date date = formatter.parse(strDate, new ParsePosition(0));	
	
		if (date == null)
			formatter = new SimpleDateFormat("yyyy.MM.dd");
			date = formatter.parse(strDate, new ParsePosition(0));	
	
		return date;		
	}
	public static Date stringToSimpleDatehyphen(String strDate){
		SimpleDateFormat formatter
		= new SimpleDateFormat("yyyy-MM-dd");		
	
		Date date = formatter.parse(strDate, new ParsePosition(0));	
		
		return date;		
	}
	public static Date stringToSimpleDatehyphenWithTime(String strDate){
		SimpleDateFormat formatter
		= new SimpleDateFormat("yyyy-MM-dd HH:mm");		
	
		Date date = formatter.parse(strDate, new ParsePosition(0));	
		
		return date;		
	}
	public static String dateToStringhyphen(Date date) {
		SimpleDateFormat formatter
			= new SimpleDateFormat("yyyy-MM-dd");
		String dateString = "0000-00-00";
		if (date != null)
			dateString = formatter.format(date);
		else
			dateString = "";
		return dateString;
		
		
	}
	public static String dateToStringhyphenAndHour24(Date date) {
		SimpleDateFormat formatter
			= new SimpleDateFormat("yyyy-MM-dd HH");
		String dateString = "0000-00-00 00";
		if (date != null)
			dateString = formatter.format(date);
		else
			dateString = "";
		return dateString;
		
		
	}
	public static String dateToStringShort(Date date) {
		SimpleDateFormat formatter
			= new SimpleDateFormat("yyyy.MM.dd");
		String dateString = "0000.00.00";
		if (date != null)
			dateString = formatter.format(date);
		else
			dateString = "";
		return dateString;
	}
	
	public static String lineBreak(String src) 
    {
        int len = src.length();
        int linenum = 0, i = 0;

        for (i = 0; i < len; i++)
          if (src.charAt(i) == '\n')
            linenum++;

        StringBuffer dest = new StringBuffer(len + linenum * 3);

        for (i = 0; i < len; i++) {
          if (src.charAt(i) == '\n')
            dest.append("<br>");
          else
            dest.append(src.charAt(i));
        }

        return dest.toString();
    }
	
	public static int BigDecimalToint(BigDecimal num) {
		if (num == null)
			return 0;
		else
			return num.intValue();
	}
	
	public static String StringEndDot(String str,int limit){
				
		if(str.length() > limit){
			str = str.substring(0,limit)+"...";
		}
		return str;
	}
	
	public static String secToDate(Integer sec) {
		int s = sec.intValue();
		int hour = s / (60 * 60);
		int min = (s - (hour * 60 * 60)) / 60;
		
		s = s - (hour * 60 * 60) - (min * 60);
		
		DecimalFormat formatter
		= new DecimalFormat("00");
		
		return hour + ":" + formatter.format(min) + ":" + formatter.format(s);

	}
    public static String convertGMT(String _time)
    {
        String _return = null;
        String _tmp = "";
        String _tmpRegister = "";
        if(_time.indexOf("PT") != -1)
        {
            _tmpRegister = _time.substring(_time.indexOf("T") + 1, _time.indexOf("H"));
            if(!"0".equals(_tmpRegister))
                _tmp = _tmpRegister + "시 ";
            _tmpRegister = _time.substring(_time.indexOf("H") + 1, _time.indexOf("M"));
            if(!"0".equals(_tmpRegister))
                _tmp = _tmp + _tmpRegister + "분 ";
            _tmpRegister = _time.substring(_time.indexOf("M") + 1, _time.indexOf("S"));
            if(!"0".equals(_tmpRegister))
                _tmp = _tmp + _tmpRegister + "초";
            if(_time.equals("PT0H0M0S"))
                _tmp = "1초";
            _return = _tmp;
        } else
        {
            _return = _time;
        }
        
               
        return _return;
    }
    
    public static Date convertToFirstTime(Date date) {
    	String strDate = dateToStringNonTime(date);
    	strDate += " 00:00:00";
    	return stringToDate(strDate);
    }
    public static Date convertToLastTime(Date date) {
    	String strDate = dateToStringNonTime(date);
    	strDate += " 23:59:59";
    	return stringToDate(strDate);
    }
    
    

    
}
