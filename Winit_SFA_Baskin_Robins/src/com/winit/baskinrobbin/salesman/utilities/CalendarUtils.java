package com.winit.baskinrobbin.salesman.utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.TextUtils;
import android.widget.TextView;

/** this class having some common methods related to the dateofJorney, dateofJorney format,dateofJorney conversion, etc.**/
public class CalendarUtils 
{
	public static final String DATE_STD_PATTERN = "yyyy-MM-dd";
	public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String DATE_STD_PATTERN_FULL = "EEEEE, MMM dd, yyyy";
	public static final String DATE_STD_PATTERN_ENROLL = "dd-MMM-yyyy";
	public static final String DATE_TIME_STD_PATTERN = "dd-MM-yyyy HH:mm:ss";
	public static final String DATE_TIME_STD_PATTERN_One = "MM/dd/yyyy HH:mm:ss";
	public static final String TIME_STD_PATTERN = "HH:mm";
	public static final String TIME_STD_PATTERN1 = "HH:mma";
	public static final String DATE_STD_PATTERN_FLIGHT = "yyyy-MM-dd'T'kk:mm:ss";
	public static final String DATE_DT_PATTERN_SHOT = "EEE dd MMM yyyy";
	public static final String TIME_PATTERN_FLIGHT_INFO = "kk:mm";
	public static final String TIME_PATTERN_FLIGHT_SEARCH = "EEE', 'kk:mm";
	public static final String TIME_PATTERN_FLIGHT_LIST = "dd MMM yyyy";
//	public static final String DATE_PATTERN_FLIGHT_LIST = "EEE dd MMM yyyy";
	public static final String DATE_PATTERN_FLIGHT_LIST_NEW = "EEE, dd MMM yyyy";
	public static int WEEK_OF_MONTH;
	public static String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	public static final String DATE_TIME_STD_PATTERN_DELIVERY = "MM/dd/yyyy";
	public static final String SYNCH_DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm:ss a";
	/** this method returns Month in string with year 
	 *@param  intMonth
	 *@param  intYear
	 **/
	public static long getDifferenceInMinutes(String dateStart, String dateStop)
	{
		/*if(dateStart.contains(""))
			dateStart = dateStart.split("")[0];
		if(dateStop.contains("T"))
			dateStop = dateStop.split("T")[0];
		
		dateStart = dateStart+"T00:00:00";
		dateStop  = dateStop+"T00:00:00";*/
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		long diffMinutes = 0;
		Date d1 = null;
		Date d2 = null;
 
		try 
		{
			d1 = format.parse(dateStart);
			d2 = format.parse(dateStop);
 
			//in milliseconds
			long diff = d2.getTime() - d1.getTime();
 
			diffMinutes = diff;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return diffMinutes;
	}
	public static String getMonthAndYearString(int intMonth, int intYear)
	{
		String monthYear = "";
		
		switch(intMonth)
		{
			case 0:
					monthYear = "January "+intYear;break;
			case 1:
					monthYear = "February "+intYear;break;
			case 2:
					monthYear = "March "+intYear;break;
			case 3:
					monthYear = "April "+intYear;break;
			case 4:
					monthYear = "May "+intYear;break;
			case 5:
					monthYear = "June "+intYear;break;
			case 6:
					monthYear = "July "+intYear;break;
			case 7:
					monthYear = "August "+intYear;break;
			case 8:
					monthYear = "September "+intYear;break;
			case 9:
					monthYear = "October "+intYear;break;
			case 10:
					monthYear = "November "+intYear;break;
			case 11:
					monthYear = "December "+intYear;break;
		}
		return monthYear;
	}
	
	
	/** this method returns Month name from the int value of month 
	 *@param  intMonth
	 **/
	public static String getMonthFromNumber(int intMonth)
	{
		String strMonth = "";
		
		switch(intMonth)
		{
			case 1:
					strMonth = "Jan";break;
			case 2:
					strMonth = "Feb";break;
			case 3:
					strMonth = "Mar";break;
			case 4:
					strMonth = "Apr";break;
			case 5:
					strMonth = "May";break;
			case 6:
					strMonth = "Jun";break;
			case 7:
					strMonth = "Jul";break;
			case 8:
					strMonth = "Aug";break;
			case 9:
					strMonth = "Sep";break;
			case 10:
					strMonth = "Oct";break;
			case 11:
					strMonth = "Nov";break;
			case 12:
					strMonth = "Dec";break;
		}
		
		return strMonth;
	}
	
	public static String getDateAsString(Date date)
	{
		String dateStr = null;
		
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_STD_PATTERN);
		dateStr = sdf.format(date);
		
		return dateStr;
	}
	
