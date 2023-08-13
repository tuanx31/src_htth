package client;

import template.EffTemplate;
import template.ItemFashionP2;
import template.ItemOptionTemplate;
import template.Skill_info;

public class Body {
	public static String[] NameAttribute = new String[] {"Sức mạnh", "Phòng thủ", "Thể lực", "Tinh thần", "Nhanh nhẹn"};
	public static byte[][] Id = new byte[][] { //
	      new byte[] {1, 10, 13}, // 1
	      new byte[] {4, 26, 27}, // 2
	      new byte[] {15, 23}, // 3
	      new byte[] {16, 11}, // 4
	      new byte[] {25, 12}}; // 5
	// point 1
	public static int[] Point1_Template_atk;
	public static int[] Point1_Template_crit;
	public static int[] Point1_Template_pierce;
	// point 2
	public static int[] Point2_Template_def;
	public static int[] Point2_Template_resist_physical;
	public static int[] Point2_Template_resist_magic;
	// point 3
	public static int[] Point3_Template_hp;
	public static int[] Point3_Template_hp_potion;
	// point 4
	public static int[] Point4_Template_mp;
	public static int[] Point4_Template_dame_crit;
	// point 5
	public static int[] Point5_Template_cooldown;
	public static int[] Point5_Template_miss;
	private static int level = 1000;
	static {
		load_point_1();
		load_point_2();
		load_point_3();
		load_point_4();
		load_point_5();
	}
	private final Player p;

	public Body(Player p) {
		this.p = p;
	}

	private static void load_point_1() {
		Point1_Template_atk = new int[level];
		Point1_Template_crit = new int[level];
		Point1_Template_pierce = new int[level];
		for (int i = 0; i < Point1_Template_crit.length; i++) {
			if (i == 19) {
				Point1_Template_crit[i] = 10;
				Point1_Template_pierce[i] = 10;
			} else if (i > 19) {
				Point1_Template_crit[i] = Point1_Template_crit[i - 1] + 1;
				Point1_Template_pierce[i] = Point1_Template_pierce[i - 1] + 2;
			} else {
				Point1_Template_crit[i] = 0;
				Point1_Template_pierce[i] = 0;
			}
		}
		//
		int[] add_per_level = new int[] {2, 4, 2, 2, 4};
		int par_add = 27;
		int index = 0;
		Point1_Template_atk[0] = 44;
		Point1_Template_atk[1] = Point1_Template_atk[0] + par_add;
		for (int i = 2; i < Point1_Template_atk.length; i++) {
			par_add += add_per_level[index++];
			if (index >= add_per_level.length) {
				index = 0;
			}
			Point1_Template_atk[i] = Point1_Template_atk[i - 1] + par_add;
		}
	}

	private static void load_point_2() {
		Point2_Template_def = new int[level];
		Point2_Template_resist_magic = new int[level];
		Point2_Template_resist_physical = new int[level];
		for (int i = 0; i < Point2_Template_resist_magic.length; i++) {
			if (i == 19) {
				Point2_Template_resist_magic[i] = 10;
				Point2_Template_resist_physical[i] = 10;
			} else if (i > 19) {
				Point2_Template_resist_magic[i] = Point2_Template_resist_magic[i - 1] + 5;
				Point2_Template_resist_physical[i] = Point2_Template_resist_physical[i - 1] + 5;
			} else {
				Point2_Template_resist_magic[i] = 0;
				Point2_Template_resist_physical[i] = 0;
			}
		}
		//
		int par_add = 10;
		Point2_Template_def[0] = 140;
		Point2_Template_def[1] = Point2_Template_def[0] + par_add;
		int change = 3;
		for (int i = 2; i < Point2_Template_def.length; i++) {
			if (((i - 2 + 1) % (10)) == 0) {
				change = 2;
			}
			if (((i - 2) % (change)) == 0) {
				par_add += 10;
				change = 3;
			}
			Point2_Template_def[i] = Point2_Template_def[i - 1] + par_add;
		}
	}

