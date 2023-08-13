package template;

import java.util.ArrayList;
import java.util.List;

public class Level {
	public static List<Level> ENTRYS = new ArrayList<>();
	public long exp;
	public short tiemnang;

	public Level(long a, int tn) {
		this.exp = a;
		this.tiemnang = (short) tn;
	}

	static {
		long a = 12_000;
		for (int i = 0; i < 150; i++) {
			Level.ENTRYS.add(new Level(a, 2));
			if (i < 99) {
				a = (a * 12) / 10;
			} else if (i < 117) {
				a = (a * 25) / 10;
			}
		}
	}

	public static int get_total_point_by_level(short level) {
		int par = 2;
		for (int i = 0; i < level; i++) {
			par += 2;
		}
		return par;
	}
}