	/** this method returns int value of month from the Month name 
	 *@param  month **/
	public static int getnumberFromMonth(String month)
	{
		int monthNum = 0 ;
		
		if(month.equalsIgnoreCase("Jan"))
			monthNum = 0;
		else if(month.equalsIgnoreCase("Feb"))
			monthNum = 1;
		else if(month.equalsIgnoreCase("Mar"))
			monthNum = 2;
		else if(month.equalsIgnoreCase("Apr"))
			monthNum = 3;
		else if(month.equalsIgnoreCase("May"))
			monthNum = 4;
		else if(month.equalsIgnoreCase("Jun"))
			monthNum = 5;
		else if(month.equalsIgnoreCase("Jul"))
			monthNum = 6;
		else if(month.equalsIgnoreCase("Aug"))
			monthNum = 7;
		else if(month.equalsIgnoreCase("Sep"))
			monthNum = 8;
		else if(month.equalsIgnoreCase("Oct"))
			monthNum = 9;
		else if(month.equalsIgnoreCase("Nov"))
			monthNum = 10;
		else if(month.equalsIgnoreCase("Dec"))
			monthNum = 11;
		
		return monthNum;
	}
	
	public static String getMonthAsString(int intMonth)
	{
		String monthYear = "";
		
		switch(intMonth)
		{
			case 0:
					monthYear = "January " ;break;
			case 1:
					monthYear = "February " ;break;
			case 2:
					monthYear = "March " ;break;
			case 3:
					monthYear = "April " ;break;
			case 4:
					monthYear = "May " ;break;
			case 5:
					monthYear = "June " ;break;
			case 6:
					monthYear = "July " ;break;
			case 7:
					monthYear = "August " ;break;
			case 8:
					monthYear = "September " ;break;
			case 9:
					monthYear = "October " ;break;
			case 10:
					monthYear = "November " ;break;
			case 11:
					monthYear = "December " ;break;
		}
		
		return monthYear;
	}
	
	/** this method sets value to the TextView  within given format 
	 * @param  year
	 * @param  month
	 * @param  day
	 * @param  tvCommon
	 * @param  format**/
	public static void setDate(int year, int month, int day, TextView tvCommon, String format)
	{
		if(tvCommon != null)
    	{    		
			SimpleDateFormat sdfReq   = new SimpleDateFormat(format);
			SimpleDateFormat sdfGiven = new SimpleDateFormat(DATE_STD_PATTERN);
			
			try 
			{
				String date = (year < 10 ? "0"+year : year) + "-" +
							  ((month+1) < 10 ? "0"+(month+1) : (month+1)) + "-" +
						      (day < 10 ? "0"+day : day);
				
				String date_selected = sdfReq.format(sdfGiven.parse(date));

	    		tvCommon.setText(date_selected);
	    	    tvCommon.setTag(date);
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
			}
    	}
	}
	
	/** this method returns Current dateofJorney in string form **/
	public static String getCurrentDateAsString()
	{
		String date = null;
		
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_STD_PATTERN);
		date = sdf.format(new Date());
        
