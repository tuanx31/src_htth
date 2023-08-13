package core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import activities.Kham_Ngoc;
import client.Body;
import client.Item;
import client.Player;
import io.Message;
import io.Session;
import map.Boss;
import map.Map;
import map.Mob;
import template.EffTemplate;
import template.ItemBag47;
import template.ItemFashion;
import template.ItemFashionP;
import template.ItemFashionP2;
import template.ItemHair;
import template.ItemSell;
import template.ItemTemplate3;
import template.ItemTemplate4;
import template.ItemTemplate7;
import template.Item_wear;
import template.Option;

public class Service {
	public static void send_msg_data(Session conn, int cmd, String path, boolean save_cache) throws IOException {
		Message m = new Message(cmd);
		m.writer().write(Util.loadfile(path));
		if (save_cache) {
			conn.p.list_msg_cache.add(m);
		} else {
			conn.addmsg(m);
		}
		m.cleanup();
	}

	public static void UpdateInfoMaincharInfo(Player p) throws IOException {
		Message m = new Message(-75);
		m.writer().writeShort(-1);
		m.writer().writeShort(0);
		m.writer().writeShort(0);
		p.conn.addmsg(m);
		m.cleanup();
	}

	public static void Main_char_Info(Player p) throws IOException {
		int hp_max = p.body.get_hp_max(true);
		int mp_max = p.body.get_mp_max(true);
		if (p.hp > hp_max) {
			p.hp = hp_max;
		}
		if (p.mp > mp_max) {
			p.mp = mp_max;
		}
		Message m = new Message(-10);
		m.writer().writeShort(p.id);
		m.writer().writeUTF(p.name);
		m.writer().writeInt(hp_max);
		m.writer().writeInt(mp_max);
		m.writer().writeInt(p.hp);
		m.writer().writeInt(p.mp);
		m.writer().writeShort(p.level);
		m.writer().writeShort(p.get_level_percent());
		m.writer().writeShort(p.thongthao);
		m.writer().writeShort(p.thongthaopercent);
		m.writer().writeInt(p.rankWanted);
		m.writer().writeByte(p.clazz);
		m.writer().writeInt(p.pointPk);
		m.writer().writeShort(p.pointAttribute);
		m.writer().writeByte(p.typePirate);
		m.writer().writeByte(p.indexGhostServer);
		m.writer().writeByte(p.numPassive);
		m.writer().writeByte(p.levelPerfect);
		//
		m.writer().writeByte(Body.NameAttribute.length); // size
		int p_, p_pluss;
		for (int i = 0; i < Body.NameAttribute.length; i++) {
			m.writer().writeUTF(Body.NameAttribute[i]);
			p_ = i == 0 ? p.point1 : (i == 1 ? p.point2 : (i == 2 ? p.point3 : (i == 3 ? p.point4 : p.point5)));
			p_pluss = p.body.get_point_plus(i + 1);
			m.writer().writeShort(p_);
			m.writer().writeShort(p_pluss);
			int dem = 0;
			int[] par_show = new int[Body.Id[i].length];
			for (int j = 0; j < Body.Id[i].length; j++) {
				switch (Body.Id[i][j]) {
					case 1: {
						par_show[j] = Body.Point1_Template_atk[p_ + p_pluss - 1];
						break;
					}
					case 10: {
						par_show[j] = Body.Point1_Template_crit[p_ + p_pluss - 1];
						break;
					}
					case 13: {
						par_show[j] = Body.Point1_Template_pierce[p_ + p_pluss - 1];
						break;
					}
					case 4: {
						par_show[j] = Body.Point2_Template_def[p_ + p_pluss - 1];
						break;
					}
					case 26: {
						par_show[j] = Body.Point2_Template_resist_physical[p_ + p_pluss - 1];
						break;
					}
					case 27: {
						par_show[j] = Body.Point2_Template_resist_magic[p_ + p_pluss - 1];
						break;
					}
					case 15: {
						par_show[j] = Body.Point3_Template_hp[p_ + p_pluss - 1];
						break;
					}
					case 23: {
						par_show[j] = Body.Point3_Template_hp_potion[p_ + p_pluss - 1];
						break;
					}
					case 11: {
						par_show[j] = Body.Point4_Template_dame_crit[p_ + p_pluss - 1];
						break;
					}
					case 16: {
						par_show[j] = Body.Point4_Template_mp[p_ + p_pluss - 1];
						break;
					}
					case 12: {
						if (p_ + p_pluss > 0) {
							par_show[j] = Body.Point5_Template_miss[p_ + p_pluss - 1];
						}
						break;
					}
					case 25: {
						if (p_ + p_pluss > 0) {
							par_show[j] = Body.Point5_Template_cooldown[p_ + p_pluss - 1];
						}
						break;
					}
				}
				if (par_show[j] > 0) {
					dem++;
				}
			}
			m.writer().writeByte(dem);
			for (int j = 0; j < Body.Id[i].length; j++) {
				if (par_show[j] > 0) {
					m.writer().writeByte(Body.Id[i][j]);
					m.writer().writeInt(par_show[j]);
				}
			}
		}
		//
		m.writer().writeShort(p.pointSkill);
		m.writer().writeByte(10);
		for (int i = 0; i < 10; i++) {
			m.writer().writeByte(1);
			m.writer().writeByte(1);
		}
		byte[] a = new byte[] {0, 1, 3, 4, 25, 26, 27, 53, 17, 18, 10, 11, 12, 13, 14, 19, 20, 21, 22, 23, 24};
		m.writer().writeByte(a.length);
		for (int i = 0; i < a.length; i++) {
			m.writer().writeByte(a[i]);
			m.writer().writeInt(p.body.view_in4(a[i]));
		}
		m.writer().writeByte(-1);
		m.writer().writeByte(-1);
		m.writer().writeByte(-1);
		p.conn.addmsg(m);
		m.cleanup();
	}

