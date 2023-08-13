package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import core.Service;
import io.Message;
import template.EffTemplate;
import template.Skill_info;

public class Buff {
	public static void process(Player p, Message m2) throws IOException {
		short id = m2.reader().readShort();
		byte cat = m2.reader().readByte();
		byte size = m2.reader().readByte();
		System.out.println("id " + id);
		System.out.println("cat " + cat);
		System.out.println("size " + size);
		short id2 = -100;
		for (int i = 0; i < size; i++) {
			id2 = m2.reader().readShort();
			System.out.println(id2);
		}
		int time_buff = 0;
		List<Integer> list_id = new ArrayList<>();
		List<Integer> list_par = new ArrayList<>();
		Skill_info sk_info = null;
		for (int i = 0; i < p.skill_point.size(); i++) {
			if (p.skill_point.get(i).temp.ID == id && p.skill_point.get(i).temp.Lv_RQ > 0) {
				sk_info = p.skill_point.get(i);
				for (int j = 0; j < sk_info.temp.op.size(); j++) {
					switch (sk_info.temp.op.get(j).id) {
						case 32: {
							time_buff = sk_info.temp.op.get(j).getParam(0) * 100;
							break;
						}
						default: {
							if (sk_info.temp.op.get(j).id < 28 && sk_info.temp.op.get(j).id >= 0) {
								list_id.add((int) sk_info.temp.op.get(j).id);
								list_par.add((int) sk_info.temp.op.get(j).getParam(0));
							}
							break;
						}
					}
				}
				break;
			}
		}
		if (sk_info != null && time_buff > 0 && cat == 0 && size == 1) {
			if (!p.time_use_skill.containsKey(((int) id))) {
				p.time_use_skill.put(((int) id), 1L);
			}
			System.out.println(p.time_use_skill.get(((int) id)) - System.currentTimeMillis());
			if (p.time_use_skill.get(((int) id)) > System.currentTimeMillis()) {
				Service.send_box_ThongBao_OK(p, "Skill chưa hồi!");
				return;
			}
			p.time_use_skill.put(((int) id),
			      (System.currentTimeMillis() + ((sk_info.temp.timeDelay * (1000 - p.body.get_agility(true))) / 1000)));
			if (p.mp < sk_info.temp.manaLost) {
				return;
			}
			Service.use_potion(p, 1, -sk_info.temp.manaLost);
			Service.pet(p, p, false);
			// Service.UpdateInfoMaincharInfo(p);
			Message m = new Message(20);
			m.writer().writeByte(1);
			m.writer().writeShort(id);
			m.writer().writeShort(p.id);
			m.writer().writeByte(0);
			m.writer().writeShort(sk_info.temp.idIcon);
			m.writer().writeShort(sk_info.temp.typeEffSkill);
			m.writer().writeInt(time_buff);
			m.writer().writeByte(0);
			m.writer().writeByte(1);
			m.writer().writeShort(p.id);
			m.writer().writeByte(list_id.size());
			for (int i = 0; i < list_id.size(); i++) {
				m.writer().writeByte(list_id.get(i));
				m.writer().writeShort(list_par.get(i));
				EffTemplate eff = p.get_eff(list_id.get(i));
				if (eff == null) {
					p.add_new_eff((list_id.get(i) - 1000), list_par.get(i), time_buff);
				}
			}
			switch (id) {
				case 2009: {
					m.writer().writeByte(3);
					m.writer().writeShort(308);
					m.writer().writeShort(309);
					m.writer().writeShort(310);
					break;
				}
				case 2016: {
					m.writer().writeByte(3);
					m.writer().writeShort(341);
					m.writer().writeShort(342);
					m.writer().writeShort(343);
					break;
				}
				case 2037: {
					m.writer().writeByte(3);
					m.writer().writeShort(490);
					m.writer().writeShort(491);
					m.writer().writeShort(492);
					break;
				}
				case 2040: {
					m.writer().writeByte(3);
					m.writer().writeShort(659);
					m.writer().writeShort(660);
					m.writer().writeShort(661);
					break;
				}
				default: {
					m.writer().writeByte(0);
					break;
				}
			}
			p.conn.addmsg(m);
			m.cleanup();
			//
			p.send_skill();
			p.update_info_to_all();
		}
	}
}
