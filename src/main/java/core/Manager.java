package core;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import client.Player;
import database.SQL;
import io.Message;
import map.Boss;
import map.Map;
import map.Mob;
import map.Vgo;
import template.ItemFashion;
import template.ItemHair;
import template.ItemOptionTemplate;
import template.ItemTemplate3;
import template.ItemTemplate4;
import template.ItemTemplate7;
import template.MobTemplate;
import template.Option;
import template.Part;
import template.Skill_Template;

public class Manager {
	private static Manager instance;
	public static String[] NAME_ITEM_SELL_TEMP = new String[] {"Shop Trang Bị Võ Sĩ", "Shop Trang Bị Kiếm Khách",
	      "Shop Trang Bị Đầu Bếp", "Shop Trang Bị Hoa Tiêu", "Shop Trang Bị Xạ Thủ"};
	private static List<String> list_icon_request_fail;
	public boolean debug;
	public String mysql_host;
	public String mysql_database;
	public String mysql_user;
	public String mysql_pass;
	public int server_port;
	public int exp;
	public int lvmax;
	public boolean server_admin;

	public static Manager gI() {
		if (instance == null) {
			instance = new Manager();
		}
		return instance;
	}

	public void init() {
		try {
			load_config();
			// load msg data
			ByteArrayInputStream bais = new ByteArrayInputStream(Util.loadfile("data/msg/hair"));
			DataInputStream dis = new DataInputStream(bais);
			load_hair(dis, 103);
			dis.close();
			bais.close();
			//
			bais = new ByteArrayInputStream(Util.loadfile("data/msg/head"));
			dis = new DataInputStream(bais);
			load_hair(dis, 108);
			dis.close();
			bais.close();
		} catch (Exception e) {
			System.out.println("config load err!");
			System.exit(0);
		}
		load_database();
		start_service();
		Manager.list_icon_request_fail = new ArrayList<>();
	}

	private void start_service() {
		Log.gI().start_log();
		for (Map[] mapall : Map.ENTRYS) {
			for (Map map : mapall) {
				map.start_map();
			}
		}
	}

	private void stop_service() {
		Log.gI().close_log();
		//
		for (Map[] mapall : Map.ENTRYS) {
			for (Map map : mapall) {
				map.stop_map();
			}
		}
	}

	private void load_hair(DataInputStream dis, int type) throws IOException {
		dis.readByte();
		dis.readUTF();
		dis.readByte();
		int n = dis.readShort();
		for (int i = 0; i < n; i++) {
			ItemHair temp = ItemHair.readUpdateItemHair(dis);
			temp.type = (byte) type;
			ItemHair.ENTRYS.add(temp);
		}
	}