	public static void UpdatePvpPoint(Player p) throws IOException {
		Message m = new Message(-66);
		m.writer().writeInt(1000);
		m.writer().writeInt(0);
		m.writer().writeInt(0);
		p.conn.addmsg(m);
		m.cleanup();
	}

	public static void update_PK(Player p0, Player p, int type, boolean save_cache) throws IOException {
		Message m = new Message(14);
		m.writer().writeShort(p0.id);
		m.writer().writeByte(type);
		m.writer().writeByte(-1);
		m.writer().writeByte(0);
		m.writer().writeShort(-1);
		m.writer().writeByte(0);
		m.writer().writeByte(0);
		if (save_cache) {
			p.list_msg_cache.add(m);
		} else {
			p.conn.addmsg(m);
		}
		m.cleanup();
	}

	public static void getThanhTich(Player p0, Player p) throws IOException {
		Message m = new Message(65);
		m.writer().writeShort(p0.id);
		m.writer().writeByte(0);
		m.writer().writeByte(-1);
		m.writer().writeByte(-1);
		m.writer().writeByte(0);
		p.conn.addmsg(m);
		m.cleanup();
	}

	public static void Weapon_fashion(Player p0, Player p, boolean save_cache) throws IOException {
		Message m = new Message(-104);
		m.writer().writeShort(p0.id);
		m.writer().writeByte(0);
		m.writer().writeByte(6);
		m.writer().writeShort(p0.head);
		if (save_cache) {
			p.list_msg_cache.add(m);
		} else {
			p.conn.addmsg(m);
		}
		m.cleanup();
	}

