package core;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import template.BXH;

public class Util {
	private static final Random random = new Random();
	public static final SimpleDateFormat fmt_save_log = new SimpleDateFormat("dd_MM_yyyy");
	private static final SimpleDateFormat fmt_get_time_now = new SimpleDateFormat("hh:mm:ss a");
	private static final SimpleDateFormat fmt_is_same_day = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
	private static final NumberFormat en = NumberFormat.getInstance(new Locale("vi"));

	public synchronized static byte[] loadfile(String url) throws IOException {
		try (FileInputStream fis = new FileInputStream(url)) {
			byte[] ab = new byte[fis.available()];
			fis.read(ab, 0, ab.length);
			return ab;
		}
	}

	public static Calendar get_calendar() {
		return Calendar.getInstance();
	}

	public static void logconsole(String s, int type, byte cmd) {
		if (Manager.gI().debug && cmd != -7 && cmd != -51 && cmd != -39 && cmd != 1 && cmd != 48 && cmd != -70 && cmd != 4
		      && cmd != -33 && cmd != 46 && cmd != -83) {
			switch (type) {
				case 0: {
					System.out.println(s);
					break;
				}
				case 1: {
					System.err.println(s);
					break;
				}
			}
		}
	}

	public static int random(int a1, int a2) {
		return random.nextInt(a1, a2);
	}

	public static int random(int a2) {
		return random.nextInt(0, a2);
	}

	public static String get_now_by_time() {
		return Util.fmt_get_time_now.format(Date.from(Instant.now()));
	}

	public synchronized static Date getDate(String day) {
		try {
			Date parsedDate = sdf.parse(day);
			return parsedDate;
		} catch (ParseException e) {
			return Date.from(Instant.now());
		}
	}

	public static boolean is_same_day(Date date1, Date date2) {
		return Util.fmt_is_same_day.format(date1).equals(Util.fmt_is_same_day.format(date2));
	}

	public static String getTime(int n) {
		int h, p, s;
		h = (n / 3600);
		p = ((n - (h * 3600)) / 60);
		s = n - (h * 3600 + p * 60);
		return String.format("%dh:%dp:%ds", h, p, s);
	}

	public static boolean isnumber(String txt) {
		try {
			Integer.valueOf(txt);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static String number_format(long num) {
		return en.format(num);
	}

	public static void sort_collections(List<BXH> list) {
		if (list.size() > 0) {
			List<BXH> list2 = new ArrayList<>();
			for (int i = 0; i < list.size(); i++) {
				boolean is_contains = false;
				BXH temp = list.get(i);
				for (int j = 0; j < list2.size(); j++) {
					BXH temp2 = list2.get(j);
					if (temp.name.equals(temp2.name)) {
						is_contains = true;
						break;
					}
				}
				if (!is_contains) {
					list2.add(list.get(i));
					list.remove(i--);
				}
			}
			try {
				Collections.sort(list2, new Comparator<BXH>() {
					@Override
					public int compare(BXH o1, BXH o2) {
						return (o1.level > o2.level) ? -1 : 1;
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("err sort " + list2);
				return;
			}
			for (int i = 0; i < list.size(); i++) {
				BXH temp = list.get(i);
				for (int j = 0; j < list2.size(); j++) {
					BXH temp2 = list2.get(j);
					if (temp.level >= temp.level) {
						list2.add(j, list.get(i));
						break;
					}
				}
				list.remove(i--);
			}
			for (int i = 0; i < list2.size(); i++) {
				list.add(list2.get(i));
			}
			list2 = null;
		}
	}
}
