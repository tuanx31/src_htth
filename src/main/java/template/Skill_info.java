package template;

public class Skill_info {
	public long exp;
	public Skill_Template temp;
	public byte lvdevil;
	public byte devilpercent;

	public short get_percent() {
		long exp_total = Skill_info.get_exp_by_level(temp.Lv_RQ);
		if (exp_total == 0) {
			return 0;
		}
		return (short) ((long) (exp * 1000L) / exp_total);
	}

	public static long get_exp_by_level(byte lv_RQ) {
		if (lv_RQ < 5) {
			return (lv_RQ * 5_000);
		} else if (lv_RQ < 10) {
			return (lv_RQ * 11_000);
		} else if (lv_RQ < 15) {
			return (lv_RQ * 16_000);
		} else if (lv_RQ < 20) {
			return (lv_RQ * 19_000);
		} else {
			return (lv_RQ * 90_000);
		}
	}

	public int get_dame() {
		return (this.temp.damage * (100 + this.lvdevil * 50)) / 100;
	}
}