	public static void Send_UI_Shop(Player p, int type) throws IOException {
		Message m = new Message(-19);
		m.writer().writeByte(type);
		switch (type) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4: {
				m.writer().writeUTF(Manager.gI().NAME_ITEM_SELL_TEMP[type]);
				m.writer().writeByte(3);
				List<ItemSell> list_sell = ItemSell.get_it_sell(p.level, type);
				m.writer().writeShort(list_sell.size());
				for (int i = 0; i < list_sell.size(); i++) {
					ItemSell it_sell_temp = list_sell.get(i);
					ItemTemplate3 it_temp = ItemTemplate3.get_it_by_id(it_sell_temp.id);
					ItemTemplate3.readUpdateItem(m.writer(), it_temp);
					m.writer().writeByte(0);
					m.writer().writeInt(it_sell_temp.price);
				}
				break;
			}
			case 20: {
				m.writer().writeUTF("Quán ăn");
				m.writer().writeByte(4);
				short[] id_sell = new short[] {2, 3, 5, 4, 214, 85, 173, 174, 80, 159};
				m.writer().writeShort(id_sell.length);
				for (int i = 0; i < id_sell.length; i++) {
					m.writer().writeShort(id_sell[i]);
					m.writer().writeShort(1);
				}
				break;
			}
			case 21: {
				m.writer().writeUTF("Shop Nguyên liệu");
				m.writer().writeByte(7);
				short[] id_sell = new short[] {1, 2, 3, 4, 5, 6};
				m.writer().writeShort(id_sell.length);
				for (int i = 0; i < id_sell.length; i++) {
					m.writer().writeByte(id_sell[i]);
					m.writer().writeShort(1);
				}
				break;
			}
			case 99: {
				m.writer().writeUTF("Rương đồ");
				m.writer().writeByte(99);
				m.writer().writeShort(0);
				break;
			}
			case 111: {
				m.writer().writeUTF("Shop Đá");
				m.writer().writeByte(4);
				m.writer().writeShort(Kham_Ngoc.ID_SELL.length);
				for (int i = 0; i < Kham_Ngoc.ID_SELL.length; i++) {
					m.writer().writeShort(Kham_Ngoc.ID_SELL[i]);
					m.writer().writeShort(1);
				}
				break;
			}
		}
		p.conn.addmsg(m);
		m.cleanup();
	}

	public static void charWearing(Player p0, Player p, boolean save_cache) throws IOException {
		Message m = new Message(19);
		m.writer().writeShort(p0.id);
		m.writer().writeByte(0);
		m.writer().writeShort(p0.get_head());
		m.writer().writeShort(p0.get_hair());
		m.writer().writeByte(8);
		short[] fashion = p0.get_fashion();
		for (int i = 0; i < 8; i++) {
			Item_wear it_w = p0.item.it_body[i];
			if (it_w != null) {
				m.writer().writeByte(1);
				if (p0.id == p.id) {
					Item.readUpdateItem(m.writer(), it_w);
				}
				if (fashion != null && fashion[i] != -1) {
					m.writer().writeShort(fashion[i]);
				} else {
					m.writer().writeShort(ItemTemplate3.get_it_by_id(it_w.id).part);
				}
			} else {
				m.writer().writeByte(0);
				m.writer().writeShort(-1);
			}
		}
		m.writer().writeByte(8);
		if (save_cache) {
			p.list_msg_cache.add(m);
		} else {
			p.conn.addmsg(m);
		}
		m.cleanup();
	}

	public static void send_icon(Message m, Session conn) {
		// if (conn.user != null && conn.pass != null && !conn.user.isEmpty() && !conn.pass.isEmpty()) {
		short idnotf = -1;
		String path = "";
		try {
			short id = m.reader().readShort();
			idnotf = id;
			Message m2 = new Message(-51);
			m2.writer().writeShort(id);
			path = "data/icon/x" + conn.zoomlv + "/" + id + ".png";
			m2.writer().write(Util.loadfile(path));
			conn.addmsg(m2);
			m2.cleanup();
		} catch (IOException e) {
			System.out.println("icon id not found " + path);
			Manager.gI().add_icon_fail(path);
		}
		// }
	}

	public static void send_obj_template(Player p, Message m) throws IOException {
		byte type = m.reader().readByte();
		short id = m.reader().readShort();
		// System.out.println(type);
		// System.out.println(id);
		if (type == 98) {
			Message m2 = new Message(48);
			m2.writer().writeByte(98);
			m2.writer().writeShort(id);
			m2.writer().write(Util.loadfile("data/template/98/x/" + id));
			p.conn.addmsg(m2);
			m2.cleanup();
		} else if (type == 1) {
			Message m2 = new Message(48);
			m2.writer().write(Util.loadfile("data/mob_temp/" + id));
			p.conn.addmsg(m2);
			m2.cleanup();
		} else if (type == 97) {
			Message m2 = new Message(48);
			m2.writer().writeByte(97);
			m2.writer().writeShort(id);
			m2.writer().write(Util.loadfile("data/template/97/" + id));
			p.conn.addmsg(m2);
			m2.cleanup();
		}
	}

	public static void request_mob_in4(Player p, int id) throws IOException {
		boolean check_in_map = false;
		for (int i = 0; i < p.map.list_mob.length; i++) {
			if (p.map.list_mob[i] == id) {
				check_in_map = true;
				break;
			}
		}
		if (check_in_map) {
			Mob temp = Mob.ENTRYS.get(id);
			Message m = new Message(4);
			m.writer().writeShort(id);
			m.writer().writeShort(temp.mob_template.mob_id); // id mob
			m.writer().writeShort(temp.x);
			m.writer().writeShort(temp.y);
			m.writer().writeShort(temp.level); // lv
			m.writer().writeInt(temp.hp);
			m.writer().writeInt(temp.hp_max);
			m.writer().writeShort(150); // type eff
			m.writer().writeShort(Mob.TIME_RESPAWN); // tgian hs
			m.writer().writeByte(temp.mob_template.typemonster); // type mons
			m.writer().writeByte(0); // lvthongthao
			p.conn.addmsg(m);
			m.cleanup();
		} else {
			for (int i = 0; i < Boss.ENTRYS.size(); i++) {
				Boss temp = Boss.ENTRYS.get(i);
				if (!temp.mob.isdie && temp.mob.mob_template.map.equals(p.map) && temp.mob.index == id) {
					Message m = new Message(4);
					m.writer().writeShort(id);
					m.writer().writeShort(temp.mob.mob_template.mob_id); // id mob
					m.writer().writeShort(temp.mob.x);
					m.writer().writeShort(temp.mob.y);
					m.writer().writeShort(temp.mob.level); // lv
					m.writer().writeInt(temp.mob.hp);
					m.writer().writeInt(temp.mob.hp_max);
					m.writer().writeShort(150); // type eff
					m.writer().writeShort(Mob.TIME_RESPAWN); // tgian hs
					m.writer().writeByte(temp.mob.mob_template.typemonster); // type mons
					m.writer().writeByte(0); // lvthongthao
					p.conn.addmsg(m);
					m.cleanup();
					break;
				}
			}
		}
	}

	public static void rms_process(Player p, Message m) {
		try {
			byte type = m.reader().readByte();
			byte id = m.reader().readByte();
			int size = 0;
			try {
				size = m.reader().readShort();
			} catch (IOException e) {
			}
			if (id == 0) {
				if (size > 0) {
					byte[] ab = new byte[size];
					for (int i = 0; i < ab.length; i++) {
						ab[i] = m.reader().readByte();
					}
					p.rms[id] = ab;
				}
				Message m2 = new Message(-33);
				if (p.rms[0].length > 0) {
					m2.writer().write(p.rms[0]);
				} else {
					m2.writer().write(Util.loadfile("data/msg/skill_" + p.clazz));
				}
				p.conn.addmsg(m2);
				m2.cleanup();
			} else if (size > 0) {
				byte[] ab = new byte[size];
				for (int i = 0; i < ab.length; i++) {
					ab[i] = m.reader().readByte();
				}
				p.rms[id] = ab;
				Message m2 = new Message(-33);
				m2.writer().writeByte(id);
				m2.writer().writeShort(size);
				m2.writer().write(ab);
				p.conn.addmsg(m2);
				m2.cleanup();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void area_select(Player p, Message m2) throws IOException {
		byte type = m2.reader().readByte();
		byte select = m2.reader().readByte();
		p.map.leave_map(p);
		Map[] map_enter = Map.get_map_by_id(p.map.id);
		p.map = map_enter[select];
		p.map.enter_map(p, false);
		//
		Message m = new Message(21);
		m.writer().writeByte(select);
		m.writer().writeByte(0);
		m.writer().writeShort(p.x);
		m.writer().writeShort(p.y);
		m.writer().writeInt(p.body.get_hp_max(true));
		m.writer().writeInt(p.hp);
		m.writer().writeInt(p.body.get_mp_max(true));
		m.writer().writeInt(p.mp);
		m.writer().writeByte(12);
		m.writer().writeShort(260);
		p.conn.addmsg(m);
		m.cleanup();
	}

	public static void pet(Player p0, Player p, boolean save_cache) throws IOException {
		Message m = new Message(-80);
		m.writer().writeByte(1);
		m.writer().writeShort(-1);
		m.writer().writeShort(p0.id);
		if (save_cache) {
			p.list_msg_cache.add(m);
		} else {
			p.conn.addmsg(m);
		}
		m.cleanup();
	}

	public static void login_ok(Player p, boolean save_cache) throws IOException {
		Message m = new Message(-2);
		if (save_cache) {
			p.list_msg_cache.add(m);
		} else {
			p.conn.addmsg(m);
		}
		m.cleanup();
	}

	public static void checkPlayInMap(Player p, Message m2) {
		try {
			short id_p = m2.reader().readShort();
			// System.out.println("player id " + id_p);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void buy_item(Player p, Message m2) throws IOException {
		byte TypeShop = m2.reader().readByte();
		short id = m2.reader().readShort();
		short value = m2.reader().readShort();
		byte cat = -1;
		if (TypeShop == 116 || TypeShop == 118) {
			cat = m2.reader().readByte();
		}
		if (value <= 0 || value > 20) {
			return;
		}
		// System.out.println(TypeShop);
		// System.out.println(id);
		// System.out.println(value);
		// System.out.println(cat);
		boolean check = false;
		if (cat == -1 && TypeShop >= 0 && TypeShop < 5) {
			List<ItemSell> list_sell = ItemSell.get_it_sell(p.level, TypeShop);
			for (int i = 0; i < list_sell.size(); i++) {
				ItemSell temp_sell = list_sell.get(i);
				if (temp_sell != null && temp_sell.id == id) {
					if (p.item.able_bag() > 0) {
						if (p.get_vang() < temp_sell.price) {
							Service.send_box_ThongBao_OK(p, "Bạn không đủ " + temp_sell.price + " beri!");
							return;
						}
						p.update_vang(-temp_sell.price);
						Log.gI().add_log(p,
						      "Mua " + ItemTemplate3.get_it_by_id(temp_sell.id).name + " -" + temp_sell.price + " beri");
						p.update_money();
						Item_wear it_add = new Item_wear();
						it_add.setup_template_by_id(temp_sell.id);
						if (it_add.id > -1) {
							p.item.add_item_bag3(it_add);
						}
						p.item.update_Inventory(4, false);
						p.item.update_Inventory(7, false);
						p.item.update_Inventory(3, false);
						check = true;
					} else {
						Service.send_box_ThongBao_OK(p, "Hành trang không đủ chỗ trống!");
						return;
					}
					break;
				}
			}
			//
			if (check) {
				Service.send_box_ThongBao_OK(p, "Mua thành công " + ItemTemplate3.get_it_by_id(id).name);
			} else {
				Service.send_box_ThongBao_OK(p, "Mua thất bại, hãy thử lại!");
			}
		} else if (cat == -1 && TypeShop == 20) {
			if (id == 2 || id == 3 || id == 214 || id == 5 || id == 4 || id == 85 || id == 173 || id == 174 || id == 80
			      || id == 159) {
				int vang_req = ItemTemplate4.get_it_by_id(id).ruby * value;
				if (id == 159) {
					vang_req = 300 * value;
				}
				if (vang_req > 0) {
					if (p.get_ngoc() < vang_req) {
						Service.send_box_ThongBao_OK(p, "Không đủ " + vang_req + " ruby");
						return;
					}
					p.update_ngoc(-vang_req);
					Log.gI().add_log(p, "Mua " + ItemTemplate4.get_it_by_id(id).name + " -" + vang_req + " ruby");
				} else {
					vang_req = ItemTemplate4.get_it_by_id(id).beri * value;
					if (p.get_vang() < vang_req) {
						Service.send_box_ThongBao_OK(p, "Không đủ " + vang_req + " beri");
						return;
					}
					p.update_vang(-vang_req);
					Log.gI().add_log(p, "Mua " + ItemTemplate4.get_it_by_id(id).name + " -" + vang_req + " beri");
				}
				p.update_money();
				ItemBag47 it = new ItemBag47();
				it.id = id;
				it.category = 4;
				it.quant = value;
				if (p.item.able_bag() > 0 && (p.item.total_item_bag_by_id(4, it.id) + it.quant) < 32_000) {
					p.item.add_item_bag47(4, it);
					Message m22 = new Message(-64);
					m22.writer().writeUTF("Mua " + value);
					p.conn.addmsg(m22);
					m22.cleanup();
					//
					p.item.update_Inventory(4, false);
					p.item.update_Inventory(7, false);
					p.item.update_Inventory(3, false);
				} else {
					Service.send_box_ThongBao_OK(p, "Hành trang không đủ chỗ trống!");
				}
			}
		} else if (cat == -1 && TypeShop == 21) {
			if ((id >= 1 && id <= 6)) {
				switch (id) {
					case 1: {
						int vang_req = 1 * value;
						if (p.get_ngoc() < vang_req) {
							Service.send_box_ThongBao_OK(p, "Không đủ " + vang_req + " ruby");
							return;
						}
						p.update_ngoc(-vang_req);
						Log.gI().add_log(p, "Mua " + ItemTemplate7.get_it_by_id(id).name + " -" + vang_req + " ruby");
						break;
					}
					case 2: {
						int vang_req = 500 * value;
						if (p.get_vang() < vang_req) {
							Service.send_box_ThongBao_OK(p, "Không đủ " + vang_req + " beri");
							return;
						}
						p.update_vang(-vang_req);
						Log.gI().add_log(p, "Mua " + ItemTemplate7.get_it_by_id(id).name + " -" + vang_req + " beri");
						break;
					}
					case 3: {
						int vang_req = 1 * value;
						if (p.get_ngoc() < vang_req) {
							Service.send_box_ThongBao_OK(p, "Không đủ " + vang_req + " ruby");
							return;
						}
						p.update_ngoc(-vang_req);
						Log.gI().add_log(p, "Mua " + ItemTemplate7.get_it_by_id(id).name + " -" + vang_req + " ruby");
						break;
					}
					case 4: {
						int vang_req = 2 * value;
						if (p.get_ngoc() < vang_req) {
							Service.send_box_ThongBao_OK(p, "Không đủ " + vang_req + " ruby");
							return;
						}
						p.update_ngoc(-vang_req);
						Log.gI().add_log(p, "Mua " + ItemTemplate7.get_it_by_id(id).name + " -" + vang_req + " ruby");
						break;
					}
					case 5: {
						int vang_req = 5 * value;
						if (p.get_ngoc() < vang_req) {
							Service.send_box_ThongBao_OK(p, "Không đủ " + vang_req + " ruby");
							return;
						}
						p.update_ngoc(-vang_req);
						Log.gI().add_log(p, "Mua " + ItemTemplate7.get_it_by_id(id).name + " -" + vang_req + " ruby");
						break;
					}
					case 6: {
						int vang_req = 7 * value;
						if (p.get_ngoc() < vang_req) {
							Service.send_box_ThongBao_OK(p, "Không đủ " + vang_req + " ruby");
							return;
						}
						p.update_ngoc(-vang_req);
						Log.gI().add_log(p, "Mua " + ItemTemplate7.get_it_by_id(id).name + " -" + vang_req + " ruby");
						break;
					}
				}
				p.update_money();
				ItemBag47 it = new ItemBag47();
				it.id = id;
				it.category = 7;
				it.quant = value;
				if (p.item.able_bag() > 0 && (p.item.total_item_bag_by_id(7, it.id) + it.quant) < 32_000) {
					p.item.add_item_bag47(7, it);
					Message m22 = new Message(-64);
					m22.writer().writeUTF("Mua " + value);
					p.conn.addmsg(m22);
					m22.cleanup();
					//
					p.item.update_Inventory(4, false);
					p.item.update_Inventory(7, false);
					p.item.update_Inventory(3, false);
				} else {
					Service.send_box_ThongBao_OK(p, "Hành trang không đủ chỗ trống!");
				}
			}
		} else if (cat == -1 && TypeShop == 103) {
			ItemHair ith = ItemHair.get_item(id, 103);
			if (ith != null) {
				if (p.get_ngoc() < 500) {
					Service.send_box_ThongBao_OK(p, "Không đủ 500 ruby!");
					return;
				}
				p.update_ngoc(-500);
				p.update_money();
				Log.gI().add_log(p, "Mua " + ith.name + " -500 ruby");
				if (p.check_itfashionP(ith.ID, 103) != null) {
					Service.send_box_ThongBao_OK(p, "Đã mua rồi!");
					return;
				}
				ItemFashionP temp_new = new ItemFashionP();
				temp_new.category = 103;
				temp_new.id = ith.ID;
				temp_new.icon = ith.idIcon;
				p.itfashionP.add(temp_new);
				p.update_itfashionP(temp_new, 103);
				for (int i = 0; i < p.map.players.size(); i++) {
					Player p0 = p.map.players.get(i);
					Service.charWearing(p, p0, false);
				}
				ItemFashionP.show_table(p, 103);
				Service.send_box_ThongBao_OK(p, "Mua thành công " + ith.name);
			} else {
				Service.send_box_ThongBao_OK(p, "Mua thất bại, hãy thử lại!");
			}
		} else if (cat == -1 && TypeShop == 112) {
			ItemHair ith = ItemHair.get_item(id, 108);
			if (ith != null) {
				if (p.get_ngoc() < 500) {
					Service.send_box_ThongBao_OK(p, "Không đủ 500 ruby!");
					return;
				}
				p.update_ngoc(-500);
				p.update_money();
				Log.gI().add_log(p, "Mua " + ith.name + " -500 ruby");
				if (p.check_itfashionP(ith.ID, 108) != null) {
					Service.send_box_ThongBao_OK(p, "Đã mua rồi!");
					return;
				}
				ItemFashionP temp_new = new ItemFashionP();
				temp_new.category = 108;
				temp_new.id = ith.ID;
				temp_new.icon = ith.idIcon;
				p.itfashionP.add(temp_new);
				p.update_itfashionP(temp_new, 108);
				for (int i = 0; i < p.map.players.size(); i++) {
					Player p0 = p.map.players.get(i);
					Service.charWearing(p, p0, false);
				}
				ItemFashionP.show_table(p, 108);
				Service.send_box_ThongBao_OK(p, "Mua thành công " + ith.name);
			} else {
				Service.send_box_ThongBao_OK(p, "Mua thất bại, hãy thử lại!");
			}
		} else if (cat == -1 && TypeShop == 105) {
			ItemFashion itf = ItemFashion.get_item(id);
			if (itf != null) {
				if (!itf.is_sell) {
					Service.send_box_ThongBao_OK(p, "Chưa bán item này");
					return;
				}
				if (p.get_ngoc() < itf.price) {
					Service.send_box_ThongBao_OK(p, "Không đủ " + itf.price + " ruby!");
					return;
				}
				if (p.check_fashion(itf.ID) != null) {
					Service.send_box_ThongBao_OK(p, "Đã mua rồi!");
					return;
				}
				p.update_ngoc(-itf.price);
				p.update_money();
				Log.gI().add_log(p, "Mua " + itf.name + " -" + itf.price + " ruby");
				ItemFashionP2 temp2 = new ItemFashionP2();
				temp2.id = itf.ID;
				temp2.data = new short[itf.mWearing.length];
				for (int i = 0; i < temp2.data.length; i++) {
					temp2.data[i] = itf.mWearing[i];
				}
				temp2.op = new ArrayList<>();
				for (int i = 0; i < itf.op.size(); i++) {
					temp2.op.add(new Option(itf.op.get(i).id, itf.op.get(i).getParam(0)));
				}
				p.fashion.add(temp2);
				p.update_fashionP2(temp2);
				for (int i = 0; i < p.map.players.size(); i++) {
					Player p0 = p.map.players.get(i);
					Service.charWearing(p, p0, false);
				}
				ItemFashionP.show_table(p, 105);
				Service.send_box_ThongBao_OK(p, "Mua thành công " + itf.name);
			} else {
				Service.send_box_ThongBao_OK(p, "Mua thất bại, hãy thử lại!");
			}
		} else if (cat == -1 && TypeShop == 111) {
			for (int i = 0; i < Kham_Ngoc.ID_SELL.length; i++) {
				if (Kham_Ngoc.ID_SELL[i] == id) {
					int vang_req = Kham_Ngoc.ID_SELL_PRICE[i] * value;
					if (p.get_ngoc() < vang_req) {
						Service.send_box_ThongBao_OK(p, "Không đủ " + vang_req + " ruby!");
						return;
					}
					p.update_ngoc(-vang_req);
					p.update_money();
					Log.gI().add_log(p, "Mua " + ItemTemplate4.get_it_by_id(id).name + " -" + vang_req + " ruby");
					ItemBag47 it = new ItemBag47();
					it.id = id;
					it.category = 4;
					it.quant = value;
					if (p.item.able_bag() > 0 && (p.item.total_item_bag_by_id(4, it.id) + it.quant) < 32_000) {
						p.item.add_item_bag47(4, it);
						// Service.send_box_ThongBao_OK(p,
						// "Mua thành công " + value + " " + ItemTemplate4.get_it_by_id(it.id).name);
						//
						Message m22 = new Message(-64);
						m22.writer().writeUTF("Mua " + value);
						p.conn.addmsg(m22);
						m22.cleanup();
						//
						p.item.update_Inventory(4, false);
						p.item.update_Inventory(7, false);
						p.item.update_Inventory(3, false);
					} else {
						Service.send_box_ThongBao_OK(p, "Hành trang không đủ chỗ trống!");
					}
					break;
				}
			}
		}
	}

	public static void send_box_ThongBao_OK(Player p, String notice) throws IOException {
		Message m = new Message(-11);
		m.writer().writeShort(0);
		m.writer().writeByte(0);
		m.writer().writeUTF("Thông Báo");
		m.writer().writeUTF(notice);
		m.writer().writeByte(0);
		p.conn.addmsg(m);
		m.cleanup();
	}

	public static void ChestWanted(Player p, boolean save_cache) throws IOException {
		Message m = new Message(-86);
		m.writer().writeByte(1);
		m.writer().writeByte(0);
		if (save_cache) {
			p.list_msg_cache.add(m);
		} else {
			p.conn.addmsg(m);
		}
		m.cleanup();
	}

	public static void use_potion(Player p, int type, int par) throws IOException {
		switch (type) {
			case 0: {
				int hp_max = p.body.get_hp_max(true);
				p.hp += par;
				if (p.hp > hp_max) {
					p.hp = hp_max;
				}
				Message m = new Message(-83);
				m.writer().writeShort(p.id);
				m.writer().writeByte(0);
				m.writer().writeInt(hp_max); // maxhp
				m.writer().writeInt(p.hp); // hp remain
				m.writer().writeInt(par); // dame
				m.writer().writeInt(p.body.get_mp_max(true)); // maxhp
				m.writer().writeInt(p.mp); // hp remain
				m.writer().writeInt(0); // dame
				p.map.send_msg_all_p(m, p, true);
				m.cleanup();
				break;
			}
			case 1: {
				int mp_max = p.body.get_mp_max(true);
				p.mp += par;
				if (p.mp > mp_max) {
					p.mp = mp_max;
				}
				Message m = new Message(-83);
				m.writer().writeShort(p.id);
				m.writer().writeByte(0);
				m.writer().writeInt(p.body.get_hp_max(true)); // maxhp
				m.writer().writeInt(p.hp); // hp remain
				m.writer().writeInt(0); // dame
				m.writer().writeInt(mp_max); // maxhp
				m.writer().writeInt(p.mp); // hp remain
				m.writer().writeInt(par); // dame
				p.map.send_msg_all_p(m, p, true);
				m.cleanup();
				break;
			}
		}
	}

	public static void send_view_other_player(Player p0, Player p) throws IOException {
		Message m = new Message(-42);
		m.writer().writeUTF(p0.name);
		m.writer().writeInt(p0.body.get_hp_max(true));
		m.writer().writeInt(p0.body.get_mp_max(true));
		m.writer().writeInt(p0.hp);
		m.writer().writeInt(p0.mp);
		m.writer().writeShort(p0.level);
		m.writer().writeShort(p0.get_level_percent());
		m.writer().writeShort(p0.head);
		m.writer().writeShort(p0.hair);
		m.writer().writeShort(-1); // clan
		m.writer().writeByte(8);
		for (int i = 0; i < 8; i++) {
			Item_wear it_w = p0.item.it_body[i];
			if (it_w != null) {
				m.writer().writeByte(1);
				Item.readUpdateItem(m.writer(), it_w);
				m.writer().writeShort(ItemTemplate3.get_it_by_id(it_w.id).part);
			} else {
				m.writer().writeByte(0);
			}
		}
		m.writer().writeByte(-1);
		m.writer().writeShort(-1);
		m.writer().writeByte(-1);
		p.conn.addmsg(m);
		m.cleanup();
	}

	public static void sell_item(Player p, Message m2) throws IOException {
		byte type = m2.reader().readByte();
		short id = m2.reader().readShort();
		byte cat = m2.reader().readByte();
		short num = m2.reader().readShort();
		// System.out.println(type);
		// System.out.println(id);
		// System.out.println(cat);
		// System.out.println(num);
		boolean check = false;
		switch (type) {
			case 1: // drop item in bag
			case 0: { // sell item in bag
				switch (cat) {
					case 3: {
						if (p.item.bag3[id] != null) {
							p.item.bag3[id] = null;
							p.item.update_Inventory(4, false);
							p.item.update_Inventory(7, false);
							p.item.update_Inventory(3, false);
							check = true;
						}
						break;
					}
					case 4:
					case 7: {
						p.item.remove_item47(cat, id, num);
						p.item.update_Inventory(4, false);
						p.item.update_Inventory(7, false);
						p.item.update_Inventory(3, false);
						check = true;
						break;
					}
				}
				break;
			}
		}
		if (check && type == 0 && cat == 3) {
			p.update_vang(45 * num);
			p.update_money();
			Log.gI().add_log(p, "Bán item +" + (45 * num) + " beri");
		} else if (check && type == 0 && cat == 4) {
			p.update_vang(num);
			p.update_money();
			Log.gI().add_log(p, "Bán item +" + (num) + " beri");
		}
	}

	public static void get_data_in4_potion(Player p, Message m2) throws IOException {
		short id = m2.reader().readShort();
		try {
			Message m = new Message(-105);
			m.writer().writeShort(id);
			m.writer().write(Util.loadfile("data/msg/in4potion/" + id));
			p.conn.addmsg(m);
			m.cleanup();
		} catch (IOException e) {
			System.out.println("infopotion fail " + id);
		}
		// String in4 = ItemTemplate4.get_item_description(id);
		// System.out.println(id + " " + in4);
		//
		// if (in4 != null && in4.length() > 5) {
		// Message m = new Message(-105);
		// m.writer().writeShort(id);
		// m.writer().writeUTF(in4);
		// p.conn.addmsg(m);
		// m.cleanup();
		// }
	}

	public static void input_text(Player p, int id, String s, String[] s2) throws IOException {
		Message m = new Message(-81);
		m.writer().writeShort(id);
		m.writer().writeUTF(s);
		m.writer().writeByte(s2.length);
		for (int i = 0; i < s2.length; i++) {
			m.writer().writeUTF(s2[i]);
		}
		p.conn.addmsg(m);
		m.cleanup();
	}

	public static void send_box_yesno(Player p, int type, String notice) throws IOException {
		Message m = new Message(-11);
		m.writer().writeShort(type);
		m.writer().writeByte(2);
		m.writer().writeUTF("Thông báo");
		m.writer().writeUTF(notice);
		m.writer().writeByte(2);
		m.writer().writeUTF("Đồng ý");
		m.writer().writeByte(0);
		m.writer().writeByte(2);
		m.writer().writeUTF("Hơi rén");
		m.writer().writeByte(1);
		m.writer().writeByte(1);
		p.conn.addmsg(m);
		m.cleanup();
	}

	public static void open_box_item3_orange(Player p, List<Item_wear> list, int id_chest, String s1, String s2)
	      throws IOException {
		Message m = new Message(-34);
		m.writer().writeByte(21);
		m.writer().writeShort(id_chest);
		m.writer().writeUTF(s1);
		m.writer().writeUTF(s2);
		m.writer().writeByte(list.size());
		for (int i = 0; i < list.size(); i++) {
			Item_wear temp = list.get(i);
			m.writer().writeByte(3);
			m.writer().writeUTF(temp.name);
			m.writer().writeShort(temp.icon);
			m.writer().writeInt(1);
			m.writer().writeByte(3);
		}
		p.conn.addmsg(m);
		m.cleanup();
	}

	public static void send_x2cd(Player p, boolean save_cache) throws IOException {
		Message m = new Message(-61);
		m.writer().writeByte(3);
		EffTemplate eff = p.get_eff(2);
		if (eff != null) {
			m.writer().writeInt((int) ((eff.time - System.currentTimeMillis()) / 1000));
		} else {
			m.writer().writeInt(0);
		}
		if (save_cache) {
			p.list_msg_cache.add(m);
		} else {
			p.conn.addmsg(m);
		}
		m.cleanup();
	}

	public static void NewDialog_eat_taq(Player p, String[] name_, short[] icon_, int id) throws IOException {
		Message m = new Message(40);
		m.writer().writeByte(1);
		m.writer().writeUTF("");
		m.writer().writeByte(name_.length + 1);
		ItemTemplate4 it_temp = ItemTemplate4.get_it_by_id(id);
		m.writer().writeByte(4);
		m.writer().writeUTF(it_temp.name);
		m.writer().writeShort(it_temp.icon);
		for (int i = 0; i < name_.length; i++) {
			m.writer().writeByte(104);
			m.writer().writeUTF(name_[i]);
			m.writer().writeShort(icon_[i]);
		}
		p.conn.addmsg(m);
		m.cleanup();
	}
}
