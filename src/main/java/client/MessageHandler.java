package client;

import java.io.IOException;
import activities.Chat;
import activities.ChuyenHoa;
import activities.Friend;
import activities.Kham_Ngoc;
import activities.Trade;
import activities.UpgradeDevil;
import activities.UpgradeItem;
import activities.VongQuay;
import core.Log;
import core.Manager;
import core.MenuController;
import core.Service;
import core.Util;
import io.Message;
import io.Session;
import io.SessionManager;
import map.Map;

public class MessageHandler {
	private Session conn;

	public MessageHandler(Session session) {
		this.conn = session;
	}

	public void process_msg(Message m) throws IOException {
		switch (m.cmd) {
			case 45: {
				UpgradeDevil.process(conn.p, m);
				break;
			}
			case -25: {
				Party.process(conn.p, m);
				break;
			}
			case 20: {
				Buff.process(conn.p, m);
				break;
			}
			case -32: {
				PlayerChest.process(conn.p, m);
				break;
			}
			case 54: {
				VongQuay.process(conn.p, m);
				break;
			}
			case 18: {
				Chat.process(conn.p, m, 0);
				break;
			}
			case -29: {
				Friend.process(conn.p, m);
				break;
			}
			case -49: {
				Trade.process(conn.p, m);
				break;
			}
			case -67: {
				Kham_Ngoc.process(conn.p, m);
				break;
			}
			case -77: {
				ChuyenHoa.process(conn.p, m);
				break;
			}
			case -16: {
				conn.p.plus_point(m);
				break;
			}
			case -13: { // use potion
				short id = m.reader().readShort();
				UseItem.use_item_potion(conn.p, id);
				break;
			}
			case -11: {
				ClientYesNo.process(conn.p, m);
				break;
			}
			case -58: {
				ClientInput.process(conn.p, m);
				break;
			}
			case -46: {
				byte type = m.reader().readByte();
				String text = m.reader().readUTF();
				if (conn.kichhoat == 0) {
					Service.send_box_ThongBao_OK(conn.p, "Tài khoản trải nghiệm không thể chat KTG!");
					return;
				}
				if (type == 0) {
					if (conn.p.get_ngoc() < 5) {
						Service.send_box_ThongBao_OK(null, "Không đủ 5 ruby để chat KTG");
						return;
					}
					conn.p.update_ngoc(-5);
					conn.p.update_money();
					Log.gI().add_log(conn.p, "Chat KTG mất -5 ruby");
					Manager.gI().chatKTG(conn.p, "@" + conn.p.name + " : " + text);
					Service.send_box_ThongBao_OK(conn.p, "Chat KTG thành công với nội dung: " + text);
				}
				break;
			}
			case -48: {
				UpgradeItem.process(conn.p, m);
				break;
			}
			case -22: {
				UseItem.process(conn.p, m);
				break;
			}
			case -105: {
				Service.get_data_in4_potion(conn.p, m);
				break;
			}
			case -21: {
				Service.sell_item(conn.p, m);
				break;
			}
			case 12: {
				conn.p.map.pick_item(conn.p, m);
				break;
			}
			case -42: {
				String name = m.reader().readUTF();
				Player p0 = Map.get_player_by_name_allmap(name);
				if (p0 != null) {
					Service.send_view_other_player(p0, conn.p);
				}
				break;
			}
			case 14: {
				conn.p.map.change_flag(conn.p, m);
				break;
			}
			case 6: {
				conn.p.request_live_from_die(m);
				break;
			}
			case -18: {
				Service.buy_item(conn.p, m);
				break;
			}
			case -5: {
				int id = m.reader().readUnsignedShort();
				conn.p.map.send_char_in4_inmap(conn.p, id);
				break;
			}
			case 46: {
				Service.checkPlayInMap(conn.p, m);
				break;
			}
			case 30: {
				conn.p.is_receiv_msg_move = true;
				// System.out.println("receiv_msg_30");
				// while (conn.p.list_msg_cache.size() > 0) {
				// try {
				// Message m_send = conn.p.list_msg_cache.take();
				// conn.addmsg(m_send);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
				// }
				// Service.update_PK(conn.p, conn.p, true);
				// Service.pet(conn.p, conn.p, true);
				break;
			}
			case 23: {
				Service.area_select(conn.p, m);
				break;
			}
			case 17: {
				conn.p.map.send_chat(conn.p, m);
				break;
			}
			case -20: {
				MenuController.process_menu(conn.p, m);
				break;
			}
			case -19: {
				MenuController.send_menu(conn.p, m);
				break;
			}
			case 0: {
				short id_map_change = m.reader().readShort();
				byte action_change = m.reader().readByte();
				// System.out.println(id_map_change);
				// System.out.println(action_change);
				conn.p.ischangemap = false;
				conn.p.is_receiv_msg_move = true;
				while (conn.p.list_msg_cache.size() > 0) {
					try {
						Message m_send = conn.p.list_msg_cache.take();
						if (m_send.cmd == 0) {
							if (id_map_change == 0) {
								conn.addmsg(m_send);
								Message m2 = new Message(21);
								m2.writer().writeByte(conn.p.map.zone_id);
								m2.writer().writeByte(0);
								m2.writer().writeShort(conn.p.x);
								m2.writer().writeShort(conn.p.y);
								m2.writer().writeInt(conn.p.body.get_hp_max(true));
								m2.writer().writeInt(conn.p.hp);
								m2.writer().writeInt(conn.p.body.get_mp_max(true));
								m2.writer().writeInt(conn.p.mp);
								m2.writer().writeByte(12);
								m2.writer().writeShort(260);
								conn.addmsg(m2);
								m2.cleanup();
							}
						} else {
							conn.addmsg(m_send);
						}
						continue;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				conn.p.map.send_in4_obj_inmap(conn.p);
				break;
			}
			case 2: {
				conn.p.map.use_skill(conn.p, m);
				break;
			}
			case -70: {
				conn.p.map.update_num_player_in_map(conn.p);
				break;
			}
			case -45: {// update pk point
				try {
					Message m2 = new Message(-45);
					m2.writer().writeInt(0);
					m2.writer().writeByte(-1);
					conn.addmsg(m2);
					m2.cleanup();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
			case -33: {
				Service.rms_process(conn.p, m);
				break;
			}
			case 1: {
				conn.p.map.send_move(conn.p, m);
				break;
			}
			case 4: {
				int id_mob = m.reader().readShort();
				Service.request_mob_in4(conn.p, id_mob);
				break;
			}
			case 48: {
				Service.send_obj_template(conn.p, m);
				break;
			}
			case -9: {
				if (conn.p == null) {
					login(m);
				}
				break;
			}
			case -8: {
				conn.create_char(m);
				break;
			}
			case -51: {
				Service.send_icon(m, conn);
				break;
			}
			case -82: {
				conn.ReadPartNew(m);
				break;
			}
			case -38: {
				conn.send_data_from_server(m);
				break;
			}
			case -2: {
				if (conn.user == null && conn.pass == null) {
					conn.login(m);
				}
				break;
			}
			case -6: {
				Service.send_msg_data(conn, -6, "data/msg/login/x2msg_-6_638026480832499923", false);
				Service.send_msg_data(conn, -7, "data/msg/login/request/18", false); // data potion clan
				break;
			}
			case -7: {
				conn.request_data_update(m);
				break;
			}
			default: {
				//Service.send_box_ThongBao_OK(conn.p, "Tính năng " + m.cmd + " chưa được update!");
				// System.out.println("default onRecieveMsg : " + m.cmd);
				break;
			}
		}
	}

	public void login(Message m2) throws IOException {
		short id = m2.reader().readShort();
		byte type = m2.reader().readByte();
		short idsupport = m2.reader().readShort();
		Player p0 = new Player(conn, conn.list_char.get(id));
		if (!p0.setup()) {
			conn.disconnect();
			return;
		}
		p0.setin4();
		conn.p = p0;
		// data login
		String path = "data/msg/login/";
		Service.UpdateInfoMaincharInfo(conn.p);
		Service.Main_char_Info(conn.p);
		Service.UpdatePvpPoint(conn.p);
		Service.update_PK(conn.p, conn.p, -1, false);
		Service.getThanhTich(conn.p, conn.p);
		conn.p.item.send_maxbag_Inventory();
		// send data map
		conn.p.map.enter_map(conn.p, true);
		// Service.send_msg_data(conn, 0, ("data/map/63"), false);
		//
		Service.update_PK(conn.p, conn.p, -1, true);
		Service.pet(conn.p, conn.p, true);
		conn.p.item.update_Inventory(5, true);
		conn.p.item.update_Inventory(4, true);
		conn.p.item.update_Inventory(7, true);
		conn.p.item.update_Inventory(3, true);
		conn.p.item.update_assets_Inventory(true);
		Service.send_x2cd(conn.p, true);
		Service.ChestWanted(conn.p, true);
		//
		conn.p.item.send_maxbox_Inventory();
		conn.p.item.update_assets_Box(true);
		conn.p.item.update_Inventory_box(4, true);
		conn.p.item.update_Inventory_box(7, true);
		conn.p.item.update_Inventory_box(3, true);
		//
		Service.send_msg_data(conn, -23, (path + "x2msg_-23_638026630723432805"), true); // quest
		Service.Weapon_fashion(conn.p, conn.p, true);
		Service.charWearing(conn.p, conn.p, true);
		Service.login_ok(conn.p, true);
		// Service.send_msg_data(conn, -56, ("data/msg/x2msg_-56_638120892401809568"), true);
		for (int i = 0; i < conn.p.rms.length; i++) {
			if (conn.p.rms[i].length > 0) {
				Message m22 = new Message(-33);
				m22.writer().writeByte(i);
				m22.writer().writeShort(conn.p.rms[i].length);
				m22.writer().write(conn.p.rms[i]);
				conn.p.list_msg_cache.add(m22);
				// conn.addmsg(m22);
				m22.cleanup();
			}
		}
		//
		String notice = "@SERVER : Chào mừng đến với server Tứ Hoàng Online, số người online : "
		      + SessionManager.CLIENT_ENTRYS.size();
		Message m = new Message(-31);
		m.writer().writeByte(0);
		m.writer().writeUTF(notice);
		m.writer().writeByte(5);
		conn.p.list_msg_cache.add(m);
		m.cleanup();
		Log.gI().add_log(conn.p, "Login : [Beri] " + Util.number_format(conn.p.get_vang()) + " [Ruby] "
		      + Util.number_format(conn.p.get_ngoc()));
		//
		// for (int i = 0; i < ItemTemplate4.ENTRYS.size(); i++) {
		// if (ItemTemplate4.ENTRYS.get(i).type == 7) {
		// ItemBag47 it = new ItemBag47();
		// it.id = ItemTemplate4.ENTRYS.get(i).id;
		// it.category = 4;
		// it.quant = 11111;
		// conn.p.item.add_item_bag47(4, it);
		// }
		// }
		// conn.p.item.update_Inventory(4, true);
	}
}
