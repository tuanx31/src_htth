package map;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import client.Player;
import core.Manager;
import core.MenuController;
import core.Service;
import core.Util;
import io.Message;
import io.SessionManager;
import template.EffTemplate;
import template.ItemMap;
import template.ItemTemplate3;
import template.Skill_Template;
import template.Skill_info;

public class Map implements Runnable {
	public static List<Map[]> ENTRYS = new ArrayList<>();
	// public static int id_eff = 0;
	public boolean running;
	public String name;
	private Thread mythread;
	public int id;
	public List<Player> players = new ArrayList<>();
	public List<Vgo> vgos = new ArrayList<>();
	public int[] list_mob;
	public ItemMap[] itmap;
	public byte zone_id;
	public byte max_zone;
	public byte max_player;
	// public static int id_eff = 0;

	public Map(int id) {
		this.id = id;
		itmap = new ItemMap[50];
		mythread = new Thread(this);
	}

	@Override
	public void run() {
		this.running = true;
		long time1 = 0;
		long time2 = 0;
		long time3 = 0;
		while (this.running) {
			try {
				time1 = System.currentTimeMillis();
				update();
				time2 = System.currentTimeMillis();
				time3 = (1_000L - (time2 - time1));
				if (time3 > 0) {
					Thread.sleep(time3);
				}
			} catch (InterruptedException e) {
			} catch (Exception e) {
				System.out.println("err map " + this.name + " " + this.zone_id);
			}
		}
	}

	private void update() throws IOException {
		update_mob();
		update_player();
		update_item_map();
	}

	private void update_item_map() {
		for (int i = 0; i < itmap.length; i++) {
			if (itmap[i] != null && itmap[i].time_pick_master < System.currentTimeMillis()) {
				itmap[i].id_master = -1;
			}
			if (itmap[i] != null && itmap[i].time_exist < System.currentTimeMillis()) {
				itmap[i] = null;
			}
		}
	}

	private synchronized void update_player() throws IOException {
		for (int i = 0; i < players.size(); i++) {
			Player p0 = players.get(i);
			// update eff
			int hp_buff = 0;
			int mp_buff = 0;
			p0.update_eff();
			EffTemplate eff = p0.get_eff(0);
			if (!p0.isdie && eff != null) { // buff hp
				hp_buff = eff.param;
			}
			eff = p0.get_eff(1);
			if (!p0.isdie && eff != null) { // buff mp
				mp_buff = eff.param;
			}
			if (p0.time_buff_hp_mp < System.currentTimeMillis()) {
				p0.time_buff_hp_mp = System.currentTimeMillis() + 5_000L;
				hp_buff += p0.body.get_hp_auto_buff(true);
				mp_buff += p0.body.get_mp_auto_buff(true);
			}
			// buff hp,mp
			if (!p0.isdie && p0.hp < p0.body.get_hp_max(true) && hp_buff > 0) {
				Service.use_potion(p0, 0, hp_buff);
			}
			if (!p0.isdie && p0.mp < p0.body.get_mp_max(true) && mp_buff > 0) {
				Service.use_potion(p0, 1, mp_buff);
			}
		}
	}

