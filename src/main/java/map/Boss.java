package map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import client.Player;
import core.Manager;
import io.Message;
import template.MobTemplate;
import template.Option;

public class Boss {
	public static List<Boss> ENTRYS;
	public int id;
	public Mob mob;
	public short[] skill;
	public List<Option> buff;
	public long[] time_atk;

	public Boss(int id, int mob_id, String site, int hp, String skill, String buff, int index, int level) {
		this.id = id;
		//
		JSONArray js = (JSONArray) JSONValue.parse(site);
		mob = new Mob();
		mob.mob_template = MobTemplate.ENTRYS.get(mob_id);
		mob.mob_template.map = Map.get_map_by_id(Short.parseShort(js.get(0).toString()))[4];
		mob.x = Short.parseShort(js.get(1).toString());
		mob.y = Short.parseShort(js.get(2).toString());
		mob.hp_max = hp;
		mob.hp = mob.hp_max;
		mob.level = (short) level;
		mob.isdie = true;
		mob.id_target = -1;
		mob.index = index;
		mob.boss_info = this;
		Mob.ENTRYS.put(index, mob);
		//
		js.clear();
		js = (JSONArray) JSONValue.parse(skill);
		this.skill = new short[js.size()];
		for (int i = 0; i < this.skill.length; i++) {
			this.skill[i] = Short.parseShort(js.get(i).toString());
		}
		time_atk = new long[this.skill.length];
		js.clear();
		js = (JSONArray) JSONValue.parse(buff);
		this.buff = new ArrayList<>();
		for (int i = 0; i < js.size(); i++) {
			JSONArray js2 = (JSONArray) JSONValue.parse(js.get(i).toString());
			this.buff.add(new Option(Byte.parseByte(js2.get(0).toString()), Integer.parseInt(js2.get(1).toString())));
		}
	}

	public static void create_boss() {
		for (int i = 0; i < Boss.ENTRYS.size(); i++) {
			Boss temp = Boss.ENTRYS.get(i);
			if (temp.mob.isdie) {
				temp.mob.isdie = false;
				temp.mob.hp = temp.mob.hp_max;
				temp.mob.id_target = -1;
				try {
					Manager.gI().chatKTG(
					      "@SERVER : " + temp.mob.mob_template.name + " xuất hiện tại " + temp.mob.mob_template.map.name);
					//
					Message m_local = new Message(1);
					m_local.writer().writeByte(1);
					m_local.writer().writeShort(temp.mob.index);
					m_local.writer().writeShort(temp.mob.x);
					m_local.writer().writeShort(temp.mob.y);
					for (int j = 0; j < temp.mob.mob_template.map.players.size(); j++) {
						Player p0 = temp.mob.mob_template.map.players.get(j);
						p0.conn.addmsg(m_local);
					}
					m_local.cleanup();
				} catch (IOException e) {
				}
				break;
			}
		}
	}
}
