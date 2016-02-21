package my.server.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeHelper {

	public static void sleep(int period) {
		try {
			Thread.sleep(period);
		} catch (InterruptedException e) {				
			e.printStackTrace();
		}
	}

	public static long getTime() {
        return new Date().getTime();
    }

    public static String formatTime(long time, String format) {
        DateFormat formatter = new SimpleDateFormat(format);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(new Date(time));
    }
}