	private static void load_point_3() {
		Point3_Template_hp = new int[level];
		Point3_Template_hp_potion = new int[level];
		for (int i = 0; i < Point3_Template_hp_potion.length; i++) {
			if (i == 19) {
				Point3_Template_hp_potion[i] = 12;
			} else if (i > 19) {
				Point3_Template_hp_potion[i] = Point3_Template_hp_potion[i - 1] + 12;
			} else {
				Point3_Template_hp_potion[i] = 0;
			}
		}
		//
		int par_add = 15;
		Point3_Template_hp[0] = 1001;
		Point3_Template_hp[1] = Point3_Template_hp[0] + par_add;
		for (int i = 2; i < Point3_Template_hp.length; i++) {
			par_add += 13;
			Point3_Template_hp[i] = Point3_Template_hp[i - 1] + par_add;
		}
	}

	private static void load_point_4() {
		Point4_Template_mp = new int[level];
		Point4_Template_dame_crit = new int[level];
		for (int i = 0; i < Point4_Template_dame_crit.length; i++) {
			if (i == 19) {
				Point4_Template_dame_crit[i] = 50;
			} else if (i > 19) {
				Point4_Template_dame_crit[i] = Point4_Template_dame_crit[i - 1] + 50;
			} else {
				Point4_Template_dame_crit[i] = 0;
			}
		}
		//
		int par_add = 1;
		Point4_Template_mp[0] = 20;
		Point4_Template_mp[1] = Point4_Template_mp[0] + par_add;
		for (int i = 2; i < Point4_Template_mp.length; i++) {
			if (((i - 2) % 2) == 0) {
				par_add += 2;
			}
			Point4_Template_mp[i] = Point4_Template_mp[i - 1] + par_add;
		}
	}

	private static void load_point_5() {
		Point5_Template_cooldown = new int[level];
		Point5_Template_miss = new int[level];
		for (int i = 0; i < Point5_Template_miss.length; i++) {
			if (i == 19) {
				Point5_Template_miss[i] = 10;
			} else if (i > 19) {
				Point5_Template_miss[i] = Point5_Template_miss[i - 1] + 2;
			} else {
				Point5_Template_miss[i] = 0;
			}
		}
		//
		int par_add = 3;
		Point5_Template_cooldown[0] = 13;
		Point5_Template_cooldown[1] = Point5_Template_cooldown[0] + par_add;
		for (int i = 2; i < Point5_Template_cooldown.length; i++) {
			Point5_Template_cooldown[i] = Point5_Template_cooldown[i - 1] + par_add;
		}
	}

	private int total_param_item(int id, boolean have_eff) {
		int par = 0;
		for (int j = 0; j < p.item.it_body.length; j++) {
			if (p.item.it_body[j] != null) {
				for (int i = 0; i < p.item.it_body[j].option_item.size(); i++) {
					if (p.item.it_body[j].option_item.get(i).id == id) {
						par += p.item.it_body[j].option_item.get(i).getParam(p.item.it_body[j].levelup);
					}
				}
				for (int i = 0; i < p.item.it_body[j].option_item_2.size(); i++) {
					if (p.item.it_body[j].option_item_2.get(i).id == id) {
						par += p.item.it_body[j].option_item_2.get(i).getParam(p.item.it_body[j].levelup);
					}
				}
			}
		}
		for (int i = 0; i < p.fashion.size(); i++) {
			ItemFashionP2 temp = p.fashion.get(i);
			if (temp.is_use) {
				for (int j = 0; j < temp.op.size(); j++) {
					if (temp.op.get(j).id == id) {
						par += temp.op.get(j).getParam(0);
					}
				}
				break;
			}
		}
		for (int i = 0; i < p.skill_point.size(); i++) {
			Skill_info temp = p.skill_point.get(i);
			if (temp.temp.Lv_RQ > 0 && (temp.temp.typeSkill == 3)) {
				for (int j = 0; j < temp.temp.op.size(); j++) {
					if (temp.temp.op.get(j).id == id) {
						par += temp.temp.op.get(j).getParam(0);
					}
				}
			}
		}
		if (have_eff) {
			EffTemplate temp = p.get_eff(id - 1000);
			if (temp != null) {
				par += temp.param;
			}
		}
		return par;
	}

