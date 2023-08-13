package client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import core.Log;
import core.Manager;
import core.Service;
import core.Util;
import database.SQL;
import io.Message;
import io.Session;
import map.Map;
import map.Vgo;
import template.EffTemplate;
import template.FriendTemp;
import template.ItemBag47;
import template.ItemFashion;
import template.ItemFashionP;
import template.ItemFashionP2;
import template.ItemMap;
import template.ItemTemplate3;
import template.Item_wear;
import template.Level;
import template.Option;
import template.Skill_Template;
import template.Skill_info;

public class Player {
	public Session conn;
	public int id;
	public byte clazz;
	public String name;
	public short x;
	public short y;
	public int hp;
	public int mp;
	public short level;
	public long exp;
	public short kynang;
	public short point1;
	public short point2;
	public short point3;
	public short point4;
	public short point5;
	public Map map;
	public int xold;
	public int yold;
	public boolean isdie;
	private long vang;
	private int kimcuong;
	public boolean ischangemap = true;
	public short thongthao;
	public short thongthaopercent;
	public int rankWanted;
	public int pointPk;
	public int pointAttribute;
	public int typePirate;
	public int indexGhostServer;
	public int numPassive;
	public int levelPerfect;
	public int pointSkill;
	public short head;
	public short hair;
	public Item item;
	public Body body;
	public BlockingQueue<Message> list_msg_cache;
	public boolean is_receiv_msg_move;
	public byte type_pk;
	// public int test;
	public ItemMap[] it_map;
	private List<EffTemplate> list_eff;
	public long time_chat_ktg;
	public short use_item_3;
	public short[] tool_upgrade;
	public byte[][] rms;
	// public Skill_Template[] skill;
	public List<Skill_info> skill_point;
	public HashMap<Integer, Long> time_use_skill;
	public Item_wear item_chuyenhoa_save_0;
	public Item_wear item_chuyenhoa_save_1;
	public Item_wear item_to_kham_ngoc;
	public short item_to_kham_ngoc_id_ngoc;
	public Player trade_target;
	public List<Item_wear> list_item_trade;
	public long money_trade;
	public boolean is_lock_trade;
	public boolean is_accept_trade;
	public List<FriendTemp> friend_list;
	public int id_request_friend;
	public List<ItemFashionP2> fashion;
	public List<ItemFashionP> itfashionP;
	public long time_buff_hp_mp;
	public int[] quest_daily;
	public Date date;
	public Party party;

	public Player(Session conn, String name) {
		this.conn = conn;
		this.name = name;
	}