	private void load_database() {
		Connection conn = null;
		Statement ps = null;
		ResultSet rs = null;
		try {
			conn = SQL.gI().getCon();
			ps = conn.createStatement();
			// load mobs
			String query = "SELECT * FROM `mobs`;";
			rs = ps.executeQuery(query);
			while (rs.next()) {
				MobTemplate temp = new MobTemplate();
				temp.mob_id = Short.parseShort(rs.getString("id"));
				temp.name = rs.getString("name");
				temp.level = Short.parseShort(rs.getString("level"));
				temp.hp_max = Integer.parseInt(rs.getString("hp"));
				temp.hOne = Short.parseShort(rs.getString("hOne"));
				temp.typemove = Byte.parseByte(rs.getString("typemove"));
				temp.ishuman = Byte.parseByte(rs.getString("ishuman"));
				temp.typemonster = Byte.parseByte(rs.getString("typemonster"));
				JSONArray js = (JSONArray) JSONValue.parse(rs.getString("idicon"));
				temp.icon = new ArrayList<>();
				if (js.size() == 2) {
					temp.icon.add(Short.valueOf(Short.parseShort(js.get(1).toString())));
				}
				// if (temp.mobid == 2) {
				// temp.icon.add(Short.valueOf((short) 69));
				// }
				js.clear();
				js = (JSONArray) JSONValue.parse(rs.getString("skill"));
				temp.skill = new short[js.size()];
				for (int i = 0; i < temp.skill.length; i++) {
					temp.skill[i] = Short.parseShort(js.get(i).toString());
				}
				js.clear();
				temp.map = null;
				MobTemplate.ENTRYS.add(temp);
			}
			rs.close();
			System.out.println("load mob ok");
			// load map
			query = "SELECT * FROM `maps`;";
			rs = ps.executeQuery(query);
			int index_mob = 1;
			while (rs.next()) {
				int map_id_add = rs.getInt("id");
				String name_map_add = rs.getString("name");
				int max_zone = rs.getInt("maxzone");
				int max_player = 15;
				Map[] m_temp = new Map[max_zone];
				for (int i2 = 0; i2 < m_temp.length; i2++) {
					m_temp[i2] = new Map(map_id_add);
					m_temp[i2].name = name_map_add;
					m_temp[i2].max_player = (byte) max_player;
					m_temp[i2].max_zone = (byte) max_zone;
					m_temp[i2].zone_id = (byte) i2;
					try {
						m_temp[i2].vgos = load_vgo(map_id_add);
					} catch (IOException e) {
					}
					JSONArray js = (JSONArray) JSONValue.parse(rs.getString("mobs"));
					m_temp[i2].list_mob = new int[js.size()];
					for (int i = 0; i < js.size(); i++) {
						JSONArray js2 = (JSONArray) JSONValue.parse(js.get(i).toString());
						Mob temp = new Mob();
						temp.mob_template = MobTemplate.ENTRYS.get(Integer.parseInt(js2.get(0).toString()));
						temp.x = Short.parseShort(js2.get(1).toString());
						temp.y = Short.parseShort(js2.get(2).toString());
						temp.hp_max = temp.mob_template.hp_max;
						temp.hp = temp.hp_max;
						temp.level = temp.mob_template.level;
						temp.isdie = false;
						temp.id_target = -1;
						temp.index = index_mob;
						temp.mob_template.map = m_temp[i2];
						temp.boss_info = null;
						Mob.ENTRYS.put(index_mob, temp);
						m_temp[i2].list_mob[i] = index_mob;
						index_mob++;
					}
					// optional
					if (server_admin && m_temp[i2].id == 2) {
						m_temp[i2].list_mob = new int[0];
					}
				}
				Map.ENTRYS.add(m_temp);
			}
			rs.close();
			for (Map[] temp_map_all : Map.ENTRYS) {
				for (Map temp_map : temp_map_all) {
					for (Vgo temp_vgo : temp_map.vgos) {
						for (Map[] temp_map_all_2 : Map.ENTRYS) {
							for (Map temp_map2 : temp_map_all_2) {
								if (temp_map2.name.equals(temp_vgo.name_map_goto)) {
									for (Vgo temp_vgo2 : temp_map2.vgos) {
										if (temp_vgo2.name_map_goto.equals(temp_map.name)) {
											temp_vgo.xnew = temp_vgo2.xold;
											temp_vgo.ynew = temp_vgo2.yold;
										}
									}
								}
							}
						}
					}
				}
			}
			System.out.println("load map ok");
			// load part
			query = "SELECT * FROM `part_temp`;";
			rs = ps.executeQuery(query);
			while (rs.next()) {
				Part.ENTRYS.add(new Part(rs.getShort("id_part"), rs.getShort("part")));
			}
			rs.close();
			// load item 3
			query = "SELECT * FROM `item3`;";
			rs = ps.executeQuery(query);
			while (rs.next()) {
				ItemTemplate3 temp = new ItemTemplate3();
				temp.id = rs.getShort("id");
				temp.name = rs.getString("name");
				temp.clazz = rs.getByte("clazz");
				temp.typeEquip = rs.getByte("typeequip");
				temp.icon = rs.getShort("icon");
				temp.level = rs.getShort("level");
				temp.color = rs.getByte("color");
				temp.typelock = rs.getByte("typelock");
				temp.numHoleDaDuc = rs.getByte("numHoleDaDuc");
				temp.valueChetac = rs.getShort("chetac");
				temp.valueChetac = (short) (temp.level * 2);
				temp.isHoanMy = rs.getByte("ishoanmy");
				temp.valueKichAn = rs.getByte("valuekichan");
				JSONArray js = (JSONArray) JSONValue.parse(rs.getString("op_1"));
				temp.option_item = new ArrayList<>();
				for (int i = 0; i < js.size(); i++) {
					JSONArray js2 = (JSONArray) JSONValue.parse(js.get(i).toString());
					temp.option_item
					      .add(new Option(Byte.parseByte(js2.get(0).toString()), Short.parseShort(js2.get(1).toString())));
				}
				js.clear();
				temp.option_item_2 = new ArrayList<>();
				js = (JSONArray) JSONValue.parse(rs.getString("op_2"));
				for (int k = 0; k < js.size(); k++) {
					JSONArray js2 = (JSONArray) JSONValue.parse(js.get(k).toString());
					temp.option_item_2
					      .add(new Option(Byte.parseByte(js2.get(0).toString()), Short.parseShort(js2.get(1).toString())));
				}
				js.clear();
				temp.numLoKham = rs.getByte("numlokham");
				js = (JSONArray) JSONValue.parse(rs.getString("mdakham"));
				temp.mdakham = new short[js.size()];
				for (int l = 0; l < temp.mdakham.length; l++) {
					temp.mdakham[l] = Short.parseShort(js.get(l).toString());
				}
				temp.part = rs.getShort("part");
				temp.beri = rs.getInt("beri");
				temp.ruby = rs.getInt("ruby");
				// Part.get_part(temp.id);;
				ItemTemplate3.ENTRYS.add(temp);
			}
			rs.close();
			// System.out.println("load part ok");
			// // load item sell
			// this.list_item_sell = new ArrayList<>();
			// query = "SELECT * FROM `itemsell`;";
			// rs = ps.executeQuery(query);
			// while (rs.next()) {
			// System.out.println(ItemSell.ENTRYS.size());
			// byte type_id = rs.getByte("id");
			// JSONArray js = (JSONArray) JSONValue.parse(rs.getString("data"));
			// for (int i = 0; i < js.size(); i++) {
			// JSONObject jsob = (JSONObject) JSONValue.parse(js.get(i).toString());
			// ItemSell temp = new ItemSell();
			// temp.id = Short.parseShort(jsob.get("id").toString());
			// temp.price = Integer.parseInt(jsob.get("price").toString());;
			// temp.price_type = Byte.parseByte(jsob.get("type").toString());;
			// list_by_id.add(temp);
			// }
			// this.list_item_sell.add(list_by_id);
			// }
			// rs.close();
			// load item temp 4
			query = "SELECT * FROM `item4`;";
			rs = ps.executeQuery(query);
			while (rs.next()) {
				ItemTemplate4 temp = new ItemTemplate4();
				temp.id = rs.getShort("id");
				temp.name = rs.getString("name");
				temp.icon = rs.getShort("icon");
				temp.type = rs.getByte("hpmpother");
				temp.ruby = rs.getInt("priceruby");
				temp.beri = rs.getInt("price");
				temp.description = rs.getString("description");
				ItemTemplate4.ENTRYS.add(temp);
			}
			rs.close();
			// load item temp 7
			query = "SELECT * FROM `item7`;";
			rs = ps.executeQuery(query);
			while (rs.next()) {
				ItemTemplate7 temp = new ItemTemplate7();
				temp.id = rs.getShort("id");
				temp.name = rs.getString("name");
				ItemTemplate7.ENTRYS.add(temp);
			}
			rs.close();
			System.out.println("load item ok");
			// load skill temp
			Skill_Template.ENTRYS = new ArrayList<>();
			query = "SELECT * FROM `skill_template`;";
			rs = ps.executeQuery(query);
			while (rs.next()) {
				// int id = rs.getInt("id");
				Skill_Template temp_add = new Skill_Template(rs.getShort("id_index"), rs.getShort("id_2"),
				      rs.getShort("icon"), rs.getByte("typeSkill"), rs.getByte("typeBuff"), rs.getString("name"),
				      rs.getShort("typeEffSkill"), rs.getShort("range"));
				temp_add.getData(rs.getByte("nTarget"), rs.getShort("rangeLan"), rs.getInt("damage"),
				      rs.getShort("manaLost"), rs.getInt("timeDelay"), rs.getByte("nKick"), rs.getString("info"),
				      rs.getByte("Lv_RQ"), 0, rs.getByte("typeDevil"));
				temp_add.op = new ArrayList<>();
				JSONArray js = (JSONArray) JSONValue.parse(rs.getString("option"));
				for (int j = 0; j < js.size(); j++) {
					JSONArray js2 = (JSONArray) JSONValue.parse(js.get(j).toString());
					temp_add.op
					      .add(new Option(Byte.parseByte(js2.get(0).toString()), Integer.parseInt(js2.get(1).toString())));
				}
				js.clear();
				js = (JSONArray) JSONValue.parse(rs.getString("EffSpec"));
				temp_add.idEffSpec = Byte.parseByte(js.get(0).toString());
				temp_add.perEffSpec = Short.parseShort(js.get(1).toString());
				temp_add.timeEffSpec = Short.parseShort(js.get(2).toString());
				js.clear();
				temp_add.LvDevilSkill = rs.getByte("LvDevilSkill");
				temp_add.phanTramDevilSkill = rs.getByte("phanTramDevilSkill");
				Skill_Template.ENTRYS.add(temp_add);
			}
			rs.close();
			System.out.println("load skill ok");
			// load item option temp
			ItemOptionTemplate.ENTRYS = new ArrayList<>();
			query = "SELECT * FROM `itemoption`;";
			rs = ps.executeQuery(query);
			while (rs.next()) {
				ItemOptionTemplate temp = new ItemOptionTemplate();
				temp.id = rs.getShort("id");
				temp.name = rs.getString("name");
				temp.color = rs.getByte("color");
				temp.is_percent = rs.getByte("percent") == 1;
				ItemOptionTemplate.ENTRYS.add(temp);
			}
			rs.close();
			System.out.println("load item op temp ok");
			// load item fashion info
			ItemFashion.ENTRYS = new ArrayList<>();
			query = "SELECT * FROM `fashiontemplate`;";
			rs = ps.executeQuery(query);
			while (rs.next()) {
				byte id = rs.getByte("id");
				short icon = rs.getShort("icon");
				String name = rs.getString("name");
				String info = rs.getString("info");
				JSONArray js = (JSONArray) JSONValue.parse(rs.getString("mwear"));
				short[] wear = new short[js.size()];
				for (int i = 0; i < wear.length; i++) {
					wear[i] = Short.parseShort(js.get(i).toString());
				}
				js.clear();
				js = (JSONArray) JSONValue.parse(rs.getString("op"));
				List<Option> op = new ArrayList<>();
				for (int i = 0; i < js.size(); i++) {
					JSONArray js2 = (JSONArray) JSONValue.parse(js.get(i).toString());
					op.add(new Option(Byte.parseByte(js2.get(0).toString()), Integer.parseInt(js2.get(1).toString())));
				}
				ItemFashion.ENTRYS
				      .add(new ItemFashion(id, icon, name, info, wear, op, rs.getByte("is_sell"), rs.getInt("price")));
			}
			rs.close();
			System.out.println("load fashion temp ok");
			// load boss
			Boss.ENTRYS = new ArrayList<>();
			query = "SELECT * FROM `boss`;";
			rs = ps.executeQuery(query);
			while (rs.next()) {
				Boss.ENTRYS.add(new Boss(rs.getInt("id"), rs.getInt("mob_id"), rs.getString("site"), rs.getInt("hp"),
				      rs.getString("skill"), rs.getString("buff"), index_mob++, rs.getInt("level")));
			}
			System.out.println("load boss ok");
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private List<Vgo> load_vgo(int id) throws IOException {
		List<Vgo> ab = new ArrayList<>();
		FileInputStream fis = new FileInputStream("data/map/" + id);
		DataInputStream dis = new DataInputStream(fis);
		//
		dis.readShort();
		dis.readByte();
		dis.readByte();
		dis.readShort();
		dis.readShort();
		dis.readInt();
		dis.readInt();
		dis.readInt();
		dis.readInt();
		byte a = dis.readByte();
		dis.readByte();
		if (a == 1) {
			int n = dis.readInt();
			for (int j = 0; j < n; j++) {
				dis.readByte();
			}
			n = dis.readInt();
			for (int j = 0; j < n; j++) {
				dis.readByte();
			}
			n = dis.readByte();
			for (int j = 0; j < n; j++) {
				Vgo temp = new Vgo();
				temp.name_map_goto = dis.readUTF();
				temp.xold = dis.readShort();
				temp.yold = dis.readShort();
				ab.add(temp);
			}
		}
		dis.close();
		fis.close();
		return ab;
	}

	private void load_config() throws IOException {
		final byte[] ab = Util.loadfile("htth.conf");
		if (ab == null) {
			System.out.println("Config file not found!");
			System.exit(0);
		}
		final String data = new String(ab);
		final HashMap<String, String> configMap = new HashMap<String, String>();
		final StringBuilder sbd = new StringBuilder();
		boolean bo = false;
		for (int i = 0; i <= data.length(); ++i) {
			final char es;
			if (i == data.length() || (es = data.charAt(i)) == '\n') {
				bo = false;
				final String sbf = sbd.toString().trim();
				if (sbf != null && !sbf.equals("") && sbf.charAt(0) != '#') {
					final int j = sbf.indexOf(58);
					if (j > 0) {
						final String key = sbf.substring(0, j).trim();
						final String value = sbf.substring(j + 1).trim();
						configMap.put(key, value);
						System.out.println("config: " + key + ": " + value);
					}
				}
				sbd.setLength(0);
			} else {
				if (es == '#') {
					bo = true;
				}
				if (!bo) {
					sbd.append(es);
				}
			}
		}
		if (configMap.containsKey("port")) {
			this.server_port = Integer.parseInt(configMap.get("port"));
		} else {
			this.server_port = 19129;
		}
		if (configMap.containsKey("debug")) {
			this.debug = Boolean.parseBoolean(configMap.get("debug"));
		} else {
			this.debug = false;
		}
		if (configMap.containsKey("mysql-host")) {
			this.mysql_host = configMap.get("mysql-host");
		} else {
			this.mysql_host = "127.0.0.1";
		}
		if (configMap.containsKey("mysql-user")) {
			this.mysql_user = configMap.get("mysql-user");
		} else {
			this.mysql_user = "root";
		}
		if (configMap.containsKey("mysql-password")) {
			this.mysql_pass = configMap.get("mysql-password");
		} else {
			this.mysql_pass = "12345678";
		}
		if (configMap.containsKey("mysql-database")) {
			this.mysql_database = configMap.get("mysql-database");
		} else {
			this.mysql_database = "hso";
		}
		if (configMap.containsKey("exp")) {
			this.exp = Integer.parseInt(configMap.get("exp"));
		} else {
			this.exp = 1;
		}
		if (configMap.containsKey("lvmax")) {
			this.lvmax = Short.parseShort(configMap.get("lvmax"));
		} else {
			this.lvmax = 150;
		}
		if (configMap.containsKey("serveradmin")) {
			this.server_admin = Boolean.parseBoolean(configMap.get("serveradmin"));
		} else {
			this.server_admin = false;
		}
	}

	public void close() {
		stop_service();
	}

	public void chatKTG(Player p, String text) throws IOException {
		if (p.time_chat_ktg < System.currentTimeMillis()) {
			p.time_chat_ktg = System.currentTimeMillis() + 30_000L;
			chatKTG(text);
		} else {
			Service.send_box_ThongBao_OK(p, "Chờ " + (p.time_chat_ktg - System.currentTimeMillis()) / 1000L + "s");
		}
	}

	public void chatKTG(String text) throws IOException {
		Message m = new Message(-31);
		m.writer().writeByte(0);
		m.writer().writeUTF(text);
		m.writer().writeByte(5);
		for (Map[] mapall : Map.ENTRYS) {
			for (Map map : mapall) {
				for (int i = 0; i < map.players.size(); i++) {
					Player p0 = map.players.get(i);
					p0.conn.addmsg(m);
				}
			}
		}
		m.cleanup();
	}

	public int get_total_p_inmap() {
		int par = 0;
		for (Map[] mapall : Map.ENTRYS) {
			for (Map map : mapall) {
				par += map.players.size();
			}
		}
		return par;
	}

	public synchronized void add_icon_fail(String path) {
		if (!Manager.list_icon_request_fail.contains(path)) {
			Manager.list_icon_request_fail.add(path);
		}
	}

	public void save_list_icon_fail() {
		File f = new File("data/icon_fail.txt");
		if (f.exists()) {
			f.delete();
		}
		FileWriter fwt = null;
		try {
			fwt = new FileWriter(f);
			for (int i = 0; i < Manager.list_icon_request_fail.size(); i++) {
				fwt.append(Manager.list_icon_request_fail.get(i) + "\n");
			}
		} catch (IOException e) {
		} finally {
			if (fwt != null) {
				try {
					fwt.close();
				} catch (IOException e) {
				}
			}
		}
	}
        
}