		return date;
	}
	/** this method returns Current dateofJorney in string form **/
	public static String getCurrentDateForJourneyPlan(String date)
	{
        
        Calendar ca1 = getCalender(date);
        
        int WEEK_OF_MONTH	=	ca1.get(Calendar.WEEK_OF_MONTH);
        int DAY_OF_WEEK		=	ca1.get(Calendar.DAY_OF_WEEK);
        
        if(WEEK_OF_MONTH %2==0)
        	WEEK_OF_MONTH =2;
        else 
        	WEEK_OF_MONTH=1;

		return /*"Week " +WEEK_OF_MONTH+", "+*/getDayOfWeek(DAY_OF_WEEK);
	}
	/** this method returns Current dateofJorney in string form **/
	public static String getCurrentDateForJourneyPlan2(String date)
	{
        
        Calendar ca1 = getCalender(date);

        int WEEK_OF_MONTH=ca1.get(Calendar.WEEK_OF_MONTH);
        int DAY_OF_WEEK=ca1.get(Calendar.DAY_OF_WEEK);
        if(WEEK_OF_MONTH %2==0)
        	WEEK_OF_MONTH =2;
        else 
        	WEEK_OF_MONTH=1;

		return "Week " +WEEK_OF_MONTH+",  "+getDayOfWeek(DAY_OF_WEEK);
	}
	
	public static void setWeekOfMonth(String date)
	{
        
        Calendar ca1 = getCalender(date);
        WEEK_OF_MONTH	=ca1.get(Calendar.WEEK_OF_MONTH);
	}
	public static String getDateFromDateOfJourney(String dateOfJourney)
	{
		String nextVisitDate = "";
		if(dateOfJourney.contains("Week "))
		{
			String strWeekNoAndDay []= dateOfJourney.substring(dateOfJourney.lastIndexOf("Week ")+5, dateOfJourney.length()).split(", ");
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_STD_PATTERN_ENROLL);
			Calendar cal = Calendar.getInstance();
			int weekNumber = StringUtils.getInt(strWeekNoAndDay[0]);
			if(strWeekNoAndDay.length == 2)
			{
				if(WEEK_OF_MONTH%2 ==1 && weekNumber ==1)
				{
					weekNumber =WEEK_OF_MONTH;
				}
				else if(WEEK_OF_MONTH%2 ==1 && weekNumber ==2)
				{
					weekNumber =WEEK_OF_MONTH+1;
				}
				else if(WEEK_OF_MONTH%2 ==0 && weekNumber ==1)
				{
					weekNumber =WEEK_OF_MONTH+1;
				}
				else if(WEEK_OF_MONTH%2 ==0 && weekNumber ==2)
				{
					weekNumber =WEEK_OF_MONTH;
				}
				cal.set(Calendar.WEEK_OF_MONTH, weekNumber);
				if(strWeekNoAndDay[1].equalsIgnoreCase("Monday"))
					cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				
				else if(strWeekNoAndDay[1].equalsIgnoreCase("Tuesday"))
					cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
				
				else if(strWeekNoAndDay[1].equalsIgnoreCase("Wednesday"))
					cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
				
				else if(strWeekNoAndDay[1].equalsIgnoreCase("Thursday"))
					cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
				
				else if(strWeekNoAndDay[1].equalsIgnoreCase("Friday"))
					cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
				
				else if(strWeekNoAndDay[1].equalsIgnoreCase("Saturday"))
					cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
				
				else if(strWeekNoAndDay[1].equalsIgnoreCase("Sunday"))
					cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				if(compareTo(sdf.format(cal.getTime())) >0)
					nextVisitDate =strWeekNoAndDay[1] +", "+sdf.format(cal.getTime());
				else
					nextVisitDate = null;
			}
		}
		return nextVisitDate;  
	}
	
	 public static long compareTo(String selected )  
	 {  
	 //returns negative value if date1 is before date2  
	 //returns 0 if dates are even  
	 //returns positive value if date1 is after date2  
		 long diffrence=0;
		 try
		 {
			 String date = null;
			 SimpleDateFormat sdf = new SimpleDateFormat(DATE_STD_PATTERN_ENROLL);
			 date = sdf.format(new Date());
			 diffrence =  sdf.parse(selected).getTime() -sdf.parse(date).getTime() ;  
//			 if(diffrence>0)
//				 diffrence =(int) diffrence / (24 * 60 * 60 * 1000);
//			 else
//				 diffrence = 2;
			 LogUtils.errorLog("diffrence",""+diffrence);
			 LogUtils.errorLog("selected",""+selected);
			 LogUtils.errorLog("date",""+date);
		 }
		 catch (Exception e) 
		 {
			 
		}
		 return diffrence;
	 } 
	 public static String getCurrentDate()
	 {
		 String date = "";
		    Calendar c = Calendar.getInstance();
		    int year = c.get(Calendar.YEAR);
		    int month = c.get(Calendar.MONTH);
		    int day = c.get(Calendar.DAY_OF_MONTH);
		    
		    date = day+" "+getMonthFromNumber(month+1)+", "+year;
		return date;
	 }
	 
	 public static String getCurrentDateForOrderId()
	 {
		String dateFormat = "yyMMdd";
		Date date 				= 	new Date();
		SimpleDateFormat sdf 	= 	new SimpleDateFormat(dateFormat);
		String dateStr 			= 	sdf.format(date);
	    return dateStr;
 }
	/** this method returns Difference Between Dates In Days 
	 * @param  startDate
	 * @param  endDate
	 **/
	public static int getDiffBtwDatesInDays(String startDate,String endDate)
	{
			Calendar calendar1 = Calendar.getInstance();
			Calendar calendar2 = Calendar.getInstance();
			
			calendar1.setTime(CalendarUtils.getDateFromString(startDate, CalendarUtils.DATE_STD_PATTERN));
			calendar2.setTime(CalendarUtils.getDateFromString(endDate, CalendarUtils.DATE_STD_PATTERN));
			
			  long milliseconds1 = calendar1.getTimeInMillis();
			  long milliseconds2 = calendar2.getTimeInMillis();
			  
			  long diff = milliseconds2 - milliseconds1;
			  int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
		
			  return diffDays;
	}
	
	/** this method returns Date From String 
	 * @param  dateofJorney
	 * @param  pattern
	 **/
	public static Date getDateFromString(String date,String pattern)
	{
		Date dateObj = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try 
		{
			dateObj = sdf.parse(date);
		}
		catch (ParseException e) 
		{
			e.printStackTrace();
		}
		
		return dateObj;
	}
	/** this method parses data from one format to another 
	 * @param  fromPattern
	 * @param  toPattern
	 * @param  dateToParse
	 **/
	public static String parseDate(String fromPattern,String toPattern, String dateToParse)
	{
		String parsedDate; 
		Date date = getDateFromString(dateToParse, fromPattern);
		SimpleDateFormat sdf = new SimpleDateFormat(toPattern);
		parsedDate = sdf.format(date);
		return parsedDate;
	}
	/** this method returns a string of dateofJorney
	 * @param  year
	 * @param  month
	 * @param  day
	 **/
	public static String getDate(int year, int month, int day)
	{
		String date = (year < 10 ? "0"+year : year) + "-" +
					  ((month) < 10 ? "0"+(month) : (month)) + "-" +
				      (day < 10 ? "0"+day : day);
		
		return date;
	}
	/** this method returns a string of DayOfWeek
	 * @param  dayOfWeek
	 **/
	public static String getDayOfWeek(int dayOfWeek) 
	{
		String strDayOfWeek = "";
		
		switch(dayOfWeek)
		{
			case Calendar.SUNDAY:
				strDayOfWeek = "Sunday";break;
			case Calendar.MONDAY:
				strDayOfWeek = "Monday";break;
			case Calendar.TUESDAY:
				strDayOfWeek = "Tuesday";break;
			case Calendar.WEDNESDAY:
				strDayOfWeek = "Wednesday";break;
			case Calendar.THURSDAY:
				strDayOfWeek = "Thursday";break;
			case Calendar.FRIDAY:
				strDayOfWeek = "Friday";break;
			case Calendar.SATURDAY:
				strDayOfWeek = "Saturday";break;
		}
		
		return strDayOfWeek;
	}
	/** this method returns a Calendar object
	 * @param  dateofJorney
	 **/
	public static Calendar getCalender(String date)
	{
		Calendar calendar = Calendar.getInstance();
		
		String[] str = date.split("-");
		int year = StringUtils.getInt(str[0]);
		int month = StringUtils.getInt(str[1]) - 1;
		int day = StringUtils.getInt(str[2]);
		
		calendar.set(year, month, day);
		
		return calendar;
	}
	
	public static String getDateasString(Date date, String pattern)
	{
		SimpleDateFormat sdf  = new SimpleDateFormat(pattern); 
		return sdf.format(date);
	}
	
	public static String getTimeAsString(int hr, int mn)
	{
		String hrs, min;
		if(hr < 10)
			hrs = "0"+hr;
		else 
			hrs = ""+hr;
		
		if(mn < 10)
			min = "0"+mn;
		else 
			min = ""+mn;
		
		return hrs+":"+min;
	}
	
	public static long getTimeAsMillSecs(int hr, int mn)
	{
		long milliSecs = hr*60*60 + mn*60;
		
		return milliSecs;
	}
	
	public static long getTimeStringAsMillSecs(String strTime)
	{
		String[] arr = strTime.split(":");
		
		int hr = StringUtils.getInt(arr[0]);
		int mn = StringUtils.getInt(arr[1]);
		
		long milliSecs = hr*60*60 + mn*60;
		
		return milliSecs;
	}
	
	public static String getTimeNotation(int hr)
	{
		String timeNotation = "";
		switch(hr)
		{
		   case 0:
		   case 1:
		   case 2:
		   case 3:
		   case 4:
		   case 5:
		   case 6:
		   case 7:
		   case 8:
		   case 9:
		   case 10:
		   case 11:
			   timeNotation =  "Morning";
			   break;
		   case 12:
		   case 13:
		   case 14:
		   case 15:
			   timeNotation =  "Afternoon";
			   break;
		   case 16:
		   case 17:
		   case 18:
			   timeNotation =  "Evening";
			   break;
		   default:
			   timeNotation =  "Night";
		}
		
		return timeNotation;
	}
	public static String getDateNotation(int day)
	{
		 String sep = "th";
		 switch(day)
		 {
		 	case 1  : 
		 	case 21 :	
		 	case 31 : sep= "st";
		 				break;
		 	case 2  :
		 	case 22 : sep = "nd";
		 				break;
		 				
		 	case 3	: sep = "rd";
		 	case 23 : sep = "rd";
		 			break;
		 			
		 	default : sep = "th";
		 }
		 return sep;
	}
	
	public static String getyear(String year)
	{
		char [] ch = new char[2];
		year.getChars(2, 3, ch, 0);
		String s="";
		for(int i =0 ; i< ch.length ; i++)
		{
			 s= s+""+ch[i];
		}
		
		return s;
	}
	
	
	public static String getCurrentTime()
	{
		String strCurrentTime="";
		
		DateFormat dateFormat = new SimpleDateFormat("h:mm a");
		Calendar cal = Calendar.getInstance();
		strCurrentTime = dateFormat.format(cal.getTime());
		
		return strCurrentTime;
	}
	
	public static String getCurrentFormattedTime()
	{
		String strCurrentTime	=	"";
		DateFormat dateFormat 	= 	new SimpleDateFormat("HH:mm:ss");
		Calendar cal 			= 	Calendar.getInstance();
		strCurrentTime 			= 	dateFormat.format(cal.getTime());
		
		return strCurrentTime;
	}
	
	public static String getDeliverydate()
	{
		Calendar c 		= 	Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 1);
		
	    int year 		= 	c.get(Calendar.YEAR);
	    int month 		= 	c.get(Calendar.MONTH);
	    int day 		=	c.get(Calendar.DAY_OF_MONTH);
	    String strMonth = "", strDate = "";
	    
	    if(month < 9)
	    	strMonth = "0"+(month+1);
		else
			strMonth = ""+(month+1);
		
		if(day < 10)
			strDate = "0"+(day);
		else
			strDate = ""+(day);
		
		String strSelectedDate = year+"-"+strMonth+"-"+strDate;
		return strSelectedDate;
	}
	/**
	 * Method to get the Date in 5/31/2012
	 * @return String
	 */
	public static String getCurrentSalesDate()
	{
		Calendar c 		= 	Calendar.getInstance();
	    int year 		= 	c.get(Calendar.YEAR);
	    int month 		= 	c.get(Calendar.MONTH);
	    int day 		=	c.get(Calendar.DAY_OF_MONTH);
	    String strDate = "";
	    
		if(day < 10)
			strDate = "0"+(day);
		else
			strDate = ""+(day);
		
		String strSelectedDate = (month+1)+"/"+strDate+"/"+year;
		
		return strSelectedDate;
	}
	
	/**
	 * Method to get the Date in 5/31/2012
	 * @return String
	 */
	public static String getSyncDateFormat()
	{
		Calendar c 		= 	Calendar.getInstance();
	    int year 		= 	c.get(Calendar.YEAR);
	    int month 		= 	c.get(Calendar.MONTH);
	    int day 		=	c.get(Calendar.DAY_OF_MONTH);
	    
		String strSelectedDate = (month+1)+"/"+day+"/"+year;
		
		return strSelectedDate;
	}

	/**
	 * Method to get the time in hours-minutes-seconds format to upload database
	 * @return String
	 */
	public static String getCurrentTimeToUpload()
	{
		//getting current time 
		Calendar c 		= 	Calendar.getInstance();
	    int seconds 	= 	c.get(Calendar.SECOND);
	    int minutes 	=	c.get(Calendar.MINUTE);
	    int hours 		=	c.get(Calendar.HOUR_OF_DAY);
	    
	    //variables to manage the seconds, minutes, hours in formats
	    String strMinutes = "", strhours = "", strSeconds = "";
	    
	    //for Seconds
	    if(seconds < 10)
	    	strSeconds = "0"+seconds;
		else
			strSeconds = ""+seconds;
	    
	  //for minutes
	    if(minutes < 10)
	    	strMinutes = "0"+minutes;
		else
			strMinutes = ""+minutes;
	    
	  //for hours
	    if(hours < 10)
	    	strhours = "0"+hours;
		else
			strhours = ""+hours;
	    
	    //setting format here
		String strTime = strhours+"-"+strMinutes+"-"+strSeconds;
		return strTime;
	}
	
	/**
	 * Method to get current date to post all the orders. Date format - 2012-01-01
	 * @return String
	 */
	public static String getOrderPostDate()
	{
		String dateStr = null;
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_STD_PATTERN);
		dateStr = sdf.format(date);
		return dateStr;
	}
	
	
	/**
	 * Method to get current date to post all the orders. Date format - 2012-01-01
	 * @return String
	 */
	public static String getCurrentDateTime()
	{
		String dateStr = null;
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
		dateStr = sdf.format(date);
		return dateStr;
	}
	
	
	/**
	 * Method to get current date to post all the orders. Date format - 2012-01-01
	 * @return String
	 */
	public static String getOrderDeliveryDate()
	{
		String dateStr = null;
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_STD_PATTERN_DELIVERY);
		dateStr = sdf.format(date);
		return dateStr;
	}
	
	
	
	/**
	 * Method to get the time in hours-minutes-seconds format to upload database
	 * @return String
	 */
	public static String getRetrunTime()
	{
		//getting current time 
		Calendar c 		= 	Calendar.getInstance();
	    int minutes 	=	c.get(Calendar.MINUTE);
	    int hours 		=	c.get(Calendar.HOUR_OF_DAY);
	    
	    //variables to manage the seconds, minutes, hours in formats
	    String strMinutes = "", strhours = "";
	    
	  //for minutes
	    if(minutes < 10)
	    	strMinutes = "0"+minutes;
		else
			strMinutes = ""+minutes;
	    
	  //for hours
	    if(hours < 10)
	    	strhours = "0"+hours;
		else
			strhours = ""+hours;
	    
	    //setting format here
		String strTime = strhours+":"+strMinutes;
		return strTime;
	}
	
	/**
	 * Method to get the time in hours-minutes-seconds format to upload database
	 * @return String
	 */
	public static String getRetrunTimeToDiffer()
	{
		//getting current time 
		Calendar c 		= 	Calendar.getInstance();
		c.add(Calendar.HOUR_OF_DAY, 1);
	    int minutes 	=	c.get(Calendar.MINUTE);
	    int hours 		=	c.get(Calendar.HOUR_OF_DAY);
	    
	    //variables to manage the seconds, minutes, hours in formats
	    String strMinutes = "", strhours = "";
	    
	  //for minutes
	    if(minutes < 10)
	    	strMinutes = "0"+minutes;
		else
			strMinutes = ""+minutes;
	    
	  //for hours
	    if(hours < 10)
	    	strhours = "0"+hours;
		else
			strhours = ""+hours;
	    
	    //setting format here
		String strTime = strhours+":"+strMinutes;
		return strTime;
	}
	
	public static int  getDateFromString(String strDate)
	{
		String []Split = strDate.split("T");
		String date = Split[0];
		 
		Split = date.split("-");
		
		date = Split[2];
		
		return StringUtils.getInt(date);
	}
	
	public static boolean isValidTimeForTeleOrder()
	{
		boolean isValidTime = true;
		try
		{
			Calendar calendar1 	= Calendar.getInstance();
			Date time1 			= new Date(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH), calendar1.get(Calendar.HOUR), calendar1.get(Calendar.MINUTE), calendar1.get(Calendar.SECOND));
			Date time2 			= new Date(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH), 9, 0, 0);
			Date time3 			= new Date(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH), 19, 0, 0);

			long difference1 = time1.getTime() - time2.getTime();  
			long difference2 = time1.getTime() - time3.getTime();  
			
			if(difference1>0 && difference2<0)
			{
				isValidTime = true;
			}
			else
			{
				isValidTime = false;
			}
		}
		catch (Exception e)
		{
		}
		isValidTime = true;
		return isValidTime;
	}
	
	public static String getFormatedDatefromString(String strDate)
	{
		String formatedDate  = "N/A";
		try
		{
			if(!TextUtils.isEmpty(strDate))
			{
				formatedDate = strDate;
				LogUtils.errorLog(strDate, strDate);
				
				if(strDate.contains(" "))
					strDate = strDate.replace(" ", "T");
				
				if(strDate.contains("T"))
				{
					String arrDate[]= strDate.split("T")[0].split("-");
					formatedDate = arrDate[2]+" "+getMonthFromNumber(StringUtils.getInt(arrDate[1]))+", "+arrDate[0];
				}
				else
				{
					String arrDate[]= strDate.split("-");
					formatedDate = arrDate[2]+" "+getMonthFromNumber(StringUtils.getInt(arrDate[1]))+", "+arrDate[0];
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			formatedDate = "N/A";
		}
		return formatedDate;
	}
	
	public static String getFormatedDatefromStringPrint(String strDate)
	{
		String formatedDate  = null;
		try
		{
			formatedDate = strDate;
			LogUtils.errorLog(strDate, strDate);
			
			if(strDate.contains(" "))
				strDate = strDate.replace(" ", "T");
			
			if(strDate.contains("T"))
			{
				String arrDate[]= strDate.split("T")[0].split("-");
				formatedDate = getMonthFromNumber(StringUtils.getInt(arrDate[1]))+" "+arrDate[2]+", "+arrDate[0];
			}
			else
			{
				String arrDate[]= strDate.split("-");
				formatedDate = getMonthFromNumber(StringUtils.getInt(arrDate[1]))+" "+arrDate[2]+", "+arrDate[0];
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			formatedDate =null;
		}
		return formatedDate;
	}
	
	
	public static String getFormatedDatefromString_(String strDate)
	{
		String formatedDate  = null;
		try
		{
			formatedDate = strDate;
			LogUtils.errorLog(strDate, strDate);
			
			if(strDate.contains(" "))
				strDate = strDate.replace(" ", "T");
			
			if(strDate.contains("T"))
			{
				String arrDate[]= strDate.split("T")[0].split("/");
				formatedDate = arrDate[1]+" "+getMonthFromNumber(StringUtils.getInt(arrDate[0]))+", "+arrDate[2];
			}
			else
			{
				String arrDate[]= strDate.split("/");
				formatedDate = arrDate[1]+" "+getMonthFromNumber(StringUtils.getInt(arrDate[0]))+", "+arrDate[2];
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			formatedDate =null;
		}
		return formatedDate;
	}
	
	public static String getFormatedDatefromStringNew(String strDate)
	{
		
		String formatedDate  = "";
		if(!strDate.equals("") && strDate.length() > 2)
		{
			String arrDate[]= strDate.split("-");
			formatedDate = arrDate[2]+"-"+arrDate[1]+"-"+arrDate[0];
		}
		return formatedDate;
		
	}
	public static String getFormatedDatefromStringWithTime(String strDate)
	{
		String formatedDate  = null;
		try
		{
			formatedDate = strDate;
			LogUtils.errorLog(strDate, strDate);
			if(strDate.contains("T"))
			{
				String dateTime[]  	= strDate.split("T");
				String arrDate[]	= dateTime[0].split("-");
				formatedDate = arrDate[2]+" "+getMonthFromNumber(StringUtils.getInt(arrDate[1]))+", "+arrDate[0];
				
				if(dateTime[1].contains(":"))
				{
					String arrTime[]	= dateTime[1].split(":");
					formatedDate = formatedDate+" at "+arrTime[0]+":"+arrTime[1];
					
				}
				
			}
			else
			{
				String arrDate[]= strDate.split("-");
				formatedDate = arrDate[2]+" "+getMonthFromNumber(StringUtils.getInt(arrDate[1]))+", "+arrDate[0];
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			formatedDate =null;
		}
		return formatedDate;
	}
	
	public static long getCurrentTimeInMilli()
	{
		return System.currentTimeMillis();
	}
	
	public static long getDateDifferenceInMinutes(String dateStart, String dateStop)
	{
		long diffMinutes = 0;
		try 
		{
			Calendar calendar1 = Calendar.getInstance();
			Calendar calendar2 = Calendar.getInstance();
			
			calendar1.setTime(CalendarUtils.getDateFromString(dateStart, DATE_PATTERN));
			calendar2.setTime(CalendarUtils.getDateFromString(dateStop, DATE_PATTERN));
			
		    long milliseconds1 = calendar1.getTimeInMillis();
		    long milliseconds2 = calendar2.getTimeInMillis();
		   
		    long diff = milliseconds2 - milliseconds1;
		    diffMinutes = diff / (60 * 1000);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	    return diffMinutes;
	}
	
	public static long getDateDifferenceInDays(String dateStart, String dateStop)
	{
		long diffDays = 0;
		try 
		{
			dateStart = dateStart+"T00:00:00";
			dateStop  = dateStop+"T00:00:00";
			
			Calendar calendar1 = Calendar.getInstance();
			Calendar calendar2 = Calendar.getInstance();
			
			calendar1.setTime(CalendarUtils.getDateFromString(dateStart, DATE_PATTERN));
			calendar2.setTime(CalendarUtils.getDateFromString(dateStop, DATE_PATTERN));
			
		    long milliseconds1 = calendar1.getTimeInMillis();
		    long milliseconds2 = calendar2.getTimeInMillis();
		   
		    long diff = milliseconds2 - milliseconds1;
		    diffDays  = diff / (60 * 1000 * 60 *24);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	    return diffDays;
	}
	
	public static long getDateDifferenceInMinutesNew(String dateStart, String dateStop)
	{
		if(dateStart.contains("T"))
			dateStart = dateStart.split("T")[0];
		if(dateStop.contains("T"))
			dateStop = dateStop.split("T")[0];
		
		dateStart = dateStart+"T00:00:00";
		dateStop  = dateStop+"T00:00:00";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		long diffMinutes = 0;
		Date d1 = null;
		Date d2 = null;
 
		try 
		{
			d1 = format.parse(dateStart);
			d2 = format.parse(dateStop);
 
			//in milliseconds
			long diff = d2.getTime() - d1.getTime();
 
			diffMinutes = diff;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return diffMinutes;
	}
	
	public static int getDateDiffInInt(String dateStart, String dateStop)
	{
		if(dateStart.contains("T"))
			dateStart = dateStart.split("T")[0];
		if(dateStop.contains("T"))
			dateStop = dateStop.split("T")[0];
		
		dateStart = dateStart+"T00:00:00";
		dateStop  = dateStop+"T00:00:00";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		int diffMinutes = 0;
		Date d1 = null;
		Date d2 = null;
 
		try 
		{
			d1 = format.parse(dateStart);
			d2 = format.parse(dateStop);
 
			//in milliseconds
			long diff = d2.getTime() - d1.getTime();
 
			diffMinutes = (int) (diff / 1000 / 60 / 60 / 24);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return diffMinutes;
	}
	
	public static String getFormatedDeliverydate(String strDate)
	{
		String strFormatedDate = "N/A";
		try {
			if(strDate != null && !strDate.equals(""))
			{
				
				String[] arrDate= strDate.split("-");
				strFormatedDate = " "+arrDate[2]+getDateNotation(StringUtils.getInt(arrDate[2]))+", "+getMonthAsString(StringUtils.getInt(arrDate[1])-1)+" "+arrDate[0];
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return strFormatedDate;
	}
	
	public static String getOdometerDate(String strDate)
	{
		String strDateNew = "";
		
		try {
			if(strDate != null && !strDate.equals(""))
			{
				
				String[] arrDate= strDate.split(" ");
				String[] arrDate1= arrDate[0].split("/");
				
				String day = "";
				if(StringUtils.getInt(arrDate1[2])<10)
					day = "0"+arrDate1[2];
				else
					day = arrDate1[2];
				
				String month = "";
				if(StringUtils.getInt(arrDate1[0])<10)
					month = "0"+arrDate1[0];
				else
					month = arrDate1[0];
				
				String time = "";
				
				if(StringUtils.getInt(arrDate[1].split(":")[0])<10)
					time = time+"0"+arrDate[1].split(":")[0]+":";
				else
					time = time+arrDate[1].split(":")[0]+":";
				if(StringUtils.getInt(arrDate[1].split(":")[1])<10)
					time = time+"0"+arrDate[1].split(":")[1]+":";
				else
					time = time+arrDate[1].split(":")[1]+":";
				time = time+"00";
				strDateNew = day+"-"+month+"-"+arrDate1[1]+"T"+time;
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
			
			strDateNew = strDate;
		}
		return strDateNew;
	}
	
	public static String getCurrentSynchDateTime()
	{
		String dateStr = null;
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(SYNCH_DATE_TIME_PATTERN);
		dateStr = sdf.format(date);
		return dateStr;
	}
	
	public static String getweakDayForRoute(int day, int month)
	{
		String date = "";
		date = date + ((day>9)?""+day:"0"+day);
		date = date + getDateNotation(day)+" ";
		switch (month) 
		{
			case 1:
				date = date +"Feb";
				break;
			case 2:
				date = date +"Mar";
				break;
			case 3:
				date = date +"Apr";
				break;
			case 4:
				date = date +"May";
				break;
			case 5:
				date = date +"Jun";
				break;
			case 6:
				date = date +"Jul";
				break;
			case 7:
				date = date +"Aug";
				break;
			case 8:
				date = date +"Sep";
				break;
			case 9:
				date = date +"Oct";
				break;
			case 10:
				date = date +"Nov";
				break;
			case 11:
				date = date +"Dec";
				break;
			default:
				date = date +"Jan";
				break;
		}
		return date;
		
	}
	public static final String DATE_STD_PATTERN_PRINT = "dd/MM/yyyy";
	public static String getOrderSummaryDate(int year, int month, int day)
	{
		month = month + 1;
		String date = (year < 10 ? "0"+year : year) + "-" +
				((month) < 10 ? "0"+(month) : (month)) + "-" +
				(day < 10 ? "0"+day : day);

		return date;
	}
}