	public boolean setup() {
		Connection connection = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			connection = SQL.gI().getCon();
			st = connection.createStatement();
			rs = st.executeQuery("SELECT * FROM `players` WHERE `name` = '" + this.name + "' LIMIT 1;");
			if (!rs.next()) {
				return false;
			}
			id = rs.getInt("id");
			clazz = rs.getByte("clazz");
			vang = rs.getLong("vang");
			kimcuong = rs.getInt("ngoc");
			level = rs.getShort("level");
			exp = rs.getLong("exp");
			date = Util.getDate(rs.getString("date"));
			JSONArray js = (JSONArray) JSONValue.parse(rs.getString("potential"));
			pointAttribute = Short.parseShort(js.get(0).toString());
			kynang = Short.parseShort(js.get(1).toString());
			point1 = Short.parseShort(js.get(2).toString());
			point2 = Short.parseShort(js.get(3).toString());
			point3 = Short.parseShort(js.get(4).toString());
			point4 = Short.parseShort(js.get(5).toString());
			point5 = Short.parseShort(js.get(6).toString());
			js.clear();
			js = (JSONArray) JSONValue.parse(rs.getString("body"));
			head = Short.parseShort(js.get(0).toString());
			hair = Short.parseShort(js.get(1).toString());
			js.clear();
			js = (JSONArray) JSONValue.parse(rs.getString("site"));
			//
			Map[] map = Map.get_map_by_id(Integer.parseInt(js.get(0).toString()));
			this.map = map[0];
			x = Short.parseShort(js.get(1).toString());
			y = Short.parseShort(js.get(2).toString());
			js.clear();
			js = (JSONArray) JSONValue.parse(rs.getString("quest_daily"));
			quest_daily = new int[js.size()];
			for (int i = 0; i < quest_daily.length; i++) {
				quest_daily[i] = Integer.parseInt(js.get(i).toString());
			}
			js.clear();
			//
			item = new Item(this);
			item.max_bag = 42;
			item.max_box = 12;
			item.bag3 = new Item_wear[item.max_bag];
			item.box3 = new Item_wear[item.max_box];
			item.it_body = new Item_wear[8];
			item.bag47 = new ArrayList<>();
			item.box47 = new ArrayList<>();
			//
			js = (JSONArray) JSONValue.parse(rs.getString("bag3"));
			for (int i = 0; i < js.size(); i++) {
				JSONArray js2 = (JSONArray) JSONValue.parse(js.get(i).toString());
				Item_wear temp = new Item_wear();
				Item.readUpdateItem(js2.toString(), temp);
				if (temp.index < item.bag3.length) {
					item.bag3[temp.index] = temp;
				}
			}
			js.clear();
			js = (JSONArray) JSONValue.parse(rs.getString("box3"));
			for (int i = 0; i < js.size(); i++) {
				JSONArray js2 = (JSONArray) JSONValue.parse(js.get(i).toString());
				Item_wear temp = new Item_wear();
				Item.readUpdateItem(js2.toString(), temp);
				if (temp.index < item.box3.length) {
					item.box3[temp.index] = temp;
				}
			}
			js.clear();
			//
			js = (JSONArray) JSONValue.parse(rs.getString("it_body"));
			for (int i = 0; i < js.size(); i++) {
				JSONArray js2 = (JSONArray) JSONValue.parse(js.get(i).toString());
				Item_wear temp = new Item_wear();
				Item.readUpdateItem(js2.toString(), temp);
				if (temp.index < item.it_body.length) {
					item.it_body[temp.index] = temp;
				}
			}
			js.clear();
			js = (JSONArray) JSONValue.parse(rs.getString("bag47"));
			for (int i = 0; i < js.size(); i++) {
				JSONArray js2 = (JSONArray) JSONValue.parse(js.get(i).toString());
				ItemBag47 temp = new ItemBag47();
				temp.category = Byte.parseByte(js2.get(0).toString());
				temp.id = Short.parseShort(js2.get(1).toString());
				temp.quant = Short.parseShort(js2.get(2).toString());
				item.bag47.add(temp);
			}
			js.clear();
			js = (JSONArray) JSONValue.parse(rs.getString("box47"));
			for (int i = 0; i < js.size(); i++) {
				JSONArray js2 = (JSONArray) JSONValue.parse(js.get(i).toString());
				ItemBag47 temp = new ItemBag47();
				temp.category = Byte.parseByte(js2.get(0).toString());
				temp.id = Short.parseShort(js2.get(1).toString());
				temp.quant = Short.parseShort(js2.get(2).toString());
				item.box47.add(temp);
			}
			js.clear();
			js = (JSONArray) JSONValue.parse(rs.getString("rms"));
			rms = new byte[11][];
			for (int i = 0; i < js.size(); i++) {
				JSONArray js2 = (JSONArray) JSONValue.parse(js.get(i).toString());
				rms[i] = new byte[js2.size()];
				for (int j = 0; j < rms[i].length; j++) {
					rms[i][j] = Byte.parseByte(js2.get(j).toString());
				}
			}
			js.clear();
			js = (JSONArray) JSONValue.parse(rs.getString("skill"));
			skill_point = new ArrayList<>();
			for (int i = 0; i < js.size(); i++) {
				JSONArray js2 = (JSONArray) JSONValue.parse(js.get(i).toString());
				Skill_info skill_add = new Skill_info();
				skill_add.exp = Long.parseLong(js2.get(1).toString());
				skill_add.temp = Skill_Template.get_temp(Short.parseShort(js2.get(0).toString()), skill_add.exp);
				skill_add.lvdevil = Byte.parseByte(js2.get(2).toString());
				skill_add.devilpercent = Byte.parseByte(js2.get(3).toString());
				skill_point.add(skill_add);
			}
			js.clear();
//                        
//                        String skill_by_clazz ="";
//                        
//                        switch (clazz) {
//				case 1: {
//					
//					skill_by_clazz =
//					      "[[487,-1,0,0],[300,-1,0,0],[305,-1,0,0],[310,-1,0,0],[315,-1,0,0],[320,-1,0,0],[325,-1,0,0],[552,-1,0,0],[557,-1,0,0]]";
//					break;
//				}
//				case 2: {
//					
//					skill_by_clazz =
//					      "[[492,-1,0,0],[300,-1,0,0],[305,-1,0,0],[310,-1,0,0],[315,-1,0,0],[320,-1,0,0],[325,-1,0,0],[552,-1,0,0],[557,-1,0,0]]";
//					break;
//				}
//				case 3: {
//					
//					skill_by_clazz =
//					      "[[497,-1,0,0],[300,-1,0,0],[305,-1,0,0],[310,-1,0,0],[315,-1,0,0],[320,-1,0,0],[325,-1,0,0],[552,-1,0,0],[557,-1,0,0]]";
//					break;
//				}
//				case 4: {
//					
//					skill_by_clazz =
//					      "[[502,-1,0,0],[300,-1,0,0],[305,-1,0,0],[310,-1,0,0],[315,-1,0,0],[320,-1,0,0],[325,-1,0,0],[552,-1,0,0],[557,-1,0,0]]";
//					break;
//				}
//				case 5: {
//					
//					skill_by_clazz =
//					      "[[507,-1,0,0],[300,-1,0,0],[305,-1,0,0],[310,-1,0,0],[315,-1,0,0],[320,-1,0,0],[325,-1,0,0],[552,-1,0,0],[557,-1,0,0]]";
//					break;
//				}
//			}
//                        js = (JSONArray) JSONValue.parse(skill_by_clazz);
//                        for (int i = 0; i < js.size(); i++) {
//                        JSONArray js2 = (JSONArray) JSONValue.parse(js.get(i).toString());
//                        Skill_info skill_add = new Skill_info();
//				skill_add.temp = Skill_Template.get_temp(Short.parseShort(js2.get(0).toString()));
//				skill_add.exp = Long.parseLong(js2.get(1).toString());
//				skill_add.lvdevil = Byte.parseByte(js2.get(2).toString());
//				skill_add.devilpercent = Byte.parseByte(js2.get(3).toString());
//				skill_point.add(skill_add);
//                    }
//                        js.clear();
                    
			//
			friend_list = new ArrayList<>();
			js = (JSONArray) JSONValue.parse(rs.getString("friend"));
			for (int i = 0; i < js.size(); i++) {
				FriendTemp temp = new FriendTemp((JSONArray) JSONValue.parse(js.get(i).toString()));
				friend_list.add(temp);
			}
			js.clear();
			//
			this.itfashionP = new ArrayList<>();
			this.fashion = new ArrayList<>();
			js = (JSONArray) JSONValue.parse(rs.getString("fashion"));
			JSONArray js_temp_2 = (JSONArray) JSONValue.parse(js.get(0).toString());
			for (int i = 0; i < js_temp_2.size(); i++) {
				JSONArray js_temp = (JSONArray) JSONValue.parse(js_temp_2.get(i).toString());
				ItemFashionP tempf = new ItemFashionP();
				tempf.category = Byte.parseByte(js_temp.get(0).toString());
				tempf.id = Short.parseShort(js_temp.get(1).toString());
				tempf.icon = Short.parseShort(js_temp.get(2).toString());
				tempf.is_use = Byte.parseByte(js_temp.get(3).toString()) == 1;
				this.itfashionP.add(tempf);
			}
			js_temp_2.clear();
			js_temp_2 = (JSONArray) JSONValue.parse(js.get(1).toString());
			for (int i = 0; i < js_temp_2.size(); i++) {
				JSONArray js_temp = (JSONArray) JSONValue.parse(js_temp_2.get(i).toString());
				ItemFashionP2 tempf = new ItemFashionP2();
				tempf.id = Short.parseShort(js_temp.get(0).toString());
				tempf.is_use = Byte.parseByte(js_temp.get(1).toString()) == 1;
				tempf.data = new short[js_temp.size() - 2];
				for (int j = 2; j < js_temp.size(); j++) {
					tempf.data[j - 2] = Short.parseShort(js_temp.get(j).toString());
				}
				tempf.op = new ArrayList<>();
				ItemFashion temp_op = ItemFashion.get_item(tempf.id);
				if (temp_op != null) {
					for (int j = 0; j < temp_op.op.size(); j++) {
						tempf.op.add(new Option(temp_op.op.get(j).id, temp_op.op.get(j).getParam(0)));
					}
				}
				this.fashion.add(tempf);
			}
			js.clear();
			js = (JSONArray) JSONValue.parse(rs.getString("eff"));
			list_eff = new ArrayList<>();
			for (int i = 0; i < js.size(); i++) {
				JSONArray js2 = (JSONArray) JSONValue.parse(js.get(i).toString());
				list_eff.add(new EffTemplate(Byte.parseByte(js2.get(0).toString()), Integer.parseInt(js2.get(1).toString()),
				      (System.currentTimeMillis() + Long.parseLong(js2.get(2).toString()))));
			}
			js.clear();
			//
			body = new Body(this);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public void setin4() {
		xold = x;
		yold = y;
		thongthao = 0;
		thongthaopercent = 0;
		rankWanted = -1;
		pointPk = 1;
		typePirate = -1;
		indexGhostServer = -1;
		numPassive = 2;
		levelPerfect = 0;
		pointSkill = 20;
		//
		hp = body.get_hp_max(true);
		mp = body.get_mp_max(true);
		isdie = false;
		ischangemap = false;
		is_receiv_msg_move = true;
		list_msg_cache = new LinkedBlockingQueue<>();
		type_pk = -1;
		it_map = new ItemMap[3];
		tool_upgrade = new short[] {-1, -1};
		time_use_skill = new HashMap<>();
		item_chuyenhoa_save_0 = null;
		item_chuyenhoa_save_1 = null;
		item_to_kham_ngoc = null;
		item_to_kham_ngoc_id_ngoc = -1;
		trade_target = null;
		list_item_trade = null;
		money_trade = 0;
		is_lock_trade = false;
		is_accept_trade = false;
		id_request_friend = id;
		use_item_3 = -1;
		time_buff_hp_mp = System.currentTimeMillis() + 5000L;
		party = null;
	}

	public int get_level_percent() {
		return (int) ((exp * 1000) / Level.ENTRYS.get(level - 1).exp);
	}

	@SuppressWarnings("unchecked")
	public static void flush(Player p, boolean print) {
		long time = System.currentTimeMillis();
		String query = "UPDATE `players` SET `level` = ?, `exp` = ?, `site` = ?, `vang` = ?, `ngoc` = ?, "
		      + "`bag3` = ?, `it_body` = ?, `potential` = ?, `bag47` = ?, "
		      + "`rms` = ?, `skill` = ?, `friend` = ?, `enemy` = ?, `fashion` = ?, `eff` = ?, `box47` = ?, `box3` = ?, `quest_daily` = ?, `date` = ? WHERE `id` = "
		      + p.id + ";";
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = SQL.gI().getCon();
			ps = connection.prepareStatement(query);
			ps.setShort(1, p.level);
			ps.setLong(2, p.exp);
			JSONArray js = new JSONArray();
			if (p.isdie || Map.map_cant_save_site(p.map.id)) {
				js.add(1);
				js.add(782);
				js.add(203);
			} else {
				js.add(p.map.id);
				js.add(p.x);
				js.add(p.y);
			}
			ps.setNString(3, js.toJSONString());
			js.clear();
			ps.setLong(4, p.vang);
			ps.setInt(5, p.kimcuong);
			js = new JSONArray();
			for (int i = 0; i < p.item.bag3.length; i++) {
				if (p.item.bag3[i] != null) {
					JSONArray js_temp = Item.it_data_to_json(p.item.bag3[i]);
					if (js_temp.size() > 0) {
						js.add(js_temp);
					}
				}
			}
			ps.setNString(6, js.toJSONString());
			js.clear();
			js = new JSONArray();
			for (int i = 0; i < p.item.it_body.length; i++) {
				if (p.item.it_body[i] != null) {
					JSONArray js_temp = Item.it_data_to_json(p.item.it_body[i]);
					if (js_temp.size() > 0) {
						js.add(js_temp);
					}
				}
			}
			ps.setNString(7, js.toJSONString());
			js.clear();
			js = new JSONArray();
			js.add(p.pointAttribute);
			js.add(p.kynang);
			js.add(p.point1);
			js.add(p.point2);
			js.add(p.point3);
			js.add(p.point4);
			js.add(p.point5);
			ps.setNString(8, js.toJSONString());
			js.clear();
			js = new JSONArray();
			for (int i = 0; i < p.item.bag47.size(); i++) {
				JSONArray js_temp = new JSONArray();
				js_temp.add(p.item.bag47.get(i).category);
				js_temp.add(p.item.bag47.get(i).id);
				js_temp.add(p.item.bag47.get(i).quant);
				js.add(js_temp);
			}
			ps.setNString(9, js.toJSONString());
			js.clear();
			js = new JSONArray();
			for (int i = 0; i < p.rms.length; i++) {
				JSONArray js_temp = new JSONArray();
				for (int j = 0; j < p.rms[i].length; j++) {
					js_temp.add(p.rms[i][j]);
				}
				js.add(js_temp);
			}
			ps.setNString(10, js.toJSONString());
			js.clear();
			js = new JSONArray();
			for (int i = 0; i < p.skill_point.size(); i++) {
				JSONArray js_temp = new JSONArray();
				js_temp.add(p.skill_point.get(i).temp.indexSkillInServer);
				js_temp.add(p.skill_point.get(i).exp);
				js_temp.add(p.skill_point.get(i).lvdevil);
				js_temp.add(p.skill_point.get(i).devilpercent);
				js.add(js_temp);
			}
			ps.setNString(11, js.toJSONString());
			js.clear();
			js = new JSONArray();
			for (int i = 0; i < p.friend_list.size(); i++) {
				js.add(p.friend_list.get(i).toJSONArray());
			}
			ps.setNString(12, js.toJSONString());
			js.clear();
			ps.setNString(13, "[]");
			js = new JSONArray();
			JSONArray js_temp_2 = new JSONArray();
			for (int i = 0; i < p.itfashionP.size(); i++) {
				JSONArray js14 = new JSONArray();
				js14.add(p.itfashionP.get(i).category);
				js14.add(p.itfashionP.get(i).id);
				js14.add(p.itfashionP.get(i).icon);
				js14.add(p.itfashionP.get(i).is_use ? 1 : 0);
				js_temp_2.add(js14);
			}
			js.add(js_temp_2);
			JSONArray js_temp_22 = new JSONArray();
			for (int i = 0; i < p.fashion.size(); i++) {
				JSONArray js14 = new JSONArray();
				js14.add(p.fashion.get(i).id);
				js14.add(p.fashion.get(i).is_use ? 1 : 0);
				for (int j = 0; j < p.fashion.get(i).data.length; j++) {
					js14.add(p.fashion.get(i).data[j]);
				}
				js_temp_22.add(js14);
			}
			js.add(js_temp_22);
			ps.setNString(14, js.toJSONString());
			js.clear();
			js = new JSONArray();
			for (int i = 0; i < p.list_eff.size(); i++) {
				JSONArray js_temp = new JSONArray();
				EffTemplate eff_temp = p.list_eff.get(i);
				if (EffTemplate.check_eff_can_save(eff_temp.id)) {
					js_temp.add(eff_temp.id);
					js_temp.add(eff_temp.param);
					js_temp.add(eff_temp.time - System.currentTimeMillis());
					js.add(js_temp);
				}
			}
			ps.setNString(15, js.toJSONString()); // eff
			js.clear();
			js = new JSONArray();
			for (int i = 0; i < p.item.box47.size(); i++) {
				JSONArray js_temp = new JSONArray();
				js_temp.add(p.item.box47.get(i).category);
				js_temp.add(p.item.box47.get(i).id);
				js_temp.add(p.item.box47.get(i).quant);
				js.add(js_temp);
			}
			ps.setNString(16, js.toJSONString());
			js.clear();
			js = new JSONArray();
			for (int i = 0; i < p.item.box3.length; i++) {
				if (p.item.box3[i] != null) {
					JSONArray js_temp = Item.it_data_to_json(p.item.box3[i]);
					if (js_temp.size() > 0) {
						js.add(js_temp);
					}
				}
			}
			ps.setNString(17, js.toJSONString());
			js.clear();
			js = new JSONArray();
			for (int i = 0; i < p.quest_daily.length; i++) {
				js.add(p.quest_daily[i]);
			}
			ps.setNString(18, js.toJSONString());
			js.clear();
			ps.setNString(19, p.date.toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (print) {
			System.out.println("flush " + p.name + " " + (System.currentTimeMillis() - time));
		}
	}

	public void change_map(Vgo vgo) throws IOException {
		this.ischangemap = false;
		this.is_receiv_msg_move = false;
		Map[] map_go = Map.get_map_by_name(vgo.name_map_goto);
		if (!Map.check_map_can_goto(map_go[0].id)) {
			Service.send_box_ThongBao_OK(this, "Chưa thể đi đến map này!");
			this.is_receiv_msg_move = true;
			return;
		}
		try {
			Util.loadfile("data/map/" + map_go[0].id);
		} catch (IOException e) {
			Service.send_box_ThongBao_OK(this, "Chưa thể đi đến map này!");
			this.is_receiv_msg_move = true;
			return;
		}
		Message m2 = new Message(30);
		this.conn.addmsg(m2);
		m2.cleanup();
		//
		this.map.leave_map(this);
		int zone_into = 0;
		while (zone_into < 14 && map_go[zone_into].players.size() >= 15) {
			zone_into++;
		}
		this.map = map_go[zone_into];
		this.x = (short) vgo.xnew;
		this.y = (short) vgo.ynew;
		this.xold = this.x;
		this.yold = this.y;
		this.map.enter_map(this, false);
		m2 = new Message(0);
		m2.writer().write(this.map.get_map_data(this, false));
		this.list_msg_cache.add(m2);
		this.conn.addmsg(m2);
		m2.cleanup();
		for (int i = 0; i < map.players.size(); i++) {
			Player p0 = map.players.get(i);
			m2 = new Message(1);
			m2.writer().writeByte(0);
			m2.writer().writeShort(this.id);
			m2.writer().writeShort(this.x);
			m2.writer().writeShort(this.y);
			if (p0.map.equals(map)) {
				p0.conn.addmsg(m2);
			}
			m2.cleanup();
		}
		// Service.send_msg_data(p.conn, -62, ("data/msg/login/x2msg_-62_638119694706361340"), false);
		//
		Service.update_PK(this, this, this.type_pk, true);
		Service.pet(this, this, true);
		//
		m2 = new Message(21);
		m2.writer().writeByte(this.map.zone_id);
		m2.writer().writeByte(0);
		m2.writer().writeShort(this.x);
		m2.writer().writeShort(this.y);
		m2.writer().writeInt(this.body.get_hp_max(true));
		m2.writer().writeInt(this.hp);
		m2.writer().writeInt(this.body.get_mp_max(true));
		m2.writer().writeInt(this.mp);
		m2.writer().writeByte(12);
		m2.writer().writeShort(260);
		// this.list_msg_cache.add(m2);
		m2.cleanup();
	}

	public void update_exp(long exp_up, boolean multi) throws IOException {
		if (multi) {
			exp_up *= Manager.gI().exp;
		}
		this.exp += exp_up;
		//
		if (this.exp >= Level.ENTRYS.get(this.level - 1).exp && this.level < Manager.gI().lvmax) {
			while (this.exp >= Level.ENTRYS.get(this.level - 1).exp && this.level < Manager.gI().lvmax) {
				this.exp -= Level.ENTRYS.get(this.level - 1).exp;
				this.level++;
				this.pointAttribute += Level.ENTRYS.get(this.level - 1).tiemnang;
			}
			Message m = new Message(-15);
			m.writer().writeByte(0);
			m.writer().writeShort(this.id);
			m.writer().writeByte(0);
			m.writer().writeShort(0);
			conn.addmsg(m);
			m.cleanup();
			this.update_info_to_all();
		}
		if (this.level >= Manager.gI().lvmax && this.exp >= Level.ENTRYS.get(this.level - 1).exp) {
			this.exp = Level.ENTRYS.get(this.level - 1).exp - 1;
		}
		//
		Message m = new Message(10);
		m.writer().writeShort(this.id);
		if (this.level == 100) {
			m.writer().writeShort(1);
		} else {
			m.writer().writeShort(get_level_percent());
		}
		m.writer().writeInt((int) exp_up);
		conn.addmsg(m);
		m.cleanup();
		//
	}

	public void request_live_from_die(Message m2) throws IOException {
		byte type = m2.reader().readByte();
		if (type == 1) { //
			this.isdie = false;
			Service.use_potion(this, 0, this.body.get_hp_max(true));
			Service.use_potion(this, 1, this.body.get_mp_max(true));
		} else { // ve lang
			this.isdie = false;
			Service.use_potion(this, 0, this.body.get_hp_max(true));
			Service.use_potion(this, 1, this.body.get_mp_max(true));
			Vgo vgo = new Vgo();
			vgo.name_map_goto = Map.get_map_by_id(1)[0].name;
			vgo.xnew = 782;
			vgo.ynew = 203;
			change_map(vgo);
		}
	}

	public boolean add_item_map(ItemMap itm, int category) {
		switch (category) {
			case 3: {
				if (this.it_map[0] == null) {
				}
				break;
			}
			case 4: {
				if (this.it_map[1] == null) {
					this.it_map[1] = itm;
					return true;
				}
				break;
			}
			case 7: {
				if (this.it_map[2] == null) {
				}
				break;
			}
		}
		return false;
	}

	public boolean pick_item_map(short id, byte category) throws IOException {
		switch (category) {
			case 3: {
				if (this.it_map[0] != null) {
				}
				break;
			}
			case 4: {
				if (this.it_map[1] != null) {
					this.vang += this.it_map[1].quant;
					update_money();
					Log.gI().add_log(this, "Nhặt +" + this.it_map[1].quant + " beri");
					//
					Message m = new Message(12);
					m.writer().writeShort(this.it_map[1].id);
					m.writer().writeByte(4);
					m.writer().writeShort(this.id);
					conn.addmsg(m);
					m.cleanup();
					//
					this.it_map[1] = null;
					return true;
				}
				break;
			}
			case 7: {
				if (this.it_map[2] != null) {
				}
				break;
			}
		}
		return false;
	}

	public void update_money() throws IOException {
		this.item.update_assets_Inventory(false);
	}

	public long get_vang() {
		return this.vang;
	}

	public int get_ngoc() {
		return this.kimcuong;
	}

	public synchronized void update_vang(long par) {
		if ((((long) par) + this.vang) < 2_000_000_000L) {
			this.vang += par;
		}
	}

	public synchronized void update_ngoc(int par) {
		if ((((long) par) + this.kimcuong) < 2_000_000_000L) {
			this.kimcuong += par;
		}
	}

	public synchronized EffTemplate get_eff(int id) {
		for (int i = 0; i < this.list_eff.size(); i++) {
			EffTemplate temp = this.list_eff.get(i);
			if (temp.id == id) {
				return temp;
			}
		}
		return null;
	}

	public void wear_item(Item_wear it) throws IOException {
		if (this.level < it.level) {
			Service.send_box_ThongBao_OK(this, "Chưa đủ level");
			this.use_item_3 = -1;
			return;
		}
		if (it.clazz != 0 && this.clazz != it.clazz) {
			Service.send_box_ThongBao_OK(this, "Không thể mặc vật phẩm này");
			this.use_item_3 = -1;
			return;
		}
		it.typelock = 1;
		byte index_wear = ItemTemplate3.get_it_by_id(it.id).typeEquip;
		if (index_wear != -1) {
			Item_wear it_inbag = it;
			item.bag3[it_inbag.index] = null;
			if (item.it_body[index_wear] != null) {
				item.add_item_bag3(item.it_body[index_wear]);
				item.it_body[index_wear] = null;
			}
			item.it_body[index_wear] = it_inbag;
			it_inbag.index = index_wear;
		}
		item.update_Inventory(4, false);
		item.update_Inventory(7, false);
		item.update_Inventory(3, false);
		//
		Service.pet(this, this, false);
		Service.UpdateInfoMaincharInfo(this);
		this.update_info_to_all();
		Service.UpdatePvpPoint(this);
		Service.update_PK(this, this, this.type_pk, false);
		Service.getThanhTich(this, this);
		Service.Weapon_fashion(this, this, false);
		Service.charWearing(this, this, false);
		this.use_item_3 = -1;
	}

	public synchronized void update_eff() throws IOException {
		List<EffTemplate> list_temp = new ArrayList<>();
		for (int i = 0; i < list_eff.size(); i++) {
			// System.out.println(list_eff.get(i).id + " " + list_eff.get(i).param + " "
			// + (list_eff.get(i).time - System.currentTimeMillis()));
			if (list_eff.get(i).time < System.currentTimeMillis()) {
				list_temp.add(list_eff.get(i));
			}
		}
		list_eff.removeAll(list_temp);
		if (list_temp.size() > 0) {
			this.send_skill();
			this.update_info_to_all();
			list_temp.clear();
		}
	}

	public synchronized void add_new_eff(int id, int param, long time) {
		this.list_eff.add(new EffTemplate(id, param, (time + System.currentTimeMillis())));
	}

	public synchronized void update_die() {
			for (int i = 0; i < list_eff.size(); i++) {
			if (!EffTemplate.check_eff_can_save(list_eff.get(i).id)) {
				list_eff.remove(i);
				i--;
			}
		}
	}

	public void plus_point(Message m2) throws IOException {
		byte index = m2.reader().readByte();
		short value = m2.reader().readShort();
		if (this.pointAttribute >= value) {
			switch (index) {
				case 0: {
					if (this.point1 < 80) {
						this.point1 += value;
					}
					break;
				}
				case 1: {
					if (this.point2 < 80) {
						this.point2 += value;
					}
					break;
				}
				case 2: {
					if (this.point3 < 80) {
						this.point3 += value;
					}
					break;
				}
				case 3: {
					if (this.point4 < 80) {
						this.point4 += value;
					}
					break;
				}
				case 4: {
					if (this.point5 < 80) {
						this.point5 += value;
					}
					break;
				}
			}
			this.pointAttribute -= value;
			this.update_info_to_all();
		} else {
			Service.send_box_ThongBao_OK(this, "Không đủ điểm tiềm năng");
		}
	}

	public void reset_point(int type) throws IOException {
		switch (type) {
			case 0: {
				this.pointAttribute = Level.get_total_point_by_level(this.level);
				this.point1 = 1;
				this.point2 = 1;
				this.point3 = 1;
				this.point4 = 1;
				this.point5 = 0;
			}
		}
		this.update_info_to_all();
	}

	public void send_skill() throws IOException {
		Message m = new Message(-7);
		m.writer().writeByte(3);
		write_data_skill(m.writer());
		conn.addmsg(m);
		m.cleanup();
	}

	public void write_data_skill(DataOutputStream dos) throws IOException {
		dos.writeByte(this.skill_point.size());
		for (int i = 0; i < this.skill_point.size(); i++) {
			Skill_info sk_info = this.skill_point.get(i);
			dos.writeShort(sk_info.temp.indexSkillInServer);
			dos.writeShort(sk_info.temp.ID);
			dos.writeShort(sk_info.temp.idIcon);
			dos.writeByte(sk_info.temp.typeSkill);
			dos.writeByte(sk_info.temp.typeBuff);
			dos.writeUTF(sk_info.temp.name);
			dos.writeShort(sk_info.temp.typeEffSkill);
			dos.writeShort(sk_info.temp.range);
			//
			dos.writeByte(sk_info.temp.nTarget);
			dos.writeShort(sk_info.temp.rangeLan);
			dos.writeInt(sk_info.get_dame());
			dos.writeShort(sk_info.temp.manaLost);
			dos.writeInt(sk_info.temp.timeDelay);
			dos.writeByte(sk_info.temp.nKick);
			dos.writeUTF(sk_info.temp.getInfo(this.clazz));
			dos.writeByte(sk_info.temp.Lv_RQ);
			dos.writeShort(sk_info.get_percent());
			dos.writeByte(sk_info.temp.typeDevil);
			//
			dos.writeByte(sk_info.temp.op.size());
			for (int j = 0; j < sk_info.temp.op.size(); j++) {
				dos.writeByte(sk_info.temp.op.get(j).id);
				dos.writeShort(sk_info.temp.op.get(j).getParam(0));
			}
			dos.writeByte(sk_info.temp.idEffSpec);
			if (sk_info.temp.idEffSpec > 0) {
				dos.writeShort(sk_info.temp.perEffSpec);
				dos.writeShort(sk_info.temp.timeEffSpec);
			}
			dos.writeByte(sk_info.lvdevil);
			dos.writeByte(sk_info.devilpercent);
		}
	}

	public void update_skill_exp(int index, long exp) throws IOException {
		// exp *= 2;
		for (int i = 0; i < this.skill_point.size(); i++) {
			Skill_info sk_info = this.skill_point.get(i);
			if (sk_info.temp.ID == index && index < 4) {
				sk_info.exp += exp;
				long exp_total = Skill_info.get_exp_by_level(sk_info.temp.Lv_RQ);
				if (sk_info.exp >= exp_total) {
					if (Skill_Template.upgrade_skill(sk_info, this.clazz)) {
						sk_info.exp -= exp_total;
						this.send_skill();
						this.update_info_to_all();
					} else {
						sk_info.exp = exp_total - 1;
						Message m = new Message(-28);
						m.writer().writeByte(2);
						m.writer().writeShort(sk_info.temp.ID);
						m.writer().writeShort(sk_info.get_percent());
						this.conn.addmsg(m);
						m.cleanup();
					}
				} else {
					Message m = new Message(-28);
					m.writer().writeByte(2);
					m.writer().writeShort(sk_info.temp.ID);
					m.writer().writeShort(sk_info.get_percent());
					this.conn.addmsg(m);
					m.cleanup();
				}
			}
		}
	}

	public int get_head() {
		for (int i = 0; i < this.fashion.size(); i++) {
			if (this.fashion.get(i).is_use && this.fashion.get(i).data[6] != -1) {
				return this.fashion.get(i).data[6];
			}
		}
		for (int i = 0; i < this.itfashionP.size(); i++) {
			if (this.itfashionP.get(i).category == 108 && this.itfashionP.get(i).is_use) {
				return this.itfashionP.get(i).icon;
			}
		}
		return this.head;
	}

	public int get_hair() {
		for (int i = 0; i < this.fashion.size(); i++) {
			if (this.fashion.get(i).is_use && this.fashion.get(i).data[6] != -1) {
				return -2;
			}
		}
		for (int i = 0; i < this.itfashionP.size(); i++) {
			if (this.itfashionP.get(i).category == 103 && this.itfashionP.get(i).is_use) {
				return this.itfashionP.get(i).icon;
			}
		}
		return this.hair;
	}

	public void update_itfashionP(ItemFashionP temp_new, int category) {
		temp_new.is_use = true;
		for (int i = 0; i < this.itfashionP.size(); i++) {
			if (this.itfashionP.get(i).category == category && !this.itfashionP.get(i).equals(temp_new)) {
				this.itfashionP.get(i).is_use = false;
			}
		}
	}

	public ItemFashionP check_itfashionP(int id, int type) {
		for (int i = 0; i < this.itfashionP.size(); i++) {
			if (this.itfashionP.get(i).category == type && this.itfashionP.get(i).id == id) {
				return this.itfashionP.get(i);
			}
		}
		return null;
	}

	public ItemFashionP2 check_fashion(int id) {
		for (int i = 0; i < this.fashion.size(); i++) {
			if (this.fashion.get(i).id == id) {
				return this.fashion.get(i);
			}
		}
		return null;
	}

	public void update_fashionP2(ItemFashionP2 temp_new) throws IOException {
		temp_new.is_use = true;
		for (int i = 0; i < this.fashion.size(); i++) {
			if (!this.fashion.get(i).equals(temp_new)) {
				this.fashion.get(i).is_use = false;
			}
		}
		this.update_info_to_all();
	}

	public short[] get_fashion() {
		short[] result = null;
		for (int i = 0; i < this.fashion.size(); i++) {
			if (this.fashion.get(i).is_use) {
				result = new short[this.fashion.get(i).data.length];
				for (int j = 0; j < result.length; j++) {
					result[j] = this.fashion.get(i).data[j];
				}
				break;
			}
		}
		return result;
	}

	public void remove_hairf() throws IOException {
		for (int i = 0; i < this.itfashionP.size(); i++) {
			if (this.itfashionP.get(i).category == 103) {
				this.itfashionP.get(i).is_use = false;
			}
		}
		for (int i = 0; i < map.players.size(); i++) {
			Player p0 = map.players.get(i);
			Service.charWearing(this, p0, false);
		}
	}

	public void remove_fashion() throws IOException {
		for (int i = 0; i < this.fashion.size(); i++) {
			this.fashion.get(i).is_use = false;
		}
		for (int i = 0; i < map.players.size(); i++) {
			Player p0 = map.players.get(i);
			Service.charWearing(this, p0, false);
		}
	}

	public void remove_headf() throws IOException {
		for (int i = 0; i < this.itfashionP.size(); i++) {
			if (this.itfashionP.get(i).category == 108) {
				this.itfashionP.get(i).is_use = false;
			}
		}
		for (int i = 0; i < map.players.size(); i++) {
			Player p0 = map.players.get(i);
			Service.charWearing(this, p0, false);
		}
	}

	public void change_new_date() {
		if (!Util.is_same_day(Date.from(Instant.now()), date)) {
			// diem danh
			quest_daily[5] = 1;
			quest_daily[4] = 100;
			//
			date = Date.from(Instant.now());
		}
	}

	public synchronized boolean update_coin(int coin_exchange) throws IOException {
		String query = "SELECT `coin` FROM `accounts` WHERE BINARY `user` = '" + conn.user + "' LIMIT 1;";
		int coin_old = 0;
		Connection connection = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			connection = SQL.gI().getCon();
			st = connection.createStatement();
			rs = st.executeQuery(query);
			rs.next();
			coin_old = rs.getInt("coin");
			if (coin_old + coin_exchange < 0) {
				Service.send_box_ThongBao_OK(this, "Không đủ coin");
				return false;
			}
			coin_old += coin_exchange;
			st.executeUpdate("UPDATE `accounts` SET `coin` = " + coin_old + " WHERE BINARY `user` = '" + conn.user + "'");
		} catch (SQLException e) {
			Service.send_box_ThongBao_OK(this, "Đã xảy ra lỗi");
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public Skill_info get_skill_temp(int idSkill) {
		for (int i = 0; i < this.skill_point.size(); i++) {
			if (this.skill_point.get(i).temp.Lv_RQ > 0 && this.skill_point.get(i).temp.ID == idSkill) {
				return this.skill_point.get(i);
			}
		}
		return null;
	}

//	public void get_skill_taq_new(int id) throws IOException {
//		id += 4000;
//		List<Skill_info> list_remove = new ArrayList<>();
//		for (int i = 0; i < this.skill_point.size(); i++) {
//			Skill_info temp = this.skill_point.get(i);
//			if (temp.temp.ID > 2000) {
//				list_remove.add(temp);
//			}
//		}
//		this.skill_point.removeAll(list_remove);
//		list_remove.clear();
//		switch (id) {
//			case 4032: {
//				short[] id_ = new short[] {478, 476, 475};
//				for (int i = 0; i < id_.length; i++) {
//					Skill_info sk_add = new Skill_info();
//					sk_add.exp = 0;
//					sk_add.temp = Skill_Template.get_temp(id_[i]);
//					if (sk_add.temp != null) {
//						list_remove.add(sk_add);
//					}
//				}
//				break;
//			}
//			case 4033: {
//				short[] id_ = new short[] {480, 479, 477};
//				for (int i = 0; i < id_.length; i++) {
//					Skill_info sk_add = new Skill_info();
//					sk_add.exp = 0;
//					sk_add.temp = Skill_Template.get_temp(id_[i]);
//					if (sk_add.temp != null) {
//						list_remove.add(sk_add);
//					}
//				}
//				break;
//			}
//			case 4034: {
//				short[] id_ = new short[] {483, 482, 481};
//				for (int i = 0; i < id_.length; i++) {
//					Skill_info sk_add = new Skill_info();
//					sk_add.exp = 0;
//					sk_add.temp = Skill_Template.get_temp(id_[i]);
//					if (sk_add.temp != null) {
//						list_remove.add(sk_add);
//					}
//				}
//				break;
//			}
//			case 4088: {
//				short[] id_ = new short[] {484, 485, 486};
//				for (int i = 0; i < id_.length; i++) {
//					Skill_info sk_add = new Skill_info();
//					sk_add.exp = 0;
//					sk_add.temp = Skill_Template.get_temp(id_[i]);
//					if (sk_add.temp != null) {
//						list_remove.add(sk_add);
//					}
//				}
//				break;
//			}
//			case 4090: {
//				short[] id_ = new short[] {514, 513, 512};
//				for (int i = 0; i < id_.length; i++) {
//					Skill_info sk_add = new Skill_info();
//					sk_add.exp = 0;
//					sk_add.temp = Skill_Template.get_temp(id_[i]);
//					if (sk_add.temp != null) {
//						list_remove.add(sk_add);
//					}
//				}
//				break;
//			}
//			case 4091: {
//				short[] id_ = new short[] {517, 516, 515};
//				for (int i = 0; i < id_.length; i++) {
//					Skill_info sk_add = new Skill_info();
//					sk_add.exp = 0;
//					sk_add.temp = Skill_Template.get_temp(id_[i]);
//					if (sk_add.temp != null) {
//						list_remove.add(sk_add);
//					}
//				}
//				break;
//			}
//			case 4092: {
//				short[] id_ = new short[] {523, 522, 519};
//				for (int i = 0; i < id_.length; i++) {
//					Skill_info sk_add = new Skill_info();
//					sk_add.exp = 0;
//					sk_add.temp = Skill_Template.get_temp(id_[i]);
//					if (sk_add.temp != null) {
//						list_remove.add(sk_add);
//					}
//				}
//				break;
//			}
//			case 4093: {
//				short[] id_ = new short[] {521, 520, 518};
//				for (int i = 0; i < id_.length; i++) {
//					Skill_info sk_add = new Skill_info();
//					sk_add.exp = 0;
//					sk_add.temp = Skill_Template.get_temp(id_[i]);
//					if (sk_add.temp != null) {
//						list_remove.add(sk_add);
//					}
//				}
//				break;
//			}
//			case 4160: {
//				short[] id_ = new short[] {527, 526, 525, 524};
//				for (int i = 0; i < id_.length; i++) {
//					Skill_info sk_add = new Skill_info();
//					sk_add.exp = 0;
//					sk_add.temp = Skill_Template.get_temp(id_[i]);
//					if (sk_add.temp != null) {
//						list_remove.add(sk_add);
//					}
//				}
//				break;
//			}
//			case 4161: {
//				short[] id_ = new short[] {531, 530, 529, 528};
//				for (int i = 0; i < id_.length; i++) {
//					Skill_info sk_add = new Skill_info();
//					sk_add.exp = 0;
//					sk_add.temp = Skill_Template.get_temp(id_[i]);
//					if (sk_add.temp != null) {
//						list_remove.add(sk_add);
//					}
//				}
//				break;
//			}
//			case 4219: {
//				short[] id_ = new short[] {538, 537, 536};
//				for (int i = 0; i < id_.length; i++) {
//					Skill_info sk_add = new Skill_info();
//					sk_add.exp = 0;
//					sk_add.temp = Skill_Template.get_temp(id_[i]);
//					if (sk_add.temp != null) {
//						list_remove.add(sk_add);
//					}
//				}
//				break;
//			}
//			case 4220: {
//				short[] id_ = new short[] {535, 534, 533};
//				for (int i = 0; i < id_.length; i++) {
//					Skill_info sk_add = new Skill_info();
//					sk_add.exp = 0;
//					sk_add.temp = Skill_Template.get_temp(id_[i]);
//					if (sk_add.temp != null) {
//						list_remove.add(sk_add);
//					}
//				}
//				break;
//			}
//			case 4240: {
//				short[] id_ = new short[] {542, 541, 539, 540};
//				for (int i = 0; i < id_.length; i++) {
//					Skill_info sk_add = new Skill_info();
//					sk_add.exp = 0;
//					sk_add.temp = Skill_Template.get_temp(id_[i]);
//					if (sk_add.temp != null) {
//						list_remove.add(sk_add);
//					}
//				}
//				break;
//			}
//			case 4316: {
//				short[] id_ = new short[] {548, 547, 546};
//				for (int i = 0; i < id_.length; i++) {
//					Skill_info sk_add = new Skill_info();
//					sk_add.exp = 0;
//					sk_add.temp = Skill_Template.get_temp(id_[i]);
//					if (sk_add.temp != null) {
//						list_remove.add(sk_add);
//					}
//				}
//				break;
//			}
//			case 4317: {
//				short[] id_ = new short[] {545, 544, 543};
//				for (int i = 0; i < id_.length; i++) {
//					Skill_info sk_add = new Skill_info();
//					sk_add.exp = 0;
//					sk_add.temp = Skill_Template.get_temp(id_[i]);
//					if (sk_add.temp != null) {
//						list_remove.add(sk_add);
//					}
//				}
//				break;
//			}
//			case 4318: {
//				String[] name_ = new String[] {"Thần hộ thể", "Tăng trọng", "Sức nặng ngàn cân"};
//				short[] id_ = new short[] {551, 550, 549};
//				for (int i = 0; i < id_.length; i++) {
//					Skill_info sk_add = new Skill_info();
//					sk_add.exp = 0;
//					sk_add.temp = Skill_Template.get_temp(id_[i]);
//					if (sk_add.temp != null) {
//						list_remove.add(sk_add);
//					}
//				}
//				break;
//			}
//			case 4427: {
//				break;
//			}
//		}
//		this.skill_point.addAll(list_remove);
//		list_remove.clear();
//		this.send_skill();
//		this.update_info_to_all();
//	}

	public void update_info_to_all() throws IOException {
		Service.Main_char_Info(this);
		for (int i = 0; i < this.map.players.size(); i++) {
			Player p0 = this.map.players.get(i);
			if (p0.id != this.id) {
				this.map.send_char_in4_inmap(p0, this.id);
			}
		}
	}
        public void get_skill_taq_new(int id) throws IOException {
		id += 4000;
		List<Skill_info> list_remove = new ArrayList<>();
		for (int i = 0; i < this.skill_point.size(); i++) {
			Skill_info temp = this.skill_point.get(i);
			if (temp.temp.ID > 2000) {
				list_remove.add(temp);
			}
		}
		this.skill_point.removeAll(list_remove);
		list_remove.clear();
		switch (id) {
			case 4032: {
				short[] id_ = new short[] {478, 476, 475};
				for (int i = 0; i < id_.length; i++) {
					Skill_info sk_add = new Skill_info();
					sk_add.exp = 0;
					sk_add.temp = Skill_Template.get_temp(id_[i], sk_add.exp);
					if (sk_add.temp != null) {
						list_remove.add(sk_add);
					}
				}
				break;
			}
			case 4033: {
				short[] id_ = new short[] {480, 479, 477};
				for (int i = 0; i < id_.length; i++) {
					Skill_info sk_add = new Skill_info();
					sk_add.exp = 0;
					sk_add.temp = Skill_Template.get_temp(id_[i], sk_add.exp);
					if (sk_add.temp != null) {
						list_remove.add(sk_add);
					}
				}
				break;
			}
			case 4034: {
				short[] id_ = new short[] {483, 482, 481};
				for (int i = 0; i < id_.length; i++) {
					Skill_info sk_add = new Skill_info();
					sk_add.exp = 0;
					sk_add.temp = Skill_Template.get_temp(id_[i], sk_add.exp);
					if (sk_add.temp != null) {
						list_remove.add(sk_add);
					}
				}
				break;
			}
			case 4088: {
				short[] id_ = new short[] {484, 485, 486};
				for (int i = 0; i < id_.length; i++) {
					Skill_info sk_add = new Skill_info();
					sk_add.exp = 0;
					sk_add.temp = Skill_Template.get_temp(id_[i], sk_add.exp);
					if (sk_add.temp != null) {
						list_remove.add(sk_add);
					}
				}
				break;
			}
			case 4090: {
				short[] id_ = new short[] {514, 513, 512};
				for (int i = 0; i < id_.length; i++) {
					Skill_info sk_add = new Skill_info();
					sk_add.exp = 0;
					sk_add.temp = Skill_Template.get_temp(id_[i], sk_add.exp);
					if (sk_add.temp != null) {
						list_remove.add(sk_add);
					}
				}
				break;
			}
			case 4091: {
				short[] id_ = new short[] {517, 516, 515};
				for (int i = 0; i < id_.length; i++) {
					Skill_info sk_add = new Skill_info();
					sk_add.exp = 0;
					sk_add.temp = Skill_Template.get_temp(id_[i], sk_add.exp);
					if (sk_add.temp != null) {
						list_remove.add(sk_add);
					}
				}
				break;
			}
			case 4092: {
				short[] id_ = new short[] {523, 522, 519};
				for (int i = 0; i < id_.length; i++) {
					Skill_info sk_add = new Skill_info();
					sk_add.exp = 0;
					sk_add.temp = Skill_Template.get_temp(id_[i], sk_add.exp);
					if (sk_add.temp != null) {
						list_remove.add(sk_add);
					}
				}
				break;
			}
			case 4093: {
				short[] id_ = new short[] {521, 520, 518};
				for (int i = 0; i < id_.length; i++) {
					Skill_info sk_add = new Skill_info();
					sk_add.exp = 0;
					sk_add.temp = Skill_Template.get_temp(id_[i], sk_add.exp);
					if (sk_add.temp != null) {
						list_remove.add(sk_add);
					}
				}
				break;
			}
			case 4160: {
				short[] id_ = new short[] {527, 526, 525, 524};
				for (int i = 0; i < id_.length; i++) {
					Skill_info sk_add = new Skill_info();
					sk_add.exp = 0;
					sk_add.temp = Skill_Template.get_temp(id_[i], sk_add.exp);
					if (sk_add.temp != null) {
						list_remove.add(sk_add);
					}
				}
				break;
			}
			case 4161: {
				short[] id_ = new short[] {531, 530, 529, 528};
				for (int i = 0; i < id_.length; i++) {
					Skill_info sk_add = new Skill_info();
					sk_add.exp = 0;
					sk_add.temp = Skill_Template.get_temp(id_[i], sk_add.exp);
					if (sk_add.temp != null) {
						list_remove.add(sk_add);
					}
				}
				break;
			}
			case 4219: {
				short[] id_ = new short[] {538, 537, 536};
				for (int i = 0; i < id_.length; i++) {
					Skill_info sk_add = new Skill_info();
					sk_add.exp = 0;
					sk_add.temp = Skill_Template.get_temp(id_[i], sk_add.exp);
					if (sk_add.temp != null) {
						list_remove.add(sk_add);
					}
				}
				break;
			}
			case 4220: {
				short[] id_ = new short[] {535, 534, 533};
				for (int i = 0; i < id_.length; i++) {
					Skill_info sk_add = new Skill_info();
					sk_add.exp = 0;
					sk_add.temp = Skill_Template.get_temp(id_[i], sk_add.exp);
					if (sk_add.temp != null) {
						list_remove.add(sk_add);
					}
				}
				break;
			}
			case 4240: {
				short[] id_ = new short[] {542, 541, 539, 540};
				for (int i = 0; i < id_.length; i++) {
					Skill_info sk_add = new Skill_info();
					sk_add.exp = 0;
					sk_add.temp = Skill_Template.get_temp(id_[i], sk_add.exp);
					if (sk_add.temp != null) {
						list_remove.add(sk_add);
					}
				}
				break;
			}
			case 4316: {
				short[] id_ = new short[] {548, 547, 546};
				for (int i = 0; i < id_.length; i++) {
					Skill_info sk_add = new Skill_info();
					sk_add.exp = 0;
					sk_add.temp = Skill_Template.get_temp(id_[i], sk_add.exp);
					if (sk_add.temp != null) {
						list_remove.add(sk_add);
					}
				}
				break;
			}
			case 4317: {
				short[] id_ = new short[] {545, 544, 543};
				for (int i = 0; i < id_.length; i++) {
					Skill_info sk_add = new Skill_info();
					sk_add.exp = 0;
					sk_add.temp = Skill_Template.get_temp(id_[i], sk_add.exp);
					if (sk_add.temp != null) {
						list_remove.add(sk_add);
					}
				}
				break;
			}
			case 4318: {
				String[] name_ = new String[] {"Thần hộ thể", "Tăng trọng", "Sức nặng ngàn cân"};
				short[] id_ = new short[] {551, 550, 549};
				for (int i = 0; i < id_.length; i++) {
					Skill_info sk_add = new Skill_info();
					sk_add.exp = 0;
					sk_add.temp = Skill_Template.get_temp(id_[i], sk_add.exp);
					if (sk_add.temp != null) {
						list_remove.add(sk_add);
					}
				}
				break;
			}
			case 4427: {
				break;
			}
		}
		this.skill_point.addAll(list_remove);
		list_remove.clear();
		this.send_skill();
		this.update_info_to_all();
	}
}
