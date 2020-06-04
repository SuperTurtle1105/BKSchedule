package com.baggarm.bkschedule.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Define methods that are used widely in project.
 *
 * @author IMBAGGAARM
 * @version 2019.1706
 * @since 1.0
 */
public class Extensions {

    /**
     * Convert json to map
     *
     * @param json
     * @return
     * @throws JSONException
     * @since 1.0
     */
    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if (json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    /**
     * Convert json to map but without checking null
     *
     * @param object
     * @return
     * @throws JSONException
     * @since 1.0
     */
    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    /**
     * Convert JsonArray to List
     *
     * @param array
     * @return
     * @throws JSONException
     * @since 1.0
     */
    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    /**
     * Convert human date to date formatted in json of data return form BKU API
     *
     * @param date
     * @return
     * @since 1.0
     */
    public static String convertDateToBKUJSONDay(Date date) {
        //get day
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case 1: //sunday
                return "8";
            default:
                return Integer.toString(dayOfWeek);
        }
    }

    /**
     * Get start of day with a date in millis
     *
     * @param date
     * @return
     * @since 1.0
     */
    public static long startOfDay(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0); //set hours to zero
        cal.set(Calendar.MINUTE, 0); // set minutes to zero
        cal.set(Calendar.SECOND, 0); //set seconds to zero
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * Get end of date timestamp in millis with a date
     *
     * @param date
     * @return
     * @since 1.0
     */
    public static long endOfDay(Date date) {
        return startOfDay(date) + 85399999;
    }

    /**
     * Convert start lesson time from lesson index in BKU JSON to millis
     *
     * @param str
     * @return
     * @since 1.0
     */
    public static long convertStartLessonTimeToTimestamp(String str) {
        int startLessonTime = Integer.valueOf(str);
        if (startLessonTime <= 13) {
            //index of start lesson time + 5 multiple with 3600 to convert to second
            return (startLessonTime + 5) * 3600;
        } else {
            switch (startLessonTime) {
                case 14:
                    return 18 * 3600 + 50 * 60;
                case 15:
                    return 19 * 3600 + 40 * 60;
                case 16:
                    return 20 * 3600 + 30 * 60;
                //17
                default:
                    return 21 * 3600 + 20 * 60;
            }
        }
    }

    /**
     * Convert end lesson time from lesson index in BKU JSON to millis
     *
     * @param str
     * @return
     * @since 1.0
     */
    public static long convertEndLessonTimeToTimestamp(String str) {
        //each lesson lasts 50 minutes
        return convertStartLessonTimeToTimestamp(str) + 50 * 60;
    }

    /**
     * Convert date to BKU json format from a given date
     *
     * @param date
     * @return
     * @since 1.0
     */
    public static String convertDateToBKUJSONFormat(Date date) {
        String pattern = "dd/MM";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    /**
     * Convert start test time in bku json to timestamp in second
     *
     * @param str
     * @return
     * @since 1.0
     */
    public static int convertStartTestTimeToTimestamp(String str) {
        Pattern p = Pattern.compile("(.*)g(.*)");
        Matcher m = p.matcher(str);
        if (m.find()) {
            System.out.println(m.group());
            int hour = Integer.valueOf(m.group(1));
            try {
                int minute = Integer.valueOf(m.group(2));
                return hour * 3600 + minute * 60;
            } catch (ArrayIndexOutOfBoundsException e) {
                return hour * 3600;
            }
        }
        return 0;
    }

    /**
     * Get week in year in string format of a given date
     *
     * @param date
     * @return
     * @since 1.0
     */
    public static String getWeekYearString(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return Integer.toString(c.get(Calendar.WEEK_OF_YEAR));
    }

    /**
     * Get current week in year in string format
     *
     * @return
     * @since 1.0
     */
    public static String getCurrentWeekYearString() {
        Date date = new Date();
        return getWeekYearString(date);
    }

    /**
     * Get time in string format "HH:mm" from a given date
     *
     * @param date
     * @return
     * @since 1.0
     */
    public static String getTimeString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }

}
