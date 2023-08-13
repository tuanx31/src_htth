package activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import client.Player;
import core.Service;
import core.Util;
import io.Message;
import template.ItemBag47;
import template.ItemTemplate4;
import template.ItemVongQuayTemplate;

public class VongQuay {
	public static void show_table(Player p) throws IOException {
		Message m = new Message(54);
		m.writer().writeByte(0);
		p.conn.addmsg(m);
		m.cleanup();
	}

	public static void process(Player p, Message m2) throws IOException {
		byte action = m2.reader().readByte();
		// System.out.println(action);
		switch (action) {
			case 3: {
				Message m = new Message(54);
				m.writer().writeByte(3);
				m.writer().writeByte(ItemVongQuayTemplate.ENTRYS.size());
				for (int i = 0; i < ItemVongQuayTemplate.ENTRYS.size(); i++) {
					m.writer().writeByte(ItemVongQuayTemplate.ENTRYS.get(i).type);
					switch (ItemVongQuayTemplate.ENTRYS.get(i).type) {
						case 4: {
							m.writer().writeShort(ItemTemplate4.get_it_by_id(ItemVongQuayTemplate.ENTRYS.get(i).id).icon);
							break;
						}
					}
				}
				p.conn.addmsg(m);
				m.cleanup();
				break;
			}
			case 4: {
				Service.input_text(p, 0, "Số lượng", new String[] {"Nhập số lượng"});
				break;
			}
			case 2:
			case 1: {
				int quant_reward = 0;
				if (action == 1) {
					if (p.item.total_item_bag_by_id(4, 232) < 1) {
						Service.send_box_ThongBao_OK(p, "Không đủ vé!");
						return;
					}
					p.item.remove_item47(4, 232, 1);
					quant_reward = 3;
				} else {
					if (p.item.total_item_bag_by_id(4, 232) < 3) {
						Service.send_box_ThongBao_OK(p, "Không đủ vé!");
						return;
					}
					p.item.remove_item47(4, 232, 3);
					quant_reward = 9;
				}
				List<ItemVongQuayTemplate> list_reward = new ArrayList<>();
				for (int i = 0; i < quant_reward; i++) {
					list_reward.add(ItemVongQuayTemplate.get_random());
				}
				boolean add_vang = false;
				Message m = new Message(54);
				m.writer().writeByte(action);
				m.writer().writeByte(list_reward.size());
				for (int i = 0; i < list_reward.size(); i++) {
					ItemVongQuayTemplate temp = list_reward.get(i);
					if (temp == null) { // lose
						if (!add_vang && 20 > Util.random(100)) {
							int vang_receiv =
							      (10 > Util.random(100)) ? Util.random(500_000, 1_000_000) : Util.random(250_000, 450_000);
							m.writer().writeByte(4);
							m.writer().writeUTF("Beri");
							m.writer().writeShort(0);
							m.writer().writeInt(vang_receiv);
							m.writer().writeByte(0);
							p.update_vang(vang_receiv);
							p.update_money();
							add_vang = true;
						} else {
							m.writer().writeByte(0);
							m.writer().writeUTF("");
							m.writer().writeShort(-1);
							m.writer().writeInt(0);
							m.writer().writeByte(0);
						}
					} else {
						m.writer().writeByte(4); // type
						m.writer().writeUTF(ItemTemplate4.get_it_by_id(temp.id).name);
						m.writer().writeShort(ItemTemplate4.get_it_by_id(temp.id).icon);
						m.writer().writeInt(1); // quant
						m.writer().writeByte(0); // color
						//
						ItemBag47 it = new ItemBag47();
						it.id = temp.id;
						it.category = 4;
						it.quant = 1;
						if (p.item.able_bag() > 0 && (p.item.total_item_bag_by_id(4, it.id) + it.quant) < 32_000) {
							p.item.add_item_bag47(4, it);
						}
					}
				}
				list_reward.clear();
				p.conn.addmsg(m);
				m.cleanup();
				p.item.update_Inventory(4, false);
				break;
			}
		}
	}
}
