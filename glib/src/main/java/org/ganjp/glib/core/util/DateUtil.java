/**
 * DateUtil.java
 *
 * Created by Gan Jianping on 07/01/15.
 * Copyright (c) 2015 GANJP. All rights reserved.
 */
package org.ganjp.glib.core.util;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

/**
 * <p>Date Utility for internal use.</p>
 *
 * @author GanJianping
 * @since 1.0
 */
@SuppressLint("SimpleDateFormat")
public class DateUtil {
    
    public static final String getNowYyyyMmDmHhMmSs() {
    	Calendar cal = Calendar.getInstance();
    	String yearStr = cal.get(Calendar.YEAR) + "";
    	int month = cal.get(Calendar.MONTH) + 1;
    	String monthStr = month<10?"0"+month:month+"";
    	int day = cal.get(Calendar.DAY_OF_MONTH);
    	String dayStr = day<10 ? "0"+day : day+"";
    	int hour = cal.get(Calendar.HOUR_OF_DAY);
    	String hourStr = hour<10 ? "0"+hour : hour+"";
    	int minute = cal.get(Calendar.MINUTE);
    	String minuteStr = minute<10 ? "0"+minute : minute+"";
    	int second = cal.get(Calendar.SECOND);
    	String secondStr = second<10 ? "0"+second : second+"";
    	return yearStr+monthStr+dayStr+hourStr+minuteStr+secondStr;
    }
    
    public static final Boolean isCurrentDate(Date aDate) {
    	if (aDate==null) {
    		return false;
    	} else {
	    	String[] date1 = getDayMonthYear(aDate, false);
	    	String[] date2 = getDayMonthYear(new Date(), false);
	    	if (date1[2].equalsIgnoreCase(date2[2])) {
	    		return true;
	    	} else {
	    		return false;
	    	}
    	}
    }

    /**
     * <p>getDdMmYYYYHhMmSsFormate</p>
     *
     * @param time
     * @return
     */
    public static final String getDdMmYYYYHhMmSsFormate(long time) {
    	if (time>0) {
    		 Date date=new Date(time);
    		 DateFormat df2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    	     String dateText = df2.format(date);
        	return dateText;
    	} else {
    		return "";
    	}
    }

    /**
     * <p>getDateString</p>
     * 
     * @param aDate
     * @return
     */
    public static final String getDateString(Date aDate) {
    	if (aDate==null) {
    		return "";
    	} else {
	    	String[] date = getDayMonthYear(aDate, false);
	    	return date[0]+"/"+date[1]+"/"+date[2];
    	}
    }
    
    /**
     * <p>formateDate</p>
     * 
     * @param aDate
     * @param aFormat : EEE, dd MMM yyyy
     * @return
     */
    public static String formateDate(Date aDate, String aFormat) {
        String formattedDate = "";
        try {
            formattedDate = new SimpleDateFormat(aFormat).format(aDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return formattedDate;
    }
    
    
    
    /**
     * <p>getDateString</p>
     * 
     * @param aDate
     * @param time
     * @return
     */
    public static final Date getDate(Date aDate, Time time) {
    	if (aDate==null) {
    		return aDate;
    	} else {
    		String dateStr = getDateString(aDate);
    		dateStr = dateStr + " " + time.toString();
    		Date date = DateUtil.parseDateOrDateTime(dateStr);
	    	return date;
    	}
    }
    
    /**
     * <p>getDate</p>
     * 
     * @param aDate
     * @param time
     * @return
     */
    public static final Date getDate(Date aDate, String time) {
    	if (aDate==null) {
    		return aDate;
    	} else {
    		String dateStr = getDateString(aDate);
    		dateStr = dateStr + " " + time;
    		Date date = DateUtil.parseDateOrDateTime(dateStr);
	    	return date;
    	}
    }
    
    public static Date parseDate(String dateString, String Format) {
		if (dateString == null || dateString.trim().length() == 0) {
			return null;
		}
		DateFormat df = new SimpleDateFormat(Format);
		try {
			Date date = (Date)df.parse(dateString);
			return date;
		} catch (Exception e) {
			return null;
		}
	}
    
    public static Date parseDateOrDateTime(String dateString) {
		if (StringUtil.isNotEmpty(dateString) && dateString.indexOf("/")!=-1 ) {
			try {
				if (dateString.indexOf(":")!=-1) {
					if (dateString.length()>16) {
						return parseDate(dateString,"dd/MM/yyyy HH:mm:ss");
					} else {
						return parseDate(dateString,"dd/MM/yyyy HH:mm");
					}
				} else {
					return parseDate(dateString,"dd/MM/yyyy");
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return DateUtil.parseDate(dateString, "dd/MM/yyyy");
	}
    
    /**
	 * <p>get day month year</p>
	 * 
	 * @param aDate
	 * @return
	 */
    public static final String[] getDayMonthYear(Date aDate, boolean isAddSpace) {
        String[] dmys = new String[3];
        if (aDate != null) {
           Calendar cal=Calendar.getInstance();
           cal.setTime(aDate);
           int day = cal.get(Calendar.DAY_OF_MONTH);
           if (day<10) {
        	   if (isAddSpace) {
        		  dmys[0] = "0 " + day;
        	   } else {
        		  dmys[0] = "0" + day;
        	   }
           } else {
        	   String tmp = day + "";
        	   if (isAddSpace) {
        		   dmys[0] =  tmp.substring(0, 1) + " " + tmp.substring(1, 2);
        	   } else {
        		   dmys[0] = tmp;
        	   }
           }
           int month = cal.get(Calendar.MONTH) + 1;
           if (month<10) {
        	   if (isAddSpace) {
        		   dmys[1] = "0 " + month;
        	   } else {
        		   dmys[1] = "0" + month;
        	   }
           } else {
        	   String tmp = month + "";
        	   if (isAddSpace) {
        		   dmys[1] = tmp.substring(0, 1) + " " + tmp.substring(1, 2);
        	   } else {
        		   dmys[1] = tmp;
        	   }
           }
           String year = cal.get(Calendar.YEAR) + "";
           if (isAddSpace) {
        	   dmys[2] = year.substring(0, 1) + " " + year.substring(1, 2) + " " + year.substring(2, 3) + " " + year.substring(3, 4);
           } else {
        	   dmys[2] = year;
           }
        }
        return dmys;
    }

    public static final String getTomorrowDateStr() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 1);
        String yearStr = cal.get(Calendar.YEAR) + "";
        int month = cal.get(Calendar.MONTH) + 1;
        String monthStr = month<10?"0"+month:month+"";
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String dayStr = day<10 ? "0"+day : day+"";
        return dayStr + "/" + monthStr + "/" + yearStr;
    }
    
}