	public int get_total_point(int type) {
		int param = 0;
		switch (type) {
			case 1: {
				param += p.point1 + get_point_plus(1);
				break;
			}
			case 2: {
				param += p.point2 + get_point_plus(2);
				break;
			}
			case 3: {
				param += p.point3 + get_point_plus(3);
				break;
			}
			case 4: {
				param += p.point4 + get_point_plus(4);
				break;
			}
			case 5: {
				param += p.point5 + get_point_plus(5);
				break;
			}
		}
		// param = (param * (p.level + 100)) / 100;
		return param;
	}

	public int get_dame(boolean have_eff) {
		Skill_info sk_temp = p.get_skill_temp(0);
		if (sk_temp == null) {
			return 0;
		}
		int dame = sk_temp.get_dame();
		// dame += total_param_item(0);
		// dame += this.get_total_point(1);
		int percent = get_dame_percent(have_eff);
		dame += (dame * percent) / 1000;
		return dame;
	}

	public int get_dame_percent(boolean have_eff) {
		int par = total_param_item(1, have_eff);
		par += Body.Point1_Template_atk[get_total_point(1) - 1];
		return par;
	}

	public int get_def(boolean have_eff) {
		int def = 5;
		def += total_param_item(3, have_eff);
		// int percent = get_def_percent();
		// def += (def * percent) / 1000;
		return def;
	}

	public int get_def_percent(boolean have_eff) {
		int par = total_param_item(4, have_eff);
		par += Body.Point2_Template_def[this.get_total_point(2) - 1];
		return par;
	}

	public int get_hp_max(boolean have_eff) {
		int hp = 1000;
		hp += Body.Point3_Template_hp[this.get_total_point(3) - 1];
		hp += total_param_item(15, have_eff);
		hp = (hp * (1000 + total_param_item(17, have_eff))) / 1000;
		return hp;
	}

	public int get_mp_max(boolean have_eff) {
		int mp = 100;
		mp += Body.Point4_Template_mp[this.get_total_point(4) - 1];
		mp += total_param_item(16, have_eff);
		mp = (mp * (1000 + total_param_item(18, have_eff))) / 1000;
		return mp;
	}

	public int get_agility(boolean have_eff) {
		int agi = total_param_item(25, have_eff);
		if (this.get_total_point(5) > 0) {
			agi += Body.Point5_Template_cooldown[this.get_total_point(5) - 1];
		}
		return get_param_by_percent(25, agi);
	}

	public int view_in4(byte b) {
		switch (b) {
			case 0:
				return get_dame(true);
			case 1:
				return get_dame_percent(false);
			case 3:
				return get_def(true);
			case 4:
				return get_def_percent(false);
			case 25:
				return get_agility(false);
			case 26:
				return get_dame_resist(false);
			case 27:
				return get_dame_resist_ap(false);
			case 17:
				return total_param_item(17, false);
			case 18:
				return total_param_item(18, false);
			case 13:
				return get_pierce(false);
			case 10:
				return get_crit(false);
			case 12:
				return get_miss(false);
			case 14:
				return get_dame_react(false);
			case 23:
				return get_hp_potion_use_percent(false);
			case 24:
				return get_mp_potion_use_percent(false);
			case 19:
				return get_hp_auto_buff(false);
			case 20:
				return get_mp_auto_buff(false);
			case 21:
				return get_hp_atk_absorb(false);
			case 22:
				return get_mp_atk_absorb(false);
			case 11:
				return get_multi_dame_when_crit(false);
			case 53:
				return get_dame_skip(false);
		}
		return total_param_item(b, false);
	}

	private int get_mp_atk_absorb(boolean have_eff) {
		return get_param_by_percent(22, total_param_item(22, have_eff));
	}

	private int get_hp_atk_absorb(boolean have_eff) {
		return get_param_by_percent(21, total_param_item(21, have_eff));
	}

	public int get_dame_react(boolean have_eff) {
		return get_param_by_percent(14, total_param_item(14, have_eff));
	}

