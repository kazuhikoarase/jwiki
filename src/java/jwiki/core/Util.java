package jwiki.core;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Util
 * @author kazuhiko arase
 */
public class Util {
	
	private Util() {
	}

	public static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}
	
	public static String rtrim(String s) {
		return s.replaceAll("[\\s\\u3000]+$", "");
	}
	
	public static String trim(String s) {
		return s.replaceAll("^[\\s\\u3000]+|[\\s\\u3000]+$", "");
	}
	
	public static List<String> strictSplit(String s, String delm) {
		List<String> list = new ArrayList<String>();
		int start = 0;
		int index;
		while ( (index = s.indexOf(delm, start) ) != -1) {
			list.add(s.substring(start, index) );
			start = index + delm.length();
		}
		if (start < s.length() ) {
			list.add(s.substring(start) );
		}
		return list;
	}
	
	public static String coalesce(String... args) {
		String arg = null;
		for (int i = 0; i < args.length; i += 1) {
			arg = args[i];
			if (!isEmpty(arg) ) {
				return arg;
			}
		}
		if (arg == null) {
			throw new NullPointerException();
		} else {
			return arg;
		}
	}
	
	public static String formatNumber(long n) {
		return new DecimalFormat("###,###,###,###").format(n);
	}
	
	public static String formatDate(Date date) {
		if (date == null) return "";
		return new SimpleDateFormat("yyyy/MM/dd HH:mm").format(date);
	}
}