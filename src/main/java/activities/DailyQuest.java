package activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import client.Player;
import core.Service;
import core.Util;
import map.Map;
import map.Mob;
import template.MobTemplate;

public class DailyQuest {
	public static void get_quest(Player p, byte select) throws IOException {
		List<Integer> list_mob = new ArrayList<>();
		for (int i = 0; i < MobTemplate.ENTRYS.size(); i++) {
			if (Math.abs(MobTemplate.ENTRYS.get(i).level - p.level) <= 10) {
				if (checkmob(i)) {
					list_mob.add(i);
				}
			}
		}
		if (list_mob.size() < 1) {
			Mob mob_add = null;
			for (Entry<Integer, Mob> en : Mob.ENTRYS.entrySet()) {
				if (mob_add == null) {
					mob_add = en.getValue();
					list_mob.add((int) mob_add.mob_template.mob_id);
				} else if (mob_add.level < en.getValue().level) {
					mob_add = en.getValue();
					list_mob.set(0, (int) mob_add.mob_template.mob_id);
				}
			}
		}
		int index = Util.random(list_mob.size());
		p.quest_daily[0] = list_mob.get(index);
		p.quest_daily[1] = select;
		p.quest_daily[2] = 0;
		p.quest_daily[4]--;
		switch (select) {
			case 0: {
				p.quest_daily[3] = Util.random(25, 50);
				break;
			}
			case 1: {
				p.quest_daily[3] = Util.random(125, 250);
				break;
			}
			case 2: {
				p.quest_daily[3] = Util.random(1000, 2000);
				break;
			}
			case 3: {
				p.quest_daily[3] = Util.random(4000, 10000);
				break;
			}
		}
		MobTemplate mob_info = MobTemplate.ENTRYS.get(p.quest_daily[0]);
		Service.send_box_ThongBao_OK(p, String.format("Nhiệm vụ hiện tại:\nTiêu diệt %s.\nMap : %s\nHôm nay còn %s lượt.",
		      (p.quest_daily[2] + " / " + p.quest_daily[3] + " " + mob_info.name), mob_info.map.name, p.quest_daily[4]));
		// else {
		// Service.send_box_ThongBao_OK(p, "Hiện tại không có nhiệm vụ phù hợp!");
		// }
	}

	private static boolean checkmob(int i) {
		for (Map[] map : Map.ENTRYS) {
			for (int intt : map[0].list_mob) {
				Mob m_temp = Mob.ENTRYS.get(intt);
				if (m_temp != null && m_temp.mob_template.mob_id == i) {
					return true;
				}
			}
		}
		return false;
	}

	public static void remove_quest(Player p) throws IOException {
		if (p.quest_daily[0] == -1) {
			Service.send_box_ThongBao_OK(p, "Hiện tại không nhận nhiệm vụ nào!");
		} else {
			p.quest_daily[0] = -1;
			p.quest_daily[1] = -1;
			p.quest_daily[2] = 0;
			p.quest_daily[3] = 0;
			Service.send_box_ThongBao_OK(p, "Hủy nhiệm vụ thành công, nào rảnh quay lại nhận tiếp nhá!");
		}
	}

	public static String info_quest(Player p) {
		if (p.quest_daily[0] == -1) {
			return String.format("Bạn chưa nhận nhiệm vụ.\nHôm nay còn %s lượt.", p.quest_daily[4]);
		} else {
			MobTemplate mob_info = MobTemplate.ENTRYS.get(p.quest_daily[0]);
			return String.format("Nhiệm vụ hiện tại:\nTiêu diệt %s.\nMap : %s\nHôm nay còn %s lượt.",
			      (p.quest_daily[2] + " / " + p.quest_daily[3] + " " + mob_info.name), mob_info.map.name,
			      p.quest_daily[4]);
		}
	}

	public static void finish_quest(Player p) throws IOException {
		if (p.quest_daily[0] == -1) {
			Service.send_box_ThongBao_OK(p, "Hiện tại không nhận nhiệm vụ nào!");
		} else if (p.quest_daily[2] == p.quest_daily[3]) {
			//
			int beri = Util.random(50, 100) * (p.quest_daily[1] + 1) * p.quest_daily[2];
			int ruby = p.quest_daily[1] == 3 ? Util.random(50, 100)
			      : (p.quest_daily[1] == 2 ? Util.random(20, 30)
			            : (p.quest_daily[1] == 1 ? Util.random(10, 20) : Util.random(5, 10)));
			int exp = Util.random(50, 100) * (p.quest_daily[1] + 1) * p.quest_daily[2];
			p.update_vang(beri);
			p.update_ngoc(ruby);
			p.update_money();
			p.update_exp(exp, true);
			Service.send_box_ThongBao_OK(p,
			      "Trả thành công, nhận được " + beri + " beri, " + ruby + " ruby và " + exp + " kinh nghiệm!");
			p.quest_daily[0] = -1;
			p.quest_daily[1] = -1;
			p.quest_daily[2] = 0;
			p.quest_daily[3] = 0;
		} else {
			Service.send_box_ThongBao_OK(p, "Chưa hoàn thành được nhiệm vụ!");
		}
	}
}