	public int get_pierce(boolean have_eff) {
		int par = total_param_item(13, have_eff);
		par += Body.Point1_Template_pierce[this.get_total_point(1) - 1];
		return get_param_by_percent(13, par);
	}

	public int get_miss(boolean have_eff) {
		int par = total_param_item(12, have_eff);
		if (this.get_total_point(5) > 0) {
			par += Body.Point5_Template_miss[this.get_total_point(5) - 1];
		}
		return get_param_by_percent(12, par);
	}

	public int get_crit(boolean have_eff) {
		int par = total_param_item(10, have_eff);
		par += Body.Point1_Template_crit[this.get_total_point(1) - 1];
		return get_param_by_percent(10, par);
	}

	public int get_multi_dame_when_crit(boolean have_eff) {
		int par = total_param_item(11, have_eff);
		par += Body.Point4_Template_dame_crit[this.get_total_point(4) - 1];
		return par;
	}

	public int get_point_plus(int type) {
		int par = 0;
		switch (type) {
			case 1: {
				par += total_param_item(5, true);
				break;
			}
			case 2: {
				par += total_param_item(6, true);
				break;
			}
			case 3: {
				par += total_param_item(7, true);
				break;
			}
			case 4: {
				par += total_param_item(8, true);
				break;
			}
			case 5: {
				par += total_param_item(9, true);
				break;
			}
		}
		return par;
	}

	public int get_dame_skip(boolean have_eff) {
		int par = get_param_by_percent(53, total_param_item(53, have_eff));
		return par;
	}

	public int get_dame_resist(boolean have_eff) {
		int par = total_param_item(26, have_eff);
		par += Body.Point2_Template_resist_physical[this.get_total_point(2) - 1];
		return get_param_by_percent(26, par);
	}

	public int get_dame_resist_ap(boolean have_eff) {
		int par = total_param_item(27, have_eff);
		par += Body.Point2_Template_resist_magic[this.get_total_point(2) - 1];
		return get_param_by_percent(27, par);
	}

	public int get_hp_potion_use_percent(boolean have_eff) {
		int par = total_param_item(23, have_eff);
		par += Body.Point3_Template_hp_potion[this.get_total_point(3) - 1];
		return par;
	}

	public int get_mp_potion_use_percent(boolean have_eff) {
		int par = total_param_item(24, have_eff);
		return par;
	}

	public int get_param_by_percent(int id, int par) {
		if (!ItemOptionTemplate.ENTRYS.get(id).is_percent) {
			return par;
		}
		int result = 0;
		if (par > 7500) {
			par -= 7500;
			result += 7500;
			while (par > 0) {
				if (result < 400) {
					par -= 10 * 25;
					result += 10;
					// } else if (result < 500) {
					// par -= 10 * 35;
					// result += 10;
					// } else if (result < 600) {
					// par -= 10 * 50;
					// result += 10;
					// } else if (result < 700) {
					// par -= 10 * 70;
					// result += 10;
				} else if (result < 800) {
					par -= 10 * 100;
					result += 10;
				} else if (result < 900) {
					par -= 10 * 200;
					result += 10;
				} else if (result < 1000) {
					par -= 10 * 500;
					result += 10;
				} else {
					par -= 10 * 1500;
					result += 10;
				}
			}
			result -= 10;
		} else {
			result = par;
		}
		return result;
	}

	public int get_hp_auto_buff(boolean have_eff) {
		return get_param_by_percent(19, total_param_item(19, have_eff));
	}

	public int get_mp_auto_buff(boolean have_eff) {
		return get_param_by_percent(20, total_param_item(20, have_eff));
	}

	public int get_hp_plus() {
		int hp = 1000;
		int size = this.get_total_point(3);
		int par_add = 15;
		for (int i = 0; i < size; i++) {
			hp += par_add;
			par_add += 15;
		}
		return hp;
	}

	public int get_mp_plus() {
		int mp = 0;
		int size = this.get_total_point(4);
		int par_add = 2;
		for (int i = 0; i < size; i++) {
			mp += par_add;
			par_add += 2;
		}
		return mp;
	}
}
