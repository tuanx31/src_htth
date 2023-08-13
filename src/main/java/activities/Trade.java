package activities;

import java.io.IOException;
import java.util.ArrayList;
import client.Item;
import client.Player;
import core.Log;
import core.Service;
import io.Message;
import template.Item_wear;

public class Trade {
	public static synchronized void process(Player p, Message m2) throws IOException {
		byte action = m2.reader().readByte();
		short id = -1;
		byte cat = -1;
		int num = -1;
		String str = "";
		if (action == 1 || action == 6) {
			id = m2.reader().readShort();
			cat = m2.reader().readByte();
			num = m2.reader().readInt();
		}
		if (action == 2) {
			str = m2.reader().readUTF();
		}
		// System.out.println(action);
		// System.out.println(id);
		// System.out.println(cat);
		// System.out.println(num);
		// System.out.println(str);
		if (p.conn.kichhoat == 0) {
			Service.send_box_ThongBao_OK(p, "Tài khoản trải nghiệm không thể giao dịch!");
			return;
		}
		switch (action) {
			case 6: { // request
				Player p0 = p.map.get_player_by_id_inmap(id);
				if (p0 != null) {
					if (p0.conn.kichhoat == 0) {
						Service.send_box_ThongBao_OK(p, "Tài khoản đối phương là tk trải nghiệm không thể giao dịch!");
						return;
					}
					if (p0.trade_target != null) {
						Service.send_box_ThongBao_OK(p, "Đối phương đang có giao dịch");
						return;
					}
					p0.trade_target = p;
					p.trade_target = p0;
					Service.send_box_yesno(p0, 500, p.name + " muốn mời bạn giao dịch?");
				} else {
					Service.send_box_ThongBao_OK(p0, "Đối phương không online");
				}
				break;
			}
			case 1: {
				if (num == 1 && cat == 3 && p.trade_target != null) { // add item vao
					Item_wear it_select = p.item.bag3[id];
					if (it_select != null && p.list_item_trade.size() < 4) {
						for (int i = 0; i < p.list_item_trade.size(); i++) {
							if (it_select.equals(p.list_item_trade.get(i))) {
								Service.send_box_ThongBao_OK(p, "Không thể thực hiện");
								return;
							}
						}
						if (it_select.typelock == 1) {
							return;
						}
						Message m = new Message(-49);
						m.writer().writeByte(1);
						m.writer().writeByte(1);
						m.writer().writeByte(3);
						m.writer().writeByte(1);
						Item.readUpdateItem(m.writer(), it_select);
						p.trade_target.conn.addmsg(m);
						m.cleanup();
						//
						m = new Message(-49);
						m.writer().writeByte(1);
						m.writer().writeByte(0);
						m.writer().writeByte(3);
						m.writer().writeByte(1);
						Item.readUpdateItem(m.writer(), it_select);
						p.conn.addmsg(m);
						m.cleanup();
						p.list_item_trade.add(it_select);
					} else {
						Service.send_box_ThongBao_OK(p, "Không thể thêm vật phẩm");
					}
				} else if (num >= 0 && cat == 6 && id == 0 && p.trade_target != null) { // add beri
					if (num > p.get_vang()) {
						Service.send_box_ThongBao_OK(p, "Không đủ beri");
						return;
					}
					Message m = new Message(-49);
					m.writer().writeByte(1);
					m.writer().writeByte(1);
					m.writer().writeByte(6);
					m.writer().writeInt(num);
					p.trade_target.conn.addmsg(m);
					m.cleanup();
					//
					m = new Message(-49);
					m.writer().writeByte(1);
					m.writer().writeByte(0);
					m.writer().writeByte(6);
					m.writer().writeInt(num);
					p.conn.addmsg(m);
					m.cleanup();
					//
					p.money_trade = num;
				}
				break;
			}
			case 5: { // thoat trade
				if (id == -1 && cat == -1 && num == -1 && p.trade_target != null) {
					end_trade_by_disconnect(p.trade_target, p, false);
					end_trade_by_disconnect(p, p.trade_target, false);
					// p.is_lock_trade = false;
					// p.is_accept_trade = false;
					// p.list_item_trade = null;
					// p.trade_target = null;
				}
				break;
			}
			case 2: { // chat popup
				if (id == -1 && cat == -1 && num == -1 && !str.isEmpty() && p.trade_target != null) {
					Message m = new Message(-49);
					m.writer().writeByte(2);
					m.writer().writeByte(1);
					m.writer().writeUTF(str);
					p.trade_target.conn.addmsg(m);
					m.cleanup();
				}
				break;
			}
			case 3: { // lock
				if (id == -1 && cat == -1 && num == -1 && p.trade_target != null && !p.is_lock_trade) {
					Message m = new Message(-49);
					m.writer().writeByte(3);
					m.writer().writeByte(1);
					p.trade_target.conn.addmsg(m);
					m.cleanup();
					//
					m = new Message(-49);
					m.writer().writeByte(3);
					m.writer().writeByte(0);
					p.conn.addmsg(m);
					m.cleanup();
					//
					p.is_lock_trade = true;
				}
				break;
			}
			case 4: { // accept
				if (id == -1 && cat == -1 && num == -1 && p.trade_target != null && p.is_lock_trade
				      && p.trade_target.is_lock_trade && !p.is_accept_trade) {
					p.is_accept_trade = true;
					if (p.trade_target.is_accept_trade) {
						// trade beri
						p.update_vang(-p.money_trade);
						p.trade_target.update_vang(-p.trade_target.money_trade);
						//
						p.update_vang(p.trade_target.money_trade);
						p.trade_target.update_vang(p.money_trade);
						//
						p.update_money();
						p.trade_target.update_money();
						Log.gI().add_log(p,
						      "Giao dịch bery với " + p.trade_target.name + " nhận +" + p.trade_target.money_trade + " beri");
						Log.gI().add_log(p.trade_target, "Giao dịch bery với " + p.trade_target.trade_target.name + " nhận +"
						      + p.trade_target.trade_target.money_trade + " beri");
						// trade item
						for (int i = 0; i < p.list_item_trade.size(); i++) {
							p.item.remove_item_wear(p.list_item_trade.get(i));
						}
						for (int i = 0; i < p.trade_target.list_item_trade.size(); i++) {
							p.trade_target.item.remove_item_wear(p.trade_target.list_item_trade.get(i));
						}
						for (int i = 0; i < p.trade_target.list_item_trade.size(); i++) {
							Item_wear it_add = new Item_wear();
							it_add.clone_obj(p.trade_target.list_item_trade.get(i));
							p.item.add_item_bag3(it_add);
							Log.gI().add_log(p, "Giao dịch bery với " + p.trade_target.name + " nhận " + it_add.name);
						}
						for (int i = 0; i < p.list_item_trade.size(); i++) {
							Item_wear it_add = new Item_wear();
							it_add.clone_obj(p.list_item_trade.get(i));
							p.trade_target.item.add_item_bag3(it_add);
							Log.gI().add_log(p.trade_target,
							      "Giao dịch bery với " + p.trade_target.trade_target.name + " nhận " + it_add.name);
						}
						p.item.update_Inventory(4, false);
						p.item.update_Inventory(7, false);
						p.item.update_Inventory(3, false);
						p.trade_target.item.update_Inventory(4, false);
						p.trade_target.item.update_Inventory(7, false);
						p.trade_target.item.update_Inventory(3, false);
						end_trade_by_disconnect(p.trade_target, p, true);
						end_trade_by_disconnect(p, p.trade_target, true);
					}
				}
				break;
			}
		}
	}

	public static void end_trade_by_disconnect(Player p_mine, Player p_target, boolean issuc) throws IOException {
		Message m = new Message(-49);
		m.writer().writeByte(5);
		m.writer().writeByte(0);
		if (issuc) {
			m.writer().writeUTF("Giao dịch với " + p_target.name + " hoàn tất");
		} else {
			m.writer().writeUTF(p_target.name + " hủy giao dịch");
		}
		p_mine.conn.addmsg(m);
		m.cleanup();
		//
		p_mine.money_trade = 0;
		p_mine.is_lock_trade = false;
		p_mine.is_accept_trade = false;
		p_mine.list_item_trade = null;
		p_mine.trade_target = null;
	}

	public static void show_table(Player p, String name) throws IOException {
		if (p.conn.kichhoat == 0) {
			Service.send_box_ThongBao_OK(p, "Tài khoản trải nghiệm không thể giao dịch!");
			return;
		}
		Message m = new Message(-49);
		m.writer().writeByte(0);
		m.writer().writeByte(0);
		m.writer().writeUTF(name);
		p.conn.addmsg(m);
		m.cleanup();
		p.list_item_trade = new ArrayList<>();
	}
}