	private void update_mob() {
		for (int i = 0; i < list_mob.length; i++) {
			Mob mob = Mob.ENTRYS.get(Integer.valueOf(list_mob[i]));
			if (mob != null) {
				if (!mob.isdie) {
					if (mob.id_target != -1) {
						try {
							mob_fire(mob, mob.id_target);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} else {
					if (mob.time_refresh < System.currentTimeMillis()) {
						mob.isdie = false;
						mob.hp = mob.hp_max;
						mob.id_target = -1;
					}
				}
			}
		}
		for (int i = 0; i < Boss.ENTRYS.size(); i++) {
			Boss boss = Boss.ENTRYS.get(i);
			Mob mob = boss.mob;
			if (mob.mob_template.map.equals(this) && !mob.isdie) {
				if (mob.id_target != -1) {
					try {
						mob_fire(mob, mob.id_target);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void mob_fire(Mob mob, int id_target) throws IOException {
		if (!mob.isdie && mob.time_skill < System.currentTimeMillis()) {
			mob.time_skill = System.currentTimeMillis() + 1800L;
			Player p0 = this.get_player_by_id_inmap(id_target);
			if (p0 != null) {
				if (!p0.isdie && Math.abs(mob.x - p0.x) < 250 && Math.abs(mob.y - p0.y) < 250) {
					//
					int dame = (mob.level * mob.level) / 15;
					dame -= (dame * Util.random(10)) / 100;
					if (mob.level < 30) {
						dame = Util.random(20, 40) + mob.level;
					} else if (mob.level < 40) {
						dame = (dame * (100 + 3 * mob.level)) / 100;
					} else if (mob.level < 50) {
						dame = (dame * (100 + 5 * mob.level)) / 100;
					} else if (mob.level < 60) {
						dame = (dame * (100 + 7 * mob.level)) / 100;
					} else {
						dame = (dame * (100 + 9 * mob.level)) / 100;
					}
                                        if ((mob.level - p0.level) > 0) {
						dame = (dame * (100 + 15 * (mob.level - p0.level))) / 100;
					}
					//
					int def = p0.body.get_def(true);
					def = (def * (1000 + p0.body.get_def_percent(true))) / 1000;
					dame -= def;
					if (dame < 1) {
						dame = 1;
					} else {
						dame = (dame * (1000 - p0.body.get_dame_skip(true))) / 1000;
						if (dame < 1) {
							dame = 1;
						} else {
							dame = (dame * (1000 - p0.body.get_dame_resist(true))) / 1000;
						}
					}
					if (dame < 1) {
						dame = 1;
					}
					if (p0.body.get_miss(true) > Util.random(1200)) {
						dame = 0;
					}
					// update hp target
					if (p0.hp == p0.body.get_hp_max(true) && dame >= p0.hp) {
						p0.hp = 1;
					} else {
						p0.hp -= dame;
					}
					if (p0.hp <= 0) {
						p0.hp = 0;
						p0.isdie = true;
						mob.id_target = -1;
						die_player(p0);
					}
					//
					Message m = new Message(100);
					m.writer().writeShort(mob.index);
					m.writer().writeByte(1);
					m.writer().writeInt(mob.hp); // hp
					m.writer().writeInt(mob.hp); // mp
					m.writer().writeShort(mob.mob_template.skill[Util.random(mob.mob_template.skill.length)]); // org 149
					m.writer().writeByte(1); // size target
					m.writer().writeShort(id_target);
					m.writer().writeByte(0);
					m.writer().writeInt(dame);
					m.writer().writeInt(0); // dame plus
					m.writer().writeInt(p0.hp);
					m.writer().writeByte(0);
					send_msg_all_p(m, p0, true);
					m.cleanup();
				} else {
					mob.id_target = -1;
					Message m2 = new Message(5);
					m2.writer().writeShort(mob.index);
					send_msg_all_p(m2, p0, true);
					m2.cleanup();
				}
			}
		}
	}

	private void die_player(Player p0) throws IOException {
		p0.isdie = true;
		p0.update_die();
		Message m = new Message(7);
		m.writer().writeShort(0);
		m.writer().writeByte(-1);
		m.writer().writeShort(p0.id);
		m.writer().writeByte(0);
		m.writer().writeShort(0);
		send_msg_all_p(m, p0, true);
		m.cleanup();
	}

	public void enter_map(Player p, boolean send_data_now) {
		synchronized (this) {
			players.add(p);
		}
		if (send_data_now) {
			try {
				get_map_data(p, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// Service.UpdateInfoMaincharInfo(p);
		// Service.Main_char_Info(p);
	}

	public byte[] get_map_data(Player p, boolean send_now) throws IOException {
		// send map
		Message m = new Message(0);
		ByteArrayInputStream bais = new ByteArrayInputStream(Util.loadfile("data/map/" + p.map.id));
		DataInputStream dis = new DataInputStream(bais);
		m.writer().writeShort(dis.readShort());
		m.writer().writeByte(this.zone_id); // zone
		dis.readByte();
		m.writer().writeByte(0);
		m.writer().writeShort(p.x);
		m.writer().writeShort(p.y);
		dis.readByte();
		dis.readShort();
		dis.readShort();
		m.writer().writeInt(p.body.get_hp_max(true));
		m.writer().writeInt(p.hp);
		m.writer().writeInt(p.body.get_mp_max(true));
		m.writer().writeInt(p.mp);
		dis.readInt();
		dis.readInt();
		dis.readInt();
		dis.readInt();
		byte[] ab = dis.readAllBytes();
		m.writer().write(ab);
		if (send_now) {
			p.conn.addmsg(m);
		}
		byte[] result = m.getData();
		m.cleanup();
		dis.close();
		bais.close();
		return result;
	}

	public static Mob get_mob_obj(int id) {
		return Mob.ENTRYS.get(id);
	}

	public void leave_map(Player p) {
		synchronized (this) {
			players.remove(p);
		}
		try {
			Message m = new Message(3);
			m.writer().writeShort(p.id);
			m.writer().writeByte(2);
			send_msg_all_p(m, p, false);
			m.cleanup();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start_map() {
		this.mythread.start();
	}

	public void stop_map() {
		this.running = false;
		this.mythread.interrupt();
	}

	public static Map[] get_map_by_id(int id) {
		for (int i = 0; i < Map.ENTRYS.size(); i++) {
			if (Map.ENTRYS.get(i)[0].id == id) {
				return Map.ENTRYS.get(i);
			}
		}
		return null;
	}

	public static Map[] get_map_by_name(String name_map_goto) {
		for (int i = 0; i < Map.ENTRYS.size(); i++) {
			if (Map.ENTRYS.get(i)[0].name.equals(name_map_goto)) {
				return Map.ENTRYS.get(i);
			}
		}
		return null;
	}

	public static Player get_player_by_name_allmap(String name) {
		for (int i = 0; i < Map.ENTRYS.size(); i++) {
			for (int j = 0; j < Map.ENTRYS.get(i).length; j++) {
				Map m = Map.ENTRYS.get(i)[j];
				for (int k = 0; k < m.players.size(); k++) {
					if (m.players.get(k).name.equals(name)) {
						return m.players.get(k);
					}
				}
			}
		}
		return null;
	}

	public void send_move(Player p, Message m) throws IOException {
		if (!p.is_receiv_msg_move) {
			return;
		}
		p.x = m.reader().readShort();
		p.y = m.reader().readShort();
		// System.out.println(p.xold + " " + p.y);
		// for (Player p1 : map.players) {
		// if (p1.id != this.id) {
		Message mmove = new Message(1);
		mmove.writer().writeByte(0);
		mmove.writer().writeShort(p.id);
		mmove.writer().writeShort(p.x);
		mmove.writer().writeShort(p.y);
		send_msg_all_p(mmove, p, false);
		mmove.cleanup();
		// }
		// }
		for (int i = 0; i < list_mob.length; i++) {
			Mob mob = Mob.ENTRYS.get(Integer.valueOf(list_mob[i]));
			if (mob != null && !mob.isdie && Math.abs(mob.x - p.x) < 70 && Math.abs(mob.y - p.y) < 70
			      && mob.id_target == -1) {
				mob.id_target = p.id;
				// } else if (mob != null && !mob.isdie && !(Math.abs(mob.x - p.x) < 70 && Math.abs(mob.y - p.y) <
				// 70)
				// && mob.id_target == p.id) {
				// mob.id_target = -1;
				// Message m2 = new Message(5);
				// m2.writer().writeShort(mob.index);
				// send_msg_all_p(m2, p, true);
				// m2.cleanup();
			}
		}
		// boss
		for (int i = 0; i < Boss.ENTRYS.size(); i++) {
			Boss temp = Boss.ENTRYS.get(i);
			if (!temp.mob.isdie && Math.abs(temp.mob.x - p.x) < 70 && Math.abs(temp.mob.y - p.y) < 70
			      && temp.mob.id_target == -1) {
				temp.mob.id_target = p.id;
			}
			// if (!Boss.ENTRYS.get(i).mob.isdie && Boss.ENTRYS.get(i).mob.mob_template.map.equals(p.map)
			// && !(Math.abs(Boss.ENTRYS.get(i).mob.x - p.x) < 120 && Math.abs(Boss.ENTRYS.get(i).mob.y - p.y) <
			// 120)
			// && Boss.ENTRYS.get(i).mob.id_target == p.id) {
			// Boss.ENTRYS.get(i).mob.id_target = -1;
			// Message m2 = new Message(5);
			// m2.writer().writeShort(Boss.ENTRYS.get(i).mob.index);
			// send_msg_all_p(m2, p, true);
			// m2.cleanup();
			// }
		}
		if (!p.ischangemap && !(Math.abs(p.xold - p.x) < 45 && Math.abs(p.yold - p.y) < 35)) {
			p.ischangemap = true;
		}
		if (p.ischangemap) {
			for (Vgo vgo : this.vgos) {
				if (!p.isdie && Math.abs(vgo.xold - p.x) < 40 && Math.abs(vgo.yold - p.y) < 30) {
					p.change_map(vgo);
					break;
				}
			}
		}
	}

	public void update_num_player_in_map(Player p) throws IOException {
		Message m = new Message(-70);
		m.writer().writeByte((byte) p.map.players.size());
		m.writer().writeByte(15);
		p.conn.addmsg(m);
		m.cleanup();
	}

	public synchronized void use_skill(Player p, Message m2) throws IOException {
		short idSkill = m2.reader().readShort();
		byte CatBeFire = m2.reader().readByte();
		byte size_target = m2.reader().readByte();
		// System.out.println(idSkill);
		// System.out.println(CatBeFire);
		// System.out.println(size_target);
		if (!p.isdie) {
			if (!p.time_use_skill.containsKey(((int) idSkill))) {
				p.time_use_skill.put(((int) idSkill), 1L);
			}
			// System.out.println(p.time_use_skill.get(((int) idSkill)) - System.currentTimeMillis());
			if (p.time_use_skill.get(((int) idSkill)) > System.currentTimeMillis()) {
				Service.send_box_ThongBao_OK(p, "Skill chưa hồi!");
				return;
			}
			Skill_info sk_temp = p.get_skill_temp(idSkill);
			if (sk_temp == null) {
				return;
			}
			p.time_use_skill.put(((int) idSkill),
			      (System.currentTimeMillis() + ((sk_temp.temp.timeDelay * (1000 - p.body.get_agility(true))) / 1000)));
			if ((p.mp - sk_temp.temp.manaLost) < 0) {
				Service.send_box_ThongBao_OK(p, "MP không đủ!");
				return;
			}
			p.mp -= sk_temp.temp.manaLost;
			long dame = p.body.get_dame(true);
			switch (idSkill) {
				case 0: {
					dame = (dame * (100 + sk_temp.temp.Lv_RQ * 2)) / 100;
					break;
				}
				case 1: {
					dame = ((dame + sk_temp.get_dame()) * (150 + sk_temp.temp.Lv_RQ * 5)) / 100;
					break;
				}
				case 2: {
					dame = ((dame + sk_temp.get_dame()) * (250 + sk_temp.temp.Lv_RQ * 5)) / 100;
					break;
				}
				case 3: {
					dame = ((dame + sk_temp.get_dame()) * (100 + sk_temp.temp.Lv_RQ * 2)) / 100;
					break;
				}
				case 2003: {
					dame = ((dame + sk_temp.get_dame()) * 450) / 100;
					break;
				}
				case 2004: {
					dame = ((dame + sk_temp.get_dame()) * 250) / 100;
					break;
				}
				case 2005: {
					dame = ((dame + sk_temp.get_dame()) * 500) / 100;
					break;
				}
				case 2013: {
					dame = ((dame + sk_temp.get_dame()) * 350) / 100;
					break;
				}
				case 2014: {
					dame = ((dame + sk_temp.get_dame()) * 250) / 100;
					break;
				}
				case 2023: {
					dame = ((dame + sk_temp.get_dame()) * 260) / 100;
					break;
				}
				case 2024: {
					dame = ((dame + sk_temp.get_dame()) * 500) / 100;
					break;
				}
				case 2025: {
					dame = ((dame + sk_temp.get_dame()) * 280) / 100;
					break;
				}
				case 2026: {
					dame = ((dame + sk_temp.get_dame()) * 500) / 100;
					break;
				}
				case 2029: {
					dame = ((dame + sk_temp.get_dame()) * 715) / 100;
					break;
				}
				case 2030: {
					dame = ((dame + sk_temp.get_dame()) * 300) / 100;
					break;
				}
				case 2033: {
					dame = ((dame + sk_temp.get_dame()) * 660) / 100;
					break;
				}
				case 2034: {
					dame = ((dame + sk_temp.get_dame()) * 280) / 100;
					break;
				}
				case 2038: {
					dame = ((dame + sk_temp.get_dame()) * 550) / 100;
					// skill trai chim ung + 15% neu dang tren k
					break;
				}
				case 2041: {
					dame = ((dame + sk_temp.get_dame()) * 600) / 100;
					// trai bao dom neu dang dang bao thi +15%
					break;
				}
				case 2044: {
					dame = ((dame + sk_temp.get_dame()) * 300) / 100;
					break;
				}
				case 2045: {
					dame = ((dame + sk_temp.get_dame()) * 300) / 100;
					break;
				}
				case 2046: {
					dame = ((dame + sk_temp.get_dame()) * 400) / 100;
					break;
				}
				case 2047: {
					dame = ((dame + sk_temp.get_dame()) * 280) / 100;
					break;
				}
				case 2049: {
					dame = ((dame + sk_temp.get_dame()) * 500) / 100;
					break;
				}
				case 2050: {
					dame = ((dame + sk_temp.get_dame()) * 260) / 100;
					break;
				}
				case 2052: {
					dame = ((dame + sk_temp.get_dame()) * 400) / 100;
					break;
				}
			}
			if (idSkill == 3) {
				dame /= 4;
			}
			dame -= (dame * Util.random(10)) / 100;
			if (sk_temp.temp.nTarget < size_target) {
				size_target = sk_temp.temp.nTarget;
			}
			Player[] p_target = new Player[size_target];
			Mob[] mob_target = new Mob[size_target];
			int[] mob_target_index = new int[size_target];
			for (int i = 0; i < size_target; i++) {
				int id_target = m2.reader().readUnsignedShort();
				switch (CatBeFire) {
					case 0: {
						p_target[i] = this.get_player_by_id_inmap(id_target);
						break;
					}
					case 1: {
						mob_target[i] = Mob.ENTRYS.get(id_target);
						mob_target_index[i] = id_target;
						break;
					}
				}
			}
			long[] exp_up = null;
			switch (CatBeFire) {
				case 0: {
					Fire_Player(p_target, p, idSkill, dame);
					break;
				}
				case 1: {
					exp_up = Fire_Monster(mob_target, mob_target_index, p, idSkill, dame);
					break;
				}
			}
			if (CatBeFire == 1) {
				if (exp_up[0] > 0) {
					p.update_exp(exp_up[0], true);
				}
				if (exp_up[1] > 0) {
					p.update_skill_exp(idSkill, exp_up[1]);
				}
			}
		}
	}

	private void Fire_Player(Player[] list_target, Player p, short idSkill, long dame) throws IOException {
		// update hp target
		Skill_Template sk_temp = p.get_skill_temp(idSkill).temp;
		if (sk_temp == null) {
			return;
		}
		int dame_plus_percent = 0;
		int dame_magic_plus_percent = 0;
		int crit_skill = 0;
		int multi_dame_skill = 0;
		int pierce_skill = 0;
		int hp_absorb = 0;
		int mp_absorb = 0;
		for (int i = 0; i < sk_temp.op.size(); i++) {
			switch (sk_temp.op.get(i).id) {
				case 1: {
					dame_plus_percent += sk_temp.op.get(i).getParam(0);
					break;
				}
				case 2: {
					dame_magic_plus_percent += sk_temp.op.get(i).getParam(0);
					break;
				}
				case 10: {
					crit_skill += sk_temp.op.get(i).getParam(0);
					break;
				}
				case 11: {
					multi_dame_skill += sk_temp.op.get(i).getParam(0);
					break;
				}
				case 13: {
					pierce_skill += sk_temp.op.get(i).getParam(0);
					break;
				}
				case 21: {
					hp_absorb += sk_temp.op.get(i).getParam(0);
					break;
				}
				case 22: {
					mp_absorb += sk_temp.op.get(i).getParam(0);
					break;
				}
			}
		}
		if (hp_absorb > 0) {
			Service.use_potion(p, 0, hp_absorb);
		}
		if (mp_absorb > 0) {
			Service.use_potion(p, 1, mp_absorb);
		}
		dame = (dame * (1000 + dame_plus_percent)) / 1000;
		//
		Message m = new Message(100);
		List<Integer> list_show = new ArrayList<>();
		boolean crit = (p.body.get_crit(true) + crit_skill) > Util.random(1500);
		if (crit) {
			dame = ((long) dame * (1000 + p.body.get_multi_dame_when_crit(true) + multi_dame_skill)) / 1000;
		}
		long dame_mine_all = 0;
		long[] dame_info = new long[list_target.length];
		//
		for (int i = 0; i < list_target.length; i++) {
			Player p_target = list_target[i];
			if (p_target != null && p_target.id != p.id && !p_target.isdie && !p.isdie) {
				//
				if (p_target.body.get_dame_react(true) > Util.random(1200)) {
					long dame_mine = ((long) dame * 9) / 10;
					int def_mine = p.body.get_def(true);
					def_mine = (def_mine * (1000 + p.body.get_def_percent(true))) / 1000;
					dame_mine -= def_mine;
					if (dame_mine < 1) {
						dame_mine = crit ? 2 : 1;
					} else {
						dame_mine = (dame_mine * (1000 - p.body.get_dame_skip(true))) / 1000;
						if (dame_mine < 1) {
							dame_mine = crit ? 2 : 1;
						} else {
							dame_mine = (dame_mine * (1000 - p.body.get_dame_resist(true))) / 1000;
						}
					}
					if (dame_mine < 1) {
						dame_mine = crit ? 2 : 1;
					}
					dame_mine_all += dame_mine;
				}
				//
				if ((pierce_skill + p.body.get_pierce(true)) < Util.random(1500)) {
					int def = p_target.body.get_def(true);
					def = (def * (1000 + p_target.body.get_def_percent(true))) / 1000;
					dame -= def;
					if (dame < 1) {
						dame = crit ? 2 : 1;
					} else {
						dame = (dame * (1000 - p_target.body.get_dame_skip(true))) / 1000;
						if (dame < 1) {
							dame = crit ? 2 : 1;
						} else {
							dame = (dame * (1000 - p_target.body.get_dame_resist(true))) / 1000;
						}
					}
					if (dame < 1) {
						dame = crit ? 2 : 1;
					}
				}
				if (p_target.body.get_miss(true) > Util.random(1200)) { // miss
					dame = 0;
				}
				if (p_target.hp == p_target.body.get_hp_max(true) && dame >= p_target.hp) {
					p_target.hp = 1;
				} else {
					p_target.hp -= dame;
				}
				dame_info[i] = dame;
				if (p_target.hp <= 0) {
					p_target.hp = 0;
					p_target.isdie = true;
					die_player(p_target);
				}
				list_show.add(i);
			}
		}
		//
		if (dame_mine_all > 0) {
			if (p.hp == p.body.get_hp_max(true) && dame_mine_all >= p.hp) {
				p.hp = 1;
			} else {
				p.hp -= dame_mine_all;
			}
			if (p.hp <= 0) {
				p.hp = 0;
				p.isdie = true;
				die_player(p);
			}
		}
		//
		m.writer().writeShort(p.id);
		m.writer().writeByte(0);
		m.writer().writeInt(p.hp);
		m.writer().writeInt(p.mp);
		m.writer().writeShort(sk_temp.typeEffSkill);
		// m.writer().writeShort(Map.id_eff);
		m.writer().writeByte((dame_mine_all > 0) ? (list_show.size() + 1) : list_show.size());
		for (int i = 0; i < list_show.size(); i++) {
			Player p_target = list_target[list_show.get(i)];
			m.writer().writeShort(p_target.id);
			m.writer().writeByte(0);
			m.writer().writeInt((int) dame_info[list_show.get(i)]);
			m.writer().writeInt(0); // dame plus
			m.writer().writeInt(p_target.hp);
			if (crit) {
				m.writer().writeByte(1);
				m.writer().writeShort(1010);
				m.writer().writeShort((int) dame_info[list_show.get(i)]);
				m.writer().writeShort(0);
			} else {
				m.writer().writeByte(0);
			}
		}
		if (dame_mine_all > 0) {
			m.writer().writeShort(p.id);
			m.writer().writeByte(0);
			m.writer().writeInt((int) dame_mine_all);
			m.writer().writeInt(0); // dame plus
			m.writer().writeInt(p.hp);
			if (crit) {
				m.writer().writeByte(1);
				m.writer().writeShort(1010);
				m.writer().writeShort((int) dame_mine_all);
				m.writer().writeShort(0);
			} else {
				m.writer().writeByte(0);
			}
		}
		send_msg_all_p(m, p, true);
		m.cleanup();
	}

	private long[] Fire_Monster(Mob[] list_target, int[] list_target_index, Player p, short idSkill, long dame)
	      throws IOException {
		// update hp target
		long[] exp_up = new long[] {0, 0};
		Skill_Template sk_temp = p.get_skill_temp(idSkill).temp;
		if (sk_temp == null) {
			return exp_up;
		}
		int dame_plus_percent = 0;
		int dame_magic_plus_percent = 0;
		int crit_skill = 0;
		int multi_dame_skill = 0;
		int pierce_skill = 0;
		int hp_absorb = 0;
		int mp_absorb = 0;
		for (int i = 0; i < sk_temp.op.size(); i++) {
			switch (sk_temp.op.get(i).id) {
				case 1: {
					dame_plus_percent += sk_temp.op.get(i).getParam(0);
					break;
				}
				case 2: {
					dame_magic_plus_percent += sk_temp.op.get(i).getParam(0);
					break;
				}
				case 10: {
					crit_skill += sk_temp.op.get(i).getParam(0);
					break;
				}
				case 11: {
					multi_dame_skill += sk_temp.op.get(i).getParam(0);
					break;
				}
				case 13: {
					pierce_skill += sk_temp.op.get(i).getParam(0);
					break;
				}
				case 21: {
					hp_absorb += sk_temp.op.get(i).getParam(0);
					break;
				}
				case 22: {
					mp_absorb += sk_temp.op.get(i).getParam(0);
					break;
				}
			}
		}
		if (hp_absorb > 0) {
			Service.use_potion(p, 0, hp_absorb);
		}
		if (mp_absorb > 0) {
			Service.use_potion(p, 1, mp_absorb);
		}
		dame = (dame * (1000 + dame_plus_percent)) / 1000;
		//
		Message m = new Message(100);
		m.writer().writeShort(p.id);
		m.writer().writeByte(0);
		m.writer().writeInt(p.hp);
		m.writer().writeInt(p.mp);
		m.writer().writeShort(sk_temp.typeEffSkill);
		List<Integer> list_show = new ArrayList<>();
		boolean crit = p.body.get_crit(true) > Util.random(1500);
		if (crit) {
			dame = (dame * (1000 + p.body.get_multi_dame_when_crit(true) + multi_dame_skill)) / 1000;
		}
		long[] dame_info = new long[list_target.length];
		//
		for (int i = 0; i < list_target.length; i++) {
			Mob mob_target = list_target[i];
			if (mob_target != null && !mob_target.isdie && !p.isdie) {
				//System.out.println(p.body.get_pierce(false));
				//System.out.println(p.body.get_pierce(true));
				if ((pierce_skill + p.body.get_pierce(true)) < Util.random(1500)) {
					int def = mob_target.level * mob_target.level / 15;
					dame -= def;
					if (dame < 1) {
						dame = crit ? 2 : 1;
					}
				}
				if (10 > Util.random(100)) { // miss
					dame = 0;
				}
				if (mob_target.hp == mob_target.hp_max && dame >= mob_target.hp) {
					mob_target.hp = 1;
				} else {
					mob_target.hp -= dame;
				}
				dame_info[i] = dame;
				mob_target.id_target = p.id;
				if (mob_target.hp <= 0) {
					mob_target.hp = 0;
					mob_target.isdie = true;
					mob_target.time_refresh = System.currentTimeMillis() + Mob.TIME_RESPAWN * 1000;
					// leave item
					if (Math.abs(p.level - mob_target.level) <= 10 && 25 > Util.random(100)) {
						LeaveItemMap.leave_vang(this, mob_target, p);
					}
					exp_up[1] += mob_target.level;
					// daily quest
					if (p.quest_daily[0] == mob_target.mob_template.mob_id && p.quest_daily[2] < p.quest_daily[3]) {
						p.quest_daily[2]++;
					}
					// boss
					if (mob_target.boss_info != null) {
						Manager.gI().chatKTG("@SERVER : " + p.name + " đã tiêu diệt " + mob_target.mob_template.name);
						//
						LeaveItemMap.get_item_boss_leave(p, mob_target);
					}
				}
				// update exxp
				long exp_up_add = dame;
				if (Math.abs(p.level - mob_target.level) < 10) {
					if (p.party != null) {
						int exp_avg = 0;
						for (int i22 = 0; i22 < p.party.list.size(); i22++) {
							if (p.party.list.get(i22).map.equals(p.map)) {
								exp_avg += 1;
							}
						}
						exp_up_add = (exp_up_add * (100 + exp_avg * 5)) / 100;
					}
					exp_up[0] += exp_up_add;
				}
				list_show.add(i);
			}
		}
		//
		m.writer().writeByte(list_show.size());
		for (int j = 0; j < list_show.size(); j++) {
			Mob mob_target = list_target[list_show.get(j)];
			m.writer().writeShort(list_target_index[list_show.get(j)]);
			m.writer().writeByte(1);
			m.writer().writeInt((int) dame_info[list_show.get(j)]);
			m.writer().writeInt(0); // dame plus
			m.writer().writeInt(mob_target.hp);
			if (crit) {
				// 12 st chuan, 1010 crit, 1058 hap thu
				m.writer().writeByte(1);
				m.writer().writeShort(1010);
				m.writer().writeShort((int) dame_info[list_show.get(j)]);
				m.writer().writeShort(0);
			} else {
				m.writer().writeByte(0);
			}
		}
		send_msg_all_p(m, p, true);
		m.cleanup();
		EffTemplate eff = p.get_eff(2);
		if (eff != null) {
			exp_up[0] *= 2;
		}
		eff = p.get_eff(3);
		if (eff != null) {
			exp_up[1] *= 2;
		}
		return exp_up;
	}

	public void send_msg_all_p(Message m, Player p, boolean all) {
		for (int i = 0; i < players.size(); i++) {
			Player p0 = players.get(i);
			if (p == null || (p != null && p0.id != p.id) || all) {
				p0.conn.addmsg(m);
			}
		}
	}

	public void send_chat(Player p, Message m2) throws IOException {
		String s = m2.reader().readUTF();
		if (p.conn.user.equals("admin") && s.equals("debug")) {
			Manager.gI().debug = !Manager.gI().debug;
		} else if (p.conn.user.equals("admin") && s.equals("xem")) {
			Service.send_box_ThongBao_OK(p,
			      "Vị trí: map " + p.map.id + "\nX : " + p.x + "\nY : " + p.y + "\n Tổng số kết nối : "
			            + SessionManager.CLIENT_ENTRYS.size() + "\n Tổng số người chơi : "
			            + Manager.gI().get_total_p_inmap());
		} else if (p.conn.user.equals("admin") && s.equals("admin")) {
			MenuController.send_dynamic_menu(
			      p, 9999, "Menu Admin", new String[] {"Bảo trì", "Refresh connection", "1t Beri + 1t Ruby", "Uplevel",
			            "setXP", "get item", "update part", "save data"},
			      new short[] {168, 168, 168, 168, 168, 168, 168, 168});
		} else if (s.equals("thoatket")) {
			Vgo vgo = new Vgo();
			vgo.name_map_goto = Map.get_map_by_id(1)[0].name;
			vgo.xnew = 782;
			vgo.ynew = 203;
			p.change_map(vgo);
		} else if (Manager.gI().server_admin && p.conn.user.equals("admin")
		      && (s.charAt(0) == 'b' || s.charAt(0) == 'l' || s.charAt(0) == 'w')) {
			try {
				int n = Integer.parseInt(s.substring(1, s.length()));
				// p.test = n;
				if (s.charAt(0) == 'b') {
					ItemTemplate3.get_it_by_id(p.item.it_body[3].id).part = (short) n;
				} else if (s.charAt(0) == 'l') {
					ItemTemplate3.get_it_by_id(p.item.it_body[5].id).part = (short) n;
				}
				if (s.charAt(0) == 'w') {
					ItemTemplate3.get_it_by_id(p.item.it_body[0].id).part = (short) n;
				}
				Service.charWearing(p, p, false);
			} catch (Exception e) {
			}
		} else if (Manager.gI().server_admin && p.conn.user.equals("admin") && (s.startsWith("m"))) {
			try {
				int n = Integer.parseInt(s.substring(1));
				// Map.id_eff = n;
				// System.out.println(Map.id_eff);
				Boss.create_boss();
			} catch (Exception e) {
			}
		} else {
			this.send_chat_popup(0, p.id, s);
		}
	}

	private void send_chat_popup(int type, int id_p, String s) throws IOException {
		Message m = new Message(17);
		switch (type) {
			case 0: {
				m.writer().writeShort(id_p);
				m.writer().writeByte(0);
				m.writer().writeUTF(s);
				Player p0 = this.get_player_by_id_inmap(id_p);
				this.send_msg_all_p(m, p0, false);
				break;
			}
		}
		m.cleanup();
	}

	public void send_in4_obj_inmap(Player p) throws IOException {
		// send npc
		try {
			Message mnpc = new Message(16);
			mnpc.writer().write(Util.loadfile("data/npc/" + p.map.id));
			p.conn.addmsg(mnpc);
			mnpc.cleanup();
		} catch (IOException e) {
		}
		// send mob
		Message m_local = new Message(1);
		for (int i = 0; i < this.list_mob.length; i++) {
			Mob mob = Map.get_mob_obj(this.list_mob[i]);
			if (mob != null) {
				m_local.writer().writeByte(1);
				m_local.writer().writeShort(this.list_mob[i]);
				m_local.writer().writeShort(mob.x);
				m_local.writer().writeShort(mob.y);
			}
		}
		// boss
		for (int i = 0; i < Boss.ENTRYS.size(); i++) {
			if (!Boss.ENTRYS.get(i).mob.isdie && Boss.ENTRYS.get(i).mob.mob_template.map.equals(p.map)) {
				m_local.writer().writeByte(1);
				m_local.writer().writeShort(Boss.ENTRYS.get(i).mob.index);
				m_local.writer().writeShort(Boss.ENTRYS.get(i).mob.x);
				m_local.writer().writeShort(Boss.ENTRYS.get(i).mob.y);
			}
		}
		// send player
		for (int i = 0; i < players.size(); i++) {
			m_local.writer().writeByte(0);
			m_local.writer().writeShort(players.get(i).id);
			m_local.writer().writeShort(players.get(i).x);
			m_local.writer().writeShort(players.get(i).y);
		}
		if (m_local.writer().size() > 0) {
			p.conn.addmsg(m_local);
		}
		m_local.cleanup();
		// party
		if (p.party != null) {
			p.party.send_info();
		}
	}

	public void send_char_in4_inmap(Player p, int id) throws IOException {
		Player p0 = get_player_by_id_inmap(id);
		if (p0 != null) {
			Message m = new Message(-5);
			m.writer().writeShort(p0.id);
			m.writer().writeByte(0);
			m.writer().writeByte(0); // typePlayer
			m.writer().writeByte(-1); // typePirate
			m.writer().writeByte(p.type_pk); // typePk
			m.writer().writeByte(0); // eff dir new
			m.writer().writeByte(-1); // index team
			m.writer().writeUTF(p0.name);
			m.writer().writeShort(p0.level);
			m.writer().writeInt(p0.body.get_hp_max(true));
			m.writer().writeInt(p0.hp);
			m.writer().writeShort(p0.thongthao);
			m.writer().writeInt(p0.rankWanted);
			m.writer().writeByte(p0.levelPerfect);
			m.writer().writeByte(p0.clazz);
			m.writer().writeByte(-1); // dir new
			m.writer().writeByte(0); // levelheart
			//
			p.conn.addmsg(m);
			m.cleanup();
			//
			Service.update_PK(p0, p, p0.type_pk, false);
			Service.Weapon_fashion(p0, p, false);
			Service.pet(p0, p, false);
			Service.getThanhTich(p0, p);
			Service.charWearing(p0, p, false);
		}
	}

	public Player get_player_by_id_inmap(int id) {
		Player p0 = null;
		for (int i = 0; i < players.size(); i++) {
			Player p01 = players.get(i);
			if (p01.id == id) {
				p0 = p01;
				break;
			}
		}
		return p0;
	}

	public Player get_player_by_name(String name) {
		Player p0 = null;
		for (int i = 0; i < players.size(); i++) {
			Player p01 = players.get(i);
			if (p01.name.equals(name)) {
				p0 = p01;
				break;
			}
		}
		return p0;
	}

	public static boolean map_cant_save_site(int id) {
		return id == 64;
	}

	public static boolean is_map_sea(int id) {
		return id == 7;
	}

	public void change_flag(Player p, Message m2) throws IOException {
		byte type = m2.reader().readByte();
		byte act = m2.reader().readByte();
		// System.out.println(type);
		// System.out.println(act);
		if (act == 0) {
			p.type_pk = type;
			Message m = new Message(14);
			m.writer().writeShort(p.id);
			m.writer().writeByte(type);
			m.writer().writeByte(-1);
			m.writer().writeByte(0);
			m.writer().writeShort(-1);
			m.writer().writeByte(0);
			m.writer().writeByte(0);
			this.send_msg_all_p(m, p, true);
			m.cleanup();
		}
	}

	public void pick_item(Player p, Message m2) throws IOException {
		short id = m2.reader().readShort();
		byte cat = m2.reader().readByte();
		// System.out.println(id);
		// System.out.println(cat);
		if (p.pick_item_map(id, cat)) {
		}
	}

	public boolean add_item_map(ItemMap itm) {
		for (int i = 0; i < itmap.length; i++) {
			if (itmap[i] == null) {
				itmap[i] = itm;
				return true;
			}
		}
		return false;
	}

	public static boolean check_map_can_goto(int id) {
		boolean check = true;
		if (id == 64) {
			check = false;
		}
		return check;
	}
}
